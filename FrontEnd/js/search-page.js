(function () {
  window.CineJunction = window.CineJunction || {};

  function performSearch(query) {
    if (!query) return [];

    const normQuery = query.toLowerCase().trim();
    const movies = window.CineJunction.mockData.movies;

    return movies.filter(m => {
      const matchTitle = m.title && m.title.toLowerCase().includes(normQuery);
      const matchDirector = m.director && m.director.toLowerCase().includes(normQuery);
      const matchTagline = m.tagline && m.tagline.toLowerCase().includes(normQuery);
      const matchGenre = m.genres && m.genres.some(g => g.toLowerCase().includes(normQuery));
      
      const matchCast = m.cast && m.cast.some(actor => actor.name && actor.name.toLowerCase().includes(normQuery));

      return matchTitle || matchDirector || matchTagline || matchGenre || matchCast;
    });
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

    const results = performSearch(query);

    if (results.length === 0) {
      resultsArea.innerHTML = `
        <div class="empty-state">
          <h4>No results found for "${query}"</h4>
          <p>Try searching for genres (like "Sci-Fi" or "Drama"), cast members, or directors.</p>
        </div>
      `;
      return;
    }

    resultsArea.innerHTML = `
      <div style="margin-bottom: 16px; color: var(--text-secondary); font-size: var(--fs-sm);">
        Found ${results.length} result(s) for "${query}"
      </div>
      <div class="movie-grid">
        ${results.map(movie => `
          <article class="movie-card discovery-card" data-movie-id="${movie.id}">
            <div class="movie-card__image">
              <img src="${movie.posterUrl}" alt="${movie.title} poster" />
            </div>
            <div class="movie-card__body">
              <div class="movie-card__meta">
                <span class="meta-pill">IMDb ${movie.imdbRating}</span>
                <span class="meta-pill">CJ ${movie.cjRating}</span>
              </div>
              <h3 class="movie-title">${movie.title}</h3>
              <p class="body-text">${movie.genres.join(' • ')} • ${movie.year}</p>
              <div class="card-actions" style="margin-top: 12px;">
                <button class="btn btn-outline" type="button" onclick="window.location.href='movie-details.html?id=${movie.id}'">Details</button>
              </div>
            </div>
          </article>
        `).join('')}
      </div>
    `;
  }

  function initSearchPage() {
    const main = document.querySelector('main');
    if (!main) return;

    // Replace the main area with the custom search hub grid
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

    // Extract query from URL
    const urlParams = new URLSearchParams(window.location.search);
    const initialQuery = urlParams.get('q') || '';

    const pageSearchInput = document.getElementById('page-search-input');
    if (pageSearchInput) {
      pageSearchInput.value = initialQuery;

      // Handle live query input
      pageSearchInput.addEventListener('input', () => {
        const query = pageSearchInput.value.trim();
        renderSearchResults(query);
        
        // Push state quietly to update url without reload
        const newUrl = `${window.location.pathname}?q=${encodeURIComponent(query)}`;
        window.history.replaceState({ path: newUrl }, '', newUrl);
      });
    }

    renderSearchResults(initialQuery);
  }

  function tryInit(attempts) {
    if (window.CineJunction?.mockData?.movies) {
      initSearchPage();
    } else if (attempts > 0) {
      window.setTimeout(() => tryInit(attempts - 1), 50);
    }
  }

  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', () => tryInit(20));
  } else {
    tryInit(20);
  }
})();
