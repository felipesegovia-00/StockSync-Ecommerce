<template>
  <v-main>
    <PublicHeader />

    <section class="hero-section py-16">
      <v-container class="text-center">
        <v-row justify="center">
          <v-col cols="12" md="8">
            <v-icon size="72" color="primary" class="mb-4">mdi-package-variant-closed</v-icon>
            <h1 class="text-h2 font-weight-bold mb-4">StockSync</h1>
            <p class="text-h5 text-medium-emphasis mb-6">
              Inventario sin errores, negocio sin límites.
            </p>
            <p class="text-body-1 text-medium-emphasis mb-8" style="max-width: 600px; margin: 0 auto;">
              Sistema de gestión de inventario y E-commerce diseñado para optimizar el control de stock,
              gestionar productos y facilitar la administración en tiempo real.
            </p>
            <v-btn
              to="/login"
              color="primary"
              size="x-large"
              variant="flat"
              class="mr-4 mb-2"
            >
              <v-icon start>mdi-login</v-icon>
              Iniciar Sesión
            </v-btn>
            <v-btn
              to="/register"
              size="x-large"
              variant="outlined"
              class="mb-2"
            >
              <v-icon start>mdi-account-plus</v-icon>
              Registrarse
            </v-btn>
          </v-col>
        </v-row>
      </v-container>
    </section>

    <section class="features-section py-12 bg-surface">
      <v-container>
        <h2 class="text-h3 font-weight-bold text-center mb-10">Características</h2>
        <v-row>
          <v-col v-for="feature in features" :key="feature.title" cols="12" md="4">
            <v-card class="pa-6 text-center h-100" elevation="2">
              <v-icon size="48" :color="feature.color" class="mb-4">{{ feature.icon }}</v-icon>
              <h3 class="text-h5 font-weight-bold mb-2">{{ feature.title }}</h3>
              <p class="text-body-1 text-medium-emphasis">{{ feature.description }}</p>
            </v-card>
          </v-col>
        </v-row>
      </v-container>
    </section>

    <section class="tech-section py-12">
      <v-container>
        <h2 class="text-h3 font-weight-bold text-center mb-10">Tecnologías</h2>
        <v-row justify="center">
          <v-col v-for="tech in techStack" :key="tech.name" cols="6" md="3">
            <v-card class="pa-4 text-center" elevation="1">
              <v-icon size="36" color="primary" class="mb-2">{{ tech.icon }}</v-icon>
              <div class="font-weight-bold">{{ tech.name }}</div>
              <div class="text-caption text-medium-emphasis">{{ tech.role }}</div>
            </v-card>
          </v-col>
        </v-row>
      </v-container>
    </section>

    <section class="cta-section py-12 bg-primary text-center">
      <v-container>
        <h2 class="text-h3 font-weight-bold text-white mb-4">¿Listo para gestionar tu inventario?</h2>
        <v-btn
          to="/register"
          size="x-large"
          variant="flat"
          color="white"
          class="text-primary font-weight-bold"
        >
          <v-icon start>mdi-rocket-launch</v-icon>
          Comenzar ahora
        </v-btn>
      </v-container>
    </section>

    <PublicFooter />
  </v-main>
</template>

<script setup>
import PublicHeader from '../../components/PublicHeader.vue'
import PublicFooter from '../../components/PublicFooter.vue'

const features = [
  {
    icon: 'mdi-package-variant-closed',
    color: 'primary',
    title: 'Gestión de Productos',
    description: 'Administra tu catálogo con SKU, precios, categorías y control de stock centralizado.',
  },
  {
    icon: 'mdi-warehouse',
    color: 'success',
    title: 'Múltiples Bodegas',
    description: 'Organiza tu inventario en diferentes bodegas y transfiere stock entre ellas.',
  },
  {
    icon: 'mdi-chart-box',
    color: 'info',
    title: 'Trazabilidad de Stock',
    description: 'Registra cada movimiento: entradas, salidas, ajustes y transferencias.',
  },
  {
    icon: 'mdi-shield-account',
    color: 'warning',
    title: 'Control de Acceso',
    description: 'Autenticación segura con JWT y roles de usuario para administración.',
  },
  {
    icon: 'mdi-database-sync',
    color: 'secondary',
    title: 'Datos en Tiempo Real',
    description: 'Toda la información actualizada al instante con arquitectura REST API.',
  },
  {
    icon: 'mdi-responsive',
    color: 'primary',
    title: 'Diseño Adaptable',
    description: 'Interfaz moderna y responsiva accesible desde cualquier dispositivo.',
  },
]

const techStack = [
  { name: 'Vue 3', icon: 'mdi-vuejs', role: 'Frontend SPA' },
  { name: 'Vuetify', icon: 'mdi-material-design', role: 'Componentes UI' },
  { name: 'Spring Boot', icon: 'mdi-leaf', role: 'API REST' },
  { name: 'PostgreSQL', icon: 'mdi-database', role: 'Base de Datos' },
  { name: 'JWT', icon: 'mdi-shield-key', role: 'Autenticación' },
  { name: 'Flyway', icon: 'mdi-database-refresh', role: 'Migraciones' },
  { name: 'Vite', icon: 'mdi-lightning-bolt', role: 'Build tool' },
  { name: 'Pinia', icon: 'mdi-state-machine', role: 'Estado global' },
]
</script>

<style scoped>
.bg-surface {
  background-color: rgb(var(--v-theme-surface));
}
.cta-section {
  background-color: rgb(var(--v-theme-primary)) !important;
}
</style>
