<template>
  <v-app-bar flat density="comfortable" class="px-2" style="padding-left: 4px;">
    <v-container class="d-flex align-center pa-0">
      <router-link to="/" class="text-decoration-none d-flex align-center">
        <v-img v-if="auth.companyLogo" :src="auth.companyLogo" max-width="40" max-height="40" class="mr-2"></v-img>
        <v-app-bar-title
            class="font-weight-bold text-primary cursor-pointer text-h6 text-sm-h5 text-md-h4"
        >
          {{ auth.companyName || 'StockSync' }}
        </v-app-bar-title>
      </router-link>

      <v-spacer />

      <template v-for="link in links" :key="link.to">
        <v-btn
          :to="link.to"
          variant="text"
          :class="{ 'text-primary': isActive(link.to) }"
          class="mx-1"
        >
          {{ link.label }}
        </v-btn>
      </template>

      <v-btn
        icon
        variant="text"
        class="ml-2"
        @click="toggleTheme"
      >
        <v-icon>{{ isDark ? 'mdi-white-balance-sunny' : 'mdi-weather-night' }}</v-icon>
      </v-btn>

      <template v-if="auth.isAuthenticated">
        <v-btn
          to="/admin"
          variant="tonal"
          color="primary"
          class="ml-2"
        >
          <v-icon start>mdi-shield-account</v-icon>
          Admin
        </v-btn>
      </template>
      <template v-else>
        <v-btn
          to="/register"
          variant="text"
          class="ml-1 px-2"
        >
          <span class="d-none d-sm-flex">Registrarse</span>
          <v-icon class="d-flex d-sm-none">mdi-account-plus</v-icon>
        </v-btn>
        <v-btn
          to="/login"
          variant="tonal"
          color="primary"
          class="ml-1 px-2"
        >
          <span class="d-none d-sm-flex">Iniciar Sesión</span>
          <v-icon class="d-flex d-sm-none">mdi-login</v-icon>
        </v-btn>
      </template>
    </v-container>
  </v-app-bar>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { useTheme } from 'vuetify'
import { useAuthStore } from '../stores/auth'

const route = useRoute()
const theme = useTheme()
const auth = useAuthStore()

// const links = [
//   { to: '/', label: 'Inicio' },
// ]

const isDark = computed(() => theme.global.name.value === 'dark')

function isActive(path) {
  return route.path === path
}

function toggleTheme() {
  const name = isDark.value ? 'light' : 'dark'
  theme.global.name.value = name
  localStorage.setItem('theme', name)
}
</script>
