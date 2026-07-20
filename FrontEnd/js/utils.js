(function () {
  window.CineJunction = window.CineJunction || {};

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
    var user = window.CineJunction?.getAuthState?.() || null;
    var button = document.querySelector('.profile-pill');
    if (!button) return;

    var displayName = user && user.user && (user.user.fullName || user.user.username) ? (user.user.fullName || user.user.username) : 'User';
    var initial = displayName.charAt(0).toUpperCase();
    button.textContent = initial;
    button.setAttribute('aria-label', user ? ('Open profile menu for ' + displayName) : 'Open profile menu');
  }

  function applyPageTransition() {
    const main = document.querySelector('main');
    if (!main) return;
    main.classList.add('page-transition');
    requestAnimationFrame(() => main.classList.add('is-ready'));
  }

  function slugify(text) {
    return text
      .toString()
      .toLowerCase()
      .trim()
      .replace(/\s+/g, '-')
      .replace(/[^\w\-]+/g, '')
      .replace(/\-\-+/g, '-');
  }

  function initCardClicks() {
    document.addEventListener('click', (e) => {
      const card = e.target.closest('.movie-card');
      if (!card) return;

      // Ignore if clicking details page action buttons (bookmark, like, etc)
      if (e.target.closest('button') || e.target.closest('a')) {
        return;
      }

      const titleElement = card.querySelector('.movie-title');
      if (titleElement) {
        const titleText = titleElement.textContent.trim();
        const slug = slugify(titleText);
        const isInPages = window.location.pathname.includes('/pages/');
        const detailsPage = isInPages ? 'movie-details.html' : 'pages/movie-details.html';
        window.location.href = `${detailsPage}?id=${slug}`;
      }
    });
  }

  function initUtilities() {
    setActiveNav();
    updateProfileChip();
    applyPageTransition();
    initCardClicks();

    document.querySelectorAll('[data-toast]').forEach((button) => {
      button.addEventListener('click', () => showToast(button.dataset.toast || 'Saved'));
    });
  }

  window.CineJunction.showToast = showToast;
  window.CineJunction.initUtilities = initUtilities;
  window.CineJunction.slugify = slugify;

  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initUtilities);
  } else {
    initUtilities();
  }
})();
