(function () {
  window.CineJunction = window.CineJunction || {};

  var POSTER_PLACEHOLDER = 'data:image/svg+xml,' + encodeURIComponent('<svg xmlns="http://www.w3.org/2000/svg" width="300" height="450" viewBox="0 0 300 450"><rect width="100%" height="100%" fill="%2318181B"/><text x="50%" y="50%" text-anchor="middle" dy=".3em" fill="%23A1A1AA" font-family="sans-serif" font-size="14">No Image</text></svg>');

  function getDetailsLink(movieId) {
    var isInPages = window.location.pathname.indexOf('/pages/') !== -1;
    return (isInPages ? 'movie-details.html' : 'pages/movie-details.html') + '?id=' + encodeURIComponent(movieId);
  }

  function navigateToDetails(movieId, event) {
    if (event.target.closest('button') || event.target.closest('a')) {
      return;
    }
    window.location.href = getDetailsLink(movieId);
  }

  function renderMovieCard(movie) {
    var card = document.createElement('article');
    card.className = 'movie-card';
    card.setAttribute('data-movie-id', movie.id);

    var posterUrl = movie.posterUrl || POSTER_PLACEHOLDER;
    var rating = movie.averageRating ? movie.averageRating.toFixed(1) : 'N/A';
    var year = '';
    if (movie.releaseDate) {
      var date = new Date(movie.releaseDate);
      if (!isNaN(date.getTime())) {
        year = date.getFullYear();
      }
    }

    card.innerHTML =
      '<div class="movie-card__image">' +
        '<img src="' + posterUrl + '" alt="' + movie.title + ' poster" loading="lazy" />' +
      '</div>' +
      '<div class="movie-card__body">' +
        '<div class="movie-card__meta">' +
          '<span class="meta-pill">IMDb ' + rating + '</span>' +
          (year ? '<span class="meta-pill">' + year + '</span>' : '') +
        '</div>' +
        '<h3 class="movie-title">' + movie.title + '</h3>' +
      '</div>';

    var img = card.querySelector('img');
    if (img) {
      img.addEventListener('error', function () {
        this.src = POSTER_PLACEHOLDER;
      });
    }

    card.addEventListener('click', function (event) {
      event.stopPropagation();
      navigateToDetails(movie.id, event);
    });

    return card;
  }

  function renderMovieCards(container, movies) {
    var row = document.createElement('div');
    row.className = 'card-row';
    movies.forEach(function (movie) {
      row.appendChild(renderMovieCard(movie));
    });
    container.innerHTML = '';
    container.appendChild(row);
  }

  function showSectionError(container, message) {
    container.innerHTML = '<p class="body-text" style="color: var(--cj-primary);">' + message + '</p>';
  }

  async function loadHero() {
    console.log("[home.js] loadHero started");

    var heroTitle = document.getElementById('featured-title');
    var heroDescription = document.querySelector('.hero-description');
    var heroPoster = document.querySelector('.hero-poster img');
    var heroMeta = document.querySelector('.meta-list');
    var heroActions = document.querySelector('.hero-actions');
    var hero = document.querySelector('.hero');

    if (heroTitle) heroTitle.textContent = 'Loading…';

    try {
        console.log("[home.js] Fetching movie list...");

        var response = await window.CineJunction.movieService.getMovies({
            sort: 'averageRating,desc',
            size: 1,
            page: 0
        });

        console.log("[home.js] Movies response:", response);

        var movies = response && response.content ? response.content : [];

        if (movies.length === 0) {
            throw new Error("No movies returned");
        }

        var topMovie = movies[0];
        console.log("[home.js] Top movie:", topMovie);

        var details = await window.CineJunction.movieService.getMovieById(topMovie.id);

        console.log("[home.js] Movie details:", details);

        console.log("[home.js] Updating hero title");

        if (heroTitle) heroTitle.textContent = details.title || 'CineJunction';

        console.log("[home.js] Updating description");

        if (heroDescription)
            heroDescription.textContent = details.overview || 'No overview available.';

        console.log("[home.js] Updating poster");

        if (heroPoster) {
            heroPoster.src = details.posterUrl || POSTER_PLACEHOLDER;
            heroPoster.alt = (details.title || "Movie") + " poster";
        }

        console.log("[home.js] Updating metadata");

        if (heroMeta) {
            heroMeta.innerHTML =
                '<li><span class="meta-pill">IMDb ' +
                (details.averageRating || 'N/A') +
                '</span></li>';
        }

        console.log("[home.js] Hero updated successfully");

    } catch (error) {
        console.error("[home.js] loadHero failed:", error);
    }
}

  async function loadSection(containerId, sort) {
    var container = document.getElementById(containerId);
    if (!container) return;

    container.innerHTML = '<p class="body-text section-loading">Loading…</p>';

    try {
      var response = await window.CineJunction.movieService.getMovies({
        sort: sort,
        size: 12,
        page: 0
      });

      var movies = response && response.content ? response.content : [];
      if (movies.length === 0) {
        container.innerHTML = '<p class="body-text">No movies available.</p>';
        return;
      }

      renderMovieCards(container, movies);

    } catch (error) {
      console.error('Failed to load section:', error);
      showSectionError(container, 'Failed to load movies. Please try again later.');
    }
  }

  async function initHome() {
    console.log("[home.js] initHome started");

    var pageName = window.location.pathname.split('/').pop() || 'index.html';
    console.log("[home.js] pageName =", pageName);

    if (pageName !== 'index.html') {
        console.log("[home.js] Not on home page, exiting");
        return;
    }

    try {
        console.log("[home.js] Before loadHero()");
        await loadHero();
        console.log("[home.js] After loadHero()");

        console.log("[home.js] Before Continue Watching");
        await loadSection('continue-watching-row', 'popularity,desc');
        console.log("[home.js] After Continue Watching");

        console.log("[home.js] Before Trending");
        await loadSection('trending-row', 'popularity,desc');
        console.log("[home.js] After Trending");

        console.log("[home.js] Before Top Rated");
        await loadSection('top-rated-row', 'averageRating,desc');
        console.log("[home.js] After Top Rated");

        console.log("[home.js] initHome completed");

    } catch (error) {
        console.error("[home.js] initHome failed:", error);
    }
}

  window.CineJunction.initHome = initHome;

console.log("[home.js] loaded");
console.log("[home.js] readyState =", document.readyState);

if (document.readyState === "loading") {
    console.log("[home.js] Registering DOMContentLoaded listener");

    document.addEventListener("DOMContentLoaded", function () {
        console.log("[home.js] DOMContentLoaded fired");
        initHome();
    });
} else {
    console.log("[home.js] Calling initHome immediately");
    initHome();
}})();