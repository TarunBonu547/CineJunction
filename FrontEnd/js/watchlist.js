(function () {
  window.CineJunction = window.CineJunction || {};

  const POSTER_PLACEHOLDER = 'data:image/svg+xml,' + encodeURIComponent('<svg xmlns="http://www.w3.org/2000/svg" width="300" height="450" viewBox="0 0 300 450"><rect width="100%" height="100%" fill="%2318181B"/><text x="50%" y="50%" text-anchor="middle" dy=".3em" fill="%23A1A1AA" font-family="sans-serif" font-size="14">No Image</text></svg>');

  const WatchlistService = {
    get() {
      try { return JSON.parse(window.localStorage.getItem('cinejunction.lists.watchlist') || '[]'); }
      catch (e) { return []; }
    },
    add(movieId) {
      const list = this.get();
      if (!list.includes(movieId)) {
        list.push(movieId);
        window.localStorage.setItem('cinejunction.lists.watchlist', JSON.stringify(list));
      }
    },
    remove(movieId) {
      const list = this.get().filter(id => id !== movieId);
      window.localStorage.setItem('cinejunction.lists.watchlist', JSON.stringify(list));
    },
    has(movieId) { return this.get().includes(movieId); }
  };

  const state = {
    items: [],
    isLoading: false,
    error: null
  };

  function showLoading() {
    const grid = document.querySelector('.watchlist-grid');
    if (grid) {
      grid.innerHTML = '<p class="body-text section-loading" style="grid-column: 1 / -1; text-align: center; padding: 48px;">Loading…</p>';
    }
  }

  function showError(message) {
    const grid = document.querySelector('.watchlist-grid');
    if (grid) {
      grid.innerHTML = `
        <div class="empty-state" style="grid-column: 1 / -1;">
          <h4>Unable to load watchlist</h4>
          <p>${message || 'Please check your connection and try again.'}</p>
          <button class="btn btn-primary" type="button" onclick="window.CineJunction.watchlistPage?.retry?.()">Retry</button>
        </div>
      `;
    }
  }

  function showEmpty(message) {
    const grid = document.querySelector('.watchlist-grid');
    if (grid) {
      grid.innerHTML = `
        <div class="empty-state" style="grid-column: 1 / -1;">
          <h4>Your watchlist is empty</h4>
          <p>${message || 'Start adding movies and TV shows to see them here.'}</p>
          <a class="btn btn-primary" href="movies.html">Discover Movies</a>
        </div>
      `;
    }
  }

  function formatYear(dateInput) {
    if (!dateInput) return 'Unknown';
    const date = new Date(dateInput);
    if (isNaN(date.getTime())) return 'Unknown';
    return date.getFullYear();
  }

  function buildMovieCard(movie) {
    const year = formatYear(movie.releaseDate);
    const rating = movie.averageRating ? movie.averageRating.toFixed(1) : 'N/A';
    const inWatchlist = WatchlistService.has(movie.id);

    return `
      <article class="movie-card discovery-card" data-movie-id="${movie.id}">
        <div class="movie-card__image">
          <img src="${movie.posterUrl || POSTER_PLACEHOLDER}" alt="${movie.title} poster" loading="lazy" />
        </div>
        <div class="movie-card__body">
          <div class="movie-card__meta">
            <span class="meta-pill">IMDb ${rating}</span>
          </div>
          <h3 class="movie-title">${movie.title}</h3>
          <p class="body-text">${year}</p>
          <div class="card-actions" style="margin-top: 12px;">
            <button class="btn btn-outline" type="button" data-details-id="${movie.id}">Details</button>
            <button class="btn btn-outline watchlist-toggle-btn" type="button" data-movie-id="${movie.id}" title="Remove from Watchlist" aria-label="Remove from Watchlist">✓</button>
          </div>
        </div>
      </article>
    `;
  }

  function renderWatchlist(movies) {
    const grid = document.querySelector('.watchlist-grid');
    if (!grid) return;

    if (!movies || movies.length === 0) {
      showEmpty('Movies you add to your watchlist will appear here.');
      return;
    }

    grid.innerHTML = movies.map(movie => buildMovieCard(movie)).join('');

    grid.querySelectorAll('.watchlist-toggle-btn').forEach(btn => {
      btn.addEventListener('click', (e) => {
        e.stopPropagation();
        const movieId = btn.dataset.movieId;
        WatchlistService.remove(movieId);
        if (window.CineJunction.showToast) window.CineJunction.showToast('Removed from Watchlist');
        loadWatchlist();
      });
    });

    grid.querySelectorAll('[data-details-id]').forEach(btn => {
      btn.addEventListener('click', (e) => {
        e.stopPropagation();
        const id = btn.dataset.detailsId;
        window.location.href = `movie-details.html?id=${id}`;
      });
    });
  }

  async function loadWatchlist() {
    if (state.isLoading) return;
    state.isLoading = true;
    state.error = null;

    showLoading();

    try {
      const ids = WatchlistService.get();
      console.log('[Watchlist] Loading... IDs:', ids);

      if (ids.length === 0) {
        state.items = [];
        renderWatchlist([]);
        return;
      }

      const promises = ids.map(id => window.CineJunction.movieService.getMovieById(id).catch(err => {
        if (err.status === 404) {
          console.warn('[Watchlist] Movie not found, skipping ID:', id);
          WatchlistService.remove(id);
          return null;
        }
        throw err;
      }));

      const results = await Promise.all(promises);
      const movies = results.filter(movie => movie !== null);
      state.items = movies;

      console.log('[Watchlist] API Success. Loaded', movies.length, 'of', ids.length, 'items');
      renderWatchlist(state.items);

    } catch (error) {
      console.error('[Watchlist] API Error:', error);
      state.error = error;
      if (error.status === 401) {
        const isInPages = window.location.pathname.indexOf('/pages/') !== -1;
        window.location.href = (isInPages ? 'login.html' : 'pages/login.html');
      } else {
        showError(error.message || 'Network error. Please try again.');
      }
    } finally {
      state.isLoading = false;
    }
  }

  async function initPage() {
    console.log('[Watchlist] Loading...');
    await loadWatchlist();
    console.log('[Watchlist] Completed');
  }

  window.CineJunction.watchlistPage = {
    init: initPage,
    retry: loadWatchlist
  };

  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initPage);
  } else {
    initPage();
  }
})();
