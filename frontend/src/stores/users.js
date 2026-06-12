import { defineStore } from 'pinia'
import { ref } from 'vue'
import * as api from '../api/users'

export const useUsersStore = defineStore('users', () => {
  const users = ref([])
  const loading = ref(false)

  async function fetchInvited() {
    loading.value = true
    try {
      const { data } = await api.getInvitedUsers()
      users.value = data
    } finally {
      loading.value = false
    }
  }

  async function fetchAll() {
    loading.value = true
    try {
      const { data } = await api.getAllUsers()
      users.value = data
    } finally {
      loading.value = false
    }
  }

  async function invite(email, role, assignedWarehouseId) {
    const { data } = await api.inviteUser(email, role, assignedWarehouseId)
    await fetchInvited()
    return data
  }

  async function update(id, payload) {
    await api.updateUser(id, payload)
    await fetchInvited()
  }

  async function remove(id) {
    await api.deleteUser(id)
    await fetchInvited()
  }

  return { users, loading, fetchInvited, fetchAll, invite, update, remove }
})
