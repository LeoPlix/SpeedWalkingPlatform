import { defineStore } from 'pinia'
import api from '../api/axios'

export const useCompetitionStore = defineStore('competition', {
  state: () => ({
    competitions: [],
    activeCompetition: null,
    loading: false,
    error: null
  }),

  actions: {
    async fetchCompetitions() {
      this.loading = true
      try {
        const res = await api.get('/competitions')
        this.competitions = res.data
        if (this.competitions.length > 0) {
          if (!this.activeCompetition) {
            // Find first active or first in list
            const active = this.competitions.find(c => c.status === 'ACTIVE') || this.competitions[0]
            this.activeCompetition = active
          } else {
            // Update active competition from fresh list
            const found = this.competitions.find(c => c.id === this.activeCompetition.id)
            if (found) this.activeCompetition = found
          }
        }
      } catch (err) {
        // Silently capture error state without dumping auth tokens or request config to console
        this.error = err.response?.data?.error || 'Erro ao carregar competições'
      } finally {
        this.loading = false
      }
    },

    setActiveCompetition(comp) {
      this.activeCompetition = comp
    },

    async createCompetition(data) {
      const res = await api.post('/competitions', data)
      await this.fetchCompetitions()
      if (res.data && res.data.status === 'ACTIVE') {
        this.activeCompetition = res.data
      }
      return res.data
    },

    async updateCompetitionStatus(id, status) {
      const res = await api.patch(`/competitions/${id}/status`, { status })
      await this.fetchCompetitions()
      return res.data
    }
  }
})
