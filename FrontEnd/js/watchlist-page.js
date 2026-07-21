(function () {
  window.CineJunction = window.CineJunction || {};

  const LISTS = {
    favorites: 'cinejunction.lists.favorites',
    watchlist: 'cinejunction.lists.watchlist',
    recently: 'cinejunction.lists.recently',
    custom: 'cinejunction.lists.custom'
  };

  function getList(key) {
    const raw = window.localStorage.getItem(key);
    if (raw) {
      try { return JSON.parse(raw); } catch(e) {}
    }
    return [];
  }

  function setList(key, val) {
    window.localStorage.setItem(key, JSON.stringify(val));
  }

  let activeTab = 'watchlist';
  let activeCustomListName = null;

  function renderListContent() {
    const contentArea = document.getElementById('lists-content-area');
    if (!contentArea) return;

    let movieIds = [];
    let isTopRated = false;
    let isCustom = false;

    if (activeTab === 'watchlist') {
      movieIds = getList(LISTS.watchlist);
    } else if (activeTab === 'favorites') {
      movieIds = getList(LISTS.favorites);
    } else if (activeTab === 'recently') {
      movieIds = getList(LISTS.recently);
    } else if (activeTab === 'top-rated') {
      isTopRated = true;
    } else if (activeTab === 'custom') {
      isCustom = true;
    }

    if (isCustom) {
      renderCustomListsView(contentArea);
      return;
    }

    if (!movieIds || movieIds.length === 0) {
      contentArea.innerHTML = `
        <div class="empty-state">
          <h4>No saved titles</h4>
          <p>This list is currently empty. Explore the catalogue to save stories.</p>
          <a class="btn btn-primary" href="movies.html" style="justify-self: center;">Discover Movies</a>
        </div>
      `;
      return;
    }

    contentArea.innerHTML = `
      <div class="movie-grid">
        ${movieIds.map(id => `
          <article class="movie-card discovery-card" data-movie-id="${id}">
            <div class="movie-card__image">
              <div style="aspect-ratio: 2/3; background: var(--surface); border-radius: 8px;"></div>
            </div>
            <div class="movie-card__body">
              <div class="movie-card__meta">
                <span class="meta-pill">ID ${id}</span>
              </div>
              <h3 class="movie-title">Saved Title</h3>
              <p class="body-text">Open details to view</p>
              <div class="card-actions" style="margin-top: 12px;">
                <button class="btn btn-outline" type="button" onclick="window.location.href='movie-details.html?id=${id}'">Details</button>
                <button class="btn btn-danger remove-item-btn" type="button" data-movie-id="${id}">Remove</button>
              </div>
            </div>
          </article>
        `).join('')}
      </div>
    `;

    contentArea.querySelectorAll('.remove-item-btn').forEach(btn => {
      btn.addEventListener('click', (e) => {
        e.stopPropagation();
        const mid = btn.dataset.movieId;
        let listKey = null;
        if (activeTab === 'watchlist') listKey = LISTS.watchlist;
        if (activeTab === 'favorites') listKey = LISTS.favorites;
        if (activeTab === 'recently') listKey = LISTS.recently;

        if (listKey) {
          let current = getList(listKey);
          current = current.filter(id => id !== mid);
          setList(listKey, current);
          renderListContent();
          if (window.CineJunction.showToast) {
            window.CineJunction.showToast("Removed from list");
          }
        }
      });
    });
  }

  function renderCustomListsView(container) {
    const customLists = getList(LISTS.custom);
    const listNames = Object.keys(customLists);

    if (activeCustomListName) {
      const movieIds = customLists[activeCustomListName] || [];

      let innerContent = '';
      if (movieIds.length === 0) {
        innerContent = `
          <div class="empty-state" style="border: 0; padding: 20px 0;">
            <h4>This list is empty</h4>
            <p>Go to movie details to add titles to this custom list.</p>
          </div>
        `;
      } else {
        innerContent = `
          <div class="movie-grid">
            ${movieIds.map(id => `
              <article class="movie-card discovery-card" data-movie-id="${id}">
                <div class="movie-card__image">
                  <div style="aspect-ratio: 2/3; background: var(--surface); border-radius: 8px;"></div>
                </div>
                <div class="movie-card__body">
                  <div class="movie-card__meta">
                    <span class="meta-pill">ID ${id}</span>
                  </div>
                  <h3 class="movie-title">Saved Title</h3>
                  <p class="body-text">Open details to view</p>
                  <div class="card-actions" style="margin-top: 12px;">
                    <button class="btn btn-outline" type="button" onclick="window.location.href='movie-details.html?id=${id}'">Details</button>
                    <button class="btn btn-danger remove-custom-item-btn" type="button" data-movie-id="${id}">Remove</button>
                  </div>
                </div>
              </article>
            `).join('')}
          </div>
        `;
      }

      container.innerHTML = `
        <div style="margin-bottom: 20px; display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 12px;">
          <div>
            <button class="btn btn-ghost" id="back-to-custom-lists" style="padding-left: 0;">← Back to Lists</button>
            <h2 class="section-title" style="margin-top: 8px;">List: ${activeCustomListName}</h2>
          </div>
          <button class="btn btn-danger" id="delete-custom-list-btn">Delete List</button>
        </div>
        ${innerContent}
      `;

      document.getElementById('back-to-custom-lists').addEventListener('click', () => {
        activeCustomListName = null;
        renderListContent();
      });

      document.getElementById('delete-custom-list-btn').addEventListener('click', () => {
        if (confirm(`Are you sure you want to delete the list "${activeCustomListName}"?`)) {
          const lists = getList(LISTS.custom);
          delete lists[activeCustomListName];
          setList(LISTS.custom, lists);
          activeCustomListName = null;
          renderListContent();
          if (window.CineJunction.showToast) {
            window.CineJunction.showToast("Custom list deleted");
          }
        }
      });

      container.querySelectorAll('.remove-custom-item-btn').forEach(btn => {
        btn.addEventListener('click', (e) => {
          e.stopPropagation();
          const mid = btn.dataset.movieId;
          const lists = getList(LISTS.custom);
          lists[activeCustomListName] = (lists[activeCustomListName] || []).filter(id => id !== mid);
          setList(LISTS.custom, lists);
          renderCustomListsView(container);
          if (window.CineJunction.showToast) {
            window.CineJunction.showToast("Removed from list");
          }
        });
      });

      return;
    }

    let listContent = '';
    if (listNames.length === 0) {
      listContent = `
        <div class="empty-state" style="border: 0;">
          <h4>No custom lists created</h4>
          <p>Create a custom list to group your favorite cinematic worlds together.</p>
        </div>
      `;
    } else {
      listContent = `
        <div style="display: grid; grid-template-columns: repeat(auto-fill, minmax(280px, 1fr)); gap: 16px;">
          ${listNames.map(name => {
            const count = customLists[name].length;
            return `
              <div class="info-card custom-list-item-card" data-list-name="${name}" style="padding: 20px; display: grid; gap: 8px; cursor: pointer; border: 1px solid var(--border); transition: border-color 150ms;" onmouseover="this.style.borderColor='rgba(229,9,20,0.4)'" onmouseout="this.style.borderColor='var(--border)'">
                <h4 class="movie-title" style="font-size: var(--fs-lg);">${name}</h4>
                <p class="body-text" style="font-size: var(--fs-sm); color: var(--text-secondary); margin: 0;">${count} titles saved</p>
                <div style="display: flex; justify-content: flex-end; margin-top: 10px;">
                  <span class="btn btn-outline" style="pointer-events: none;">View List</span>
                </div>
              </div>
            `;
          }).join('')}
        </div>
      `;
    }

    container.innerHTML = `
      <div style="margin-bottom: 20px; display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 12px;">
        <h2 class="section-title">Your Custom Lists</h2>
        <button class="btn btn-primary" id="create-custom-list-btn">+ Create List</button>
      </div>
      ${listContent}
    `;

    container.querySelectorAll('.custom-list-item-card').forEach(card => {
      card.addEventListener('click', () => {
        activeCustomListName = card.dataset.listName;
        renderListContent();
      });
    });

    document.getElementById('create-custom-list-btn').addEventListener('click', () => {
      const name = prompt("Enter list name:");
      if (name && name.trim()) {
        const lists = getList(LISTS.custom);
        if (lists[name.trim()]) {
          alert("A list with this name already exists.");
        } else {
          lists[name.trim()] = [];
          setList(LISTS.custom, lists);
          renderListContent();
          if (window.CineJunction.showToast) {
            window.CineJunction.showToast("Custom list created!");
          }
        }
      }
    });
  }

  function initWatchlistPage() {
    const main = document.querySelector('main');
    if (!main) return;

    main.innerHTML = `
      <section class="info-card" style="margin-bottom: 24px; padding: 24px;">
        <div style="display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 16px;">
          <div>
            <p class="section-label">Personal Lists</p>
            <h1 class="section-title" style="margin: 0 0 8px;">Your saved stories are waiting.</h1>
            <p class="body-text" style="margin: 0;">Organize and manage your custom playlists, favorites, and history.</p>
          </div>
        </div>
      </section>

      <div class="tabs-row" id="watchlist-tabs" style="margin-bottom: 24px;">
        <button class="tab tab-active" data-tab="watchlist">Watch Later</button>
        <button class="tab" data-tab="favorites">Favorites</button>
        <button class="tab" data-tab="recently">Recently Watched</button>
        <button class="tab" data-tab="top-rated">Top Rated</button>
        <button class="tab" data-tab="custom">Custom Lists</button>
      </div>

      <div id="lists-content-area" style="min-height: 200px;"></div>
    `;

    const tabRow = document.getElementById('watchlist-tabs');
    tabRow.querySelectorAll('.tab').forEach(btn => {
      btn.style.cursor = 'pointer';
      btn.style.border = '0';
      btn.style.fontFamily = 'inherit';
      btn.style.fontSize = 'var(--fs-sm)';
      btn.style.fontWeight = 'var(--weight-medium)';

      btn.addEventListener('click', () => {
        tabRow.querySelectorAll('.tab').forEach(t => t.classList.remove('tab-active'));
        btn.classList.add('tab-active');
        activeTab = btn.dataset.tab;
        activeCustomListName = null;
        renderListContent();
      });
    });

    renderListContent();
  }

  function initWatchlistPageDirect() {
    initWatchlistPage();
  }

  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initWatchlistPageDirect);
  } else {
    initWatchlistPageDirect();
  }
})();
