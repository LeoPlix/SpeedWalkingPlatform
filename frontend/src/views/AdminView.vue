<template>
  <div class="admin-view">
    <div class="header-row">
      <div>
        <h2 class="page-title">Painel de Administração</h2>
        <p class="page-subtitle">Gestão oficial de competições, juízes e atletas da Federação Portuguesa de Atletismo</p>
      </div>
      <div v-if="competitionStore.activeCompetition" class="active-badge-comp">
        <span class="active-dot"></span>
        <span>Prova em curso: <strong>{{ competitionStore.activeCompetition.name }}</strong></span>
      </div>
    </div>

    <!-- Feedback messages -->
    <div v-if="successMsg" class="toast-success">
      ✓ {{ successMsg }}
    </div>
    <div v-if="errorMsg" class="toast-error">
      ⚠ {{ errorMsg }}
    </div>

    <!-- Navigation Tabs -->
    <div class="tabs-container">
      <button
        class="tab-btn"
        :class="{ active: currentTab === 'competitions' }"
        @click="currentTab = 'competitions'"
      >
        🏆 Competições ({{ competitionStore.competitions.length }})
      </button>
      <button
        class="tab-btn"
        :class="{ active: currentTab === 'judges' }"
        @click="currentTab = 'judges'"
      >
        ⚖ Juízes de Marcha ({{ judges.length }})
      </button>
      <button
        class="tab-btn"
        :class="{ active: currentTab === 'athletes' }"
        @click="currentTab = 'athletes'"
      >
        🏃 Atletas Inscritos ({{ athletes.length }})
      </button>
    </div>

    <!-- TAB 1: COMPETIÇÕES -->
    <div v-if="currentTab === 'competitions'" class="tab-pane">
      <div class="section-actions">
        <h3>Lista de Competições de Marcha</h3>
        <button @click="showNewCompModal = true" class="btn btn-primary">
          + Criar Nova Competição
        </button>
      </div>

      <div class="card table-card">
        <div class="table-container">
          <table class="data-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>COMPETIÇÃO</th>
                <th>LOCALIDADE</th>
                <th>DATA</th>
                <th>ESTADO</th>
                <th>AÇÕES</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="comp in competitionStore.competitions" :key="comp.id">
                <td class="font-mono">#{{ comp.id }}</td>
                <td class="font-bold">
                  {{ comp.name }}
                  <span v-if="competitionStore.activeCompetition?.id === comp.id" class="badge badge-green ml-2">
                    SELECIONADA
                  </span>
                </td>
                <td>{{ comp.location || 'Portugal' }}</td>
                <td class="font-mono">{{ comp.date }}</td>
                <td>
                  <span
                    class="badge"
                    :class="comp.status === 'ACTIVE' ? 'badge-green' : 'badge-gray'"
                  >
                    {{ comp.status === 'ACTIVE' ? 'ATIVA / ABERTA' : 'ENCERRADA / FECHADA' }}
                  </span>
                </td>
                <td>
                  <div class="action-buttons">
                    <button
                      v-if="competitionStore.activeCompetition?.id !== comp.id"
                      @click="selectCompetition(comp)"
                      class="btn btn-secondary btn-sm"
                      title="Definir como competição ativa para arbitragem"
                    >
                      Selecionar
                    </button>

                    <button
                      v-if="comp.status === 'ACTIVE'"
                      @click="toggleCompStatus(comp, 'CLOSED')"
                      class="btn btn-danger btn-sm"
                      title="Fechar e encerrar competição"
                    >
                      Fechar
                    </button>
                    <button
                      v-else
                      @click="toggleCompStatus(comp, 'ACTIVE')"
                      class="btn btn-primary btn-sm"
                      title="Reabrir competição"
                    >
                      Abrir
                    </button>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>

    <!-- TAB 2: JUÍZES -->
    <div v-if="currentTab === 'judges'" class="tab-pane">
      <div class="section-actions">
        <h3>Quadro Oficial de Juízes</h3>
        <button @click="showNewJudgeModal = true" class="btn btn-primary">
          + Adicionar Juiz
        </button>
      </div>

      <div class="card table-card">
        <div class="table-container">
          <table class="data-table">
            <thead>
              <tr>
                <th>CÓDIGO</th>
                <th>NOME COMPLETO</th>
                <th>UTILIZADOR (LOGIN)</th>
                <th>FUNÇÃO</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="j in judges" :key="j.id">
                <td class="font-mono font-bold">
                  <span class="badge badge-yellow">{{ j.judgeCode || 'J' }}</span>
                </td>
                <td class="font-bold">{{ j.name }}</td>
                <td class="font-mono text-muted">{{ j.username }}</td>
                <td>
                  <span class="badge" :class="j.role === 'ROLE_ADMIN' ? 'badge-red' : 'badge-gray'">
                    {{ j.role === 'ROLE_ADMIN' ? 'Secretariado Chefe' : 'Juiz de Marcha' }}
                  </span>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>

    <!-- TAB 3: ATLETAS -->
    <div v-if="currentTab === 'athletes'" class="tab-pane">
      <div class="section-actions">
        <div>
          <h3>Atletas Registados</h3>
          <p class="section-desc">Na prova: {{ competitionStore.activeCompetition?.name }}</p>
        </div>
        <button @click="showNewAthleteModal = true" class="btn btn-primary">
          + Adicionar Atleta
        </button>
      </div>

      <div class="card table-card">
        <div class="table-container">
          <table class="data-table">
            <thead>
              <tr>
                <th>DORSAL</th>
                <th>NOME COMPLETO</th>
                <th>CLUBE / EQUIPA</th>
                <th>CATEGORIA / PROVA</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="a in athletes" :key="a.id">
                <td class="font-mono font-bold">#{{ a.bibNumber }}</td>
                <td class="font-bold">{{ a.name }}</td>
                <td class="text-muted">{{ a.team || 'Individual' }}</td>
                <td>
                  <span class="badge badge-gray">{{ a.category || '20km Marcha' }}</span>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>

    <!-- MODAL: CRIAR COMPETIÇÃO -->
    <div v-if="showNewCompModal" class="modal-overlay" @click.self="showNewCompModal = false">
      <div class="modal-content">
        <div class="modal-header bg-modal-header">
          <h3 class="modal-title">Criar Nova Competição de Marcha</h3>
        </div>
        <form @submit.prevent="createCompetition">
          <div class="modal-body">
            <div class="form-group">
              <label class="form-label">Nome da Competição:</label>
              <input v-model="newComp.name" type="text" class="form-input" placeholder="Ex: Taça de Portugal de Marcha" required />
            </div>
            <div class="form-group">
              <label class="form-label">Localidade:</label>
              <input v-model="newComp.location" type="text" class="form-input" placeholder="Ex: Olhão, Portugal" required />
            </div>
            <div class="form-group">
              <label class="form-label">Data da Prova:</label>
              <input v-model="newComp.date" type="date" class="form-input" required />
            </div>
            <div class="form-group">
              <label class="form-label">Estado Inicial:</label>
              <select v-model="newComp.status" class="form-input">
                <option value="ACTIVE">Aberta / Ativa</option>
                <option value="CLOSED">Fechada / Encerrada</option>
              </select>
            </div>
          </div>
          <div class="modal-footer">
            <button type="button" @click="showNewCompModal = false" class="btn btn-secondary">Cancelar</button>
            <button type="submit" class="btn btn-primary" :disabled="saving">
              {{ saving ? 'A guardar...' : 'Criar Competição' }}
            </button>
          </div>
        </form>
      </div>
    </div>

    <!-- MODAL: ADICIONAR JUIZ -->
    <div v-if="showNewJudgeModal" class="modal-overlay" @click.self="showNewJudgeModal = false">
      <div class="modal-content">
        <div class="modal-header bg-modal-header">
          <h3 class="modal-title">Adicionar Novo Juiz de Marcha</h3>
        </div>
        <form @submit.prevent="createJudge">
          <div class="modal-body">
            <div class="form-group">
              <label class="form-label">Nome Completo do Juiz:</label>
              <input v-model="newJudge.name" type="text" class="form-input" placeholder="Ex: Rui Manuel Silva" required />
            </div>
            <div class="form-group">
              <label class="form-label">Código de Juiz (sigla no quadro):</label>
              <input v-model="newJudge.judgeCode" type="text" class="form-input font-mono" placeholder="Ex: J05" required />
            </div>
            <div class="form-group">
              <label class="form-label">Nome de Utilizador (Login):</label>
              <input v-model="newJudge.username" type="text" class="form-input font-mono" placeholder="Ex: juiz5" required />
            </div>
            <div class="form-group">
              <label class="form-label">Palavra-passe:</label>
              <input v-model="newJudge.password" type="password" class="form-input" placeholder="••••••••" required />
            </div>
          </div>
          <div class="modal-footer">
            <button type="button" @click="showNewJudgeModal = false" class="btn btn-secondary">Cancelar</button>
            <button type="submit" class="btn btn-primary" :disabled="saving">
              {{ saving ? 'A guardar...' : 'Guardar Juiz' }}
            </button>
          </div>
        </form>
      </div>
    </div>

    <!-- MODAL: ADICIONAR ATLETA -->
    <div v-if="showNewAthleteModal" class="modal-overlay" @click.self="showNewAthleteModal = false">
      <div class="modal-content">
        <div class="modal-header bg-modal-header">
          <h3 class="modal-title">Adicionar Atleta à Prova Atual</h3>
        </div>
        <form @submit.prevent="createAthlete">
          <div class="modal-body">
            <div class="form-group">
              <label class="form-label">Número de Dorsal (BIB):</label>
              <input v-model="newAthlete.bibNumber" type="text" class="form-input font-mono" placeholder="Ex: 112" required />
            </div>
            <div class="form-group">
              <label class="form-label">Nome Completo do Atleta:</label>
              <input v-model="newAthlete.name" type="text" class="form-input" placeholder="Ex: Pedro Martins" required />
            </div>
            <div class="form-group">
              <label class="form-label">Clube / Equipa:</label>
              <input v-model="newAthlete.team" type="text" class="form-input" placeholder="Ex: Sporting CP ou SL Benfica" />
            </div>
            <div class="form-group">
              <label class="form-label">Categoria / Prova:</label>
              <input v-model="newAthlete.category" type="text" class="form-input" placeholder="Ex: 20km Masculino" />
            </div>
          </div>
          <div class="modal-footer">
            <button type="button" @click="showNewAthleteModal = false" class="btn btn-secondary">Cancelar</button>
            <button type="submit" class="btn btn-primary" :disabled="saving">
              {{ saving ? 'A guardar...' : 'Guardar Atleta' }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useCompetitionStore } from '../stores/competition'
import api from '../api/axios'

const competitionStore = useCompetitionStore()

const currentTab = ref('competitions')
const judges = ref([])
const athletes = ref([])
const saving = ref(false)
const successMsg = ref('')
const errorMsg = ref('')

const showNewCompModal = ref(false)
const showNewJudgeModal = ref(false)
const showNewAthleteModal = ref(false)

const newComp = ref({
  name: '',
  location: '',
  date: new Date().toISOString().split('T')[0],
  status: 'ACTIVE',
  penaltyZoneEnabled: true
})

const newJudge = ref({
  name: '',
  judgeCode: '',
  username: '',
  password: ''
})

const newAthlete = ref({
  bibNumber: '',
  name: '',
  team: '',
  category: '20km Marcha'
})

const showSuccess = (msg) => {
  successMsg.value = msg
  setTimeout(() => { successMsg.value = '' }, 3500)
}

const showError = (msg) => {
  errorMsg.value = msg
  setTimeout(() => { errorMsg.value = '' }, 4000)
}

const fetchJudges = async () => {
  try {
    const res = await api.get('/auth/judges')
    judges.value = res.data
  } catch (err) {
    console.error('Erro ao carregar juízes:', err)
  }
}

const fetchAthletes = async () => {
  try {
    const compId = competitionStore.activeCompetition?.id || 1
    const res = await api.get(`/athletes?competitionId=${compId}`)
    athletes.value = res.data
  } catch (err) {
    console.error('Erro ao carregar atletas:', err)
  }
}

const selectCompetition = (comp) => {
  competitionStore.setActiveCompetition(comp)
  showSuccess(`Competição ativa alterada para: ${comp.name}`)
  fetchAthletes()
}

const toggleCompStatus = async (comp, newStatus) => {
  try {
    await competitionStore.updateCompetitionStatus(comp.id, newStatus)
    showSuccess(`Estado da competição '${comp.name}' alterado para ${newStatus === 'ACTIVE' ? 'ABERTA' : 'FECHADA'}`)
  } catch (err) {
    showError(err.response?.data?.error || 'Erro ao alterar estado da competição')
  }
}

const createCompetition = async () => {
  saving.value = true
  try {
    await competitionStore.createCompetition({
      name: newComp.value.name.trim(),
      location: newComp.value.location.trim(),
      date: newComp.value.date,
      status: newComp.value.status,
      penaltyZoneEnabled: true
    })
    showNewCompModal.value = false
    newComp.value = {
      name: '',
      location: '',
      date: new Date().toISOString().split('T')[0],
      status: 'ACTIVE',
      penaltyZoneEnabled: true
    }
    showSuccess('Competição criada com sucesso!')
  } catch (err) {
    showError(err.response?.data?.error || 'Erro ao criar competição')
  } finally {
    saving.value = false
  }
}

const createJudge = async () => {
  saving.value = true
  try {
    await api.post('/auth/judges', {
      name: newJudge.value.name.trim(),
      judgeCode: newJudge.value.judgeCode.trim().toUpperCase(),
      username: newJudge.value.username.trim(),
      password: newJudge.value.password.trim()
    })
    showNewJudgeModal.value = false
    newJudge.value = { name: '', judgeCode: '', username: '', password: '' }
    await fetchJudges()
    showSuccess('Novo juiz registado com sucesso!')
  } catch (err) {
    showError(err.response?.data?.error || 'Erro ao criar juiz')
  } finally {
    saving.value = false
  }
}

const createAthlete = async () => {
  saving.value = true
  try {
    const compId = competitionStore.activeCompetition?.id || 1
    await api.post('/athletes', {
      competitionId: compId,
      bibNumber: newAthlete.value.bibNumber.trim(),
      name: newAthlete.value.name.trim(),
      team: newAthlete.value.team.trim(),
      category: newAthlete.value.category.trim()
    })
    showNewAthleteModal.value = false
    newAthlete.value = { bibNumber: '', name: '', team: '', category: '20km Marcha' }
    await fetchAthletes()
    showSuccess('Atleta adicionado com sucesso!')
  } catch (err) {
    showError(err.response?.data?.error || 'Erro ao registar atleta')
  } finally {
    saving.value = false
  }
}

onMounted(async () => {
  await competitionStore.fetchCompetitions()
  await Promise.all([fetchJudges(), fetchAthletes()])
})
</script>

<style scoped>
.admin-view {
  padding-bottom: 5rem;
}

.header-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 1rem;
  margin-bottom: 1.5rem;
}

.page-title {
  font-size: 1.5rem;
  font-weight: 800;
}

.page-subtitle {
  font-size: 0.875rem;
  color: #64748b;
}

.active-badge-comp {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  background-color: #f0fdf4;
  border: 1px solid #bbf7d0;
  padding: 0.4rem 0.75rem;
  border-radius: 6px;
  font-size: 0.85rem;
  color: #166534;
}

.active-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background-color: #16a34a;
}

.toast-success {
  background-color: #16a34a;
  color: white;
  padding: 0.75rem 1rem;
  border-radius: 8px;
  font-weight: 700;
  margin-bottom: 1rem;
  text-align: center;
}

.toast-error {
  background-color: #dc2626;
  color: white;
  padding: 0.75rem 1rem;
  border-radius: 8px;
  font-weight: 700;
  margin-bottom: 1rem;
  text-align: center;
}

.tabs-container {
  display: flex;
  gap: 0.5rem;
  border-bottom: 2px solid #e2e8f0;
  margin-bottom: 1.5rem;
  overflow-x: auto;
}

.tab-btn {
  padding: 0.75rem 1.25rem;
  font-weight: 700;
  font-size: 0.95rem;
  border: none;
  background: none;
  color: #64748b;
  cursor: pointer;
  border-bottom: 3px solid transparent;
  transition: all 0.15s ease;
  white-space: nowrap;
}

.tab-btn:hover {
  color: #0f172a;
}

.tab-btn.active {
  color: #9333ea;
  border-bottom-color: #9333ea;
}

.section-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
  flex-wrap: wrap;
  gap: 0.75rem;
}

.section-desc {
  font-size: 0.8rem;
  color: #64748b;
}

.action-buttons {
  display: flex;
  gap: 0.5rem;
}

.btn-sm {
  padding: 0.35rem 0.65rem;
  font-size: 0.75rem;
}

.ml-2 {
  margin-left: 0.5rem;
}

.bg-modal-header {
  padding: 1.25rem;
  border-bottom: 1px solid #e2e8f0;
  background-color: #f8fafc;
}
</style>
