import { createStore } from 'vuex';
import axios from 'axios';

const savedUser = sessionStorage.getItem('user');
const savedRoles = sessionStorage.getItem('roles');

const normalizeRoles = (roles) => {
  return roles.map(role =>
    role.toUpperCase().startsWith('ROLE_') ? role.toUpperCase() : `ROLE_${role.toUpperCase()}`
  );
};

export default createStore({
  state: {
    user: savedUser ? JSON.parse(savedUser) : null,
    roles: savedRoles ? JSON.parse(savedRoles) : []
  },
  getters: {
    isAdmin(state) {
      if (!state.roles || state.roles.length === 0) return false;
      const roleNames = state.roles.map(role => String(role).toLowerCase());
      return roleNames.includes('admin');
    }
  },
  mutations: {
    setUserInfo(state, payload) {
      state.user = payload;
      if (Array.isArray(payload.roles)) {
        state.roles = normalizeRoles(payload.roles);
      } else {
        state.roles = [];
      }
      sessionStorage.setItem('user', JSON.stringify(state.user));
      sessionStorage.setItem('roles', JSON.stringify(state.roles));
    },
    clearUserInfo(state) {
      state.user = null;
      state.roles = [];
      sessionStorage.removeItem('user');
      sessionStorage.removeItem('roles');
    }
  },
  actions: {
    async fetchUserInfo({ commit }) {
      try {
        const response = await axios.get('/api/auth/userinfo')
        commit('setUserInfo', response.data)
        return response.data
      } catch (error) {
        commit('clearUserInfo')
        throw error
      }
    },
    async logout({ commit }) {
      try {
        await axios.post('/api/auth/dang-xuat', {}, { withCredentials: true });
      } catch (error) {
        console.error('Logout error:', error);
      }
      commit('clearUserInfo');
    }
  }
});