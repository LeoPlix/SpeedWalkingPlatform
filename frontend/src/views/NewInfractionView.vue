<template>
  <div class="new-infraction-view">
    <!-- Online / Offline Banner -->
    <div v-if="!isOnline" class="offline-banner">
      📡 Modo offline: As infrações serão guardadas quando recuperar a ligação.
    </div>

    <!-- Active Competition Banner -->
    <div class="comp-banner" v-if="competitionStore.activeCompetition">
      <span class="comp-label">Competição Ativa:</span>
      <span class="comp-name">{{ competitionStore.activeCompetition.name }}</span>
    </div>

    <!-- Feedback Toast -->
    <div v-if="successToast" class="toast-success">
      ✓ {{ successToast }}
    </div>
    <div v-if="errorToast" class="toast-error">
      ⚠ {{ errorToast }}
    </div>

    <div class="form-container card">
      <!-- BIB NUMBER INPUT -->
      <div class="form-group">
        <div class="label-row">
          <label class="form-label" for="bib">BIB NUMBER (DORSAL)</label>
          <span v-if="loadingAthlete" class="searching-tag">A pesquisar...</span>
        </div>
        <div class="input-with-athlete">
          <div class="bib-input-relative">
            <input
              id="bib"
              v-model="bibNumber"
              type="number"
              inputmode="numeric"
              class="form-input bib-input font-mono"
              placeholder="Ex: 104"
              @input="onBibInput"
            />
            <button
              v-if="bibNumber"
              type="button"
              class="clear-bib-btn"
              @click="clearBib"
              aria-label="Limpar Dorsal"
            >
              ✕
            </button>
          </div>
          <div class="athlete-display" :class="{ 'has-athlete': !!athleteName }">
            <span class="athlete-label">ATLETA:</span>
            <span class="athlete-name">{{ athleteName || 'Atleta Desconhecido' }}</span>
            <span v-if="athleteTeam" class="athlete-team">({{ athleteTeam }})</span>
          </div>
        </div>
      </div>

      <!-- Quick Athlete Dorsal Chips -->
      <div class="quick-athletes" v-if="popularAthletes.length > 0">
        <span class="quick-label">Dorsais rápidos:</span>
        <div class="chips-container">
          <button
            v-for="a in popularAthletes"
            :key="a.id"
            type="button"
            class="bib-chip"
            :class="{ active: bibNumber === a.bibNumber }"
            @click="selectBib(a)"
          >
            #{{ a.bibNumber }} {{ a.name.split(' ')[0] }}
          </button>
        </div>
      </div>

      <!-- TIME INPUT -->
      <div class="form-group">
        <label class="form-label" for="time">TIME (HORA DA INFRAÇÃO)</label>
        <div class="time-input-group">
          <input
            id="time"
            v-model="raceTime"
            type="text"
            class="form-input font-mono time-field"
            placeholder="HH:mm:ss"
          />
          <button type="button" @click="resetTimeToNow" class="btn btn-secondary reset-time-btn" title="Atualizar para agora">
            ↺ Agora
          </button>
        </div>
      </div>

      <div class="section-divider"></div>

      <!-- YELLOW PADDLES (YP) -->
      <div class="paddles-section">
        <div class="section-header">
          <h3 class="section-title">YELLOW PADDLES (YP)</h3>
          <span class="section-subtitle">Avisos preliminares</span>
        </div>
        <div class="divider-line"></div>

        <div class="paddle-buttons-row">
          <!-- FLEXAO > -->
          <div class="circle-btn-container" @click="handlePaddleClick('flexao', 'YP', '>')">
            <div class="circle-btn circle-btn-yellow">
              <span>&gt;</span>
            </div>
            <span class="circle-btn-label">FLEXAO</span>
          </div>

          <!-- CONTACTO ~ -->
          <div class="circle-btn-container" @click="handlePaddleClick('contacto', 'YP', '~')">
            <div class="circle-btn circle-btn-yellow">
              <span>~</span>
            </div>
            <span class="circle-btn-label">CONTACTO</span>
          </div>
        </div>
      </div>

      <div class="section-divider"></div>

      <!-- RED CARDS (RC) -->
      <div class="paddles-section">
        <div class="section-header">
          <h3 class="section-title text-red">RED CARDS (RC)</h3>
          <span class="section-subtitle">Propostas de desqualificação</span>
        </div>
        <div class="divider-line"></div>

        <div class="paddle-buttons-row">
          <!-- FLEXAO > -->
          <div class="circle-btn-container" @click="handlePaddleClick('flexao', 'RC', '>')">
            <div class="circle-btn circle-btn-red">
              <span>&gt;</span>
            </div>
            <span class="circle-btn-label">FLEXAO</span>
          </div>

          <!-- CONTACTO ~ -->
          <div class="circle-btn-container" @click="handlePaddleClick('contacto', 'RC', '~')">
            <div class="circle-btn circle-btn-red">
              <span>~</span>
            </div>
            <span class="circle-btn-label">CONTACTO</span>
          </div>
        </div>
      </div>
    </div>

    <!-- CONFIRMATION MODAL -->
    <ConfirmModal
      :is-open="modalOpen"
      :athlete-name="athleteName"
      :bib-number="bibNumber"
      :time="raceTime"
      :type="selectedType"
      :category="selectedCategory"
      :symbol="selectedSymbol"
      :loading="submitting"
      @confirm="submitInfraction"
      @cancel="modalOpen = false"
    />
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useAuthStore } from '../stores/auth'
import { useCompetitionStore } from '../stores/competition'
import api from '../api/axios'
import ConfirmModal from '../components/ConfirmModal.vue'

const authStore = useAuthStore()
const competitionStore = useCompetitionStore()

const bibNumber = ref('')
const athleteName = ref('')
const athleteTeam = ref('')
const raceTime = ref('')
const loadingAthlete = ref(false)
const submitting = ref(false)
const successToast = ref('')
const errorToast = ref('')
const popularAthletes = ref([])
const isOnline = ref(typeof navigator !== 'undefined' ? navigator.onLine : true)

// Modal state
const modalOpen = ref(false)
const selectedType = ref('')
const selectedCategory = ref('')
const selectedSymbol = ref('')

let timeInterval = null

// Haptic feedback helper for mobile touch feedback
const triggerHaptic = (pattern = 25) => {
  if (typeof navigator !== 'undefined' && 'vibrate' in navigator) {
    try {
      navigator.vibrate(pattern)
    } catch (e) {
      // Ignore vibration error on unsupported browsers
    }
  }
}

const updateOnlineStatus = () => {
  isOnline.value = navigator.onLine
}

const clearBib = () => {
  triggerHaptic(15)
  bibNumber.value = ''
  athleteName.value = ''
  athleteTeam.value = ''
}

const resetTimeToNow = () => {
  triggerHaptic(15)
  const now = new Date()
  raceTime.value = now.toTimeString().split(' ')[0]
}

const onBibInput = async () => {
  const bib = bibNumber.value.trim()
  if (!bib) {
    athleteName.value = ''
    athleteTeam.value = ''
    return
  }

  loadingAthlete.value = true
  try {
    const compId = competitionStore.activeCompetition?.id || 1
    const res = await api.get(`/athletes/bib/${bib}?competitionId=${compId}`)
    if (res.data) {
      athleteName.value = res.data.name
      athleteTeam.value = res.data.team || ''
    }
  } catch (err) {
    athleteName.value = 'Atleta Desconhecido'
    athleteTeam.value = ''
  } finally {
    loadingAthlete.value = false
  }
}

const selectBib = (athlete) => {
  triggerHaptic(20)
  bibNumber.value = athlete.bibNumber
  athleteName.value = athlete.name
  athleteTeam.value = athlete.team || ''
}

const fetchAthletes = async () => {
  try {
    const compId = competitionStore.activeCompetition?.id || 1
    const res = await api.get(`/athletes?competitionId=${compId}`)
    popularAthletes.value = res.data.slice(0, 6)
  } catch (err) {
    console.error(err)
  }
}

const handlePaddleClick = (type, category, symbol) => {
  if (!bibNumber.value.trim()) {
    triggerHaptic([30, 50, 30])
    errorToast.value = 'Por favor introduza o Dorsal (BIB NUMBER) primeiro!'
    setTimeout(() => { errorToast.value = '' }, 3000)
    return
  }

  triggerHaptic(category === 'RC' ? 40 : 25)
  selectedType.value = type
  selectedCategory.value = category
  selectedSymbol.value = symbol
  modalOpen.value = true
}

const submitInfraction = async () => {
  submitting.value = true
  errorToast.value = ''

  try {
    const compId = competitionStore.activeCompetition?.id || 1
    const payload = {
      competitionId: compId,
      judgeId: authStore.user?.id,
      bibNumber: bibNumber.value.trim(),
      athleteName: athleteName.value,
      time: raceTime.value.trim(),
      infractionType: selectedType.value,
      cardCategory: selectedCategory.value
    }

    await api.post('/infractions', payload)

    triggerHaptic([50, 50, 50])
    modalOpen.value = false
    successToast.value = `SENT! Infração registada para o dorsal #${bibNumber.value}`
    bibNumber.value = ''
    athleteName.value = ''
    athleteTeam.value = ''
    resetTimeToNow()

    setTimeout(() => {
      successToast.value = ''
    }, 4000)
  } catch (err) {
    triggerHaptic([80, 50, 80])
    errorToast.value = err.response?.data?.error || 'Erro ao submeter infração'
    setTimeout(() => { errorToast.value = '' }, 4000)
  } finally {
    submitting.value = false
  }
}

onMounted(async () => {
  resetTimeToNow()
  timeInterval = setInterval(resetTimeToNow, 30000) // refresh time every 30s
  window.addEventListener('online', updateOnlineStatus)
  window.addEventListener('offline', updateOnlineStatus)
  await competitionStore.fetchCompetitions()
  await fetchAthletes()
})

onUnmounted(() => {
  if (timeInterval) clearInterval(timeInterval)
  window.removeEventListener('online', updateOnlineStatus)
  window.removeEventListener('offline', updateOnlineStatus)
})
</script>

<style scoped>
.new-infraction-view {
  max-width: 650px;
  margin: 0 auto;
  padding-bottom: 5rem;
}

.comp-banner {
  background-color: #f1f5f9;
  border-left: 4px solid #dc2626;
  padding: 0.6rem 1rem;
  border-radius: 6px;
  font-size: 0.85rem;
  margin-bottom: 1.25rem;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.comp-label {
  color: #64748b;
  font-weight: 600;
}

.comp-name {
  color: #0f172a;
  font-weight: 700;
}

.toast-success {
  background-color: #16a34a;
  color: white;
  padding: 0.85rem 1rem;
  border-radius: 8px;
  font-weight: 700;
  margin-bottom: 1rem;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  text-align: center;
}

.toast-error {
  background-color: #dc2626;
  color: white;
  padding: 0.85rem 1rem;
  border-radius: 8px;
  font-weight: 700;
  margin-bottom: 1rem;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  text-align: center;
}

.form-container {
  padding: 2rem 1.5rem;
}

.label-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.searching-tag {
  font-size: 0.75rem;
  color: #dc2626;
  font-style: italic;
}

.offline-banner {
  background-color: #ea580c;
  color: white;
  padding: 0.6rem 1rem;
  border-radius: 6px;
  font-size: 0.85rem;
  font-weight: 700;
  margin-bottom: 1rem;
  text-align: center;
}

.bib-input-relative {
  position: relative;
  width: 100%;
}

.clear-bib-btn {
  position: absolute;
  right: 0.75rem;
  top: 50%;
  transform: translateY(-50%);
  background: #e2e8f0;
  border: none;
  border-radius: 50%;
  width: 28px;
  height: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.85rem;
  color: #475569;
  cursor: pointer;
  touch-action: manipulation;
  transition: all 0.1s ease;
}

.clear-bib-btn:active {
  background: #cbd5e1;
  transform: translateY(-50%) scale(0.9);
}

.bib-input {
  font-size: 1.5rem;
  font-weight: 700;
  padding: 0.6rem 2.5rem 0.6rem 1rem;
  text-align: center;
  letter-spacing: 0.1em;
}

.athlete-display {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.5rem 0.75rem;
  background-color: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  font-size: 0.9rem;
}

.athlete-display.has-athlete {
  background-color: #eff6ff;
  border-color: #bfdbfe;
}

.athlete-label {
  font-weight: 700;
  color: #64748b;
  font-size: 0.75rem;
}

.athlete-name {
  font-weight: 700;
  color: #1e293b;
}

.athlete-team {
  font-size: 0.8rem;
  color: #64748b;
}

.quick-athletes {
  margin-top: -0.5rem;
  margin-bottom: 1.25rem;
}

.quick-label {
  font-size: 0.75rem;
  font-weight: 600;
  color: #94a3b8;
  display: block;
  margin-bottom: 0.35rem;
}

.chips-container {
  display: flex;
  flex-wrap: wrap;
  gap: 0.35rem;
}

.bib-chip {
  background-color: #f1f5f9;
  border: 1px solid #cbd5e1;
  padding: 0.25rem 0.6rem;
  font-size: 0.8rem;
  font-weight: 600;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.1s ease;
}

.bib-chip:hover {
  background-color: #e2e8f0;
}

.bib-chip.active {
  background-color: #dc2626;
  color: white;
  border-color: #dc2626;
}

.time-input-group {
  display: flex;
  gap: 0.5rem;
}

.time-field {
  font-size: 1.25rem;
  font-weight: 600;
  text-align: center;
}

.reset-time-btn {
  white-space: nowrap;
  padding: 0.6rem 1rem;
}

.section-divider {
  height: 1.5rem;
}

.paddles-section {
  margin-top: 0.5rem;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: baseline;
  margin-bottom: 0.5rem;
}

.section-title {
  font-size: 1.15rem;
  font-weight: 800;
  letter-spacing: 0.05em;
}

.text-red {
  color: #dc2626;
}

.section-subtitle {
  font-size: 0.75rem;
  color: #64748b;
  font-weight: 600;
}

.divider-line {
  height: 2px;
  background-color: #e2e8f0;
  margin-bottom: 1.5rem;
}

.paddle-buttons-row {
  display: flex;
  justify-content: space-around;
  align-items: center;
  padding: 0.5rem 0;
}
</style>
