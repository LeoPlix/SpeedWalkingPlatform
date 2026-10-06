<template>
  <div class="login-wrapper">
    <div class="login-card">
      <div class="login-header">
        <div class="logo-circle">
          <span>FPA</span>
        </div>
        <h2 class="app-name">FPA Marcha Atlética</h2>
        <p class="app-desc">Plataforma Oficial do Conselho de Arbitragem (Portugal)</p>
      </div>

      <form @submit.prevent="handleLogin" class="login-form">
        <div v-if="errorMessage" class="error-alert">
          {{ errorMessage }}
        </div>

        <div class="form-group">
          <label class="form-label" for="username">USER NAME</label>
          <input
            id="username"
            v-model="username"
            type="text"
            class="form-input"
            placeholder="Ex: juiz1 ou admin"
            required
            autocomplete="username"
          />
        </div>

        <div class="form-group">
          <label class="form-label" for="password">PASSWORD</label>
          <input
            id="password"
            v-model="password"
            type="password"
            class="form-input"
            placeholder="••••••••"
            required
            autocomplete="current-password"
          />
        </div>

        <button type="submit" class="btn btn-primary login-btn" :disabled="loading">
          <span v-if="loading">A AUTENTICAR...</span>
          <span v-else>LOG IN</span>
        </button>
      </form>

      <!-- Fast login shortcut for convenience -->
      <div class="demo-section">
        <p class="demo-title">Acesso Rápido de Demonstração:</p>
        <div class="demo-buttons">
          <button type="button" @click="setDemo('juiz1', 'pass123')" class="demo-chip">
            Juiz 1 (António)
          </button>
          <button type="button" @click="setDemo('juiz2', 'pass123')" class="demo-chip">
            Juiz 2 (Beatriz)
          </button>
          <button type="button" @click="setDemo('admin', 'admin123')" class="demo-chip">
            Secretariado Chefe
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const authStore = useAuthStore()

const username = ref('juiz1')
const password = ref('pass123')
const errorMessage = ref('')
const loading = ref(false)

const setDemo = (user, pass) => {
  username.value = user
  password.value = pass
}

const handleLogin = async () => {
  loading.value = true
  errorMessage.value = ''
  try {
    await authStore.login(username.value, password.value)
    router.push('/new')
  } catch (err) {
    errorMessage.value = err.message || 'Erro ao iniciar sessão'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-wrapper {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 1.5rem;
  background: linear-gradient(135deg, #0f172a 0%, #1e293b 100%);
}

.login-card {
  background: #ffffff;
  border-radius: 1rem;
  padding: 2.5rem 2rem;
  width: 100%;
  max-width: 440px;
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.3), 0 8px 10px -6px rgba(0, 0, 0, 0.3);
}

.login-header {
  text-align: center;
  margin-bottom: 2rem;
}

.logo-circle {
  width: 64px;
  height: 64px;
  background: linear-gradient(135deg, #046a38 0%, #046a38 45%, #da291c 45%, #da291c 100%);
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 1.35rem;
  font-weight: 900;
  margin: 0 auto 1rem;
  box-shadow: 0 4px 14px rgba(4, 106, 56, 0.4);
}

.app-name {
  font-size: 1.5rem;
  font-weight: 800;
  color: #0f172a;
}

.app-desc {
  font-size: 0.85rem;
  color: #64748b;
  margin-top: 0.25rem;
}

.login-btn {
  width: 100%;
  padding: 0.875rem;
  font-size: 1rem;
  letter-spacing: 0.05em;
  font-weight: 700;
  margin-top: 0.5rem;
}

.error-alert {
  background-color: #fef2f2;
  border-left: 4px solid #ef4444;
  color: #b91c1c;
  padding: 0.75rem 1rem;
  border-radius: 6px;
  font-size: 0.875rem;
  margin-bottom: 1.25rem;
}

.demo-section {
  margin-top: 2rem;
  padding-top: 1.25rem;
  border-top: 1px solid #f1f5f9;
}

.demo-title {
  font-size: 0.75rem;
  font-weight: 700;
  text-transform: uppercase;
  color: #94a3b8;
  letter-spacing: 0.05em;
  margin-bottom: 0.75rem;
  text-align: center;
}

.demo-buttons {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
  justify-content: center;
}

.demo-chip {
  background: #f1f5f9;
  border: 1px solid #cbd5e1;
  padding: 0.35rem 0.65rem;
  font-size: 0.75rem;
  font-weight: 600;
  color: #334155;
  border-radius: 9999px;
  cursor: pointer;
  transition: all 0.15s ease;
}

.demo-chip:hover {
  background: #e2e8f0;
  border-color: #94a3b8;
}
</style>
