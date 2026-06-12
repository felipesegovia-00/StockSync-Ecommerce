<template>
  <v-main>
    <PublicHeader />

    <v-container class="py-12" max-width="480">
      <v-card class="pa-6" elevation="4">
        <v-card-title class="text-h5 text-md-h4 font-weight-bold text-center mb-2">
          Crear Cuenta
        </v-card-title>

        <v-card-text>
          <v-alert v-if="error" type="error" variant="tonal" class="mb-4" closable @click:close="error = ''">
            {{ error }}
          </v-alert>
          <v-alert v-if="successMessage" type="success" variant="tonal" class="mb-4">
            {{ successMessage }}
          </v-alert>

          <v-form @submit.prevent="handleRegister" v-if="!successMessage">
            <v-text-field
              v-model="companyName"
              label="Nombre de la Empresa"
              prepend-inner-icon="mdi-domain"
              :rules="[rules.required]"
              required
            />

            <v-text-field
              v-model="nombre"
              label="Nombre del Administrador"
              prepend-inner-icon="mdi-account"
              :rules="[rules.required]"
              required
            />

            <v-text-field
              v-model="email"
              label="Email"
              type="email"
              prepend-inner-icon="mdi-email"
              :rules="[rules.required, rules.email]"
              required
            />

            <v-text-field
              v-model="password"
              label="Contraseña"
              :append-inner-icon="showPassword ? 'mdi-eye-off' : 'mdi-eye'"
              :type="showPassword ? 'text' : 'password'"
              @click:append-inner="showPassword = !showPassword"
              prepend-inner-icon="mdi-lock"
              :rules="[rules.required, rules.minLength, rules.uppercase, rules.lowercase, rules.number]"
              required
              hint="Mínimo 8 caracteres, con mayúsculas, minúsculas y números."
              persistent-hint
            />

            <v-btn
              type="submit"
              color="primary"
              block
              size="large"
              class="mt-4"
              :loading="loading"
            >
              Registrarse
            </v-btn>
          </v-form>

          <div class="text-center mt-4">
            <span class="text-body-2 text-medium-emphasis">¿Ya tienes cuenta?</span>
            <v-btn variant="text" color="primary" to="/login" size="small">
              Iniciar Sesión
            </v-btn>
          </div>
        </v-card-text>
      </v-card>
    </v-container>

    <PublicFooter />
  </v-main>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import PublicHeader from '../../components/PublicHeader.vue'
import PublicFooter from '../../components/PublicFooter.vue'
import { useAuthStore } from '../../stores/auth'

const router = useRouter()
const auth = useAuthStore()

const companyName = ref('')
const nombre = ref('')
const email = ref('')
const password = ref('')
const error = ref('')
const successMessage = ref('')
const loading = ref(false)
const showPassword = ref(false)

const rules = {
  required: (v) => !!v || 'Campo requerido',
  email: (v) => /.+@.+/.test(v) || 'Email inválido',
  minLength: (v) => (v && v.length >= 8) || 'Mínimo 8 caracteres',
  uppercase: (v) => /[A-Z]/.test(v) || 'Debe contener al menos una mayúscula',
  lowercase: (v) => /[a-z]/.test(v) || 'Debe contener al menos una minúscula',
  number: (v) => /\d/.test(v) || 'Debe contener al menos un número',
}

async function handleRegister() {
  loading.value = true
  error.value = ''
  successMessage.value = ''
  try {
    const response = await auth.register(companyName.value, nombre.value, email.value, password.value)
    successMessage.value = response.data.message + " Redirigiendo al login..."
    setTimeout(() => {
      router.push('/login')
    }, 2000)
  } catch (e) {
    const err = e.response?.data
    if (err?.errors) {
      error.value = Object.values(err.errors).flat().join(', ')
    } else {
      error.value = err?.message || 'Error al registrarse'
    }
  } finally {
    loading.value = false
  }
}
</script>