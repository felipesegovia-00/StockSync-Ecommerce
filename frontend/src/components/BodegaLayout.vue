<template>
  <v-layout>

    <!-- Sidebar -->
    <v-navigation-drawer
        v-model="drawer"
        :rail="rail && !mobile"
        :permanent="mdAndUp"
        :temporary="smAndDown"
        app
        width="260"
        :rail-width="64"
    >

      <template #prepend>
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

        <v-divider class="mx-4 mb-2" />
      </template>

      <v-list density="compact" nav>

        <v-list-item
            prepend-icon="mdi-view-dashboard"
            title="Dashboard"
            to="/bodega"
            exact
            rounded="lg"
            class="mx-2 my-1"
        />

        <v-list-item
            prepend-icon="mdi-clipboard-list"
            title="Solicitudes"
            to="/bodega/solicitudes"
            rounded="lg"
            class="mx-2 my-1"
        />

        <v-list-item
            prepend-icon="mdi-package-variant"
            title="Inventario"
            to="/bodega/inventario"
            rounded="lg"
            class="mx-2 my-1"
        />

        <v-list-item
            prepend-icon="mdi-truck-delivery"
            title="Despacho"
            to="/bodega/despacho"
            rounded="lg"
            class="mx-2 my-1"
        />

        <v-list-item
            prepend-icon="mdi-history"
            title="Historial"
            to="/bodega/historial"
            rounded="lg"
            class="mx-2 my-1"
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

        <v-list-item
            v-if="auth.isAdmin"
            prepend-icon="mdi-arrow-left"
            title="Volver al Admin"
            rounded="lg"
            class="mx-2 my-1"
            base-color="info"
            @click="volverAdmin"
        />

        <v-list-item
            prepend-icon="mdi-logout"
            title="Cerrar Sesión"
            rounded="lg"
            class="mx-2 my-1 mb-2"
            base-color="error"
            @click="handleLogout"
        />

      </template>

    </v-navigation-drawer>

    <!-- Top bar -->
    <v-app-bar flat>

      <v-app-bar-nav-icon
          @click="toggleDrawer"
      />

      <v-app-bar-title>
        {{ pageTitle }}
      </v-app-bar-title>

      <v-spacer />

      <v-btn
          :icon="isDark ? 'mdi-white-balance-sunny' : 'mdi-weather-night'"
          variant="text"
          @click="toggleTheme"
      />

    </v-app-bar>

    <!-- Content -->
    <v-main>
      <v-container
          fluid
          class="pa-6"
      >
        <router-view />
      </v-container>
    </v-main>

  </v-layout>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useDisplay, useTheme } from 'vuetify'
import { useAuthStore } from '../stores/auth'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()

const theme = useTheme()

const { mobile, mdAndUp, smAndDown } = useDisplay()

const drawer = ref(true)
const rail = ref(false)

const nombre = computed(() => auth.userName || 'Usuario Bodega')
const email = computed(() => auth.userEmail || 'bodega@stocksync.cl')

const initials = computed(() => {
  const name = auth.companyName || auth.userName || 'A'
  return name.split(' ').map(w => w[0]).join('').slice(0, 2).toUpperCase()
})

const isDark = computed(() =>
    theme.global.name.value === 'dark'
)

const pageTitle = computed(() => {

  const map = {
    '/bodega': 'Dashboard',
    '/bodega/solicitudes': 'Solicitudes',
    '/bodega/inventario': 'Inventario',
    '/bodega/despacho': 'Despacho',
    '/bodega/historial': 'historial'
  }

  return map[route.path] || 'Bodega'
})

function toggleDrawer() {

  if (mobile.value) {
    drawer.value = !drawer.value
  } else {
    rail.value = !rail.value
  }

}

function toggleTheme() {

  const name =
      isDark.value
          ? 'light'
          : 'dark'

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