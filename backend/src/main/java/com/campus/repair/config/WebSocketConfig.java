package com.campus.repair.config;

import com.campus.repair.security.JwtTokenProvider;
import com.campus.repair.websocket.NotifyWebSocketServer;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

@Configuration
@RequiredArgsConstructor
public class WebSocketConfig {

    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        NotifyWebSocketServer.setJwtTokenProvider(jwtTokenProvider);
        return new ServerEndpointExporter();
    }
}
