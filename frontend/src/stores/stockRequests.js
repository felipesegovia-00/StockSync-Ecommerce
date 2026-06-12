import { defineStore } from 'pinia'
import { ref } from 'vue'
import * as api from '../api/stockRequests'

export const useStockRequestsStore = defineStore('stockRequests', () => {
  const incomingRequests = ref([])
  const outgoingRequests = ref([])
  const loading = ref(false)

  async function fetchIncoming(warehouseId) {
    loading.value = true
    try {
      const { data } = await api.getRequestsBySource(warehouseId)
      incomingRequests.value = data
    } finally {
      loading.value = false
    }
  }

  async function fetchOutgoing(warehouseId) {
    loading.value = true
    try {
      const { data } = await api.getRequestsByDestination(warehouseId)
      outgoingRequests.value = data
    } finally {
      loading.value = false
    }
  }

  async function create(payload) {
    const { data } = await api.createStockRequest(payload)
    return data
  }

  async function createBatch(payloadArray) {
    const { data } = await api.createStockRequestsBatch(payloadArray)
    return data
  }

  async function updateStatus(id, status) {
    const { data } = await api.updateRequestStatus(id, status)
    return data
  }

  async function receiveByScanner(sku, destinationWarehouseId) {
    const { data } = await api.receiveStockByScanner(sku, destinationWarehouseId)
    return data
  }

  return {
    incomingRequests,
    outgoingRequests,
    loading,
    fetchIncoming,
    fetchOutgoing,
    create,
    createBatch,
    updateStatus,
    receiveByScanner
  }
})
