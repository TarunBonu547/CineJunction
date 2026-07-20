(function () {
  window.CineJunction = window.CineJunction || {};

  function getBaseUrl() {
    return (window.CineJunction.API_BASE_URL || '').replace(/\/$/, '');
  }

  function getAuthHeaders() {
    const auth = window.CineJunction.getAuthState ? window.CineJunction.getAuthState() : null;
    const headers = {
      'Content-Type': 'application/json',
      'Accept': 'application/json'
    };
    if (auth && auth.token) {
      headers['Authorization'] = 'Bearer ' + auth.token;
    }
    return headers;
  }

  async function apiFetch(path, options) {
    options = options || {};
    const url = getBaseUrl() + path;
    const config = {
      ...options,
      headers: Object.assign({}, getAuthHeaders(), options.headers || {})
    };

    if (config.body && typeof config.body === 'object' && !(config.body instanceof FormData)) {
      config.body = JSON.stringify(config.body);
    }

    var response = await fetch(url, config);

    if (response.status === 204) {
      return null;
    }

    var contentType = response.headers.get('content-type');
    var isJson = contentType && contentType.indexOf('application/json') !== -1;
    var data = isJson ? await response.json() : null;

    if (!response.ok) {
      var message = (data && data.message) || (data && data.error) || ('Request failed with status ' + response.status);
      var error = new Error(message);
      error.status = response.status;
      error.data = data;
      throw error;
    }

    return data;
  }

  window.CineJunction.apiFetch = apiFetch;
})();
