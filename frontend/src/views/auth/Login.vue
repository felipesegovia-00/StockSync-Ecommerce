<template>
  <v-main fluid class="login-background">
    <PublicHeader />

    <v-container fluid class="py-12" max-width="480">
      <v-card class="pa-6" elevation="4">
        <v-card-title class="text-h5 text-md-h4 font-weight-bold text-center mb-2">
          Iniciar Sesión
        </v-card-title>

        <v-card-text>
          <v-alert v-if="error" type="error" variant="tonal" class="mb-4" closable @click:close="error = ''">
            {{ error }}
          </v-alert>

          <v-form ref="form" @submit.prevent="handleLogin">
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
              :type="showPassword ? 'text' : 'password'"
              prepend-inner-icon="mdi-lock"
              :append-inner-icon="showPassword ? 'mdi-eye-off' : 'mdi-eye'"
              @click:append-inner="showPassword = !showPassword"
              :rules="[rules.required]"
              required
            />

            <v-checkbox
              v-model="rememberMe"
              label="Recuérdame"
              color="primary"
              hide-details
              class="mt-2"
            />

            <div class="d-flex justify-end mt-2 mb-3">
              <v-btn variant="text" color="primary" to="/forgot-password" size="small">
                ¿Olvidaste tu contraseña?
              </v-btn>
            </div>

            <v-btn
              type="submit"
              color="primary"
              block
              size="large"
              class="mt-4"
              :loading="loading"
            >
              Ingresar
            </v-btn>
          </v-form>

          <div class="text-center mt-4">
            <span class="text-body-2 text-medium-emphasis">¿No tienes cuenta?</span>
            <v-btn variant="text" color="primary" to="/register" size="small">
              Registrarse
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

const form = ref(null) // Referencia para el formulario
const email = ref('')
const password = ref('')
const showPassword = ref(false)
const rememberMe = ref(true)
const error = ref('')
const loading = ref(false)

const rules = {
  required: (v) => !!v || 'Campo requerido',
  email: (v) => /.+@.+/.test(v) || 'Email inválido',
}

async function handleLogin() {
  // Validar el formulario antes de enviar
  const { valid } = await form.value.validate()
  if (!valid) return

  loading.value = true
  error.value = ''
  try {
    const data = await auth.login(email.value, password.value, rememberMe.value)
    if (data.forcePasswordChange) {
      router.push('/change-password')
    } else {
      if (auth.isAdmin) {
        router.push('/admin')
      } else if (auth.isBodega) {
        router.push('/bodega')
      } else if (auth.isLocal) {
        router.push('/local')
      } else {
        router.push('/')
      }
    }
  } catch (e) {
    error.value = e.response?.data?.message || 'Credenciales inválidas'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-background {
  background-image:
      linear-gradient(
          rgba(2, 3, 0, 0.72),
          rgba(2, 3, 0, 0.72)
      ),
      url('/loginback.jpg');

  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;

  min-height: 100vh;
  width: 100%;

  display: flex;
  justify-content: center;
  align-items: center;
}

.login-card {
  backdrop-filter: blur(10px);
  background: rgba(20, 20, 20, 0.75) !important;
  border: 1px solid rgba(255,255,255,0.08);
}

.login-background {
  position: fixed;
  inset: 0;
}

</style>