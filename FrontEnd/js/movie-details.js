(function () {
  function populateHeroFromData() {
    const script = document.getElementById('movie-data');
    if (!script) return;

    try {
      const data = JSON.parse(script.textContent);
      const movie = data.movie || {};

      const title = document.querySelector('[data-hero-title]');
      const tagline = document.querySelector('[data-hero-tagline]');
      const year = document.querySelector('[data-hero-year]');
      const runtime = document.querySelector('[data-hero-runtime]');
      const genre = document.querySelector('[data-hero-genre]');
      const language = document.querySelector('[data-hero-language]');
      const rating = document.querySelector('[data-hero-rating]');

      if (title && movie.title) title.textContent = movie.title;
      if (tagline && movie.tagline) tagline.textContent = movie.tagline;
      if (year && movie.year) year.textContent = movie.year;
      if (runtime && movie.runtime) runtime.textContent = movie.runtime;
      if (genre && movie.genre) genre.textContent = movie.genre;
      if (language && movie.language) language.textContent = movie.language;
      if (rating && movie.ageRating) rating.textContent = movie.ageRating;
    } catch (error) {
      console.warn('Movie data not available:', error);
    }
  }

  function initMovieDetails() {
    populateHeroFromData();
    if (window.CineJunction?.initGallery) {
      window.CineJunction.initGallery();
    }
    if (window.CineJunction?.initReviews) {
      window.CineJunction.initReviews();
    }
  }

  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initMovieDetails);
  } else {
    initMovieDetails();
  }
})();
