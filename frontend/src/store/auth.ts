import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useAuthStore = defineStore('auth', () => {
  const token = ref<string>(localStorage.getItem('token') || '')
  const user = ref<any>(JSON.parse(localStorage.getItem('user') || 'null'))

  const isLoggedIn = computed(() => !!token.value)
  const role = computed(() => user.value?.role)
  const isStudent = computed(() => role.value === 0)
  const isWorker = computed(() => role.value === 1)
  const isAdmin = computed(() => role.value === 2)

  function setAuth(t: string, u: any) {
    token.value = t
    user.value = u
    localStorage.setItem('token', t)
    localStorage.setItem('user', JSON.stringify(u))
  }

  function logout() {
    token.value = ''
    user.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('user')
  }

  return { token, user, isLoggedIn, role, isStudent, isWorker, isAdmin, setAuth, logout }
})
