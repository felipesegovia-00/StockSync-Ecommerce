import client from './client'

export function login(email, password, rememberMe = false) {
  return client.post('/login', { email, password, rememberMe: !!rememberMe })
}

export function register(companyName, nombre, email, password) {
  return client.post('/register', { companyName, nombre, email, password })
}

export function changePassword(oldPassword, newPassword) {
  return client.post('/users/change-password', { oldPassword, newPassword })
}

export function forgotPassword(email) {
  return client.post('/forgot-password', { email })
}

export function resetPassword(token, newPassword) {
  return client.post('/reset-password', { token, newPassword })
}

export const authService = {
  login,
  register,
  changePassword,
  forgotPassword,
  resetPassword
}