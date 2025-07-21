import axios from 'axios';

const API_BASE_URL = 'http://localhost:8081/api/auth';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

export const registerUser = async (userData) => {
  return api.post('/signup', userData);
};

export const verifyEmail = async (email, otp) => {
  return api.post('/verify', { email, otp });
};

export const loginUser = async (credentials) => {
  return api.post('/login', credentials);
};

// export const forgotPassword = async (email) => {
//   return api.post('/forgot-password', { email });
// };

// export const resetPassword = async (email, otp, newPassword, confirmPassword) => {
//   return api.post('/reset-password', { email, otp, newPassword, confirmPassword });
// };

export const checkAuth = async () => {
  return api.get('/display');
};