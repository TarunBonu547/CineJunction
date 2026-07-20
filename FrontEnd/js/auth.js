(function () {
  var STORAGE_KEY = 'cinejunction.auth';
  var publicPages = ['login.html', 'register.html', 'forgot-password.html', 'reset-password.html', 'verify-email.html', 'email-sent.html', '404.html', '500.html', 'offline.html'];
  var protectedPages = ['index.html', 'movies.html', 'tv-shows.html', 'anime.html', 'trending.html', 'movie-details.html', 'search.html', 'profile.html', 'watchlist.html', 'community.html', 'settings.html'];

  function getCurrentPageName() {
    return window.location.pathname.split('/').pop() || 'index.html';
  }

  function getHomePath() {
    return window.location.pathname.indexOf('/pages/') !== -1 ? '../index.html' : 'index.html';
  }

  function getLoginPath() {
    return window.location.pathname.indexOf('/pages/') !== -1 ? 'login.html' : 'pages/login.html';
  }

  function getRegisterPath() {
    return window.location.pathname.indexOf('/pages/') !== -1 ? 'register.html' : 'pages/register.html';
  }

  function getStoredAuth() {
    try {
      var raw = window.localStorage.getItem(STORAGE_KEY);
      return raw ? JSON.parse(raw) : null;
    } catch (error) {
      return null;
    }
  }

  function setStoredAuth(authData) {
    window.localStorage.setItem(STORAGE_KEY, JSON.stringify(authData));
  }

  function clearStoredAuth() {
    window.localStorage.removeItem(STORAGE_KEY);
  }

  function isProtectedPage(pageName) {
    return protectedPages.indexOf(pageName) !== -1;
  }

  function isPublicPage(pageName) {
    return publicPages.indexOf(pageName) !== -1;
  }

  function redirectToLogin() {
    window.location.replace(getLoginPath());
  }

  function redirectToHome() {
    window.location.replace(getHomePath());
  }

  function setFormLoading(form, isLoading) {
    var submit = form.querySelector('.auth-submit');
    var inputs = form.querySelectorAll('input:not([type="checkbox"])');
    if (submit) {
      submit.disabled = isLoading;
      submit.textContent = isLoading ? 'Please wait…' : (form.dataset.authForm === 'login' ? 'Sign In' : 'Create account');
    }
    inputs.forEach(function (input) {
      input.disabled = isLoading;
    });
  }

  function setFormMessage(form, message, isError) {
    var el = form.querySelector('.form-message');
    if (!el) return;
    el.textContent = message;
    el.style.color = isError ? 'var(--cj-primary)' : '#4ade80';
  }

  async function login(email, password) {
    var data = await window.CineJunction.apiFetch('/api/auth/login', {
      method: 'POST',
      body: { email: email, password: password }
    });

    var username = email.split('@')[0];
    var authData = {
      token: data.token,
      user: {
        fullName: username.replace(/[._-]+/g, ' '),
        username: username,
        email: email
      }
    };
    setStoredAuth(authData);
    return authData;
  }

  async function register(fullName, username, email, password) {
    var data = await window.CineJunction.apiFetch('/api/auth/register', {
      method: 'POST',
      body: { fullName: fullName, username: username, email: email, password: password }
    });

    var authData = {
      token: data.token,
      user: {
        fullName: fullName,
        username: username,
        email: email
      }
    };
    setStoredAuth(authData);
    return authData;
  }

  function logout() {
    clearStoredAuth();
    window.location.replace(getLoginPath());
  }

  function isAuthenticated() {
    var auth = getStoredAuth();
    return !!(auth && auth.token);
  }

  function attachPasswordToggles() {
    var toggleButtons = document.querySelectorAll('.pwd-toggle');
    toggleButtons.forEach(function (button) {
      button.addEventListener('click', function () {
        var targetId = button.getAttribute('data-toggle-password');
        var input = document.getElementById(targetId);
        if (!input) return;

        var isHidden = input.type === 'password';
        input.type = isHidden ? 'text' : 'password';
        button.textContent = isHidden ? '◉' : '◌';
        button.setAttribute('aria-label', isHidden ? 'Hide password' : 'Show password');
      });
    });
  }

  function attachStrengthMeters() {
    var strengthMeters = document.querySelectorAll('[data-strength-meter]');
    strengthMeters.forEach(function (meter) {
      var input = meter.parentElement && meter.parentElement.querySelector('input[type="password"]');
      var label = meter.querySelector('[data-strength-text]');
      var bar = meter.querySelector('.strength-meter__bar');
      if (!input || !label || !bar) return;

      input.addEventListener('input', function () {
        var value = input.value;
        var strength = 0;
        if (value.length >= 8) strength += 1;
        if (/[A-Z]/.test(value) && /[0-9]/.test(value)) strength += 1;
        if (/[^A-Za-z0-9]/.test(value)) strength += 1;

        var labels = ['Weak', 'Fair', 'Strong'];
        var levels = ['35%', '70%', '100%'];
        var colors = ['var(--cj-primary)', 'var(--cj-primary)', 'var(--text-primary)'];
        var labelText = labels[Math.min(strength - 1, labels.length - 1)] || labels[0];
        label.textContent = labelText;
        bar.style.width = levels[Math.min(strength - 1, levels.length - 1)] || levels[0];
        bar.style.background = colors[Math.min(strength - 1, colors.length - 1)] || colors[0];
      });
    });
  }

  function attachFormHandlers() {
    var forms = document.querySelectorAll('.auth-form');
    forms.forEach(function (form) {
      form.addEventListener('submit', function (event) {
        event.preventDefault();
        var message = form.querySelector('.form-message');
        var submitButton = form.querySelector('.auth-submit');

        if (form.dataset.authForm === 'login') {
          var email = form.querySelector('input[name="email"]').value.trim();
          var password = form.querySelector('input[name="password"]').value;

          if (!email || !password) {
            setFormMessage(form, 'Please enter your email and password.', true);
            return;
          }

          setFormLoading(form, true);
          setFormMessage(form, '', false);

          login(email, password)
            .then(function () {
              setFormMessage(form, 'Welcome back. Redirecting you to the app…', false);
              setTimeout(redirectToHome, 700);
            })
            .catch(function (error) {
              setFormMessage(form, error.message || 'Login failed. Please try again.', true);
              setFormLoading(form, false);
            });
          return;
        }

        if (form.dataset.authForm === 'register') {
          var name = form.querySelector('input[name="name"]').value.trim();
          var username = form.querySelector('input[name="username"]').value.trim();
          var email = form.querySelector('input[name="email"]').value.trim();
          var password = form.querySelector('input[name="password"]').value;
          var confirm = form.querySelector('input[name="confirm"]').value;

          if (!name || !username || !email || !password || !confirm) {
            setFormMessage(form, 'Please complete every field.', true);
            return;
          }

          if (password !== confirm) {
            setFormMessage(form, 'Passwords do not match.', true);
            return;
          }

          if (password.length < 8) {
            setFormMessage(form, 'Use at least 8 characters for your password.', true);
            return;
          }

          setFormLoading(form, true);
          setFormMessage(form, '', false);

          register(name, username, email, password)
            .then(function () {
              setFormMessage(form, 'Account ready. Taking you into CineJunction…', false);
              setTimeout(redirectToHome, 700);
            })
            .catch(function (error) {
              setFormMessage(form, error.message || 'Registration failed. Please try again.', true);
              setFormLoading(form, false);
            });
        }
      });
    });
  }

  function updateAuthUI() {
    var user = getStoredAuth();
    var profileButtons = document.querySelectorAll('.profile-pill');
    profileButtons.forEach(function (button) {
      var displayName = user && user.user && (user.user.fullName || user.user.username) ? (user.user.fullName || user.user.username) : 'User';
      var initial = displayName.charAt(0).toUpperCase();
      button.textContent = initial;
      button.setAttribute('aria-label', user ? ('Open profile menu for ' + displayName) : 'Open profile menu');
    });
  }

  function initAuth() {
    var pageName = getCurrentPageName();
    var isAuthPage = isPublicPage(pageName);

    if (!isAuthenticated() && isProtectedPage(pageName)) {
      redirectToLogin();
      return;
    }

    if (isAuthenticated() && isAuthPage) {
      redirectToHome();
      return;
    }

    if (pageName === 'index.html' && !isAuthenticated()) {
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
  window.CineJunction.login = login;
  window.CineJunction.register = register;
  window.CineJunction.logout = logout;
  window.CineJunction.isAuthenticated = isAuthenticated;
  window.CineJunction.updateAuthUI = updateAuthUI;

  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initAuth);
  } else {
    initAuth();
  }
})();
