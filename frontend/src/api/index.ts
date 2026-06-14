import http from '@/utils/http'

export const authApi = {
  login: (data: { username: string; password: string }) =>
    http.post<any, any>('/auth/login', data),
}

export const userApi = {
  me: () => http.get<any, any>('/users/me'),
  list: (params: any) => http.get<any, any>('/users', { params }),
  workers: () => http.get<any, any>('/users/workers'),
  updateStatus: (id: number, status: number) =>
    http.put<any, any>(`/users/${id}/status`, null, { params: { status } }),
  create: (data: any) => http.post<any, any>('/users', data),
}

export const orderApi = {
  create: (data: any) => http.post<any, any>('/orders', data),
  myOrders: (params: any) => http.get<any, any>('/orders/my', { params }),
  workerOrders: (params: any) => http.get<any, any>('/orders/worker', { params }),
  allOrders: (params: any) => http.get<any, any>('/orders', { params }),
  detail: (id: number) => http.get<any, any>(`/orders/${id}`),
  assign: (data: any) => http.post<any, any>('/orders/assign', data),
  accept: (id: number) => http.post<any, any>(`/orders/${id}/accept`),
  finish: (id: number, remark?: string) =>
    http.post<any, any>(`/orders/${id}/finish`, null, { params: { remark } }),
  cancel: (id: number) => http.post<any, any>(`/orders/${id}/cancel`),
  evaluate: (data: any) => http.post<any, any>('/orders/evaluate', data),
}

export const statisticsApi = {
  overview: () => http.get<any, any>('/statistics/overview'),
}

export const noticeApi = {
  list: () => http.get<any, any>('/notices'),
  listAll: () => http.get<any, any>('/notices/all'),
  create: (data: { title: string; content: string }) => http.post<any, any>('/notices', data),
  toggle: (id: number) => http.put<any, any>(`/notices/${id}/toggle`),
  remove: (id: number) => http.delete<any, any>(`/notices/${id}`),
}
