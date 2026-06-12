import client from './client'

export function createStockRequest(data) {
  return client.post('/v1/stock-requests', data)
}

export function createStockRequestsBatch(dataArray) {
  return client.post('/v1/stock-requests/batch', dataArray)
}

export function getAllRequests() {
  return client.get('/v1/stock-requests')
}

export function getRequestsByDestination(warehouseId) {
  return client.get(`/v1/stock-requests/destination/${warehouseId}`)
}

export function getRequestsBySource(warehouseId) {
  return client.get(`/v1/stock-requests/source/${warehouseId}`)
}

export function updateRequestStatus(id, status) {
  return client.patch(`/v1/stock-requests/${id}/status`, { status })
}

export function receiveStockByScanner(sku, destinationWarehouseId) {
  return client.post('/v1/stock-requests/receive', { sku, destinationWarehouseId })
}
