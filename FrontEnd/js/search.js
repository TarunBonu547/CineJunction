(function () {
  const suggestions = [
    'Watchlist',
    'Community Picks',
    'Top Rated'
  ];

  function initSearch() {
    const searchInputs = Array.from(document.querySelectorAll('.search-shell input[type="search"]'));

    searchInputs.forEach((input) => {
      const shell = input.closest('.search-shell');
      const list = shell?.querySelector('.search-suggestions');

      input.addEventListener('focus', () => {
        shell?.classList.add('is-active');
        renderSuggestions(list, input.value);
      });

      input.addEventListener('input', () => renderSuggestions(list, input.value));
      input.addEventListener('blur', () => {
        window.setTimeout(() => shell?.classList.remove('is-active'), 120);
      });
    });
  }

  function renderSuggestions(list, value) {
    if (!list) return;
    const filtered = suggestions.filter((item) => item.toLowerCase().includes((value || '').toLowerCase()));
    const items = (filtered.length ? filtered : suggestions).slice(0, 5);
    list.innerHTML = items.map((item) => `<button class="suggestion-item" type="button">${item}</button>`).join('');
  }

  window.CineJunction = window.CineJunction || {};
  window.CineJunction.initSearch = initSearch;

  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initSearch);
  } else {
    initSearch();
  }
})();
