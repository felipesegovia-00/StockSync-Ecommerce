<template>
  <v-container fluid class="pa-6">

    <!-- Encabezado -->
    <div class="mb-6">
      <h1 class="text-h5 text-md-h4 font-weight-bold">
        Gestión de Despachos
      </h1>

      <p class="text-medium-emphasis">
        Administra los pedidos aprobados y realiza seguimiento de los envíos.
      </p>
    </div>

    <!-- Resumen -->
    <v-row class="mx-0 mb-6">

      <v-col cols="12" sm="6" md="3">
        <v-card class="text-center pa-4">
          <v-icon size="50" color="warning">
            mdi-package-variant-closed
          </v-icon>

          <div class="text-subtitle-1 mt-2">
            Preparando
          </div>

          <div class="text-h5 text-md-h4 font-weight-bold">
            {{ preparandoCount }}
          </div>
        </v-card>
      </v-col>

      <v-col cols="12" sm="6" md="3">
        <v-card class="text-center pa-4">
          <v-icon size="50" color="info">
            mdi-truck-delivery
          </v-icon>

          <div class="text-subtitle-1 mt-2">
            En Ruta
          </div>

          <div class="text-h5 text-md-h4 font-weight-bold">
            {{ enRutaCount }}
          </div>
        </v-card>
      </v-col>

      <v-col cols="12" sm="6" md="3">
        <v-card class="text-center pa-4">
          <v-icon size="50" color="success">
            mdi-check-circle
          </v-icon>

          <div class="text-subtitle-1 mt-2">
            Entregados Hoy
          </div>

          <div class="text-h5 text-md-h4 font-weight-bold">
            {{ entregadosHoyCount }}
          </div>
        </v-card>
      </v-col>

    </v-row>

    <!-- Despachos -->
    <v-row class="mx-0">

      <v-col
          v-for="despacho in despachos"
          :key="despacho.id"
          cols="12"
          lg="6"
      >

        <v-card
            class="despacho-card"
            elevation="4"
        >

          <v-card-title class="d-flex align-center">

            <v-icon
                color="primary"
                class="mr-2"
            >
              mdi-truck-delivery
            </v-icon>

            Despacho #{{ despacho.id }}

            <v-spacer />

            <v-chip
                :color="estadoColor(despacho.estado)"
                variant="tonal"
            >
              {{ despacho.estado }}
            </v-chip>

          </v-card-title>

          <v-divider />

          <v-card-text>

            <div class="mb-2">
              <strong>Local:</strong>
              {{ despacho.destinationWarehouseName }}
            </div>

            <div class="mb-2">
              <strong>Fecha:</strong>
              {{ new Date(despacho.createdAt).toLocaleDateString() }}
            </div>

            <div class="mb-4">
              <strong>Productos:</strong>
            </div>

            <v-list density="compact">

              <v-list-item>
                <template #prepend>
                  <v-icon>
                    mdi-package-variant
                  </v-icon>
                </template>

                <v-list-item-title>
                  {{ despacho.productName }}
                </v-list-item-title>

                <v-list-item-subtitle>
                  Cantidad: {{ despacho.quantity }}
                </v-list-item-subtitle>
              </v-list-item>

            </v-list>
            
            <div class="mt-4" v-if="despacho.trackingSku">
              <strong>SKU de Envío:</strong>
              <v-chip color="secondary" variant="flat" size="small" class="ml-2 font-weight-bold">
                {{ despacho.trackingSku }}
              </v-chip>
            </div>

          </v-card-text>

          <v-divider />

          <v-card-actions>

            <v-btn
                v-if="despacho.estado === 'APROBADO'"
                color="info"
                prepend-icon="mdi-truck-fast"
                @click="despachar(despacho)"
                :loading="loadingDespacho === despacho.id"
            >
              Despachar
            </v-btn>
            
            <v-btn
                v-if="despacho.estado === 'ENVIADO' && despacho.trackingSku"
                color="secondary"
                variant="tonal"
                prepend-icon="mdi-qrcode"
                @click="verCodigoQR(despacho.trackingSku)"
            >
              Ver QR
            </v-btn>

          </v-card-actions>

        </v-card>

      </v-col>

    </v-row>

    <!-- Dialogo de QR -->
    <v-dialog v-model="showSuccessDialog" max-width="500">
      <v-card class="text-center pa-6">
        <v-icon size="64" color="primary" class="mb-4">mdi-qrcode-scan</v-icon>
        <h3 class="text-h5 mb-2">Código de Envío</h3>
        <p class="mb-4">Este código QR debe ser escaneado en el local para confirmar la recepción del despacho.</p>
        
        <v-card variant="tonal" color="info" class="pa-4 mb-6">
          <div class="text-subtitle-2 mb-2">CÓDIGO DE BARRAS / QR PARA RECEPCIÓN</div>
          <div class="d-flex justify-center mb-4">
            <div style="background-color: white; padding: 16px; border-radius: 8px; display: inline-block;">
              <qrcode-vue :value="currentTrackingSku" :size="200" level="H" />
            </div>
          </div>
          <div class="text-h4 font-weight-bold tracking-widest">
            {{ currentTrackingSku }}
          </div>
        </v-card>
        
        <v-btn color="primary" block @click="showSuccessDialog = false">
          Aceptar
        </v-btn>
      </v-card>
    </v-dialog>

  </v-container>
</template>

<script setup>

import { ref, computed, onMounted } from 'vue'
import QrcodeVue from 'qrcode.vue'
import { useStockRequestsStore } from '../../stores/stockRequests'
import { useAuthStore } from '../../stores/auth'

const store = useStockRequestsStore()
const authStore = useAuthStore()

const loading = ref(false)
const loadingDespacho = ref(null)
const showSuccessDialog = ref(false)
const currentTrackingSku = ref('')

onMounted(async () => {
  loading.value = true
  try {
    if (authStore.assignedWarehouseId) {
      await store.fetchIncoming(authStore.assignedWarehouseId)
    }
  } finally {
    loading.value = false
  }
})

const despachos = computed(() => {
  return store.incomingRequests
      .filter(r => ['APROBADO', 'ENVIADO', 'RECIBIDO'].includes(r.status))
      .map(r => ({
        ...r,
        estado: r.status // APROBADO, ENVIADO, RECIBIDO
      }))
})

const preparandoCount = computed(() => despachos.value.filter(d => d.estado === 'APROBADO').length)
const enRutaCount = computed(() => despachos.value.filter(d => d.estado === 'ENVIADO').length)
const entregadosHoyCount = computed(() => despachos.value.filter(d => d.estado === 'RECIBIDO').length)

async function despachar(despacho) {
  loadingDespacho.value = despacho.id
  try {
    const response = await store.updateStatus(despacho.id, 'ENVIADO')
    if (response && response.trackingSku) {
      currentTrackingSku.value = response.trackingSku
      showSuccessDialog.value = true
    }
    await store.fetchIncoming(authStore.assignedWarehouseId)
  } catch (e) {
    alert('Error al despachar el pedido')
  } finally {
    loadingDespacho.value = null
  }
}

function verCodigoQR(sku) {
  currentTrackingSku.value = sku
  showSuccessDialog.value = true
}

function estadoColor(estado) {
  switch (estado) {
    case 'APROBADO':
      return 'warning'
    case 'ENVIADO':
      return 'info'
    case 'RECIBIDO':
      return 'success'
    default:
      return 'grey'
  }
}
</script>

<style scoped>

.despacho-card {
  transition: all .25s ease;
  border-radius: 18px;
}

.despacho-card:hover {
  transform: translateY(-4px);
}

@media (max-width: 960px) {

  .v-card-actions {
    flex-direction: column;
    gap: 10px;
  }

  .v-card-actions .v-btn {
    width: 100%;
  }

}

</style>