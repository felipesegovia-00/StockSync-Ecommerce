<template>
  <v-row class="mx-0" align="start">
    <v-col cols="12" md="5">
      <v-card elevation="2" class="mb-4">
        <v-card-title class="text-wrap">Logo de la Empresa</v-card-title>
        <v-card-text>
          <v-alert v-if="logoSuccess" type="success" variant="tonal" class="mb-4" closable @click:close="logoSuccess = false">
            Logo actualizado correctamente. Por favor recarga la página para ver los cambios en todo el menú.
          </v-alert>
          <v-alert v-if="logoError" type="error" variant="tonal" class="mb-4" closable @click:close="logoError = ''">
            {{ logoError }}
          </v-alert>
          <div v-if="auth.companyLogo" class="mb-4 text-center">
             <v-img :src="auth.companyLogo" max-height="100" contain></v-img>
          </div>
          <v-file-input
            v-model="logoFile"
            label="Seleccionar nueva imagen"
            accept="image/*"
            prepend-inner-icon="mdi-camera"
            prepend-icon=""
            @change="handleLogoSelect"
            :loading="uploadingLogo"
            variant="outlined"
          ></v-file-input>
          <v-btn color="primary" block class="mt-2" :loading="uploadingLogo" :disabled="!logoBase64" @click="uploadLogo">Guardar Logo</v-btn>
        </v-card-text>
      </v-card>

      <v-card elevation="2">
        <v-card-title class="text-wrap">Invitar Usuario</v-card-title>
        <v-card-text>
          <v-alert v-if="inviteError" type="error" variant="tonal" class="mb-4" closable @click:close="inviteError = ''">
            {{ inviteError }}
          </v-alert>

          <v-alert v-if="inviteSuccess" type="success" variant="tonal" class="mb-4" closable @click:close="inviteSuccess = ''">
            Usuario invitado exitosamente. Contraseña temporal: <strong>{{ tempPassword }}</strong>
          </v-alert>

          <v-form @submit.prevent="handleInvite">
            <v-text-field
              v-model="form.email"
              label="Email"
              type="email"
              prepend-inner-icon="mdi-email"
              :rules="[rules.required, rules.email]"
              required
            />

            <v-select
              v-model="form.role"
              :items="roles"
              item-title="label"
              item-value="value"
              label="Rol"
              prepend-inner-icon="mdi-shield-account"
              :rules="[rules.required]"
              required
            />

            <v-select
              v-model="form.assignedWarehouseId"
              :items="warehouses"
              item-title="name"
              item-value="id"
              label="Bodega Asignada (Opcional)"
              prepend-inner-icon="mdi-warehouse"
              clearable
            />

            <v-btn
              type="submit"
              color="primary"
              block
              :loading="inviting"
            >
              Invitar
            </v-btn>
          </v-form>
        </v-card-text>
      </v-card>
    </v-col>

    <v-col cols="12" md="7">
      <v-card elevation="2">
        <v-card-title class="text-wrap">Todos los Usuarios</v-card-title>
        <v-card-text>
          <v-progress-linear v-if="loading" indeterminate color="primary" />

          <v-row dense class="mb-4" align="center">
            <v-col cols="12" sm="4">
              <v-text-field
                v-model="filterText"
                label="Buscar email o nombre"
                prepend-inner-icon="mdi-magnify"
                clearable
                variant="outlined"
                density="compact"
                hide-details
              />
            </v-col>
            <v-col cols="12" sm="4">
              <v-select
                v-model="filterRole"
                :items="roles"
                item-title="label"
                item-value="value"
                label="Rol"
                clearable
                variant="outlined"
                density="compact"
                hide-details
              />
            </v-col>
            <v-col cols="12" sm="4">
              <v-select
                v-model="filterStatus"
                :items="statusOptions"
                label="Estado"
                clearable
                variant="outlined"
                density="compact"
                hide-details
              />
            </v-col>
          </v-row>

          <ResponsiveTable :loading="loading" :empty="!filteredUsers.length" empty-text="No hay usuarios registrados" :colspan="6">
            <template #headers>
              <tr>
                <th>Email</th>
                <th>Nombre</th>
                <th>Rol</th>
                <th>Bodega</th>
                <th>Estado</th>
                <th>Acciones</th>
              </tr>
            </template>
            <template #body>
              <tr v-for="u in filteredUsers" :key="u.id">
                <td>{{ u.email }}</td>
                <td>{{ u.nombre }}</td>
                <td>
                  <v-chip :color="chipColor(u.role)" size="small" variant="tonal">
                    {{ u.role }}
                  </v-chip>
                </td>
                <td>{{ u.assignedWarehouse?.name || 'Ninguna' }}</td>
                <td>
                  <v-chip :color="u.enabled ? 'success' : 'error'" size="small" variant="tonal">
                    {{ u.enabled ? 'Activo' : 'Inactivo' }}
                  </v-chip>
                </td>
                <td>
                  <v-btn icon="mdi-pencil" variant="text" size="small" color="primary" @click="openEditDialog(u)"></v-btn>
                  <v-btn 
                    :icon="u.enabled ? 'mdi-account-off' : 'mdi-account-check'" 
                    variant="text" 
                    size="small" 
                    :color="u.enabled ? 'error' : 'success'" 
                    @click="toggleActive(u)">
                  </v-btn>
                  <v-btn icon="mdi-delete" variant="text" size="small" color="error" @click="openDeleteDialog(u)"></v-btn>
                </td>
              </tr>
            </template>
            <template #cards>
              <v-card v-for="u in filteredUsers" :key="u.id" variant="outlined" class="mb-3">
                <v-card-title>{{ u.nombre }}</v-card-title>
                <v-card-text>
                  <div><strong>Email:</strong> {{ u.email }}</div>
                  <div><strong>Rol:</strong> <v-chip :color="chipColor(u.role)" size="small" variant="tonal">{{ u.role }}</v-chip></div>
                  <div><strong>Bodega:</strong> {{ u.assignedWarehouse?.name || 'Ninguna' }}</div>
                  <div><strong>Estado:</strong> <v-chip :color="u.enabled ? 'success' : 'error'" size="small" variant="tonal">{{ u.enabled ? 'Activo' : 'Inactivo' }}</v-chip></div>
                </v-card-text>
                <v-card-actions>
                  <v-btn icon="mdi-pencil" variant="text" size="small" color="primary" @click="openEditDialog(u)"></v-btn>
                  <v-btn :icon="u.enabled ? 'mdi-account-off' : 'mdi-account-check'" variant="text" size="small" :color="u.enabled ? 'error' : 'success'" @click="toggleActive(u)"></v-btn>
                  <v-btn icon="mdi-delete" variant="text" size="small" color="error" @click="openDeleteDialog(u)"></v-btn>
                </v-card-actions>
              </v-card>
            </template>
          </ResponsiveTable>
        </v-card-text>
      </v-card>

      <v-dialog v-model="editDialog" max-width="500">
        <v-card>
          <v-card-title class="text-wrap">Editar Usuario</v-card-title>
          <v-card-text>
            <v-form @submit.prevent="handleEdit" ref="editForm">
              <v-select
                v-model="editingUser.role"
                :items="roles"
                item-title="label"
                item-value="value"
                label="Rol"
                required
              />
              <v-select
                v-model="editingUser.assignedWarehouseId"
                :items="warehouses"
                item-title="name"
                item-value="id"
                label="Bodega Asignada"
                clearable
              />
            </v-form>
          </v-card-text>
          <v-card-actions>
            <v-spacer />
            <v-btn variant="text" @click="editDialog = false">Cancelar</v-btn>
            <v-btn color="primary" @click="handleEdit" :loading="saving">Guardar</v-btn>
          </v-card-actions>
        </v-card>
      </v-dialog>

      <v-dialog v-model="deleteDialog" max-width="400">
        <v-card>
          <v-card-title class="text-wrap">Eliminar Usuario</v-card-title>
          <v-card-text>
            ¿Estás seguro de que deseas eliminar al usuario <strong>{{ userToDelete?.nombre || userToDelete?.email }}</strong>? Esta acción no se puede deshacer.
          </v-card-text>
          <v-card-actions>
            <v-spacer />
            <v-btn variant="text" @click="deleteDialog = false">Cancelar</v-btn>
            <v-btn color="error" @click="handleDelete" :loading="deleting">Eliminar</v-btn>
          </v-card-actions>
        </v-card>
      </v-dialog>
    </v-col>
  </v-row>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useUsersStore } from '../../stores/users'
import { getWarehouses } from '../../api/warehouses'
import ResponsiveTable from '../../components/ResponsiveTable.vue'

import api from '../../api/client'
import { useAuthStore } from '../../stores/auth'

const store = useUsersStore()
const auth = useAuthStore()
const warehouses = ref([])
const loading = ref(false)
const filterText = ref('')
const filterRole = ref(null)
const filterStatus = ref(null)

const statusOptions = [
  { title: 'Activo', value: 'active' },
  { title: 'Inactivo', value: 'inactive' },
]

const filteredUsers = computed(() => {
  let result = store.users

  if (filterText.value) {
    const q = filterText.value.toLowerCase()
    result = result.filter(u =>
      u.email?.toLowerCase().includes(q) ||
      u.nombre?.toLowerCase().includes(q)
    )
  }

  if (filterRole.value) {
    result = result.filter(u => u.role === filterRole.value)
  }

  if (filterStatus.value) {
    result = result.filter(u =>
      filterStatus.value === 'active' ? u.enabled : !u.enabled
    )
  }

  return result
})
const inviting = ref(false)
const saving = ref(false)
const editDialog = ref(false)
const inviteError = ref('')
const inviteSuccess = ref(false)
const tempPassword = ref('')
const deleteDialog = ref(false)
const userToDelete = ref(null)
const deleting = ref(false)

const uploadingLogo = ref(false)
const logoSuccess = ref(false)
const logoError = ref('')
const logoFile = ref(null)
const logoBase64 = ref('')

const form = ref({
  email: '',
  role: null,
  assignedWarehouseId: null
})

const editingUser = ref({
  id: null,
  role: null,
  assignedWarehouseId: null
})

const roles = [
  { label: 'Admin', value: 'ADMIN' },
  { label: 'Local', value: 'LOCAL' },
  { label: 'Bodega', value: 'BODEGA' },
]

const rules = {
  required: (v) => !!v || 'Campo requerido',
  email: (v) => /.+@.+/.test(v) || 'Email inválido',
}

function chipColor(role) {
  const map = { ADMIN: 'warning', LOCAL: 'primary', BODEGA: 'success' }
  return map[role] || 'grey'
}

async function handleInvite() {
  inviting.value = true
  inviteError.value = ''
  inviteSuccess.value = false
  try {
    const result = await store.invite(form.value.email, form.value.role, form.value.assignedWarehouseId)
    tempPassword.value = result.temporaryPassword
    inviteSuccess.value = true
    form.value = { email: '', role: null, assignedWarehouseId: null }
  } catch (e) {
    inviteError.value = e.response?.data?.message || 'Error al invitar usuario'
  } finally {
    inviting.value = false
  }
}

function handleLogoSelect(event) {
  const file = event.target.files[0]
  if (file) {
    const reader = new FileReader()
    reader.onload = (e) => {
      logoBase64.value = e.target.result
    }
    reader.readAsDataURL(file)
  } else {
    logoBase64.value = ''
  }
}

async function uploadLogo() {
  if (!logoBase64.value) return
  uploadingLogo.value = true
  logoError.value = ''
  logoSuccess.value = false
  try {
    const response = await api.put('/company/logo', { companyLogo: logoBase64.value })
    auth.user.companyLogo = response.data.companyLogo
    localStorage.setItem('user', JSON.stringify(auth.user)) // Force refresh local storage if needed
    logoSuccess.value = true
    logoFile.value = null
    logoBase64.value = ''
  } catch (e) {
    logoError.value = e.response?.data?.message || 'Error al subir el logo'
  } finally {
    uploadingLogo.value = false
  }
}

function openEditDialog(u) {
  editingUser.value = {
    id: u.id,
    role: u.role,
    assignedWarehouseId: u.assignedWarehouse?.id || null
  }
  editDialog.value = true
}

async function handleEdit() {
  saving.value = true
  try {
    // Si no hay bodega, mandamos -1 para que el backend la limpie (como se definió en el servicio)
    const wid = editingUser.value.assignedWarehouseId || -1
    await store.update(editingUser.value.id, {
      role: editingUser.value.role,
      assignedWarehouseId: wid
    })
    editDialog.value = false
  } catch (e) {
    alert('Error al actualizar usuario')
  } finally {
    saving.value = false
  }
}

async function toggleActive(u) {
  try {
    await store.update(u.id, { active: !u.enabled })
  } catch (e) {
    alert('Error al cambiar estado')
  }
}

function openDeleteDialog(u) {
  userToDelete.value = u
  deleteDialog.value = true
}

async function handleDelete() {
  deleting.value = true
  try {
    await store.remove(userToDelete.value.id)
    deleteDialog.value = false
  } catch (e) {
    alert('Error al eliminar usuario')
  } finally {
    deleting.value = false
  }
}

onMounted(async () => {
  loading.value = true
  try {
    const [wRes] = await Promise.all([getWarehouses(), store.fetchInvited()])
    warehouses.value = wRes.data
  } finally {
    loading.value = false
  }
})
</script>
