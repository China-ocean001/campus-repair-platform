package com.campus.repair.websocket;

import com.campus.repair.security.JwtTokenProvider;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket服务端 — 向指定用户推送工单状态变更通知
 * 连接路径: ws://host/ws/notify/{userId}?token=xxx
 */
@Component
@ServerEndpoint("/ws/notify/{userId}")
public class NotifyWebSocketServer {

    private static final Logger log = LoggerFactory.getLogger(NotifyWebSocketServer.class);
    private static final ConcurrentHashMap<Long, Session> SESSIONS = new ConcurrentHashMap<>();

    // 通过 Spring 容器注入，需静态持有（JSR-356每次连接都new一个实例）
    private static JwtTokenProvider jwtTokenProvider;

    public static void setJwtTokenProvider(JwtTokenProvider provider) {
        jwtTokenProvider = provider;
    }

    @OnOpen
    public void onOpen(Session session, @PathParam("userId") Long userId) {
        // 从 query 参数取 token 校验
        String query = session.getQueryString(); // "token=xxx"
        String token = null;
        if (query != null) {
            for (String param : query.split("&")) {
                if (param.startsWith("token=")) {
                    token = param.substring(6);
                    break;
                }
            }
        }
        if (jwtTokenProvider == null || token == null || !jwtTokenProvider.validateToken(token)) {
            log.warn("用户[{}]WebSocket连接被拒绝：token无效", userId);
            try { session.close(new CloseReason(CloseReason.CloseCodes.VIOLATED_POLICY, "Unauthorized")); } catch (IOException ignored) {}
            return;
        }
        // 校验token中的userId与路径userId一致
        try {
            String username = jwtTokenProvider.getUsernameFromToken(token);
            Long tokenUserId = jwtTokenProvider.getUserIdFromToken(token);
            if (!userId.equals(tokenUserId)) {
                log.warn("用户[{}]WebSocket连接被拒绝：userId不匹配", userId);
                session.close(new CloseReason(CloseReason.CloseCodes.VIOLATED_POLICY, "Forbidden"));
                return;
            }
        } catch (Exception e) {
            log.warn("用户[{}]WebSocket token解析失败", userId);
            try { session.close(new CloseReason(CloseReason.CloseCodes.VIOLATED_POLICY, "Unauthorized")); } catch (IOException ignored) {}
            return;
        }
        SESSIONS.put(userId, session);
        log.info("用户[{}]建立WebSocket连接", userId);
    }

    @OnClose
    public void onClose(@PathParam("userId") Long userId) {
        SESSIONS.remove(userId);
        log.info("用户[{}]断开WebSocket连接", userId);
    }

    @OnError
    public void onError(Throwable error, @PathParam("userId") Long userId) {
        log.error("用户[{}]WebSocket错误: {}", userId, error.getMessage());
        SESSIONS.remove(userId);
    }

    @OnMessage
    public void onMessage(String message, @PathParam("userId") Long userId) {
        sendToUser(userId, "pong");
    }

    public static void sendToUser(Long userId, String message) {
        Session session = SESSIONS.get(userId);
        if (session != null && session.isOpen()) {
            try {
                session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                log.error("向用户[{}]推送消息失败: {}", userId, e.getMessage());
            }
        }
    }

    public static void broadcast(String message) {
        SESSIONS.forEach((uid, session) -> sendToUser(uid, message));
    }
}
