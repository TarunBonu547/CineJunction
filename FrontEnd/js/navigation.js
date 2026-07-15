(function () {
  window.CineJunction = window.CineJunction || {};

  function injectHeaderAndFooter() {
    const pathname = window.location.pathname;
    const isInPages = pathname.includes('/pages/');

    function getLink(path) {
      if (path === 'index.html') {
        return isInPages ? '../index.html' : 'index.html';
      }
      return isInPages ? path : 'pages/' + path;
    }

    const header = document.querySelector('.site-header');
    if (header) {
      header.innerHTML = `
        <div class="container header-inner">
          <button class="hamburger" aria-label="Open menu" aria-expanded="false" aria-controls="site-navigation">
            <svg width="22" height="14" viewBox="0 0 22 14" fill="none" xmlns="http://www.w3.org/2000/svg" aria-hidden="true">
              <rect y="1" width="22" height="2" rx="1" fill="#fff" />
              <rect y="6" width="22" height="2" rx="1" fill="#fff" />
              <rect y="11" width="22" height="2" rx="1" fill="#fff" />
            </svg>
          </button>

          <a class="brand" href="${getLink('index.html')}" aria-label="CineJunction Home">
            <span class="logo">CineJunction</span>
          </a>

          <nav id="site-navigation" class="site-nav" role="navigation" aria-label="Primary navigation">
            <ul class="nav-list">
              <li><a class="nav-link" href="${getLink('index.html')}">Home</a></li>
              <li><a class="nav-link" href="${getLink('movies.html')}">Movies</a></li>
              <li><a class="nav-link" href="${getLink('tv-shows.html')}">TV Shows</a></li>
              <li><a class="nav-link" href="${getLink('anime.html')}">Anime</a></li>
              <li><a class="nav-link" href="${getLink('trending.html')}">Trending</a></li>
              <li><a class="nav-link" href="${getLink('community.html')}">Community</a></li>
              <li><a class="nav-link" href="${getLink('watchlist.html')}">Watchlist</a></li>
            </ul>
          </nav>

          <div class="header-actions">
            <div class="search-shell" aria-label="Search site">
              <span class="search-icon" aria-hidden="true">⌕</span>
              <input type="search" id="global-search-input" placeholder="Search titles, people, moods..." aria-label="Search site" />
              <div class="search-suggestions" aria-label="Search suggestions"></div>
            </div>

            <a class="btn btn-icon" href="${getLink('settings.html')}" aria-label="Settings">⎋</a>
            
            <div class="profile-dropdown" data-profile-toggle>
              <button class="btn btn-icon profile-pill" aria-label="Open profile menu">A</button>
              <div class="profile-menu" role="menu">
                <a href="${getLink('profile.html')}" role="menuitem">Profile</a>
                <a href="${getLink('watchlist.html')}" role="menuitem">Watchlist</a>
                <a href="${getLink('settings.html')}" role="menuitem">Settings</a>
                <button id="global-logout-btn" role="menuitem">Logout</button>
              </div>
            </div>
          </div>
        </div>

        <div class="mobile-sheet" id="mobile-sheet" hidden>
          <nav class="mobile-nav" aria-label="Mobile primary navigation">
            <ul>
              <li><a class="nav-link" href="${getLink('index.html')}">Home</a></li>
              <li><a class="nav-link" href="${getLink('movies.html')}">Movies</a></li>
              <li><a class="nav-link" href="${getLink('tv-shows.html')}">TV Shows</a></li>
              <li><a class="nav-link" href="${getLink('anime.html')}">Anime</a></li>
              <li><a class="nav-link" href="${getLink('trending.html')}">Trending</a></li>
              <li><a class="nav-link" href="${getLink('community.html')}">Community</a></li>
              <li><a class="nav-link" href="${getLink('watchlist.html')}">Watchlist</a></li>
            </ul>
          </nav>
          <div class="mobile-actions">
            <a href="${getLink('profile.html')}">Profile</a>
            <a href="${getLink('settings.html')}">Settings</a>
          </div>
        </div>
      `;
    }

    const footer = document.querySelector('.site-footer');
    if (footer) {
      footer.innerHTML = `
        <div class="container footer-grid">
          <div>
            <a class="brand" href="${getLink('index.html')}" aria-label="CineJunction Home">
              <span class="logo">CineJunction</span>
            </a>
            <p class="body-text">A premium destination for cinematic discovery with editorial taste, intelligent curation, and calm confidence.</p>
          </div>
          <div>
            <h3 class="movie-title">Quick Links</h3>
            <ul class="footer-list">
              <li><a href="${getLink('about.html')}">About</a></li>
              <li><a href="${getLink('contact.html')}">Contact</a></li>
              <li><a href="${getLink('help.html')}">Help & Support</a></li>
            </ul>
          </div>
          <div>
            <h3 class="movie-title">Legal</h3>
            <ul class="footer-list">
              <li><a href="${getLink('privacy.html')}">Privacy Policy</a></li>
              <li><a href="${getLink('terms.html')}">Terms of Service</a></li>
              <li><a href="${getLink('community.html')}">Community</a></li>
            </ul>
          </div>
          <div>
            <h3 class="movie-title">Contact</h3>
            <ul class="footer-list">
              <li><a href="mailto:hello@cinejunction.com">hello@cinejunction.com</a></li>
              <li><a href="tel:+14155550188">+1 (415) 555-0188</a></li>
              <li><span style="color: var(--text-secondary);">© 2026 CineJunction</span></li>
            </ul>
          </div>
        </div>
      `;
    }
  }

  function initNavigation() {
    injectHeaderAndFooter();

    const button = document.querySelector('.hamburger');
    const sheet = document.getElementById('mobile-sheet');
    const profileDropdown = document.querySelector('.profile-dropdown');
    const profileButton = profileDropdown?.querySelector('.profile-pill');
    const logoutBtn = document.getElementById('global-logout-btn');

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
      profileButton.addEventListener('click', (e) => {
        e.stopPropagation();
        profileDropdown.classList.toggle('is-open');
      });
    }

    document.addEventListener('click', (event) => {
      if (profileDropdown && !profileDropdown.contains(event.target)) {
        profileDropdown.classList.remove('is-open');
      }
      if (sheet && !sheet.hidden && button && !button.contains(event.target) && !sheet.contains(event.target)) {
        sheet.hidden = true;
        button.setAttribute('aria-expanded', 'false');
      }
    });

    if (logoutBtn) {
      logoutBtn.addEventListener('click', () => {
        if (window.CineJunction.logout) {
          window.CineJunction.logout();
        }
      });
    }

    // Connect Search bar input enter key
    const searchInput = document.getElementById('global-search-input');
    if (searchInput) {
      searchInput.addEventListener('keydown', (e) => {
        if (e.key === 'Enter') {
          const query = searchInput.value.trim();
          if (query) {
            const pathname = window.location.pathname;
            const isInPages = pathname.includes('/pages/');
            const searchPageLink = isInPages ? 'search.html' : 'pages/search.html';
            window.location.href = `${searchPageLink}?q=${encodeURIComponent(query)}`;
          }
        }
      });
    }

    // Run active link highlighting and profile updates
    if (window.CineJunction.initUtilities) {
      window.CineJunction.initUtilities();
    }
  }

  window.CineJunction = window.CineJunction || {};
  window.CineJunction.initNavigation = initNavigation;

  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initNavigation);
  } else {
    initNavigation();
  }
})();
