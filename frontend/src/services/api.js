import axios from 'axios';
import Cookies from 'js-cookie';

const ORDER_SERVICE_URL = 'http://localhost:8080';
const USER_SERVICE_URL = 'http://localhost:8083';
const DELIVERY_SERVICE_URL = 'http://localhost:8082';

const createApiClient = (baseURL) => {
  const client = axios.create({
    baseURL,
    headers: {
      'Content-Type': 'application/json',
    },
  });

  // Добавляем токен в каждый запрос
  client.interceptors.request.use((config) => {
    const token = Cookies.get('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  });

  return client;
};

const orderApi = createApiClient(ORDER_SERVICE_URL);
const userApi = createApiClient(USER_SERVICE_URL);
const deliveryApi = createApiClient(DELIVERY_SERVICE_URL);

export const authAPI = {
  register: (name, email, password) =>
    userApi.post('/api/v1/auth/register', { name, email, password }),
  login: (email, password) =>
    userApi.post('/api/v1/auth/authenticate', { email, password }),
  generateBackupCodes: () =>
    userApi.post('/api/v1/auth/generate-backup-codes'),
};

export const userAPI = {
  getProfile: () =>
    userApi.get('/api/v1/users/me'),
};

export const itemAPI = {
  getAllItems: () =>
    orderApi.get('/api/items'),
  createItem: (name, price, imageUrl) =>
    orderApi.post('/api/items', { name, price, imageUrl }),
};

export const orderAPI = {
  getAllOrders: () =>
    orderApi.get('/api/orders'),
  getOrder: (id) =>
    orderApi.get(`/api/orders/${id}`),
  createOrder: (street, houseNumber, items) =>
    orderApi.post('/api/orders', { street, houseNumber, items }),
  payOrder: (id, paymentMethod) =>
    orderApi.post(`/api/orders/${id}/pay`, { paymentMethod }),
};

export const deliveryAPI = {
  getStreets: () =>
    deliveryApi.get('/api/deliveries/streets'),
};

export default orderApi;
