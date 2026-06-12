<template>
  <v-container fluid>
    <v-row class="mx-0">
      <v-col cols="12">
        <h1 class="text-h5 text-md-h4 font-weight-bold mb-4">
          Solicitar Reposición
        </h1>
      </v-col>
    </v-row>

    <!-- Formulario -->
    <v-row class="mx-0">
      <v-col cols="12" md="8">
        <v-card elevation="2">
          <v-card-title class="text-wrap">
            Nueva Solicitud
          </v-card-title>

          <v-card-text>
            <v-form ref="formRef">
              <v-select
                  v-model="formulario.sourceWarehouseId"
                  label="Bodega Origen"
                  :items="bodegasDisponibles"
                  item-title="name"
                  item-value="id"
                  variant="outlined"
                  prepend-inner-icon="mdi-warehouse"
                  :rules="[v => !!v || 'Campo requerido']"
                  @update:model-value="onSourceWarehouseChange"
              />

              <v-select
                  v-model="formulario.productId"
                  label="Producto"
                  :items="productosDisponibles"
                  item-title="displayName"
                  item-value="productId"
                  variant="outlined"
                  prepend-inner-icon="mdi-package-variant"
                  :rules="[v => !!v || 'Campo requerido']"
                  class="mt-4"
                  :disabled="!formulario.sourceWarehouseId"
              />

              <v-text-field
                  v-model="formulario.quantity"
                  label="Cantidad solicitada"
                  type="number"
                  variant="outlined"
                  prepend-inner-icon="mdi-counter"
                  class="mt-4"
                  :rules="[
                    v => !!v || 'Campo requerido',
                    v => v > 0 || 'Debe ser mayor a cero'
                  ]"
              />

              <div class="d-flex justify-end mt-4">
                <v-btn
                    color="secondary"
                    variant="tonal"
                    prepend-icon="mdi-cart-plus"
                    @click="agregarAlCarrito"
                >
                  Agregar a Canasta
                </v-btn>
              </div>
            </v-form>

            <v-divider class="my-6"></v-divider>

            <h3 class="text-h6 mb-4">Canasta Actual</h3>
            
            <v-table v-if="carrito.length > 0">
              <thead>
                <tr>
                  <th>Bodega</th>
                  <th>Producto</th>
                  <th>Cantidad</th>
                  <th width="80"></th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="(item, index) in carrito" :key="index">
                  <td>{{ item.sourceWarehouseName }}</td>
                  <td>{{ item.productName }}</td>
                  <td>{{ item.quantity }}</td>
                  <td>
                    <v-btn icon="mdi-delete" color="error" variant="text" size="small" @click="eliminarDelCarrito(index)"></v-btn>
                  </td>
                </tr>
              </tbody>
            </v-table>
            
            <v-alert v-else type="info" variant="tonal" class="mb-4">
              La canasta está vacía.
            </v-alert>

            <div class="d-flex justify-end mt-4" v-if="carrito.length > 0">
              <v-btn
                  color="primary"
                  prepend-icon="mdi-send"
                  @click="enviarSolicitudBatch"
                  :loading="enviando"
              >
                Generar Solicitud
              </v-btn>
            </div>
          </v-card-text>
        </v-card>
      </v-col>

      <!-- Productos con bajo stock -->
      <v-col cols="12" md="4">
        <v-card elevation="2">
          <v-card-title class="text-wrap">
            Productos con Bajo Stock
          </v-card-title>

          <v-list>
            <v-list-item
                v-for="producto in bajoStock"
                :key="producto.nombre"
            >
              <template #prepend>
                <v-icon color="warning">
                  mdi-alert-circle
                </v-icon>
              </template>

              <v-list-item-title>
                {{ producto.productName }}
              </v-list-item-title>

              <v-list-item-subtitle>
                Stock actual: {{ producto.quantity }}
              </v-list-item-subtitle>
            </v-list-item>
          </v-list>
        </v-card>
      </v-col>
    </v-row>

    <!-- Historial de solicitudes -->
    <v-row class="mx-0 mt-6">
      <v-col cols="12">
        <v-card elevation="2">
          <v-card-title class="text-wrap">
            Estado de Solicitudes
          </v-card-title>

          <ResponsiveTable :empty="!solicitudes.length" empty-text="No hay solicitudes" :colspan="4">
            <template #headers>
              <tr>
                <th>Producto</th>
                <th>Cantidad</th>
                <th>Fecha</th>
                <th>Estado</th>
              </tr>
            </template>

            <template #body>
              <tr
                  v-for="solicitud in solicitudes"
                  :key="solicitud.id"
              >
                <td>{{ solicitud.productName }}</td>
                <td>{{ solicitud.quantity }}</td>
                <td>{{ new Date(solicitud.createdAt).toLocaleDateString() }}</td>

                <td>
                  <v-chip
                      :color="getColor(solicitud.status)"
                      size="small"
                  >
                    {{ solicitud.status }}
                  </v-chip>
                </td>
              </tr>
            </template>

            <template #cards>
              <v-card
                  v-for="solicitud in solicitudes"
                  :key="solicitud.id"
                  variant="outlined"
                  class="mb-3"
              >
                <v-card-title>{{ solicitud.productName }}</v-card-title>
                <v-card-text>
                  <div>Cantidad: {{ solicitud.quantity }}</div>
                  <div>Fecha: {{ new Date(solicitud.createdAt).toLocaleDateString() }}</div>
                  <v-chip
                      :color="getColor(solicitud.status)"
                      size="small"
                  >
                    {{ solicitud.status }}
                  </v-chip>
                </v-card-text>
              </v-card>
            </template>
          </ResponsiveTable>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useAuthStore } from '../../stores/auth'
import { useStockStore } from '../../stores/stock'
import { useStockRequestsStore } from '../../stores/stockRequests'
import { useWarehousesStore } from '../../stores/warehouses'
import { useProductsStore } from '../../stores/products'
import ResponsiveTable from '../../components/ResponsiveTable.vue'

const authStore = useAuthStore()
const stockStore = useStockStore()
const requestsStore = useStockRequestsStore()
const warehousesStore = useWarehousesStore()
const productsStore = useProductsStore()

const formRef = ref(null)
const enviando = ref(false)
const filterProduct = ref('')
const filterStatus = ref(null)
const formulario = ref({
  productId: null,
  quantity: null,
  sourceWarehouseId: null
})

const carrito = ref([])

const sourceWarehouseStock = ref([])

const productosDisponibles = computed(() => {
  return sourceWarehouseStock.value
    .filter(stock => stock.quantity > 0)
    .map(stock => ({
      productId: stock.productId,
      displayName: `${stock.productName} (Disponible: ${stock.quantity})`,
      quantity: stock.quantity
    }))
})

async function onSourceWarehouseChange(warehouseId) {
  formulario.value.productId = null
  if (!warehouseId) {
    sourceWarehouseStock.value = []
    return
  }
  sourceWarehouseStock.value = await stockStore.fetchByWarehouse(warehouseId)
}

const bodegasDisponibles = computed(() => 
  warehousesStore.warehouses.filter(w => w.id !== authStore.assignedWarehouseId)
)
const stockData = ref([])

onMounted(async () => {
  if (authStore.assignedWarehouseId) {
    await Promise.all([
      warehousesStore.fetchAll(),
      productsStore.fetchAll(),
      requestsStore.fetchOutgoing(authStore.assignedWarehouseId),
      fetchStock()
    ])
  }
})

async function fetchStock() {
  stockData.value = await stockStore.fetchByWarehouse(authStore.assignedWarehouseId)
}

const bajoStock = computed(() => {
  return stockData.value.filter(s => s.quantity <= 10)
})

const statusOptions = [
  { title: 'Pendiente', value: 'PENDIENTE' },
  { title: 'Aprobado', value: 'APROBADO' },
  { title: 'Enviado', value: 'ENVIADO' },
  { title: 'Recibido', value: 'RECIBIDO' },
  { title: 'Rechazado', value: 'RECHAZADO' },
]

const solicitudes = computed(() => {
  return [...requestsStore.outgoingRequests]
    .sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))
})

const filteredSolicitudes = computed(() => {
  let result = solicitudes.value

  if (filterProduct.value) {
    const q = filterProduct.value.toLowerCase()
    result = result.filter(s => s.productName?.toLowerCase().includes(q))
  }

  if (filterStatus.value) {
    result = result.filter(s => s.status === filterStatus.value)
  }

  return result
})

async function agregarAlCarrito() {
  const { valid } = await formRef.value?.validate() || { valid: true }
  if (!valid) return
  
  if (!formulario.value.productId || !formulario.value.quantity || !formulario.value.sourceWarehouseId) {
    alert('Por favor completa todos los campos requeridos.')
    return
  }

  const selectedProduct = productosDisponibles.value.find(p => p.productId === formulario.value.productId)
  if (selectedProduct && formulario.value.quantity > selectedProduct.quantity) {
    alert(`La cantidad solicitada supera el stock disponible (${selectedProduct.quantity}) en la bodega origen.`)
    return
  }

  const warehouse = bodegasDisponibles.value.find(w => w.id === formulario.value.sourceWarehouseId)

  carrito.value.push({
    productId: formulario.value.productId,
    productName: selectedProduct.displayName.split(' (')[0],
    quantity: formulario.value.quantity,
    sourceWarehouseId: formulario.value.sourceWarehouseId,
    sourceWarehouseName: warehouse ? warehouse.name : 'Bodega',
    destinationWarehouseId: authStore.assignedWarehouseId
  })

  // Limpiar form
  formulario.value.productId = null
  formulario.value.quantity = null
}

function eliminarDelCarrito(index) {
  carrito.value.splice(index, 1)
}

async function enviarSolicitudBatch() {
  if (carrito.value.length === 0) return

  enviando.value = true
  try {
    const payloads = carrito.value.map(item => ({
      productId: item.productId,
      quantity: item.quantity,
      sourceWarehouseId: item.sourceWarehouseId,
      destinationWarehouseId: item.destinationWarehouseId
    }))

    await requestsStore.createBatch(payloads)
    
    carrito.value = []
    formulario.value.sourceWarehouseId = null
    sourceWarehouseStock.value = []
    await requestsStore.fetchOutgoing(authStore.assignedWarehouseId)
    alert('Solicitud enviada exitosamente')
  } catch (error) {
    console.error('Error al enviar solicitud batch', error)
    alert('Error al enviar la solicitud')
  } finally {
    enviando.value = false
  }
}

function getColor(estado) {
  switch (estado) {
    case 'PENDIENTE':
      return 'warning'
    case 'APROBADO':
      return 'success'
    case 'ENVIADO':
      return 'info'
    case 'RECIBIDO':
      return 'primary'
    case 'RECHAZADO':
      return 'error'
    default:
      return 'grey'
  }
}
</script>