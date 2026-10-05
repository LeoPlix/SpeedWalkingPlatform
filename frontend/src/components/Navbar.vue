<template>
  <header class="header">
    <div class="header-container">
      <div class="brand-section">
        <div class="logo-box">
          <span class="logo-text">RW</span>
        </div>
        <div>
          <h1 class="brand-title">Race Walking System</h1>
          <p class="brand-subtitle">R.F.E.A. / World Athletics Marcha</p>
        </div>
      </div>

      <!-- Desktop Nav Links -->
      <nav class="desktop-nav">
        <router-link to="/" class="nav-link" active-class="active">
          Início
        </router-link>
        <router-link to="/new" class="nav-link" active-class="active">
          Novo Registo
        </router-link>
        <router-link to="/lists" class="nav-link" active-class="active">
          Quadro & Listas
        </router-link>
        <router-link to="/athletes" class="nav-link" active-class="active">
          Atletas
        </router-link>
      </nav>

      <!-- User & Logout section -->
      <div class="user-section">
        <div class="judge-badge" v-if="authStore.user">
          <span class="judge-code" v-if="authStore.judgeCode">{{ authStore.judgeCode }}</span>
          <span class="judge-name">{{ authStore.judgeName }}</span>
        </div>

        <button @click="handleLogout" class="btn btn-secondary logout-btn" title="Terminar Sessão">
          Sair
        </button>
      </div>
    </div>
  </header>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const authStore = useAuthStore()

const handleLogout = () => {
  authStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.header {
  background-color: #0f172a;
  color: #ffffff;
  border-bottom: 3px solid #dc2626;
  position: sticky;
  top: 0;
  z-index: 100;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.header-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0.75rem 1rem;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.brand-section {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.logo-box {
  width: 40px;
  height: 40px;
  border-radius: 8px;
  background-color: #dc2626;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 900;
  font-size: 1.1rem;
  color: white;
}

.brand-title {
  font-size: 1.125rem;
  font-weight: 800;
  color: #ffffff;
  line-height: 1.2;
}

.brand-subtitle {
  font-size: 0.75rem;
  color: #94a3b8;
}

.desktop-nav {
  display: none;
  gap: 0.5rem;
}

@media (min-width: 768px) {
  .desktop-nav {
    display: flex;
  }
}

.nav-link {
  color: #cbd5e1;
  text-decoration: none;
  padding: 0.5rem 0.875rem;
  font-size: 0.875rem;
  font-weight: 600;
  border-radius: 6px;
  transition: all 0.15s ease;
}

.nav-link:hover {
  color: #ffffff;
  background-color: rgba(255, 255, 255, 0.1);
}

.nav-link.active {
  color: #ffffff;
  background-color: #dc2626;
}

.user-section {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.judge-badge {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  background-color: #1e293b;
  padding: 0.35rem 0.65rem;
  border-radius: 6px;
  border: 1px solid #334155;
  font-size: 0.8rem;
}

.judge-code {
  background-color: #facc15;
  color: #0f172a;
  font-weight: 800;
  padding: 0.1rem 0.35rem;
  border-radius: 4px;
}

.judge-name {
  color: #f8fafc;
  font-weight: 600;
}

.logout-btn {
  padding: 0.35rem 0.75rem;
  font-size: 0.8rem;
}
</style>
