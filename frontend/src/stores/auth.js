import { defineStore } from 'pinia'
import api from '../api/axios'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem('race_walking_token') || null,
    user: JSON.parse(localStorage.getItem('race_walking_user') || 'null'),
    loading: false,
    error: null
  }),

  getters: {
    isAuthenticated: (state) => !!state.token,
    currentJudge: (state) => state.user,
    judgeName: (state) => state.user?.name || 'Juiz',
    judgeCode: (state) => state.user?.judgeCode || '',
    isAdmin: (state) => state.user?.role === 'ROLE_ADMIN'
  },

  actions: {
    async login(username, password) {
      this.loading = true
      this.error = null
      try {
        const response = await api.post('/auth/login', { username, password })
        const data = response.data
        this.token = data.token
        this.user = {
          id: data.id,
          username: data.username,
          name: data.name,
          role: data.role,
          judgeCode: data.judgeCode
        }
        localStorage.setItem('race_walking_token', data.token)
        localStorage.setItem('race_walking_user', JSON.stringify(this.user))
        return true
      } catch (err) {
        this.error = err.response?.data?.error || 'Erro ao efetuar login'
        throw new Error(this.error)
      } finally {
        this.loading = false
      }
    },

    logout() {
      this.token = null
      this.user = null
      localStorage.removeItem('race_walking_token')
      localStorage.removeItem('race_walking_user')
    }
  }
})
