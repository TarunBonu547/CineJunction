// Small accessible mobile nav toggle
document.addEventListener('DOMContentLoaded', function () {
  const button = document.querySelector('.hamburger');
  const sheet = document.getElementById('mobile-sheet');
  const nav = document.getElementById('site-navigation');

  if (!button) return;

  button.addEventListener('click', () => {
    const expanded = button.getAttribute('aria-expanded') === 'true';
    button.setAttribute('aria-expanded', String(!expanded));
    if (sheet) {
      sheet.hidden = expanded; // toggle
    }
  });

  // keyboard: allow Enter/Space to toggle
  button.addEventListener('keydown', (e) => {
    if (e.key === 'Enter' || e.key === ' ') {
      e.preventDefault();
      button.click();
    }
  });

  // close mobile sheet on outside click
  document.addEventListener('click', (e) => {
    if (!sheet || sheet.hidden) return;
    const path = e.composedPath ? e.composedPath() : (e.path || []);
    if (!path.includes(sheet) && !path.includes(button)) {
      sheet.hidden = true;
      button.setAttribute('aria-expanded', 'false');
    }
  });
});
