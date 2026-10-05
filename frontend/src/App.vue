<template>
  <div class="app-layout">
    <Navbar v-if="authStore.isAuthenticated" />

    <main class="main-content">
      <router-view />
    </main>

    <!-- Mobile Bottom Navigation (Matching Flutter BottomNavigationBar) -->
    <nav v-if="authStore.isAuthenticated" class="mobile-nav">
      <router-link to="/" class="mobile-nav-item" active-class="active">
        <span class="nav-icon">🏠</span>
        <span class="nav-text">HOME</span>
      </router-link>

      <router-link to="/new" class="mobile-nav-item" active-class="active">
        <span class="nav-icon">➕</span>
        <span class="nav-text">NEW</span>
      </router-link>

      <router-link to="/lists" class="mobile-nav-item" active-class="active">
        <span class="nav-icon">📋</span>
        <span class="nav-text">LISTS</span>
      </router-link>
    </nav>
  </div>
</template>

<script setup>
import { useAuthStore } from './stores/auth'
import Navbar from './components/Navbar.vue'

const authStore = useAuthStore()
</script>

<style scoped>
.mobile-nav-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-decoration: none;
  color: #64748b;
  font-size: 0.75rem;
  font-weight: 700;
  width: 33.33%;
  height: 100%;
  transition: all 0.15s ease;
}

.mobile-nav-item .nav-icon {
  font-size: 1.25rem;
  margin-bottom: 2px;
}

.mobile-nav-item.active {
  color: #dc2626;
}

.mobile-nav-item:active {
  background-color: #f1f5f9;
}
</style>
