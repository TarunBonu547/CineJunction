(function () {
  window.CineJunction = window.CineJunction || {};

  function getUserAuth() {
    const raw = window.localStorage.getItem('cinejunction.auth');
    if (raw) {
      try { return JSON.parse(raw); } catch(e) {}
    }
    // Fallback to default user
    const def = window.CineJunction.mockData.defaultUser;
    window.localStorage.setItem('cinejunction.auth', JSON.stringify(def));
    return def;
  }

  function setUserAuth(user) {
    window.localStorage.setItem('cinejunction.auth', JSON.stringify(user));
  }

  function getList(key) {
    const raw = window.localStorage.getItem(key);
    if (raw) {
      try { return JSON.parse(raw); } catch(e) {}
    }
    return [];
  }

  function renderProfile() {
    const main = document.querySelector('main');
    if (!main) return;

    const user = getUserAuth();

    // Setup stats counts based on lists count
    const watchlistCount = getList('cinejunction.lists.watchlist').length;
    const favoritesCount = getList('cinejunction.lists.favorites').length;
    const recentlyCount = getList('cinejunction.lists.recently').length;

    // Build stats object
    const moviesCount = recentlyCount > 0 ? recentlyCount + 39 : user.stats.moviesWatched;
    const reviewsCount = user.stats.reviewsWritten;
    const hoursCount = recentlyCount > 0 ? 80 + recentlyCount * 2 : parseInt(user.stats.watchTime);

    const initial = user.name ? user.name.charAt(0).toUpperCase() : 'A';

    main.innerHTML = `
      <section class="info-card" style="margin-bottom: 24px; padding: 24px;">
        <div class="section-heading" style="margin-bottom: 0;">
          <div>
            <p class="section-label">Account Identity</p>
            <h1 class="section-title" style="margin: 0 0 8px;">Your Cinematic Profile</h1>
            <p class="body-text" style="margin: 0;">Manage your preferences, view stats, and customize your recommendations.</p>
          </div>
        </div>
      </section>

      <div class="movie-info-grid">
        <!-- Left Side: Customizations and Previews -->
        <div class="discovery-content" style="display: grid; gap: 24px;">
          
          <!-- Edit Profile Card -->
          <div class="info-card" style="padding: 24px;">
            <h3 class="movie-title" style="font-size: var(--fs-lg); margin-bottom: 18px;">Edit Profile</h3>
            <form id="edit-profile-form" style="display: grid; gap: 16px;">
              <div class="field-group" style="display: grid; gap: 6px;">
                <label class="input-label" style="font-size: var(--fs-xs); color: var(--text-secondary);">Full Name</label>
                <div class="input-shell" style="padding: 0 14px; border-radius: 999px;">
                  <input type="text" id="edit-name" value="${user.name || ''}" style="width: 100%; min-height: 44px; border: 0; outline: 0; background: transparent; color: var(--text-primary);" required />
                </div>
              </div>

              <div class="field-group" style="display: grid; gap: 6px;">
                <label class="input-label" style="font-size: var(--fs-xs); color: var(--text-secondary);">Username</label>
                <div class="input-shell" style="padding: 0 14px; border-radius: 999px;">
                  <input type="text" id="edit-username" value="${user.username || ''}" style="width: 100%; min-height: 44px; border: 0; outline: 0; background: transparent; color: var(--text-primary);" required />
                </div>
              </div>

              <div class="field-group" style="display: grid; gap: 6px;">
                <label class="input-label" style="font-size: var(--fs-xs); color: var(--text-secondary);">Bio</label>
                <div class="input-shell" style="padding: 10px 14px; border-radius: 16px;">
                  <textarea id="edit-bio" style="width: 100%; min-height: 70px; background: transparent; border: 0; outline: 0; color: var(--text-primary); resize: vertical; font-family: inherit; font-size: inherit; line-height: 1.5;">${user.bio || ''}</textarea>
                </div>
              </div>

              <div class="field-group" style="display: grid; gap: 8px;">
                <label class="input-label" style="font-size: var(--fs-xs); color: var(--text-secondary);">Favorite Genres (Select all that apply)</label>
                <div class="chip-row" id="genres-select-row">
                  <button type="button" class="chip ${user.favoriteGenres?.includes('Sci-Fi') ? 'is-active' : ''}" data-genre="Sci-Fi">Sci-Fi</button>
                  <button type="button" class="chip ${user.favoriteGenres?.includes('Drama') ? 'is-active' : ''}" data-genre="Drama">Drama</button>
                  <button type="button" class="chip ${user.favoriteGenres?.includes('Thriller') ? 'is-active' : ''}" data-genre="Thriller">Thriller</button>
                  <button type="button" class="chip ${user.favoriteGenres?.includes('Mystery') ? 'is-active' : ''}" data-genre="Mystery">Mystery</button>
                  <button type="button" class="chip ${user.favoriteGenres?.includes('Romance') ? 'is-active' : ''}" data-genre="Romance">Romance</button>
                  <button type="button" class="chip ${user.favoriteGenres?.includes('Action') ? 'is-active' : ''}" data-genre="Action">Action</button>
                  <button type="button" class="chip ${user.favoriteGenres?.includes('Fantasy') ? 'is-active' : ''}" data-genre="Fantasy">Fantasy</button>
                  <button type="button" class="chip ${user.favoriteGenres?.includes('Adventure') ? 'is-active' : ''}" data-genre="Adventure">Adventure</button>
                </div>
              </div>

              <button class="btn btn-primary" type="submit" style="justify-self: start; margin-top: 10px;">Save Profile Changes</button>
            </form>
          </div>

          <!-- Watchlist Preview Card -->
          <div class="info-card" style="padding: 24px;">
            <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px;">
              <h3 class="movie-title" style="font-size: var(--fs-lg);">Watchlist Preview</h3>
              <a class="section-link" href="watchlist.html" style="font-size: var(--fs-sm);">Open Watchlist (${watchlistCount})</a>
            </div>
            <div id="watchlist-preview-row" class="card-row" style="display: grid; grid-template-columns: repeat(auto-fill, minmax(200px, 1fr)); gap: 16px;"></div>
          </div>

          <!-- Recently Watched Card -->
          <div class="info-card" style="padding: 24px;">
            <h3 class="movie-title" style="font-size: var(--fs-lg); margin-bottom: 16px;">Recently Watched</h3>
            <div id="recently-preview-row" class="card-row" style="display: grid; grid-template-columns: repeat(auto-fill, minmax(200px, 1fr)); gap: 16px;"></div>
          </div>

        </div>

        <!-- Right Side: Profile Card & Sidebar -->
        <div class="discovery-sidebar" style="display: grid; gap: 24px; align-self: start;">
          
          <!-- Avatar Card -->
          <div class="info-card" style="padding: 24px; text-align: center; display: grid; justify-items: center; gap: 12px;">
            <div class="avatar" style="width: 80px; height: 80px; font-size: 2.2rem; font-weight: bold; color: var(--text-primary); border: 2px solid var(--cj-primary); border-radius: 50%; display: flex; align-items: center; justify-content: center; background: linear-gradient(135deg, var(--cj-primary), rgba(127,29,29,1));">
              ${initial}
            </div>
            <div>
              <h2 class="movie-title" style="font-size: var(--fs-2xl); margin: 0;">${user.name}</h2>
              <p class="body-text" style="font-size: var(--fs-sm); color: var(--text-secondary); margin: 4px 0 0;">@${user.username}</p>
            </div>
            <p class="body-text" style="font-size: var(--fs-xs); color: var(--text-secondary); margin: 0; word-break: break-all;">${user.email}</p>
            <p class="body-text" style="font-size: var(--fs-sm); line-height: 1.5; color: var(--text-primary); margin: 6px 0 0;">${user.bio || 'No bio written yet.'}</p>
            
            <div style="display: flex; flex-wrap: wrap; justify-content: center; gap: 6px; margin-top: 6px;">
              ${(user.favoriteGenres || []).map(g => `<span class="chip is-active" style="padding: 0 10px; min-height: 28px; font-size: var(--fs-xs); pointer-events: none;">${g}</span>`).join('')}
            </div>
          </div>

          <!-- User Stats Card -->
          <div class="info-card" style="padding: 20px;">
            <h3 class="movie-title" style="font-size: var(--fs-lg); margin-bottom: 12px;">Statistics</h3>
            <div class="hero-stats" style="display: grid; gap: 8px;">
              <div class="stat-pill"><span>Movies Watched</span><strong>${moviesCount}</strong></div>
              <div class="stat-pill"><span>Lists Created</span><strong>${watchlistCount > 0 ? 4 : 1}</strong></div>
              <div class="stat-pill"><span>Reviews Written</span><strong>${reviewsCount}</strong></div>
              <div class="stat-pill"><span>Watch Time</span><strong>${hoursCount}h</strong></div>
            </div>
          </div>

          <!-- Actions Card -->
          <div class="info-card" style="padding: 20px; display: grid; gap: 8px;">
            <a class="btn btn-outline" href="watchlist.html" style="text-align: center; justify-content: center;">Open Lists Panel</a>
            <a class="btn btn-outline" href="settings.html" style="text-align: center; justify-content: center;">Account Settings</a>
            <button class="btn btn-danger" id="profile-logout-btn" style="width: 100%;">Sign Out</button>
          </div>

        </div>
      </div>
    `;

    // Bind Favorite Genre Selectors
    const genreSelectRow = document.getElementById('genres-select-row');
    if (genreSelectRow) {
      genreSelectRow.querySelectorAll('.chip').forEach(btn => {
        btn.addEventListener('click', () => {
          btn.classList.toggle('is-active');
        });
      });
    }

    // Bind Form Save Submit
    const editForm = document.getElementById('edit-profile-form');
    if (editForm) {
      editForm.addEventListener('submit', (e) => {
        e.preventDefault();
        const updatedName = document.getElementById('edit-name').value.trim();
        const updatedUsername = document.getElementById('edit-username').value.trim();
        const updatedBio = document.getElementById('edit-bio').value.trim();

        // Get selected genres
        const selectedGenres = [];
        genreSelectRow.querySelectorAll('.chip.is-active').forEach(btn => {
          selectedGenres.push(btn.dataset.genre);
        });

        if (!updatedName || !updatedUsername) return;

        const currentUser = getUserAuth();
        currentUser.name = updatedName;
        currentUser.username = updatedUsername;
        currentUser.bio = updatedBio;
        currentUser.favoriteGenres = selectedGenres;

        setUserAuth(currentUser);
        renderProfile();

        // Update navbar chip
        const navPill = document.querySelector('.profile-pill');
        if (navPill) {
          navPill.textContent = updatedName.charAt(0).toUpperCase();
        }

        if (window.CineJunction.showToast) {
          window.CineJunction.showToast("Profile saved successfully");
        }
      });
    }

    // Bind Logout
    const logoutBtn = document.getElementById('profile-logout-btn');
    if (logoutBtn) {
      logoutBtn.addEventListener('click', () => {
        if (window.CineJunction.logout) {
          window.CineJunction.logout();
        }
      });
    }

    // Render Watchlist Preview (Limit 3)
    const watchlistIds = getList('cinejunction.lists.watchlist').slice(0, 3);
    const recentlyIds = getList('cinejunction.lists.recently').slice(0, 3);
    const movies = window.CineJunction.mockData.movies;

    const wPreviewRow = document.getElementById('watchlist-preview-row');
    if (wPreviewRow) {
      if (watchlistIds.length === 0) {
        wPreviewRow.innerHTML = `<div style="font-size: var(--fs-xs); color: var(--text-secondary); width: 100%;">Your Watchlist is empty.</div>`;
      } else {
        const filtered = movies.filter(m => watchlistIds.includes(m.id));
        wPreviewRow.innerHTML = filtered.map(movie => `
          <div class="movie-card" style="border: 1px solid var(--border); border-radius: 12px; padding: 8px; background: rgba(255,255,255,0.02);">
            <div style="aspect-ratio: 16/9; overflow: hidden; border-radius: 8px; margin-bottom: 6px;">
              <img src="${movie.posterUrl}" style="width: 100%; height: 100%; object-fit: cover;" />
            </div>
            <h4 class="movie-title" style="font-size: var(--fs-sm); white-space: nowrap; overflow: hidden; text-overflow: ellipsis; margin: 0;">${movie.title}</h4>
            <p class="body-text" style="font-size: var(--fs-xs); color: var(--text-secondary); margin: 2px 0 0;">${movie.genres[0]} • ${movie.year}</p>
          </div>
        `).join('');
      }
    }

    const rPreviewRow = document.getElementById('recently-preview-row');
    if (rPreviewRow) {
      if (recentlyIds.length === 0) {
        rPreviewRow.innerHTML = `<div style="font-size: var(--fs-xs); color: var(--text-secondary); width: 100%;">No recently watched titles.</div>`;
      } else {
        const filtered = movies.filter(m => recentlyIds.includes(m.id));
        rPreviewRow.innerHTML = filtered.map(movie => `
          <div class="movie-card" style="border: 1px solid var(--border); border-radius: 12px; padding: 8px; background: rgba(255,255,255,0.02);">
            <div style="aspect-ratio: 16/9; overflow: hidden; border-radius: 8px; margin-bottom: 6px;">
              <img src="${movie.posterUrl}" style="width: 100%; height: 100%; object-fit: cover;" />
            </div>
            <h4 class="movie-title" style="font-size: var(--fs-sm); white-space: nowrap; overflow: hidden; text-overflow: ellipsis; margin: 0;">${movie.title}</h4>
            <p class="body-text" style="font-size: var(--fs-xs); color: var(--text-secondary); margin: 2px 0 0;">${movie.genres[0]} • ${movie.year}</p>
          </div>
        `).join('');
      }
    }
  }

  function tryInit(attempts) {
    if (window.CineJunction?.mockData?.movies) {
      renderProfile();
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
