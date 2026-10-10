<template>
  <div class="lists-view">
    <div class="header-row">
      <div>
        <h2 class="page-title">Painel de Controlo & Listas</h2>
        <p class="page-subtitle">Acompanhamento oficial de infrações e desqualificações</p>
      </div>

      <div class="actions-row">
        <button @click="refreshData" class="btn btn-secondary refresh-btn" :disabled="loading">
          <span v-if="loading">A atualizar...</span>
          <span v-else>↻ Atualizar</span>
        </button>
      </div>
    </div>

    <!-- TABS -->
    <div class="tabs-container">
      <button
        class="tab-btn"
        :class="{ active: activeTab === 'board' }"
        @click="activeTab = 'board'"
      >
        Quadro de Desqualificações (Posting Board)
      </button>
      <button
        class="tab-btn"
        :class="{ active: activeTab === 'feed' }"
        @click="activeTab = 'feed'"
      >
        Histórico de Infrações ({{ infractions.length }})
      </button>
    </div>

    <!-- TAB 1: POSTING BOARD -->
    <div v-if="activeTab === 'board'" class="tab-content">
      <!-- Stats summary cards -->
      <div class="stats-grid">
        <div class="stat-card">
          <span class="stat-label">Total Atletas</span>
          <span class="stat-val">{{ boardSummary?.totalAthletes || 0 }}</span>
        </div>
        <div class="stat-card">
          <span class="stat-label">Advertências (YP)</span>
          <span class="stat-val text-yellow">{{ boardSummary?.totalYellowPaddles || 0 }}</span>
        </div>
        <div class="stat-card">
          <span class="stat-label">Notas Desqualificação (RC)</span>
          <span class="stat-val text-red">{{ boardSummary?.totalRedCards || 0 }}</span>
        </div>
        <div class="stat-card pitlane-card">
          <span class="stat-label">Penalizados (Pit Lane)</span>
          <span class="stat-val text-yellow">{{ boardSummary?.totalPenalized || 0 }}</span>
        </div>
        <div class="stat-card dq-card">
          <span class="stat-label">Desqualificados (DQ)</span>
          <span class="stat-val text-danger">{{ boardSummary?.totalDisqualified || 0 }}</span>
        </div>
      </div>

      <!-- BOARD TABLE -->
      <div class="card table-card">
        <h3 class="card-title">Quadro de Desqualificações (Posting Board Oficial)</h3>
        <p class="card-caption">
          Regra Oficial FPA / World Athletics: As advertências (amarelos) são avisos prévios. <strong>3 Notas (RC)</strong> = Penalização na Zona de Penalização (Pit Lane). <strong>4 Notas (RC)</strong> = Desqualificação (DQ) imediata.
        </p>

        <div class="table-container">
          <table class="data-table">
            <thead>
              <tr>
                <th>DORSAL</th>
                <th>ATLETA</th>
                <th>CLUBE / EQUIPA</th>
                <th>ADVERTÊNCIAS (YP)</th>
                <th>NOTAS DE DESQUALIFICAÇÃO (RC)</th>
                <th>TOTAL RC</th>
                <th>ESTADO</th>
              </tr>
            </thead>
            <tbody>
              <tr
                v-for="athlete in boardSummary?.athletes || []"
                :key="athlete.athleteId"
                :class="{ 'row-dq': athlete.disqualified, 'row-pitlane': athlete.inPenaltyZone, 'row-warning': athlete.redCardCount > 0 && !athlete.disqualified && !athlete.inPenaltyZone }"
              >
                <td class="font-mono font-bold text-center">
                  #{{ athlete.bibNumber }}
                </td>
                <td class="font-bold">
                  {{ athlete.name }}
                </td>
                <td class="text-muted">
                  {{ athlete.team || '—' }}
                </td>
                <td>
                  <div class="paddles-list">
                    <span
                      v-for="yp in athlete.yellowPaddles"
                      :key="yp.id"
                      class="paddle-tag paddle-yp"
                      :title="`${formatTime(yp.time)} - Juiz: ${yp.judgeCode || yp.judgeName} (${yp.infractionType?.toLowerCase() === 'flexao' ? 'Flexão' : 'Suspensão'})`"
                    >
                      {{ yp.symbol }}
                    </span>
                    <span v-if="athlete.yellowPaddles.length === 0" class="empty-dash">—</span>
                  </div>
                </td>
                <td>
                  <div class="paddles-list">
                    <span
                      v-for="rc in athlete.redCards"
                      :key="rc.id"
                      class="paddle-tag paddle-rc"
                      :title="`${formatTime(rc.time)} - Juiz: ${rc.judgeCode || rc.judgeName} (${rc.infractionType?.toLowerCase() === 'flexao' ? 'Flexão' : 'Suspensão'})`"
                    >
                      <span class="judge-sub">{{ rc.judgeCode || 'J' }}</span>
                      <span class="symbol-sub">{{ rc.symbol }}</span>
                    </span>
                    <span v-if="athlete.redCards.length === 0" class="empty-dash">—</span>
                  </div>
                </td>
                <td class="font-mono font-bold text-center">
                  {{ athlete.redCardCount }} / 4
                </td>
                <td>
                  <span v-if="athlete.disqualified" class="badge badge-red badge-dq">
                    DESQUALIFICADO (4º RC)
                  </span>
                  <span v-else-if="athlete.inPenaltyZone" class="badge badge-yellow badge-pitlane">
                    PENALTY ZONE (3º RC)
                  </span>
                  <span v-else-if="athlete.redCardCount > 0" class="badge badge-yellow">
                    {{ athlete.redCardCount }} NOTA(S) RC
                  </span>
                  <span v-else class="badge badge-green">
                    REGULAR
                  </span>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>

    <!-- TAB 2: INFRACTIONS FEED -->
    <div v-if="activeTab === 'feed'" class="tab-content">
      <div class="card feed-card">
        <!-- Filters -->
        <div class="filters-row">
          <div class="filter-group">
            <label class="filter-label">Filtrar Dorsal:</label>
            <input
              v-model="filterBib"
              type="text"
              class="form-input filter-input font-mono"
              placeholder="Ex: 104"
            />
          </div>

          <div class="filter-group">
            <label class="filter-label">Tipo de Notificação:</label>
            <select v-model="filterCategory" class="form-input filter-input">
              <option value="">Todas</option>
              <option value="YP">Advertências (YP)</option>
              <option value="RC">Notas de Desqualificação (RC)</option>
            </select>
          </div>
        </div>

        <!-- Feed List -->
        <div class="feed-list" v-if="filteredInfractions.length > 0">
          <div
            v-for="inf in filteredInfractions"
            :key="inf.id"
            class="feed-item"
            :class="inf.cardCategory === 'RC' ? 'border-red' : 'border-yellow'"
          >
            <div class="feed-left">
              <div
                class="feed-icon"
                :class="inf.cardCategory === 'RC' ? 'bg-red-icon' : 'bg-yellow-icon'"
              >
                <span>{{ inf.symbol }}</span>
              </div>
              <div class="feed-details">
                <div class="feed-athlete-line">
                  <span class="feed-bib font-mono">#{{ inf.bibNumber }}</span>
                  <span class="feed-name">{{ inf.athleteName }}</span>
                  <span
                    class="badge"
                    :class="inf.cardCategory === 'RC' ? 'badge-red' : 'badge-yellow'"
                  >
                    {{ inf.cardCategory === 'RC' ? 'NOTA DE DESQUALIFICAÇÃO' : 'ADVERTÊNCIA' }} • {{ (inf.infractionType?.toLowerCase() === 'flexao' ? 'FLEXÃO' : 'SUSPENSÃO') }}
                  </span>
                </div>
                <div class="feed-meta">
                  <span class="feed-time font-mono">🕒 {{ formatTime(inf.time) }}</span>
                  <span class="feed-judge">
                    👤 Juiz: {{ inf.judgeName }} ({{ inf.judgeCode || 'J' }})
                  </span>
                  <span v-if="inf.notes" class="feed-notes">
                    📝 {{ inf.notes }}
                  </span>
                </div>
              </div>
            </div>

            <div class="feed-right" v-if="authStore.isAdmin">
              <button @click="deleteInfraction(inf.id)" class="btn-delete" title="Eliminar infração">
                ✕
              </button>
            </div>
          </div>
        </div>

        <div v-else class="empty-state">
          <p>Nenhuma infração encontrada para os filtros selecionados.</p>
        </div>
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

const activeTab = ref('board')
const boardSummary = ref(null)
const infractions = ref([])
const loading = ref(false)

const filterBib = ref('')
const filterCategory = ref('')

const formatTime = (time) => {
  if (!time) return ''
  const parts = String(time).split(':')
  if (parts.length >= 2) {
    return `${parts[0]}:${parts[1]}`
  }
  return time
}

const fetchBoard = async () => {
  try {
    const compId = competitionStore.activeCompetition?.id || 1
    const res = await api.get(`/board/summary?competitionId=${compId}`)
    boardSummary.value = res.data
  } catch (err) {
    console.error('Erro ao carregar quadro:', err)
  }
}

const fetchInfractions = async () => {
  try {
    const compId = competitionStore.activeCompetition?.id || 1
    const res = await api.get(`/infractions?competitionId=${compId}`)
    infractions.value = res.data
  } catch (err) {
    console.error('Erro ao carregar infrações:', err)
  }
}

const refreshData = async () => {
  loading.value = true
  await Promise.all([fetchBoard(), fetchInfractions()])
  loading.value = false
}

const deleteInfraction = async (id) => {
  if (!confirm('Tem a certeza que deseja eliminar esta infração?')) return
  try {
    await api.delete(`/infractions/${id}`)
    await refreshData()
  } catch (err) {
    alert('Erro ao eliminar infração')
  }
}

const filteredInfractions = computed(() => {
  return infractions.value.filter(item => {
    if (filterBib.value && !item.bibNumber.includes(filterBib.value.trim())) {
      return false
    }
    if (filterCategory.value && item.cardCategory !== filterCategory.value) {
      return false
    }
    return true
  })
})

onMounted(async () => {
  await competitionStore.fetchCompetitions()
  await refreshData()
})
</script>

<style scoped>
.lists-view {
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

.tabs-container {
  display: flex;
  gap: 0.5rem;
  border-bottom: 2px solid #e2e8f0;
  margin-bottom: 1.5rem;
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
}

.tab-btn:hover {
  color: #0f172a;
}

.tab-btn.active {
  color: #dc2626;
  border-bottom-color: #dc2626;
}

/* Stats */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  gap: 1rem;
  margin-bottom: 1.5rem;
}

.stat-card {
  background: white;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  padding: 1.25rem;
  display: flex;
  flex-direction: column;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
}

.stat-label {
  font-size: 0.8rem;
  font-weight: 700;
  text-transform: uppercase;
  color: #64748b;
  margin-bottom: 0.25rem;
}

.stat-val {
  font-size: 2rem;
  font-weight: 900;
  color: #0f172a;
}

.text-yellow {
  color: #ca8a04;
}

.text-red {
  color: #dc2626;
}

.dq-card {
  background-color: #fef2f2;
  border-color: #fca5a5;
}

.pitlane-card {
  background-color: #fefce8;
  border-color: #fde047;
}

.row-pitlane {
  background-color: #fefce8 !important;
}

.badge-pitlane {
  background-color: #eab308;
  color: #713f12;
  font-weight: 800;
  border: 1px solid #ca8a04;
}

.text-danger {
  color: #b91c1c;
}

.card-title {
  font-size: 1.15rem;
  font-weight: 800;
  margin-bottom: 0.25rem;
}

.card-caption {
  font-size: 0.8rem;
  color: #64748b;
  margin-bottom: 1rem;
}

.paddles-list {
  display: flex;
  flex-wrap: wrap;
  gap: 0.35rem;
  align-items: center;
}

.paddle-tag {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 0.2rem 0.5rem;
  border-radius: 4px;
  font-weight: 800;
  font-size: 0.85rem;
}

.paddle-yp {
  background-color: #fef08a;
  color: #854d0e;
  border: 1px solid #facc15;
}

.paddle-rc {
  background-color: #fee2e2;
  color: #991b1b;
  border: 1px solid #f87171;
  gap: 0.3rem;
}

.judge-sub {
  font-size: 0.65rem;
  background-color: #dc2626;
  color: white;
  padding: 0 0.25rem;
  border-radius: 3px;
}

.symbol-sub {
  font-weight: 900;
}

.empty-dash {
  color: #94a3b8;
}

.row-dq {
  background-color: #fff1f2 !important;
}

.row-warning {
  background-color: #fffbeb;
}

.badge-dq {
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.7; }
}

/* Feed filters */
.filters-row {
  display: flex;
  gap: 1rem;
  margin-bottom: 1.5rem;
  flex-wrap: wrap;
}

.filter-group {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}

.filter-label {
  font-size: 0.75rem;
  font-weight: 700;
  color: #64748b;
}

.filter-input {
  padding: 0.5rem 0.75rem;
  font-size: 0.85rem;
}

.feed-list {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.feed-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  background-color: white;
  transition: all 0.15s ease;
}

.feed-item:hover {
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05);
}

.border-red {
  border-left: 5px solid #dc2626;
}

.border-yellow {
  border-left: 5px solid #eab308;
}

.feed-left {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.feed-icon {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.5rem;
  font-weight: 900;
  border: 2px solid #0f172a;
}

.bg-red-icon {
  background-color: #dc2626;
  color: white;
}

.bg-yellow-icon {
  background-color: #facc15;
  color: #0f172a;
}

.feed-details {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}

.feed-athlete-line {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  flex-wrap: wrap;
}

.feed-bib {
  font-weight: 800;
  font-size: 1rem;
  color: #0f172a;
}

.feed-name {
  font-weight: 700;
  font-size: 0.95rem;
  color: #1e293b;
}

.feed-meta {
  display: flex;
  align-items: center;
  gap: 1rem;
  font-size: 0.75rem;
  color: #64748b;
  flex-wrap: wrap;
}

.feed-notes {
  color: #b45309;
  font-style: italic;
}

.btn-delete {
  background: none;
  border: none;
  color: #94a3b8;
  font-size: 1.1rem;
  cursor: pointer;
  padding: 0.25rem 0.5rem;
  border-radius: 4px;
  transition: all 0.15s ease;
}

.btn-delete:hover {
  color: #dc2626;
  background-color: #fee2e2;
}

.empty-state {
  text-align: center;
  padding: 3rem 1rem;
  color: #94a3b8;
  font-weight: 500;
}
</style>
