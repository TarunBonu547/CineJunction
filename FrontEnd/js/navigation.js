(function () {
  function initNavigation() {
    const button = document.querySelector('.hamburger');
    const sheet = document.getElementById('mobile-sheet');
    const profileButton = document.querySelector('[data-profile-toggle]');
    const profileDropdown = document.querySelector('.profile-dropdown');

    if (button) {
      button.addEventListener('click', () => {
        const expanded = button.getAttribute('aria-expanded') === 'true';
        button.setAttribute('aria-expanded', String(!expanded));
        if (sheet) {
          sheet.hidden = expanded;
        }
      });
    }

    if (profileButton && profileDropdown) {
      profileButton.addEventListener('click', () => {
        profileDropdown.classList.toggle('is-open');
      });
    }

    document.addEventListener('click', (event) => {
      if (!profileDropdown) return;
      const withinDropdown = profileDropdown.contains(event.target);
      if (!withinDropdown) {
        profileDropdown.classList.remove('is-open');
      }
    });
  }

  window.CineJunction = window.CineJunction || {};
  window.CineJunction.initNavigation = initNavigation;

  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initNavigation);
  } else {
    initNavigation();
  }
})();
