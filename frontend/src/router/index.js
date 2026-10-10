import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import LoginView from '../views/LoginView.vue'
import HomeView from '../views/HomeView.vue'
import NewInfractionView from '../views/NewInfractionView.vue'
import ListsView from '../views/ListsView.vue'
import AthletesView from '../views/AthletesView.vue'
import AdminView from '../views/AdminView.vue'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: LoginView,
    meta: { public: true }
  },
  {
    path: '/',
    name: 'Home',
    component: HomeView
  },
  {
    path: '/new',
    name: 'NewInfraction',
    component: NewInfractionView
  },
  {
    path: '/lists',
    name: 'Lists',
    component: ListsView
  },
  {
    path: '/athletes',
    name: 'Athletes',
    component: AthletesView
  },
  {
    path: '/admin',
    name: 'Admin',
    component: AdminView
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/'
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()
  if (!to.meta.public && !authStore.isAuthenticated) {
    next('/login')
  } else if (to.path === '/login' && authStore.isAuthenticated) {
    next('/')
  } else {
    next()
  }
})

export default router
