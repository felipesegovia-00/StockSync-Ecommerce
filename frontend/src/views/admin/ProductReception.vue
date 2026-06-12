<template>
  <v-container fluid class="pa-6">
    <v-card elevation="2" class="mx-auto" max-width="600">
      <v-card-title class="text-center pt-6 text-h5 font-weight-bold">
        <v-icon size="36" color="primary" class="mr-2">mdi-barcode-scan</v-icon>
        Recepción de Productos
      </v-card-title>

      <v-card-text class="pa-6">
        <v-alert
          v-if="!auth.isAdmin && !auth.user?.assignedWarehouse?.id"
          type="warning"
          variant="tonal"
          class="mb-4"
        >
          No tienes una bodega/local asignado para recepcionar productos.
        </v-alert>

        <v-form @submit.prevent="handleScan" v-else>
          <v-select
            v-if="auth.isAdmin"
            v-model="destinationWarehouseId"
            :items="warehouses"
            item-title="name"
            item-value="id"
            label="Local Destino"
            prepend-inner-icon="mdi-warehouse"
            required
            class="mb-4"
          />
          
          <div class="d-flex align-start gap-4 mb-2">
            <v-text-field
              ref="scannerInput"
              v-model="sku"
              label="Escanea el código de barras (SKU)"
              prepend-inner-icon="mdi-barcode"
              variant="outlined"
              autofocus
              clearable
              :loading="processing"
              :disabled="processing"
              hint="Utiliza la pistola de escáner o escribe el SKU y presiona Enter"
              persistent-hint
              class="flex-grow-1"
            />
            <v-btn
              color="primary"
              variant="tonal"
              icon="mdi-camera"
              size="large"
              class="mt-1"
              @click="showCameraDialog = true"
              title="Escanear con Cámara"
            ></v-btn>
          </div>
          
          <div class="d-flex justify-center mt-6">
            <v-btn
              color="primary"
              size="large"
              type="submit"
              :loading="processing"
              :disabled="(!sku || (auth.isAdmin && !destinationWarehouseId)) || processing"
              prepend-icon="mdi-check"
            >
              Recepcionar
            </v-btn>
          </div>
        </v-form>

        <!-- Feedback Messages -->
        <v-slide-y-transition>
          <v-alert
            v-if="successMessage"
            type="success"
            variant="tonal"
            class="mt-6 text-center"
          >
            <div class="font-weight-bold mb-1">¡Recepción Exitosa!</div>
            {{ successMessage }}
          </v-alert>
        </v-slide-y-transition>

        <v-slide-y-transition>
          <v-alert
            v-if="errorMessage"
            type="error"
            variant="tonal"
            class="mt-6 text-center"
          >
            <div class="font-weight-bold mb-1">Error de Recepción</div>
            {{ errorMessage }}
          </v-alert>
        </v-slide-y-transition>
      </v-card-text>
    </v-card>

    <!-- Dialogo de Cámara -->
    <v-dialog v-model="showCameraDialog" max-width="500">
      <v-card>
        <v-card-title class="d-flex align-center bg-primary text-white pa-4">
          <v-icon class="mr-2">mdi-camera-scan</v-icon>
          Escanear Código
          <v-spacer></v-spacer>
          <v-btn icon="mdi-close" variant="text" @click="showCameraDialog = false"></v-btn>
        </v-card-title>
        
        <v-card-text class="pa-0">
          <v-alert v-if="cameraError" type="error" variant="tonal" class="ma-4">
            {{ cameraError }}
          </v-alert>
          <div v-else class="camera-container" style="min-height: 300px; position: relative;">
            <qrcode-stream 
              v-if="showCameraDialog"
              @detect="onDetect" 
              @error="onError"
            ></qrcode-stream>
            <div class="scan-overlay">
              <div class="scan-frame"></div>
            </div>
          </div>
        </v-card-text>
        <v-card-actions class="pa-4 bg-grey-lighten-4">
          <v-spacer></v-spacer>
          <v-btn color="error" variant="tonal" @click="showCameraDialog = false">Cancelar</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-container>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { receiveStockByScanner } from '../../api/stockRequests'
import { useAuthStore } from '../../stores/auth'
import { getWarehouses } from '../../api/warehouses'
import { QrcodeStream } from 'vue-qrcode-reader'

const auth = useAuthStore()
const sku = ref('')
const destinationWarehouseId = ref(null)
const warehouses = ref([])
const scannerInput = ref(null)
const processing = ref(false)
const successMessage = ref('')
const errorMessage = ref('')

const showCameraDialog = ref(false)
const cameraError = ref('')

async function handleScan() {
  const targetWarehouse = auth.isAdmin ? destinationWarehouseId.value : auth.user?.assignedWarehouse?.id
  if (!sku.value || !targetWarehouse) return

  processing.value = true
  successMessage.value = ''
  errorMessage.value = ''

  try {
    const res = await receiveStockByScanner(sku.value.trim(), targetWarehouse)
    successMessage.value = `Se recepcionaron ${res.data.quantity} unidades de ${res.data.productName} (SKU: ${res.data.sku}).`
  } catch (e) {
    console.error('Error in reception', e)
    errorMessage.value = e.response?.data?.message || 'No se pudo procesar el código o no hay envíos pendientes.'
  } finally {
    processing.value = false
    sku.value = ''
    
    // Devolver el foco al input después de un escaneo exitoso o fallido
    nextTick(() => {
      if (scannerInput.value) {
        scannerInput.value.focus()
      }
    })
  }
}

function onDetect(detectedCodes) {
  if (detectedCodes && detectedCodes.length > 0) {
    const detected = detectedCodes[0]
    const code = typeof detected === 'string' ? detected : (detected.rawValue || detected.content)
    if (!code) return
    
    sku.value = code
    showCameraDialog.value = false
    handleScan()
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

// Asegurarse de que el input tenga el foco al entrar a la vista
onMounted(async () => {
  if (auth.isAdmin) {
    try {
      const res = await getWarehouses()
      warehouses.value = res.data
    } catch (e) {
      console.error('Error fetching warehouses', e)
    }
  }

  nextTick(() => {
    if (scannerInput.value && (auth.isAdmin || auth.user?.assignedWarehouse?.id)) {
      scannerInput.value.focus()
    }
  })
})
</script>

<style scoped>
.gap-4 {
  gap: 16px;
}
.camera-container {
  overflow: hidden;
  background-color: #000;
}
.scan-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  pointer-events: none;
}
.scan-frame {
  width: 200px;
  height: 200px;
  border: 2px solid rgba(255, 255, 255, 0.5);
  border-radius: 12px;
  box-shadow: 0 0 0 100vw rgba(0, 0, 0, 0.5);
}
</style>
