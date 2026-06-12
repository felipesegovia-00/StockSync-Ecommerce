<template>
  <v-container fluid>
    <h1 class="text-h5 text-md-h4 mb-6">
      Recepción de Productos
    </h1>

    <v-row class="mx-0 mb-4">
      <v-col cols="12" md="6">
        <v-card class="pa-4">
          <v-card-title class="px-0 pt-0">Escáner de Recepción</v-card-title>
          <v-card-text class="px-0 pb-0">
            <v-text-field
                v-model="scannerSku"
                label="Escanear o ingresar SKU de Envío (Ej: ENV-...)"
                variant="outlined"
                prepend-inner-icon="mdi-barcode-scan"
                append-inner-icon="mdi-send"
                @click:append-inner="procesarEscaneo"
                @keyup.enter="procesarEscaneo"
                :loading="escaneando"
                hint="Presiona Enter para procesar"
                clearable
            ></v-text-field>
            <v-btn
              color="primary"
              variant="tonal"
              prepend-icon="mdi-camera"
              class="mt-2"
              block
              @click="showCameraDialog = true"
            >
              Escanear con Cámara
            </v-btn>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>

    <v-row class="mx-0">
      <v-col
          v-for="item in recepciones"
          :key="item.id"
          cols="12"
      >
        <v-card>
          <v-card-title class="text-wrap">
            Solicitud #{{ item.id }}
          </v-card-title>

          <v-card-text>
            <p><strong>Producto:</strong> {{ item.productName }}</p>
            <p><strong>Cantidad:</strong> {{ item.quantity }}</p>
            <p><strong>SKU de Envío:</strong> <v-chip size="small">{{ item.trackingSku }}</v-chip></p>

            <v-chip color="primary" class="mt-2">
              En Camino
            </v-chip>
          </v-card-text>

          <v-card-actions>
            <v-btn
                color="success"
                @click="confirmar(item)"
                :loading="loadingId === item.id"
            >
              Confirmar Recepción
            </v-btn>
          </v-card-actions>
        </v-card>
      </v-col>

      <v-col v-if="loading">
        <p class="text-center">Cargando...</p>
      </v-col>
      <v-col v-else-if="recepciones.length === 0">
        <p class="text-center text-medium-emphasis">No hay envíos pendientes.</p>
      </v-col>
    </v-row>

    <!-- Modal para escanear con cámara -->
    <v-dialog v-model="showCameraDialog" max-width="500">
      <v-card>
        <v-card-title class="d-flex justify-space-between align-center">
          <span>Escáner de Cámara</span>
          <v-btn icon="mdi-close" variant="text" @click="showCameraDialog = false"></v-btn>
        </v-card-title>
        <v-card-text class="pa-0">
          <div style="height: 300px; width: 100%; position: relative; background: #000;">
            <qrcode-stream 
              v-if="showCameraDialog"
              @detect="onDetect" 
              @error="onError"
            ></qrcode-stream>
            <div v-if="cameraError" class="text-error pa-4 text-center">
              {{ cameraError }}
            </div>
          </div>
        </v-card-text>
        <v-card-actions>
          <v-btn block color="error" variant="tonal" @click="showCameraDialog = false">Cancelar</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-container>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { QrcodeStream } from 'vue-qrcode-reader'
import { useAuthStore } from '../../stores/auth'
import { useStockRequestsStore } from '../../stores/stockRequests'

const authStore = useAuthStore()
const store = useStockRequestsStore()

const loading = ref(false)
const loadingId = ref(null)

onMounted(async () => {
  loading.value = true
  try {
    if (authStore.assignedWarehouseId) {
      await store.fetchOutgoing(authStore.assignedWarehouseId)
    }
  } finally {
    loading.value = false
  }
})

const recepciones = computed(() => {
  return store.outgoingRequests.filter(r => r.status === 'ENVIADO')
})

const scannerSku = ref('')
const escaneando = ref(false)
const showCameraDialog = ref(false)
const cameraError = ref('')

function onDetect(detectedCodes) {
  if (detectedCodes && detectedCodes.length > 0) {
    const detected = detectedCodes[0]
    const code = typeof detected === 'string' ? detected : (detected.rawValue || detected.content)
    if (!code) return
    
    scannerSku.value = code
    showCameraDialog.value = false
    procesarEscaneo()
  }
}

function onError(err) {
  if (err.name === 'NotAllowedError') {
    cameraError.value = 'Permiso de cámara denegado. Asegúrate de permitir el acceso a la cámara y que la página use HTTPS.'
  } else if (err.name === 'NotFoundError') {
    cameraError.value = 'No se encontró ninguna cámara en el dispositivo.'
  } else {
    cameraError.value = 'Error al iniciar la cámara: ' + err.message
  }
}

async function procesarEscaneo() {
  if (!scannerSku.value) return
  
  escaneando.value = true
  try {
    await store.receiveByScanner(scannerSku.value, authStore.assignedWarehouseId)
    await store.fetchOutgoing(authStore.assignedWarehouseId)
    scannerSku.value = ''
    alert('Recepción confirmada con éxito')
  } catch (error) {
    console.error('Error al procesar SKU', error)
    alert(error.response?.data?.message || 'Error al procesar SKU. Verifica que el SKU sea correcto y esté en camino.')
  } finally {
    escaneando.value = false
  }
}

async function confirmar(item) {
  loadingId.value = item.id
  try {
    await store.receiveByScanner(item.trackingSku, authStore.assignedWarehouseId)
    await store.fetchOutgoing(authStore.assignedWarehouseId)
  } catch (error) {
    console.error('Error al confirmar recepción', error)
    alert('Error al confirmar recepción')
  } finally {
    loadingId.value = null
  }
}
</script>