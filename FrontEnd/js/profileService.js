(function () {
  window.CineJunction = window.CineJunction || {};

  async function getCurrentUser() {
    return window.CineJunction.apiFetch('/api/users/me');
  }

  async function updateProfile(data) {
    return window.CineJunction.apiFetch('/api/users/me', {
      method: 'PUT',
      body: data
    });
  }

  async function changePassword(data) {
    return window.CineJunction.apiFetch('/api/users/change-password', {
      method: 'PUT',
      body: data
    });
  }

  window.CineJunction.profileService = {
    getCurrentUser: getCurrentUser,
    updateProfile: updateProfile,
    changePassword: changePassword
  };
})();
