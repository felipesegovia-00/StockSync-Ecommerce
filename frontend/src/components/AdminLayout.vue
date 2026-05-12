<template>
  <v-layout>
    <v-navigation-drawer
      v-model="drawer"
      :rail="rail"
      :temporary="$vuetify.display.mobile"
      expand-on-hover
      app
      width="260"
      :rail-width="64"
    >
      <v-list-item
        class="pa-4"
        :title="auth.userName"
        :subtitle="auth.userEmail"
        prepend-avatar="https://api.dicebear.com/7.x/initials/svg?seed=admin"
      >
        <template v-slot:append>
          <v-btn
            v-if="!$vuetify.display.mobile"
            :icon="rail ? 'mdi-chevron-right' : 'mdi-chevron-left'"
            variant="text"
            size="small"
            @click.stop="rail = !rail"
          />
        </template>
      </v-list-item>

      <v-divider />

      <v-list density="compact" nav>
        <v-list-item
          v-for="item in menuItems"
          :key="item.to"
          :to="item.to"
          :title="item.title"
          :prepend-icon="item.icon"
          :active="route.path === item.to"
          color="primary"
          rounded="lg"
          class="ma-1"
        />
      </v-list>

      <template v-slot:append>
        <v-divider class="mb-2" />
        <v-list-item
          prepend-icon="mdi-theme-light-dark"
          title="Tema"
          @click="toggleTheme"
        />
        <v-list-item
          prepend-icon="mdi-logout"
          title="Cerrar Sesión"
          @click="handleLogout"
        />
      </template>
    </v-navigation-drawer>

    <v-app-bar flat>
      <v-app-bar-nav-icon @click="$vuetify.display.mobile ? drawer = !drawer : rail = !rail" />
      <v-app-bar-title>{{ pageTitle }}</v-app-bar-title>
      <v-spacer />
      <v-btn
        :icon="isDark ? 'mdi-white-balance-sunny' : 'mdi-weather-night'"
        variant="text"
        @click="toggleTheme"
      />
      <v-btn
        icon="mdi-logout"
        variant="text"
        @click="handleLogout"
      />
    </v-app-bar>

    <v-main>
      <v-container fluid class="pa-6">
        <router-view />
      </v-container>
    </v-main>
  </v-layout>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useTheme } from 'vuetify'
import { useAuthStore } from '../stores/auth'

const route = useRoute()
const router = useRouter()
const theme = useTheme()
const auth = useAuthStore()

const drawer = ref(true)
const rail = ref(false)
const isDark = computed(() => theme.global.name.value === 'dark')

const menuItems = [
  { title: 'Dashboard', icon: 'mdi-view-dashboard', to: '/admin' },
  { title: 'Productos', icon: 'mdi-package-variant-closed', to: '/admin/productos' },
  { title: 'Bodegas', icon: 'mdi-warehouse', to: '/admin/bodegas' },
  { title: 'Stock', icon: 'mdi-chart-box', to: '/admin/stock' },
]

const pageTitle = computed(() => {
  const map = {
    'Dashboard': 'Dashboard',
    'AdminProductos': 'Productos',
    'ProductoNuevo': 'Nuevo Producto',
    'ProductoEditar': 'Editar Producto',
    'AdminBodegas': 'Bodegas',
    'BodegaNueva': 'Nueva Bodega',
    'BodegaEditar': 'Editar Bodega',
    'AdminStock': 'Stock',
    'StockNuevo': 'Nuevo Stock',
    'StockEditar': 'Editar Stock',
  }
  return map[route.name] || 'Admin'
})

function toggleTheme() {
  const name = isDark.value ? 'light' : 'dark'
  theme.global.name.value = name
  localStorage.setItem('theme', name)
}

function handleLogout() {
  auth.logout()
  router.push('/')
}
</script>
