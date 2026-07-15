(function () {
  function showToast(message) {
    const stack = document.querySelector('.toast-stack');
    if (!stack) {
      const host = document.createElement('div');
      host.className = 'toast-stack';
      document.body.appendChild(host);
    }

    const container = document.querySelector('.toast-stack');
    const toast = document.createElement('div');
    toast.className = 'toast';
    toast.textContent = message;
    container.appendChild(toast);

    window.setTimeout(() => {
      toast.remove();
    }, 2400);
  }

  function setActiveNav() {
    const pathname = window.location.pathname;
    const currentPage = pathname.split('/').pop() || 'index.html';
    const normalizedCurrentPage = currentPage.replace(/%20/g, ' ');

    document.querySelectorAll('.nav-link').forEach((link) => {
      const href = link.getAttribute('href') || '';
      const normalizedHref = href.split('/').pop() || '';
      const isActive = normalizedHref === normalizedCurrentPage || (normalizedCurrentPage === 'index.html' && normalizedHref === 'index.html');
      link.classList.toggle('is-active', isActive);
    });
  }

  function updateProfileChip() {
    const user = window.CineJunction?.getAuthState?.() || null;
    const button = document.querySelector('.profile-pill');
    if (!button) return;

    const initial = user?.name?.charAt(0)?.toUpperCase() || 'A';
    button.textContent = initial;
    button.setAttribute('aria-label', user ? `Open profile menu for ${user.name}` : 'Open profile menu');
  }

  function applyPageTransition() {
    const main = document.querySelector('main');
    if (!main) return;
    main.classList.add('page-transition');
    requestAnimationFrame(() => main.classList.add('is-ready'));
  }

  function initUtilities() {
    setActiveNav();
    updateProfileChip();
    applyPageTransition();

    document.querySelectorAll('[data-toast]').forEach((button) => {
      button.addEventListener('click', () => showToast(button.dataset.toast || 'Saved'));
    });
  }

  window.CineJunction = window.CineJunction || {};
  window.CineJunction.showToast = showToast;
  window.CineJunction.initUtilities = initUtilities;

  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initUtilities);
  } else {
    initUtilities();
  }
})();
