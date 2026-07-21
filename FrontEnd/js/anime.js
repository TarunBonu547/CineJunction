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
    currentPage: 0,
    totalPages: 0,
    totalElements: 0,
    searchQuery: "",
    sort: "popularity,desc",
    genre: "",
    language: "",
    minRating: "",
    isLoading: false,
    error: null
  };

  function getSortValue(selectText) {
    switch (selectText) {
      case 'Trending': return 'popularity,desc';
      case 'Popularity': return 'popularity,desc';
      case 'Release Date': return 'releaseDate,desc';
      default: return 'popularity,desc';
    }
  }

  function getRatingFilter(chipText) {
    const match = chipText.match(/(\d+)/);
    return match ? match[1] : '';
  }

  function showLoading() {
    const grid = document.querySelector('.movie-grid');
    if (grid) {
      grid.innerHTML = '<p class="body-text section-loading" style="grid-column: 1 / -1; text-align: center; padding: 48px;">Loading…</p>';
    }
  }

  function showError(message) {
    const grid = document.querySelector('.movie-grid');
    if (grid) {
      grid.innerHTML = `
        <div class="empty-state" style="grid-column: 1 / -1;">
          <h4>Unable to load anime</h4>
          <p>${message || 'Please check your connection and try again.'}</p>
          <button class="btn btn-primary" type="button" onclick="window.CineJunction.animePage?.retry?.()">Retry</button>
        </div>
      `;
    }
  }

  function showEmpty(message) {
    const grid = document.querySelector('.movie-grid');
    if (grid) {
      grid.innerHTML = `
        <div class="empty-state" style="grid-column: 1 / -1;">
          <h4>No anime found</h4>
          <p>${message || 'Try adjusting your filters or search terms.'}</p>
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
            <button class="btn btn-outline watchlist-toggle-btn" type="button" data-movie-id="${movie.id}" title="Add to Watchlist" aria-label="Add to Watchlist">${inWatchlist ? '✓' : '+'}</button>
          </div>
        </div>
      </article>
    `;
  }

  function renderAnime(movies) {
    const grid = document.querySelector('.movie-grid');
    if (!grid) return;

    if (!movies || movies.length === 0) {
      showEmpty();
      return;
    }

    grid.innerHTML = movies.map(movie => buildMovieCard(movie)).join('');

    grid.querySelectorAll('.watchlist-toggle-btn').forEach(btn => {
      btn.addEventListener('click', (e) => {
        e.stopPropagation();
        const movieId = btn.dataset.movieId;
        const wasAdded = !WatchlistService.has(movieId);
        if (wasAdded) {
          WatchlistService.add(movieId);
          btn.textContent = '✓';
          btn.classList.add('is-active');
          if (window.CineJunction.showToast) window.CineJunction.showToast('Added to Watchlist');
        } else {
          WatchlistService.remove(movieId);
          btn.textContent = '+';
          btn.classList.remove('is-active');
          if (window.CineJunction.showToast) window.CineJunction.showToast('Removed from Watchlist');
        }
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

  function renderPagination(page, totalPages, totalElements) {
    const pagination = document.querySelector('.pagination');
    if (!pagination) return;

    if (totalPages <= 1) {
      pagination.innerHTML = '';
      return;
    }

    let pagesHtml = '';
    const maxVisible = 5;
    let startPage = Math.max(0, page - Math.floor(maxVisible / 2));
    let endPage = Math.min(totalPages - 1, startPage + maxVisible - 1);
    if (endPage - startPage + 1 < maxVisible) {
      startPage = Math.max(0, endPage - maxVisible + 1);
    }

    pagesHtml += `<button class="btn btn-ghost" type="button" data-page="${page - 1}" ${page === 0 ? 'disabled' : ''}>Previous</button>`;

    for (let i = startPage; i <= endPage; i++) {
      pagesHtml += `<button class="btn ${i === page ? 'btn-primary' : 'btn-outline'}" type="button" data-page="${i}">${i + 1}</button>`;
    }

    pagesHtml += `<button class="btn btn-ghost" type="button" data-page="${page + 1}" ${page >= totalPages - 1 ? 'disabled' : ''}>Next</button>`;

    pagination.innerHTML = `
      <div class="pagination__pages" aria-label="Page numbers">
        ${pagesHtml}
      </div>
    `;

    pagination.querySelectorAll('button[data-page]').forEach(btn => {
      btn.addEventListener('click', () => {
        const targetPage = parseInt(btn.dataset.page, 10);
        if (!isNaN(targetPage) && targetPage >= 0 && targetPage < totalPages) {
          state.currentPage = targetPage;
          fetchAnime();
        }
      });
    });
  }

  async function fetchAnime(reset = true) {
    if (state.isLoading) return;
    state.isLoading = true;
    state.error = null;

    if (reset) {
      state.currentPage = 0;
      showLoading();
    }

    try {
      const params = {
        sort: state.sort,
        page: state.currentPage,
        size: 12
      };

      if (state.genre) params.genre = state.genre;
      if (state.language) params.language = state.language;
      if (state.minRating) params.minRating = state.minRating;

      console.log('[Anime] Fetching...', params);
      const response = await window.CineJunction.movieService.getMovies(params);
      console.log('[Anime] API Success. Total elements:', response.totalElements);

      state.items = response.content || [];
      state.totalPages = response.totalPages || 0;
      state.totalElements = response.totalElements || 0;

      if (state.items.length === 0) {
        showEmpty('No anime match your current filters.');
      } else {
        renderAnime(state.items);
        renderPagination(state.currentPage, state.totalPages, state.totalElements);
      }

    } catch (error) {
      console.error('[Anime] API Error:', error);
      state.error = error;
      if (error.status === 401) {
        const isInPages = window.location.pathname.indexOf('/pages/') !== -1;
        window.location.href = (isInPages ? 'login.html' : 'pages/login.html');
      } else if (error.status === 404) {
        showEmpty('No anime found.');
      } else {
        showError(error.message || 'Network error. Please try again.');
      }
    } finally {
      state.isLoading = false;
    }
  }

  async function performSearch(query) {
    if (state.isLoading) return;
    state.isLoading = true;
    state.searchQuery = query;
    state.currentPage = 0;
    state.error = null;

    showLoading();

    try {
      console.log('[Anime] Searching for:', query);
      const response = await window.CineJunction.movieService.searchMovies(query, state.currentPage, 12);
      console.log('[Anime] Search API Success. Results:', response.totalElements);

      state.items = response.content || [];
      state.totalPages = response.totalPages || 0;
      state.totalElements = response.totalElements || 0;

      if (state.items.length === 0) {
        showEmpty('No anime match your search.');
      } else {
        renderAnime(state.items);
        renderPagination(state.currentPage, state.totalPages, state.totalElements);
      }

    } catch (error) {
      console.error('[Anime] Search API Error:', error);
      state.error = error;
      if (error.status === 401) {
        const isInPages = window.location.pathname.indexOf('/pages/') !== -1;
        window.location.href = (isInPages ? 'login.html' : 'pages/login.html');
      } else {
        showError(error.message || 'Search failed. Please try again.');
      }
    } finally {
      state.isLoading = false;
    }
  }

  function bindEvents() {
    const searchInput = document.getElementById('anime-search');
    const sortSelect = document.querySelector('.sort-shell select');
    const chipRows = document.querySelectorAll('.chip-row');

    let searchDebounce = null;

    if (searchInput) {
      searchInput.addEventListener('input', (e) => {
        const query = e.target.value.trim();
        window.clearTimeout(searchDebounce);
        searchDebounce = window.setTimeout(() => {
          if (query.length > 0) {
            performSearch(query);
          } else {
            state.searchQuery = '';
            fetchAnime();
          }
        }, 300);
      });
    }

    if (sortSelect) {
      sortSelect.addEventListener('change', (e) => {
        state.sort = getSortValue(e.target.value);
        fetchAnime();
      });
    }

    chipRows.forEach(row => {
      row.querySelectorAll('.chip').forEach(chip => {
        chip.addEventListener('click', () => {
          const group = chip.closest('.chip-row');
          if (!group) return;
          group.querySelectorAll('.chip').forEach(item => item.classList.remove('is-active'));
          chip.classList.add('is-active');

          const groupTitle = group.querySelector('h3')?.textContent?.toLowerCase() || '';
          const chipText = chip.textContent.trim();

          if (groupTitle.includes('genre')) {
            state.genre = chipText === 'All' ? '' : chipText;
          } else if (groupTitle.includes('language')) {
            state.language = chipText === 'All' ? '' : chipText;
          } else if (groupTitle.includes('rating')) {
            state.minRating = chipText === 'All' ? '' : getRatingFilter(chipText);
          }

          fetchAnime();
        });
      });
    });
  }

  async function initPage() {
    console.log('[Anime] Loading...');
    bindEvents();
    await fetchAnime();
    console.log('[Anime] Completed');
  }

  window.CineJunction.animePage = {
    init: initPage,
    retry: () => fetchAnime()
  };

  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initPage);
  } else {
    initPage();
  }
})();
