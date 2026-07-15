(function () {
  window.CineJunction = window.CineJunction || {};

  // Chip filter interactions (single-select per chip-row)
  function initChips() {
    const chips = Array.from(document.querySelectorAll('.chip-row .chip'));
    chips.forEach((chip) => {
      chip.addEventListener('click', () => {
        const group = chip.closest('.chip-row');
        if (!group) return;
        group.querySelectorAll('.chip').forEach((item) => item.classList.remove('is-active'));
        chip.classList.add('is-active');
        // Fire re-filter
        renderFilteredCards();
      });
    });
  }

  // Search input focus effects
  function initSearchFocus() {
    const searchInputs = Array.from(document.querySelectorAll('input[type="search"]'));
    searchInputs.forEach((input) => {
      input.addEventListener('focus', () => input.parentElement?.classList.add('is-focused'));
      input.addEventListener('blur', () => input.parentElement?.classList.remove('is-focused'));
    });
  }

  // Determine which page type we're on
  function getPageType() {
    const pathname = window.location.pathname;
    const page = pathname.split('/').pop() || '';
    if (page.includes('tv-show')) return 'tv';
    if (page.includes('anime')) return 'anime';
    if (page.includes('trending')) return 'trending';
    return 'movie';
  }

  // Render movie cards into the page's .movie-grid container
  function renderFilteredCards() {
    const grid = document.querySelector('.movie-grid');
    if (!grid) return;

    const allMovies = window.CineJunction?.mockData?.movies;
    if (!allMovies) return;

    // Determine active genre chip filter
    const activeChip = document.querySelector('.chip-row .chip.is-active');
    const activeGenre = activeChip ? activeChip.textContent.trim() : 'All';

    const pageType = getPageType();

    let filtered = allMovies.filter(m => {
      // Type filter based on page
      if (pageType === 'tv') return m.type === 'tv';
      if (pageType === 'anime') return m.type === 'anime';
      if (pageType === 'trending') return true; // Show all on trending
      return m.type === 'movie' || !m.type; // Default: movies
    });

    // Genre filter
    if (activeGenre && activeGenre !== 'All') {
      filtered = filtered.filter(m => m.genres && m.genres.some(g => g.toLowerCase() === activeGenre.toLowerCase()));
    }

    if (filtered.length === 0) {
      grid.innerHTML = `
        <div class="empty-state" style="grid-column: 1 / -1;">
          <h4>No titles found</h4>
          <p>Try a different genre filter or explore another category.</p>
        </div>
      `;
      return;
    }

    grid.innerHTML = filtered.map(movie => `
      <article class="movie-card discovery-card" data-movie-id="${movie.id}">
        <div class="movie-card__image">
          <img src="${movie.posterUrl}" alt="${movie.title} poster" loading="lazy" />
        </div>
        <div class="movie-card__body">
          <div class="movie-card__meta">
            <span class="meta-pill">IMDb ${movie.imdbRating}</span>
            <span class="meta-pill">CJ ${movie.cjRating}</span>
          </div>
          <h3 class="movie-title">${movie.title}</h3>
          <p class="body-text">${movie.genres ? movie.genres.join(' • ') : ''} • ${movie.year}</p>
          <div class="card-actions" style="margin-top: 12px;">
            <button class="btn btn-outline" type="button" data-details-id="${movie.id}">Details</button>
            <button class="btn btn-outline watchlist-toggle-btn" type="button" data-movie-id="${movie.id}" title="Add to Watchlist">+</button>
          </div>
        </div>
      </article>
    `).join('');

    // Bind quick-add to watchlist
    grid.querySelectorAll('.watchlist-toggle-btn').forEach(btn => {
      btn.addEventListener('click', (e) => {
        e.stopPropagation();
        const mid = btn.dataset.movieId;
        const key = 'cinejunction.lists.watchlist';
        let list = [];
        try { list = JSON.parse(window.localStorage.getItem(key) || '[]'); } catch(err) {}
        
        const idx = list.indexOf(mid);
        if (idx === -1) {
          list.push(mid);
          btn.textContent = '✓';
          btn.classList.add('is-active');
          if (window.CineJunction.showToast) window.CineJunction.showToast('Added to Watchlist');
        } else {
          list.splice(idx, 1);
          btn.textContent = '+';
          btn.classList.remove('is-active');
          if (window.CineJunction.showToast) window.CineJunction.showToast('Removed from Watchlist');
        }
        window.localStorage.setItem(key, JSON.stringify(list));
      });
    });

    // Bind details buttons
    grid.querySelectorAll('[data-details-id]').forEach(btn => {
      btn.addEventListener('click', (e) => {
        e.stopPropagation();
        const id = btn.dataset.detailsId;
        window.location.href = `movie-details.html?id=${id}`;
      });
    });

    // Update watchlist toggle button state based on current list
    const watchlistKey = 'cinejunction.lists.watchlist';
    let currentList = [];
    try { currentList = JSON.parse(window.localStorage.getItem(watchlistKey) || '[]'); } catch(err) {}

    grid.querySelectorAll('.watchlist-toggle-btn').forEach(btn => {
      const mid = btn.dataset.movieId;
      if (currentList.includes(mid)) {
        btn.textContent = '✓';
        btn.classList.add('is-active');
      }
    });
  }

  function initMoviesPage() {
    initChips();
    initSearchFocus();

    // If there's a movie grid, populate it from mock data
    // Guard against deferred script ordering race: poll until mockData is ready
    if (document.querySelector('.movie-grid')) {
      function tryRender(attempts) {
        if (window.CineJunction?.mockData?.movies) {
          renderFilteredCards();
        } else if (attempts > 0) {
          window.setTimeout(() => tryRender(attempts - 1), 50);
        }
      }
      tryRender(20); // up to ~1 second of retries
    }
  }

  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initMoviesPage);
  } else {
    initMoviesPage();
  }
})();
