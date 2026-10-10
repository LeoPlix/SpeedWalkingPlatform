<template>
  <div class="home-view">
    <!-- HERO / ACTIVE COMPETITION BANNER -->
    <div class="hero-card card">
      <div class="hero-content">
        <span class="badge badge-red">Competição Ativa</span>
        <h2 class="hero-title">{{ competitionStore.activeCompetition?.name || 'Campeonato Nacional de Marcha' }}</h2>
        <p class="hero-subtitle">
          📍 {{ competitionStore.activeCompetition?.location || 'Portugal' }} •
          📅 {{ competitionStore.activeCompetition?.date || '2026' }}
        </p>
      </div>

      <div class="hero-actions">
        <router-link to="/new" class="btn btn-primary btn-large">
          + Novo Registo de Infração
        </router-link>
      </div>
    </div>

    <!-- QUICK STATS -->
    <div class="stats-row">
      <div class="stat-box">
        <span class="stat-number">{{ boardSummary?.totalAthletes || 0 }}</span>
        <span class="stat-title">Atletas em Prova</span>
      </div>
      <div class="stat-box">
        <span class="stat-number text-yellow">{{ boardSummary?.totalYellowPaddles || 0 }}</span>
        <span class="stat-title">Advertências (YP)</span>
      </div>
      <div class="stat-box">
        <span class="stat-number text-red">{{ boardSummary?.totalRedCards || 0 }}</span>
        <span class="stat-title">Notas Desqualificação (RC)</span>
      </div>
      <div class="stat-box">
        <span class="stat-number text-yellow">{{ boardSummary?.totalPenalized || 0 }}</span>
        <span class="stat-title">Pit Lane (3º RC)</span>
      </div>
      <div class="stat-box" :class="{ 'stat-alert': (boardSummary?.totalDisqualified || 0) > 0 }">
        <span class="stat-number text-danger">{{ boardSummary?.totalDisqualified || 0 }}</span>
        <span class="stat-title">Desqualificados (DQ)</span>
      </div>
    </div>

    <!-- NAVIGATION GRID -->
    <div class="nav-grid">
      <router-link to="/new" class="nav-card card">
        <div class="nav-icon bg-red">⚡</div>
        <div class="nav-info">
          <h3 class="nav-heading">Registo de Infrações</h3>
          <p class="nav-desc">Interface tátil rápida para juízes registarem Advertências (YP) e Notas de Desqualificação (RC).</p>
        </div>
      </router-link>

      <router-link to="/lists" class="nav-card card">
        <div class="nav-icon bg-blue">📋</div>
        <div class="nav-info">
          <h3 class="nav-heading">Quadro de Desqualificações</h3>
          <p class="nav-desc">Posting Board oficial da World Athletics / FPA em tempo real (Regra 3 RC = Pit Lane, 4 RC = DQ).</p>
        </div>
      </router-link>

      <router-link to="/athletes" class="nav-card card">
        <div class="nav-icon bg-green">🏃</div>
        <div class="nav-info">
          <h3 class="nav-heading">Lista de Atletas</h3>
          <p class="nav-desc">Consulta de dorsais, nomes, categorias e clubes inscritos na prova atual.</p>
        </div>
      </router-link>

      <router-link v-if="authStore.isAdmin" to="/admin" class="nav-card card border-admin">
        <div class="nav-icon bg-purple">⚙</div>
        <div class="nav-info">
          <h3 class="nav-heading">Painel de Administração</h3>
          <p class="nav-desc">Gestão de competições (abrir/fechar/criar), juízes de marcha e atletas inscritos.</p>
        </div>
      </router-link>
    </div>

    <!-- TECHNICAL RULES CARD -->
    <div class="rules-card card">
      <h3 class="rules-heading">Regulamento Técnico da Marcha Atlética (World Athletics / Federação Portuguesa de Atletismo)</h3>
      <div class="rules-grid">
        <div class="rule-item">
          <div class="rule-symbol">&gt;</div>
          <div>
            <h4 class="rule-name">Flexão de Joelho (Bent Knee)</h4>
            <p class="rule-text">A perna que avança tem de estar estendida (não flexionada no joelho) desde o momento do primeiro contacto com o solo até à posição vertical ereta.</p>
          </div>
        </div>

        <div class="rule-item">
          <div class="rule-symbol">~</div>
          <div>
            <h4 class="rule-name">Suspensão (Loss of Contact)</h4>
            <p class="rule-text">Contacto ininterrupto com o solo. O pé da frente tem de contactar o solo antes que o pé de trás perca o contacto com o mesmo.</p>
          </div>
        </div>

        <div class="rule-item">
          <div class="rule-symbol bg-yellow-dark">YP</div>
          <div>
            <h4 class="rule-name">Advertência (Aviso Preliminar)</h4>
            <p class="rule-text">As advertências (amarelos) são apenas avisos prévios quando o atleta está em risco de não cumprir a regra. Cada juiz só pode dar 1 aviso de cada tipo por atleta. Se o juiz já deu cartão vermelho, já não pode exibir advertências.</p>
          </div>
        </div>

        <div class="rule-item">
          <div class="rule-symbol bg-red-dark">RC</div>
          <div>
            <h4 class="rule-name">Nota de Desqualificação (Cartão Vermelho)</h4>
            <p class="rule-text">Enviada ao Secretariado quando o atleta infringe o regulamento. O mesmo juiz não pode atribuir a mesma infração ao mesmo atleta.</p>
          </div>
        </div>

        <div class="rule-item">
          <div class="rule-symbol bg-yellow-dark">3 RC</div>
          <div>
            <h4 class="rule-name">Zona de Penalização (Pit Lane - 3º RC)</h4>
            <p class="rule-text">Ao 3º Cartão Vermelho de 3 juízes distintos, o atleta cumpre paragem obrigatória conforme a distância: 30s (5km), 1 min (10km), 2 min (20km), 3,5 min (35km), 5 min (50km).</p>
          </div>
        </div>

        <div class="rule-item">
          <div class="rule-symbol bg-red-dark">4 RC</div>
          <div>
            <h4 class="rule-name">Desqualificação (DQ - 4º RC)</h4>
            <p class="rule-text">Ao 4º Cartão Vermelho de 4 juízes distintos (ou 3º caso não se aplique Pit Lane), o atleta é imediatamente desqualificado da prova pelo Juiz Chefe.</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useAuthStore } from '../stores/auth'
import { useCompetitionStore } from '../stores/competition'
import api from '../api/axios'

const authStore = useAuthStore()
const competitionStore = useCompetitionStore()
const boardSummary = ref(null)

onMounted(async () => {
  await competitionStore.fetchCompetitions()
  try {
    const compId = competitionStore.activeCompetition?.id || 1
    const res = await api.get(`/board/summary?competitionId=${compId}`)
    boardSummary.value = res.data
  } catch (err) {
    console.error(err)
  }
})
</script>

<style scoped>
.home-view {
  padding-bottom: 5rem;
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.hero-card {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 1.5rem;
  background: linear-gradient(to right, #ffffff, #fef2f2);
  border-left: 6px solid #dc2626;
}

.hero-title {
  font-size: 1.5rem;
  font-weight: 800;
  margin-top: 0.5rem;
  margin-bottom: 0.25rem;
}

.hero-subtitle {
  color: #64748b;
  font-size: 0.875rem;
}

.btn-large {
  padding: 0.85rem 1.5rem;
  font-size: 1rem;
}

.stats-row {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 1rem;
}

.stat-box {
  background: white;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  padding: 1.25rem;
  display: flex;
  flex-direction: column;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.stat-number {
  font-size: 2.25rem;
  font-weight: 900;
  line-height: 1;
  margin-bottom: 0.5rem;
  color: #0f172a;
}

.stat-title {
  font-size: 0.8rem;
  font-weight: 700;
  text-transform: uppercase;
  color: #64748b;
}

.text-yellow { color: #ca8a04; }
.text-red { color: #dc2626; }
.text-danger { color: #b91c1c; }

.stat-alert {
  background-color: #fef2f2;
  border-color: #fca5a5;
}

.nav-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 1rem;
}

.nav-card {
  display: flex;
  gap: 1rem;
  text-decoration: none;
  color: inherit;
  transition: transform 0.15s ease, box-shadow 0.15s ease;
}

.nav-card:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-md);
}

.nav-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.5rem;
  flex-shrink: 0;
}

.bg-red { background-color: #fee2e2; }
.bg-blue { background-color: #e0f2fe; }
.bg-green { background-color: #dcfce7; }
.bg-purple { background-color: #f3e8ff; }

.border-admin {
  border-left: 4px solid #9333ea;
}

.bg-yellow-dark {
  background-color: #ca8a04 !important;
}

.bg-red-dark {
  background-color: #dc2626 !important;
}

.nav-heading {
  font-size: 1.1rem;
  font-weight: 700;
  margin-bottom: 0.25rem;
}

.nav-desc {
  font-size: 0.825rem;
  color: #64748b;
  line-height: 1.4;
}

.rules-card {
  background-color: #f8fafc;
  border: 1px solid #e2e8f0;
}

.rules-heading {
  font-size: 1.15rem;
  font-weight: 800;
  margin-bottom: 1.25rem;
  color: #1e293b;
}

.rules-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 1.25rem;
}

.rule-item {
  display: flex;
  gap: 0.85rem;
  align-items: flex-start;
}

.rule-symbol {
  width: 40px;
  height: 40px;
  background-color: #0f172a;
  color: white;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.25rem;
  font-weight: 900;
  flex-shrink: 0;
}

.rule-name {
  font-size: 0.95rem;
  font-weight: 700;
  margin-bottom: 0.25rem;
  color: #0f172a;
}

.rule-text {
  font-size: 0.8rem;
  color: #64748b;
  line-height: 1.4;
}
</style>
