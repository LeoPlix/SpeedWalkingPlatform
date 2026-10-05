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
        if (this.competitions.length > 0 && !this.activeCompetition) {
          this.activeCompetition = this.competitions[0]
        }
      } catch (err) {
        console.error('Erro ao carregar competições:', err)
      } finally {
        this.loading = false
      }
    },

    setActiveCompetition(comp) {
      this.activeCompetition = comp
    }
  }
})
