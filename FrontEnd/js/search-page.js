(function () {
  window.CineJunction = window.CineJunction || {};

  function performSearch(query) {
    if (!query) return [];

    const normQuery = query.toLowerCase().trim();

    if (!window.CineJunction.movieService) {
      return [];
    }

    return [];
  }

  function renderSearchResults(query) {
    const resultsArea = document.getElementById('search-results-area');
    if (!resultsArea) return;

    if (!query) {
      resultsArea.innerHTML = `
        <div class="empty-state">
          <h4>Search is ready for your catalogue</h4>
          <p>Start typing above to search for titles, directors, actors, or genres.</p>
        </div>
      `;
      return;
    }

    resultsArea.innerHTML = `
      <div class="empty-state">
        <h4>No results found for "${query}"</h4>
        <p>Try searching for genres, cast members, or directors.</p>
      </div>
    `;
  }

  function initSearchPage() {
    const main = document.querySelector('main');
    if (!main) return;

    main.innerHTML = `
      <section class="info-card" style="margin-bottom: 24px; padding: 24px;">
        <p class="section-label">Search Hub</p>
        <h1 class="section-title" style="margin: 0 0 10px;">Explore the Entire Collection</h1>
        <p class="body-text" style="margin: 0 0 14px;">Find movies, series, anime, actors, or genres.</p>
        <div class="input-shell" style="border-radius: 999px; padding: 0 16px;">
          <span class="search-icon" style="color: var(--text-secondary); margin-right: 8px;">⌕</span>
          <input type="search" id="page-search-input" placeholder="Search titles, genres, actors, directors..." style="width: 100%; min-height: 48px; border: 0; outline: 0; background: transparent; color: var(--text-primary);" />
        </div>
      </section>

      <div id="search-results-area" style="min-height: 200px;"></div>
    `;

    const urlParams = new URLSearchParams(window.location.search);
    const initialQuery = urlParams.get('q') || '';

    const pageSearchInput = document.getElementById('page-search-input');
    if (pageSearchInput) {
      pageSearchInput.value = initialQuery;

      pageSearchInput.addEventListener('input', () => {
        const query = pageSearchInput.value.trim();
        renderSearchResults(query);

        const newUrl = `${window.location.pathname}?q=${encodeURIComponent(query)}`;
        window.history.replaceState({ path: newUrl }, '', newUrl);
      });
    }

    renderSearchResults(initialQuery);
  }

  function initSearchPageDirect() {
    initSearchPage();
  }

  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initSearchPageDirect);
  } else {
    initSearchPageDirect();
  }
})();
