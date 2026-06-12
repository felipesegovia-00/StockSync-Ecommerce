
<template>
  <v-layout>
    <v-navigation-drawer
        v-model="drawer"
        :rail="rail && !mobile"
        :permanent="mdAndUp"
        :temporary="smAndDown"
        app
        width="260"
        :rail-width="64"
    >
      <v-divider />

      <v-list nav>
        <v-list-item
          class="px-4 pt-4 pb-2"
          :title="auth.companyName || auth.userName"
          :subtitle="auth.userEmail"
          lines="two"
        >
          <template v-slot:prepend>
            <v-avatar v-if="auth.companyLogo" size="40" class="mr-3" rounded="0">
              <v-img :src="auth.companyLogo"></v-img>
            </v-avatar>
            <v-avatar v-else color="primary" size="40" class="mr-3">
              <span class="text-white font-weight-bold">{{ initials }}</span>
            </v-avatar>
          </template>
        </v-list-item>

        <v-list-item
            prepend-icon="mdi-view-dashboard"
            title="Dashboard"
            to="/local"
            exact
        />

        <v-list-item
            prepend-icon="mdi-point-of-sale"
            title="Ventas"
            to="/local/ventas"
        />

        <v-list-item
            prepend-icon="mdi-package-variant"
            title="Inventario"
            to="/local/inventario"
        />

        <v-list-item
            prepend-icon="mdi-file-document-plus"
            title="Solicitar Reposición"
            to="/local/solicitudReposicion"
        />

        <v-list-item
            prepend-icon="mdi-truck-delivery"
            title="Seguimiento"
            to="/local/seguimiento"
        />

        <v-list-item
            prepend-icon="mdi-clipboard-check"
            title="Recepciones"
            to="/local/recepcion"
        />
      </v-list>

      <template #append>
        <v-list-item
            prepend-icon="mdi-theme-light-dark"
            title="Tema"
            rounded="lg"
            class="mx-2 my-1"
            @click="toggleTheme"
        />
        <div class="pa-4">
          <v-btn
              v-if="auth.isAdmin"
              @click="volverAdmin"
              block
              prepend-icon="mdi-arrow-left"
              color="info"
              class="mb-4"
          >
            Volver al Admin
          </v-btn>
          <v-btn
              @click="handleLogout"
              block
              prepend-icon="mdi-logout"
              color="error"
          >
            Cerrar sesión
          </v-btn>
        </div>
      </template>
    </v-navigation-drawer>

    <!-- Top bar -->
    <v-app-bar flat>
      <v-app-bar-nav-icon @click="toggleDrawer" />
      <v-app-bar-title>Local</v-app-bar-title>
      <v-spacer />
      <v-btn
          :icon="isDark ? 'mdi-white-balance-sunny' : 'mdi-weather-night'"
          variant="text"
          @click="toggleTheme"
      />
    </v-app-bar>

    <!-- Content -->
    <v-main>
      <v-container fluid class="pa-6">
        <router-view />
      </v-container>
    </v-main>
  </v-layout>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { useDisplay, useTheme } from 'vuetify'

const auth = useAuthStore()
const router = useRouter()
const theme = useTheme()
const { mobile, mdAndUp, smAndDown } = useDisplay()

const drawer = ref(true)
const rail = ref(false)

const nombre = computed(() => auth.userName || 'Usuario Local')
const email = computed(() => auth.userEmail || 'local@stocksync.cl')

const initials = computed(() => {
  const name = auth.companyName || auth.userName || 'A'
  return name.split(' ').map(w => w[0]).join('').slice(0, 2).toUpperCase()
})

const isDark = computed(() => theme.global.name.value === 'dark')

function toggleTheme() {
  const name = isDark.value ? 'light' : 'dark'
  theme.change(name)
  localStorage.setItem('theme', name)
}

function handleLogout() {
  auth.logout()
  router.push('/login')
}

function volverAdmin() {
  auth.setAdminViewWarehouseId(null)
  router.push('/admin')
}

function toggleDrawer() {
  if (mobile.value) {
    drawer.value = !drawer.value
  } else {
    rail.value = !rail.value
  }
}
</script>

<style scoped>
.v-navigation-drawer {
  border-right: 1px solid rgba(255,255,255,0.05);
}
.v-list-item {
  transition: all .2s ease;
}
.v-list-item:hover {
  transform: translateX(4px);
}
</style>