<template>
  <div v-if="isOpen" class="modal-overlay" @click.self="$emit('cancel')">
    <div class="modal-content">
      <div class="modal-header" :class="category === 'RC' ? 'bg-red' : 'bg-yellow'">
        <div class="modal-icon">
          <span>{{ symbol }}</span>
        </div>
        <div>
          <h3 class="modal-title">Confirmar Notificação?</h3>
          <p class="modal-subtitle">{{ category === 'RC' ? 'Cartão Vermelho (RC)' : 'Aviso / Yellow Paddle (YP)' }}</p>
        </div>
      </div>

      <div class="modal-body">
        <div class="info-row">
          <span class="info-label">Atleta:</span>
          <span class="info-value font-bold">{{ athleteName || 'Atleta Desconhecido' }}</span>
        </div>
        <div class="info-row">
          <span class="info-label">Dorsal (BIB):</span>
          <span class="info-value font-mono font-bold">#{{ bibNumber }}</span>
        </div>
        <div class="info-row">
          <span class="info-label">Hora:</span>
          <span class="info-value font-mono">{{ time }}</span>
        </div>
        <div class="info-row">
          <span class="info-label">Infração:</span>
          <span class="info-value">
            <span class="badge" :class="category === 'RC' ? 'badge-red' : 'badge-yellow'">
              {{ category }} - {{ type.toUpperCase() }} ({{ symbol }})
            </span>
          </span>
        </div>
      </div>

      <div class="modal-footer">
        <button @click="$emit('cancel')" class="btn btn-secondary" :disabled="loading">
          CANCELAR
        </button>
        <button @click="$emit('confirm')" class="btn btn-primary" :disabled="loading">
          <span v-if="loading">A ENVIAR...</span>
          <span v-else>CONFIRMAR</span>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
defineProps({
  isOpen: Boolean,
  athleteName: String,
  bibNumber: String,
  time: String,
  type: String,
  category: String,
  symbol: String,
  loading: Boolean
})

defineEmits(['confirm', 'cancel'])
</script>

<style scoped>
.modal-header {
  padding: 1.25rem;
  display: flex;
  align-items: center;
  gap: 1rem;
  border-bottom: 1px solid #e2e8f0;
}

.bg-red {
  background-color: #fee2e2;
  border-left: 6px solid #dc2626;
}

.bg-yellow {
  background-color: #fef9c3;
  border-left: 6px solid #eab308;
}

.modal-icon {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.75rem;
  font-weight: 900;
  border: 2px solid #0f172a;
  background-color: white;
}

.bg-red .modal-icon {
  color: #dc2626;
}

.bg-yellow .modal-icon {
  color: #854d0e;
}

.modal-title {
  font-size: 1.15rem;
  margin-bottom: 0.15rem;
}

.modal-subtitle {
  font-size: 0.8rem;
  color: #64748b;
  font-weight: 600;
}

.modal-body {
  padding: 1.25rem;
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.info-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 0.5rem;
  border-bottom: 1px dashed #e2e8f0;
  font-size: 0.95rem;
}

.info-label {
  color: #64748b;
  font-weight: 500;
}

.info-value {
  color: #0f172a;
}

.font-bold {
  font-weight: 700;
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
