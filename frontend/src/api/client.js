const API_BASE = window.__API_BASE__ || 'http://localhost:8085/api/v1';

function getToken() {
  return localStorage.getItem('shelfiq_token');
}

async function request(endpoint, options = {}) {
  const headers = options.headers || {};
  const token = getToken();

  if (token && !headers['Authorization']) {
    headers['Authorization'] = `Bearer ${token}`;
  }

  if (!(options.body instanceof FormData) && !headers['Content-Type']) {
    headers['Content-Type'] = 'application/json';
  }

  const response = await fetch(`${API_BASE}${endpoint}`, {
    ...options,
    headers,
  });

  const data = await response.json().catch(() => ({}));

  if (!response.ok) {
    const errorMsg = data?.message || data?.error || `Request failed with status ${response.status}`;
    throw new Error(errorMsg);
  }

  return data?.data !== undefined ? data.data : data;
}

export const api = {
  // Auth
  auth: {
    login: (email, password) =>
      request('/auth/login', {
        method: 'POST',
        body: JSON.stringify({ email, password }),
      }),
    register: (userData) =>
      request('/auth/register', {
        method: 'POST',
        body: JSON.stringify(userData),
      }),
    getProfile: () => request('/auth/me'),
  },

  // Inventory
  inventory: {
    getSummary: () => request('/inventory/summary'),
    getMovements: async (page = 0, size = 50) => {
      const res = await request(`/inventory/movements?page=${page}&size=${size}`);
      return Array.isArray(res) ? res : (res?.content || []);
    },
    getMovementsPaged: (page = 0, size = 50) =>
      request(`/inventory/movements?page=${page}&size=${size}`),
    getProductMovements: (productId) =>
      request(`/inventory/${productId}/movements`),
    adjustStock: (productId, data) =>
      request(`/inventory/${productId}/adjust`, {
        method: 'POST',
        body: JSON.stringify(data),
      }),
  },

  // Products & Categories
  products: {
    getAll: async () => {
      const res = await request('/products?size=100');
      return Array.isArray(res) ? res : (res?.content || []);
    },
    getPaged: (page = 0, size = 20) => request(`/products?page=${page}&size=${size}`),
    getById: (id) => request(`/products/${id}`),
    create: (data) =>
      request('/products', {
        method: 'POST',
        body: JSON.stringify(data),
      }),
    update: (id, data) =>
      request(`/products/${id}`, {
        method: 'PUT',
        body: JSON.stringify(data),
      }),
  },
  categories: {
    getAll: () => request('/categories'),
  },

  // Sales & POS
  sales: {
    checkout: (data) =>
      request('/sales/checkout', {
        method: 'POST',
        body: JSON.stringify(data),
      }),
    getTodayAnalytics: () => request('/sales/analytics/today'),
    getLeaderboard: (groupBy = 'CITY', type = 'ITEM', timeRange = 'TODAY') =>
      request(`/sales/analytics/leaderboard?groupBy=${groupBy}&type=${type}&timeRange=${timeRange}`),
    getHistory: (page = 0, size = 20) =>
      request(`/sales?page=${page}&size=${size}`),
    getById: (id) => request(`/sales/${id}`),
  },

  // AI Retail Intelligence
  ai: {
    getRestockRecommendations: () => request('/ai/restock-recommendations'),
    getDeadStock: () => request('/ai/dead-stock'),
    getInsights: () => request('/ai/insights'),
    chat: (message) =>
      request('/ai/chat', {
        method: 'POST',
        body: JSON.stringify({ message }),
      }),
  },

  // Suppliers & Purchase Orders
  suppliers: {
    getAll: () => request('/suppliers'),
  },
  purchaseOrders: {
    getAll: (page = 0, size = 20) =>
      request(`/purchase-orders?page=${page}&size=${size}`),
    getById: (id) => request(`/purchase-orders/${id}`),
    create: (data) =>
      request('/purchase-orders', {
        method: 'POST',
        body: JSON.stringify(data),
      }),
    receive: (id) =>
      request(`/purchase-orders/${id}/receive`, {
        method: 'POST',
      }),
  },

  // OCR Invoice Scanner
  ocr: {
    scanInvoice: (file) => {
      const formData = new FormData();
      formData.append('file', file);
      return request('/ocr/scan-invoice', {
        method: 'POST',
        body: formData,
      });
    },
    confirmRestock: (data) =>
      request('/ocr/confirm-restock', {
        method: 'POST',
        body: JSON.stringify(data),
      }),
  },
};
