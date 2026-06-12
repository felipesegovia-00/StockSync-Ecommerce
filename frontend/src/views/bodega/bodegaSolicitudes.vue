<template>
  <v-container fluid class="pa-6">

    <div class="mb-6">
      <h1 class="text-h5 text-md-h4 font-weight-bold">
        Solicitudes de Reposición
      </h1>

      <p class="text-medium-emphasis">
        Revisa y aprueba las solicitudes enviadas por los locales.
      </p>
    </div>

    <v-row class="mx-0">

      <v-col
          v-for="grupo in groupedSolicitudes"
          :key="grupo.requestGroupCode"
          cols="12"
      >
        <v-card
            class="solicitud-card mb-4"
            elevation="4"
        >

          <v-card-title class="d-flex align-center">
            <v-icon color="primary" class="mr-2">mdi-package-variant</v-icon>
            Paquete de Solicitud #{{ grupo.requestGroupCode }}
            <v-spacer />
            <v-chip color="warning" variant="tonal">Pendiente</v-chip>
          </v-card-title>

          <v-divider />

          <v-card-text>
            <v-row>
              <v-col cols="12" sm="6">
                <div class="info-item">
                  <v-icon size="20" class="mr-2">mdi-store</v-icon>
                  <strong>Local Solicitante:</strong> &nbsp; {{ grupo.destinationWarehouseName }}
                </div>
              </v-col>
              <v-col cols="12" sm="6">
                <div class="info-item">
                  <v-icon size="20" class="mr-2">mdi-calendar</v-icon>
                  <strong>Fecha:</strong> &nbsp; {{ new Date(grupo.createdAt).toLocaleDateString() }}
                </div>
              </v-col>
            </v-row>

            <v-expansion-panels class="mt-4" variant="accordion">
              <v-expansion-panel>
                <v-expansion-panel-title>
                  <span class="font-weight-medium">Ver {{ grupo.items.length }} Productos Solicitados</span>
                </v-expansion-panel-title>
                <v-expansion-panel-text>
                  <v-table density="compact">
                    <thead>
                      <tr>
                        <th class="text-left">Producto</th>
                        <th class="text-left">SKU</th>
                        <th class="text-left">Cantidad</th>
                      </tr>
                    </thead>
                    <tbody>
                      <tr v-for="item in grupo.items" :key="item.id">
                        <td>{{ item.productName }}</td>
                        <td>{{ item.sku }}</td>
                        <td>{{ item.quantity }}</td>
                      </tr>
                    </tbody>
                  </v-table>
                </v-expansion-panel-text>
              </v-expansion-panel>
            </v-expansion-panels>
          </v-card-text>

          <v-card-actions class="pa-4 bg-grey-lighten-4">
            <v-spacer></v-spacer>
            <v-btn
                color="error"
                variant="outlined"
                prepend-icon="mdi-close"
                @click="rechazarPaquete(grupo)"
                :loading="loadingAction === 'reject-' + grupo.requestGroupCode"
                :disabled="loadingAction !== null"
            >
              Rechazar Paquete
            </v-btn>

            <v-btn
                color="success"
                prepend-icon="mdi-check"
                @click="aprobarPaquete(grupo)"
                class="ml-2"
                :loading="loadingAction === 'approve-' + grupo.requestGroupCode"
                :disabled="loadingAction !== null"
            >
              Aprobar Paquete
            </v-btn>
          </v-card-actions>

        </v-card>
      </v-col>

      <v-col cols="12" v-if="groupedSolicitudes.length === 0 && !loading">
        <v-alert type="info" variant="tonal">No hay solicitudes pendientes en este momento.</v-alert>
      </v-col>

    </v-row>

  </v-container>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useStockRequestsStore } from '../../stores/stockRequests'
import { useAuthStore } from '../../stores/auth'

const store = useStockRequestsStore()
const authStore = useAuthStore()

const loading = ref(false)
const loadingAction = ref(null)

const solicitudes = ref([])

onMounted(async () => {
  await fetchSolicitudes()
})

async function fetchSolicitudes() {
  loading.value = true
  try {
    if (authStore.assignedWarehouseId) {
      await store.fetchIncoming(authStore.assignedWarehouseId)
      solicitudes.value = store.incomingRequests.filter(r => r.status === 'PENDIENTE')
    }
  } finally {
    loading.value = false
  }
}

const groupedSolicitudes = computed(() => {
  const groups = {}
  solicitudes.value.forEach(s => {
    // Para retrocompatibilidad con solicitudes viejas que no tengan código
    const code = s.requestGroupCode || `SINGLE-${s.id}`
    if (!groups[code]) {
      groups[code] = {
        requestGroupCode: code,
        destinationWarehouseName: s.destinationWarehouseName,
        createdAt: s.createdAt,
        items: []
      }
    }
    groups[code].items.push(s)
  })
  return Object.values(groups)
})

async function aprobarPaquete(grupo) {
  loadingAction.value = 'approve-' + grupo.requestGroupCode
  try {
    // Aprobar todos los items del paquete secuencialmente
    for (const item of grupo.items) {
      await store.updateStatus(item.id, 'APROBADO')
    }
    await fetchSolicitudes() // refrescar
  } catch(e) {
    alert('Error al aprobar el paquete de solicitudes')
  } finally {
    loadingAction.value = null
  }
}

async function rechazarPaquete(grupo) {
  loadingAction.value = 'reject-' + grupo.requestGroupCode
  try {
    for (const item of grupo.items) {
      await store.updateStatus(item.id, 'RECHAZADO')
    }
    await fetchSolicitudes() // refrescar
  } catch(e) {
    alert('Error al rechazar el paquete de solicitudes')
  } finally {
    loadingAction.value = null
  }
}
</script>

<style scoped>
.solicitud-card {
  transition: all .25s ease;
  border-radius: 12px;
}

.solicitud-card:hover {
  transform: translateY(-2px);
}

.info-item {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
}
</style>