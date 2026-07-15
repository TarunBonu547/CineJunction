(function () {
  const STORAGE_KEY = 'cinejunction.auth';
  const publicPages = ['login.html', 'register.html', 'forgot-password.html', 'reset-password.html', 'verify-email.html', 'email-sent.html', '404.html', '500.html', 'offline.html'];
  const protectedPages = ['index.html', 'movies.html', 'tv-shows.html', 'anime.html', 'trending.html', 'movie-details.html', 'search.html', 'profile.html', 'watchlist.html', 'community.html', 'settings.html'];

  function getCurrentPageName() {
    return window.location.pathname.split('/').pop() || 'index.html';
  }

  function getHomePath() {
    return window.location.pathname.includes('/pages/') ? '../index.html' : 'index.html';
  }

  function getLoginPath() {
    return window.location.pathname.includes('/pages/') ? 'login.html' : 'pages/login.html';
  }

  function getStoredAuth() {
    try {
      const raw = window.localStorage.getItem(STORAGE_KEY);
      return raw ? JSON.parse(raw) : null;
    } catch (error) {
      return null;
    }
  }

  function setStoredAuth(user) {
    window.localStorage.setItem(STORAGE_KEY, JSON.stringify(user));
  }

  function clearStoredAuth() {
    window.localStorage.removeItem(STORAGE_KEY);
  }

  function isProtectedPage(pageName) {
    return protectedPages.includes(pageName);
  }

  function isPublicPage(pageName) {
    return publicPages.includes(pageName);
  }

  function redirectToLogin() {
    window.location.replace(getLoginPath());
  }

  function redirectToHome() {
    window.location.replace(getHomePath());
  }

  function updateAuthUI() {
    const user = getStoredAuth();
    const profileButtons = document.querySelectorAll('.profile-pill');
    profileButtons.forEach((button) => {
      const initial = user?.name?.charAt(0)?.toUpperCase() || 'A';
      button.textContent = initial;
      button.setAttribute('aria-label', user ? `Open profile menu for ${user.name}` : 'Open profile menu');
    });
  }

  function attachPasswordToggles() {
    const toggleButtons = document.querySelectorAll('.pwd-toggle');
    toggleButtons.forEach((button) => {
      button.addEventListener('click', () => {
        const targetId = button.getAttribute('data-toggle-password');
        const input = document.getElementById(targetId);
        if (!input) return;

        const isHidden = input.type === 'password';
        input.type = isHidden ? 'text' : 'password';
        button.textContent = isHidden ? '◉' : '◌';
        button.setAttribute('aria-label', isHidden ? 'Hide password' : 'Show password');
      });
    });
  }

  function attachStrengthMeters() {
    const strengthMeters = document.querySelectorAll('[data-strength-meter]');
    strengthMeters.forEach((meter) => {
      const input = meter.parentElement?.querySelector('input[type="password"]');
      const label = meter.querySelector('[data-strength-text]');
      const bar = meter.querySelector('.strength-meter__bar');
      if (!input || !label || !bar) return;

      input.addEventListener('input', () => {
        const value = input.value;
        let strength = 0;
        if (value.length >= 8) strength += 1;
        if (/[A-Z]/.test(value) && /[0-9]/.test(value)) strength += 1;
        if (/[^A-Za-z0-9]/.test(value)) strength += 1;

        const labels = ['Weak', 'Fair', 'Strong'];
        const levels = ['35%', '70%', '100%'];
        const colors = ['var(--cj-primary)', 'var(--cj-primary)', 'var(--text-primary)'];
        const labelText = labels[Math.min(strength - 1, labels.length - 1)] || labels[0];
        label.textContent = labelText;
        bar.style.setProperty('--strength-width', levels[Math.min(strength - 1, levels.length - 1)] || levels[0]);
        bar.style.width = bar.style.getPropertyValue('--strength-width') || levels[0];
        bar.style.background = colors[Math.min(strength - 1, colors.length - 1)] || colors[0];
      });
    });
  }

  function attachFormHandlers() {
    const forms = document.querySelectorAll('.auth-form');
    forms.forEach((form) => {
      form.addEventListener('submit', (event) => {
        event.preventDefault();
        const message = form.querySelector('.form-message');
        const submitButton = form.querySelector('.auth-submit');

        if (form.dataset.authForm === 'login') {
          const email = form.querySelector('input[name="email"]').value.trim();
          const password = form.querySelector('input[name="password"]').value;
          if (!email || !password) {
            if (message) message.textContent = 'Please enter your email and password.';
            return;
          }

          if (submitButton) {
            submitButton.textContent = 'Signing in…';
            submitButton.disabled = true;
          }

          const user = {
            name: email.split('@')[0].replace(/[._-]+/g, ' '),
            username: email.split('@')[0],
            email,
            joinedAt: new Date().toISOString()
          };

          setStoredAuth(user);
          if (message) message.textContent = 'Welcome back. Redirecting you to the app…';
          window.setTimeout(redirectToHome, 700);
          return;
        }

        if (form.dataset.authForm === 'register') {
          const name = form.querySelector('input[name="name"]').value.trim();
          const username = form.querySelector('input[name="username"]').value.trim();
          const email = form.querySelector('input[name="email"]').value.trim();
          const password = form.querySelector('input[name="password"]').value;
          const confirm = form.querySelector('input[name="confirm"]').value;

          if (!name || !username || !email || !password || !confirm) {
            if (message) message.textContent = 'Please complete every field.';
            return;
          }

          if (password !== confirm) {
            if (message) message.textContent = 'Passwords do not match.';
            return;
          }

          if (password.length < 8) {
            if (message) message.textContent = 'Use at least 8 characters for your password.';
            return;
          }

          if (submitButton) {
            submitButton.textContent = 'Creating account…';
            submitButton.disabled = true;
          }

          const user = {
            name,
            username,
            email,
            joinedAt: new Date().toISOString()
          };

          setStoredAuth(user);
          if (message) message.textContent = 'Account ready. Taking you into CineJunction…';
          window.setTimeout(redirectToHome, 700);
        }
      });
    });
  }

  function initAuth() {
    const pageName = getCurrentPageName();
    const isAuthPage = isPublicPage(pageName);

    if (!isAuthPage && !getStoredAuth()) {
      redirectToLogin();
      return;
    }

    if (isAuthPage && getStoredAuth() && ['login.html', 'register.html', 'forgot-password.html', 'reset-password.html', 'verify-email.html', 'email-sent.html'].includes(pageName)) {
      redirectToHome();
      return;
    }

    if (pageName === 'index.html' && !getStoredAuth()) {
      redirectToLogin();
      return;
    }

    attachPasswordToggles();
    attachStrengthMeters();
    attachFormHandlers();
    updateAuthUI();
  }

  window.CineJunction = window.CineJunction || {};
  window.CineJunction.getAuthState = getStoredAuth;
  window.CineJunction.logout = function logout() {
    clearStoredAuth();
    redirectToLogin();
  };

  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initAuth);
  } else {
    initAuth();
  }
})();
