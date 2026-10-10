<template>
  <div class="athletes-view">
    <div class="header-row">
      <div>
        <h2 class="page-title">Atletas Inscritos</h2>
        <p class="page-subtitle">Lista de atletas na prova atual</p>
      </div>

      <button v-if="authStore.isAdmin" @click="showAddModal = true" class="btn btn-primary">
        + Adicionar Atleta
      </button>
    </div>

    <!-- Search bar -->
    <div class="search-box card">
      <input
        v-model="searchTerm"
        type="text"
        class="form-input"
        placeholder="Pesquisar por Dorsal ou Nome do Atleta..."
      />
    </div>

    <!-- Athletes Table -->
    <div class="card table-card">
      <div class="table-container">
        <table class="data-table">
          <thead>
            <tr>
              <th>DORSAL</th>
              <th>NOME</th>
              <th>EQUIPA / CLUBE</th>
              <th>CATEGORIA</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="athlete in filteredAthletes" :key="athlete.id">
              <td class="font-mono font-bold">#{{ athlete.bibNumber }}</td>
              <td class="font-bold">{{ athlete.name }}</td>
              <td class="text-muted">{{ athlete.team || '—' }}</td>
              <td>
                <span class="badge badge-gray">{{ athlete.category || 'Senior' }}</span>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- ADD ATHLETE MODAL -->
    <div v-if="showAddModal" class="modal-overlay" @click.self="showAddModal = false">
      <div class="modal-content">
        <div class="modal-header">
          <h3>Adicionar Novo Atleta</h3>
        </div>
        <form @submit.prevent="createAthlete">
          <div class="modal-body">
            <div class="form-group">
              <label class="form-label">Dorsal (BIB):</label>
              <input v-model="newBib" type="text" class="form-input font-mono" placeholder="Ex: 112" required />
            </div>
            <div class="form-group">
              <label class="form-label">Nome Completo:</label>
              <input v-model="newName" type="text" class="form-input" placeholder="Ex: Pedro Martins" required />
            </div>
            <div class="form-group">
              <label class="form-label">Equipa / Clube:</label>
              <input v-model="newTeam" type="text" class="form-input" placeholder="Ex: Sporting CP ou POR" />
            </div>
            <div class="form-group">
              <label class="form-label">Categoria:</label>
              <input v-model="newCategory" type="text" class="form-input" placeholder="Ex: 20km Masculino" />
            </div>
          </div>
          <div class="modal-footer">
            <button type="button" @click="showAddModal = false" class="btn btn-secondary">Cancelar</button>
            <button type="submit" class="btn btn-primary" :disabled="saving">
              <span v-if="saving">A guardar...</span>
              <span v-else>Guardar Atleta</span>
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useAuthStore } from '../stores/auth'
import { useCompetitionStore } from '../stores/competition'
import api from '../api/axios'

const authStore = useAuthStore()
const competitionStore = useCompetitionStore()
const athletes = ref([])
const searchTerm = ref('')
const showAddModal = ref(false)
const saving = ref(false)

const newBib = ref('')
const newName = ref('')
const newTeam = ref('')
const newCategory = ref('20km Marcha')

const fetchAthletes = async () => {
  try {
    const compId = competitionStore.activeCompetition?.id || 1
    const res = await api.get(`/athletes?competitionId=${compId}`)
    athletes.value = res.data
  } catch (err) {
    console.error(err)
  }
}

const createAthlete = async () => {
  saving.value = true
  try {
    const compId = competitionStore.activeCompetition?.id || 1
    await api.post('/athletes', {
      competitionId: compId,
      bibNumber: newBib.value.trim(),
      name: newName.value.trim(),
      team: newTeam.value.trim(),
      category: newCategory.value.trim()
    })
    showAddModal.value = false
    newBib.value = ''
    newName.value = ''
    newTeam.value = ''
    await fetchAthletes()
  } catch (err) {
    alert('Erro ao registar atleta')
  } finally {
    saving.value = false
  }
}

const filteredAthletes = computed(() => {
  const query = searchTerm.value.toLowerCase().trim()
  if (!query) return athletes.value
  return athletes.value.filter(a =>
    a.name.toLowerCase().includes(query) || a.bibNumber.includes(query)
  )
})

onMounted(async () => {
  await competitionStore.fetchCompetitions()
  await fetchAthletes()
})
</script>

<style scoped>
.athletes-view {
  padding-bottom: 5rem;
}

.header-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
  flex-wrap: wrap;
  gap: 1rem;
}

.page-title {
  font-size: 1.5rem;
  font-weight: 800;
}

.page-subtitle {
  font-size: 0.875rem;
  color: #64748b;
}

.search-box {
  margin-bottom: 1.5rem;
  padding: 1rem;
}

.modal-header {
  padding: 1.25rem;
  border-bottom: 1px solid #e2e8f0;
}

.modal-body {
  padding: 1.25rem;
}

.modal-footer {
  padding: 1rem 1.25rem;
  background-color: #f8fafc;
  display: flex;
  justify-content: flex-end;
  gap: 0.75rem;
  border-top: 1px solid #e2e8f0;
}
</style>
