(function () {
  window.CineJunction = window.CineJunction || {};

  const POSTER_PLACEHOLDER = 'data:image/svg+xml,' + encodeURIComponent('<svg xmlns="http://www.w3.org/2000/svg" width="300" height="450" viewBox="0 0 300 450"><rect width="100%" height="100%" fill="%2318181B"/><text x="50%" y="50%" text-anchor="middle" dy=".3em" fill="%23A1A1AA" font-family="sans-serif" font-size="14">No Image</text></svg>');
  const BACKDROP_PLACEHOLDER = 'data:image/svg+xml,' + encodeURIComponent('<svg xmlns="http://www.w3.org/2000/svg" width="1800" height="800" viewBox="0 0 1800 800"><rect width="100%" height="100%" fill="%2318181B"/><text x="50%" y="50%" text-anchor="middle" dy=".3em" fill="%23A1A1AA" font-family="sans-serif" font-size="14">No Backdrop</text></svg>');

  let currentMovieId = null;
  let currentUserRating = null;
  let ratingStats = null;
  let currentMovie = null;
  let isSubmittingRating = false;

  function getQueryParam(param) {
    const urlParams = new URLSearchParams(window.location.search);
    return urlParams.get(param);
  }

  function getMovieId() {
    const id = getQueryParam('id');
    if (!id) {
      console.error('[MovieDetails] Missing movie id in query string');
      renderError('Movie not found');
      return null;
    }
    return id;
  }

  async function fetchMovie(movieId) {
    console.log('[MovieDetails] Loading movie... id:', movieId);
    try {
      const movie = await window.CineJunction.movieService.getMovieById(movieId);
      console.log('[MovieDetails] API Success');
      return movie;
    } catch (error) {
      console.error('[MovieDetails] API Error:', error);
      if (error.status === 404) {
        renderError('Movie not found');
      } else if (error.status === 401) {
        redirectToLogin();
      } else {
        renderError('Failed to load movie details. Please try again later.');
      }
      return null;
    }
  }

  async function loadRatingData(movieId) {
    if (!window.CineJunction.isAuthenticated || !window.CineJunction.isAuthenticated()) {
      renderRatings(null, null);
      return;
    }

    try {
      const [stats, ratings] = await Promise.all([
        window.CineJunction.ratingService.getMovieRatingStats(movieId),
        window.CineJunction.ratingService.getMovieRatings(movieId)
      ]);
      ratingStats = stats;

      const auth = window.CineJunction.getAuthState ? window.CineJunction.getAuthState() : null;
      let profileUserId = null;
      if (auth && auth.user && auth.user.id) {
        profileUserId = auth.user.id;
      } else if (window.CineJunction.profileService) {
  try {
    const profile = await window.CineJunction.profileService.getCurrentUser();

    console.log("===== Profile Debug =====");
    console.log("Profile Response:", profile);

    profileUserId = profile.data.id;

  } catch (e) {
    console.error("Profile API Error:", e);
  }
}

      console.log("===== Rating Debug =====");
console.log("Profile User ID:", profileUserId);
console.log("Ratings Response:", ratings);

const userRating = ratings.find(
    r => profileUserId && r.userId === profileUserId
) || null;

console.log("Matched User Rating:", userRating);

currentUserRating = userRating;

renderRatings(userRating, stats);
    } catch (error) {
      console.error('[MovieDetails] Rating API Error:', error);
      renderRatings(null, null);
    }
  }

  function redirectToLogin() {
    const pathname = window.location.pathname;
    const isInPages = pathname.indexOf('/pages/') !== -1;
    const loginPath = isInPages ? 'login.html' : 'pages/login.html';
    window.location.href = loginPath;
  }

  function renderError(message) {
    const titleEl = document.querySelector('[data-hero-title]');
    const taglineEl = document.querySelector('[data-hero-tagline]');
    const overviewEl = document.querySelector('[aria-labelledby="story-title"] .body-text');
    const backdropImg = document.querySelector('.movie-hero__backdrop img');
    const posterImg = document.querySelector('.movie-hero__poster img');

    if (titleEl) titleEl.textContent = message;
    if (taglineEl) taglineEl.textContent = '';
    if (overviewEl) overviewEl.textContent = '';

    if (backdropImg) {
      backdropImg.src = BACKDROP_PLACEHOLDER;
      backdropImg.alt = '';
    }
    if (posterImg) {
      posterImg.src = POSTER_PLACEHOLDER;
      posterImg.alt = '';
    }

    const personGrid = document.querySelector('.person-grid');
    if (personGrid) personGrid.innerHTML = '';

    const galleryGrid = document.querySelector('.gallery-grid');
    if (galleryGrid) galleryGrid.innerHTML = '';

    const reviewList = document.getElementById('review-list');
    if (reviewList) reviewList.innerHTML = '';

    const insightGrid = document.querySelector('.insight-grid');
    if (insightGrid) insightGrid.innerHTML = '';

    const availabilityGrid = document.querySelector('.availability-grid');
    if (availabilityGrid) availabilityGrid.innerHTML = '';

    const cardRow = document.querySelector('.card-row');
    if (cardRow) cardRow.innerHTML = '';

    const ratingsGrid = document.querySelector('.ratings-grid');
    if (ratingsGrid) ratingsGrid.innerHTML = '';
  }

  function renderHero(movie) {
    console.log('[MovieDetails] Rendering Hero');
    const backdropImg = document.querySelector('.movie-hero__backdrop img');
    const posterImg = document.querySelector('.movie-hero__poster img');
    const titleEl = document.querySelector('[data-hero-title]');
    const taglineEl = document.querySelector('[data-hero-tagline]');
    const yearEl = document.querySelector('[data-hero-year]');
    const runtimeEl = document.querySelector('[data-hero-runtime]');
    const genreEl = document.querySelector('[data-hero-genre]');
    const languageEl = document.querySelector('[data-hero-language]');
    const ratingEl = document.querySelector('[data-hero-rating]');

    if (backdropImg) {
      backdropImg.src = movie.backdropUrl || BACKDROP_PLACEHOLDER;
      backdropImg.alt = movie.title ? movie.title + ' backdrop' : '';
    }

    if (posterImg) {
      posterImg.src = movie.posterUrl || POSTER_PLACEHOLDER;
      posterImg.alt = movie.title ? movie.title + ' poster' : '';
    }

    if (titleEl) titleEl.textContent = movie.title || 'N/A';

    if (taglineEl) taglineEl.textContent = movie.overview || 'No overview available.';

    if (yearEl) {
      let releaseYear = 'N/A';
      if (movie.releaseDate) {
        const date = new Date(movie.releaseDate);
        if (!isNaN(date.getTime())) {
          releaseYear = date.getFullYear();
        }
      }
      yearEl.textContent = releaseYear;
    }

    if (runtimeEl) {
      let runtimeText = 'N/A';
      if (movie.runtime) {
        const hours = Math.floor(movie.runtime / 60);
        const minutes = movie.runtime % 60;
        if (hours > 0) {
          runtimeText = hours + 'h ' + (minutes > 0 ? minutes + 'm' : '');
        } else {
          runtimeText = minutes + 'm';
        }
      }
      runtimeEl.textContent = runtimeText;
    }

    if (genreEl) {
      const genres = movie.genres && movie.genres.length > 0 ? movie.genres[0] : 'N/A';
      genreEl.textContent = genres;
    }

    if (languageEl) languageEl.textContent = movie.language || 'N/A';

    if (ratingEl) ratingEl.textContent = movie.adult !== undefined ? (movie.adult ? 'R' : 'G') : 'N/A';
  }

  function renderHeroStats(movie, stats) {
    const statsEls = document.querySelectorAll('.hero-stat strong');
    let avg = 'N/A';
    let total = 'N/A';
    if (stats) {
      avg = stats.averageRating ? stats.averageRating.toFixed(1) : 'N/A';
      total = stats.totalRatings !== null && stats.totalRatings !== undefined ? stats.totalRatings : 'N/A';
    } else if (movie.averageRating) {
      avg = movie.averageRating.toFixed(1);
    }

    if (statsEls.length >= 4) {
      statsEls[0].textContent = avg;
      statsEls[1].textContent = total;
      statsEls[2].textContent = 'N/A';
      statsEls[3].textContent = 'N/A';
    }
  }

  function renderStoryline(movie) {
    const storyText = document.querySelector('[aria-labelledby="story-title"] .body-text');
    if (storyText) storyText.textContent = movie.overview || 'No overview available.';

    const detailList = document.querySelector('.detail-list');
    if (detailList) {
      detailList.innerHTML = `
        <div role="listitem"><span>Director</span><strong>N/A</strong></div>
        <div role="listitem"><span>Writers</span><strong>N/A</strong></div>
        <div role="listitem"><span>Producers</span><strong>N/A</strong></div>
        <div role="listitem"><span>Production Company</span><strong>N/A</strong></div>
        <div role="listitem"><span>Budget</span><strong>${movie.budget ? '$' + movie.budget.toLocaleString() : 'N/A'}</strong></div>
        <div role="listitem"><span>Box Office</span><strong>${movie.revenue ? '$' + movie.revenue.toLocaleString() : 'N/A'}</strong></div>
        <div role="listitem"><span>Country</span><strong>N/A</strong></div>
        <div role="listitem"><span>Languages</span><strong>${movie.language || 'N/A'}</strong></div>
        <div role="listitem"><span>Awards</span><strong>N/A</strong></div>
        <div role="listitem"><span>Filming Locations</span><strong>N/A</strong></div>
      `;
    }
  }

  function renderKeyFacts(movie) {
    const facts = document.querySelectorAll('.fact-list strong');
    if (facts.length >= 6) {
      facts[0].textContent = movie.genres && movie.genres.length > 0 ? movie.genres.join(' • ') : 'N/A';
      facts[1].textContent = movie.releaseDate ? formatDate(movie.releaseDate) : 'N/A';
      facts[2].textContent = movie.runtime ? formatRuntime(movie.runtime) : 'N/A';
      facts[3].textContent = 'N/A';
      facts[4].textContent = movie.adult !== undefined ? (movie.adult ? 'R' : 'G') : 'N/A';
      facts[5].textContent = movie.language || 'N/A';
    }
  }

  function renderCast() {
    console.log('[MovieDetails] Rendering Cast');
    const personGrid = document.querySelector('.person-grid');
    if (personGrid) {
      personGrid.innerHTML = '<p class="body-text" style="grid-column: 1 / -1; text-align: center; color: var(--text-secondary);">No cast information available.</p>';
    }
  }

  function renderMediaGallery() {
    const galleryGrid = document.querySelector('.gallery-grid');
    if (galleryGrid) {
      galleryGrid.innerHTML = '<p class="body-text" style="grid-column: 1 / -1; text-align: center; color: var(--text-secondary);">No media gallery available.</p>';
    }
  }

  function renderRatings(userRating, stats) {
    console.log('[MovieDetails] Rendering Ratings');
    const ratingsGrid = document.querySelector('.ratings-grid');
    if (!ratingsGrid) return;

    let avgRating = 'N/A';
    let totalRatings = 'N/A';
    let distribution = [0, 0, 0, 0, 0];
    if (stats) {
      avgRating = stats.averageRating ? stats.averageRating.toFixed(1) : 'N/A';
      totalRatings = stats.totalRatings !== null && stats.totalRatings !== undefined ? stats.totalRatings : 'N/A';
      if (stats.ratingDistribution) {
        for (let i = 1; i <= 5; i++) {
          distribution[i - 1] = stats.ratingDistribution[i] || 0;
        }
      }
    }
    const total = distribution.reduce((a, b) => a + b, 0);
    const percentages = distribution.map(v => total > 0 ? Math.round((v / total) * 100) : 0);

    const isAuthenticated = window.CineJunction.isAuthenticated && window.CineJunction.isAuthenticated();
    let userRatingHtml = '';
    if (isAuthenticated) {
      const currentValue = userRating ? Math.ceil(userRating.rating / 2) : 0;
      userRatingHtml = `
        <div style="grid-column: 1 / -1; margin-top: 18px; padding-top: 18px; border-top: 1px solid var(--border);">
          <p class="section-label" style="margin-bottom: 10px;">Your Rating</p>
          <div class="rating-widget" style="display: flex; align-items: center; gap: 10px; flex-wrap: wrap;">
            <div class="star-rating" data-rating-value="${currentValue}" style="display: flex; gap: 6px; font-size: 1.6rem; cursor: pointer;">
              ${[1,2,3,4,5].map(star => `<button class="star-btn" type="button" data-value="${star}" aria-label="Rate ${star} out of 10" style="background: 0; border: 0; color: ${star * 2 <= currentValue ? 'var(--cj-primary)' : 'var(--text-secondary)'}; cursor: pointer; font-size: inherit; padding: 0;">★</button>`).join('')}
            </div>
            <span class="body-text" style="color: var(--text-secondary);">${currentValue > 0 ? currentValue + '/10' : 'Not rated'}</span>
            <button class="btn btn-outline" id="submit-rating-btn" type="button" style="font-size: var(--fs-sm); padding: 6px 14px;">${currentValue > 0 ? 'Update Rating' : 'Rate'}</button>
            ${currentValue > 0 ? `<button class="btn btn-outline" id="remove-rating-btn" type="button" style="font-size: var(--fs-sm); padding: 6px 14px; color: var(--cj-primary);">Remove</button>` : ''}
          </div>
          <p id="rating-message" class="body-text" style="margin-top: 8px; font-size: var(--fs-sm);"></p>
        </div>
      `;
    } else {
      userRatingHtml = `
        <div style="grid-column: 1 / -1; margin-top: 18px; padding-top: 18px; border-top: 1px solid var(--border);">
          <p class="body-text" style="color: var(--text-secondary);">Sign in to rate this movie.</p>
        </div>
      `;
    }

    ratingsGrid.innerHTML = `
      <article class="ratings-card">
        <div class="rating-overview">
          <div>
            <p class="section-label">CineJunction Score</p>
            <h3 class="hero-title">${avgRating}</h3>
          </div>
          <div class="score-pill">${totalRatings !== 'N/A' ? totalRatings + ' ratings' : ''}</div>
        </div>
        <ul class="score-list" aria-label="External ratings">
          <li><span>Average Rating</span><strong>${avgRating}/10</strong></li>
          <li><span>Total Ratings</span><strong>${totalRatings}</strong></li>
          <li><span>Your Rating</span><strong>${userRating ? userRating.rating + '/10' : 'N/A'}</strong></li>
        </ul>
        ${userRatingHtml}
      </article>
      <article class="ratings-card">
        <h3 class="movie-title">Rating Breakdown</h3>
        <ul class="rating-chart" aria-label="Score distribution">
          <li><span>★★★★★</span><div class="bar"><i style="width: ${percentages[4]}%"></i></div><strong>${percentages[4]}%</strong></li>
          <li><span>★★★★</span><div class="bar"><i style="width: ${percentages[3]}%"></i></div><strong>${percentages[3]}%</strong></li>
          <li><span>★★★</span><div class="bar"><i style="width: ${percentages[2]}%"></i></div><strong>${percentages[2]}%</strong></li>
          <li><span>★★</span><div class="bar"><i style="width: ${percentages[1]}%"></i></div><strong>${percentages[1]}%</strong></li>
          <li><span>★</span><div class="bar"><i style="width: ${percentages[0]}%"></i></div><strong>${percentages[0]}%</strong></li>
        </ul>
      </article>
    `;

    bindRatingEvents();
  }

  function bindRatingEvents() {
    const starRating = document.querySelector('.star-rating');
    const submitBtn = document.getElementById('submit-rating-btn');
    const removeBtn = document.getElementById('remove-rating-btn');
    const messageEl = document.getElementById('rating-message');

    let selectedValue = currentUserRating ? currentUserRating.rating : 0;

if (starRating) {

      starRating.querySelectorAll('.star-btn').forEach(btn => {
        btn.addEventListener('click', () => {
          selectedValue = parseInt(btn.dataset.value, 10)*2;
          starRating.querySelectorAll('.star-btn').forEach(b => {
            b.style.color = (parseInt(b.dataset.value, 10) * 2) <= selectedValue
    ? 'var(--cj-primary)'
    : 'var(--text-secondary)';
          });
          const span = starRating.nextElementSibling;
          if (span && span.classList.contains('body-text')) {
            span.textContent = selectedValue + '/10';
          }
        });

        btn.addEventListener('mouseenter', () => {
          const hoverVal = parseInt(btn.dataset.value, 10) * 2;
          starRating.querySelectorAll('.star-btn').forEach(b => {
            const val = parseInt(b.dataset.value, 10);
            b.style.color = val <= hoverVal ? 'var(--cj-primary)' : 'var(--text-secondary)';
          });
        });

        btn.addEventListener('mouseleave', () => {
          starRating.querySelectorAll('.star-btn').forEach(b => {
            b.style.color = parseInt(b.dataset.value, 10) <= selectedValue ? 'var(--cj-primary)' : 'var(--text-secondary)';
          });
        });
      });
    }

    if (submitBtn) {
      submitBtn.addEventListener('click', async () => {
        if (isSubmittingRating) return;
        if (!selectedValue || selectedValue < 1 || selectedValue > 10) {
          if (messageEl) { messageEl.textContent = 'Please select a rating value.'; messageEl.style.color = 'var(--cj-primary)'; }
          return;
        }

        isSubmittingRating = true;
        submitBtn.disabled = true;
        submitBtn.textContent = currentUserRating ? 'Updating...' : 'Submitting...';
        if (messageEl) { messageEl.textContent = ''; }

        try {
          if (currentUserRating) {
            const updated = await window.CineJunction.ratingService.updateRating(currentUserRating.id, selectedValue);
            currentUserRating = updated;
            if (messageEl) { messageEl.textContent = 'Rating updated successfully'; messageEl.style.color = '#4ade80'; }
          } else {
            const created = await window.CineJunction.ratingService.createRating({ movieId: currentMovieId, rating: selectedValue });
            currentUserRating = created;
            if (messageEl) { messageEl.textContent = 'Rating submitted successfully'; messageEl.style.color = '#4ade80'; }
          }
          await loadRatingData(currentMovieId);
          renderHeroStats(currentMovie, ratingStats);
        } catch (error) {
          console.error('[MovieDetails] Rating Error:', error);
          if (messageEl) { messageEl.textContent = error.message || 'Failed to submit rating.'; messageEl.style.color = 'var(--cj-primary)'; }
        } finally {
          isSubmittingRating = false;
          submitBtn.disabled = false;
          submitBtn.textContent = currentUserRating ? 'Update Rating' : 'Rate';
        }
      });
    }

    if (removeBtn && currentUserRating) {
      removeBtn.addEventListener('click', async () => {
        if (isSubmittingRating) return;
        if (!confirm('Are you sure you want to remove your rating?')) return;

        isSubmittingRating = true;
        removeBtn.disabled = true;
        removeBtn.textContent = 'Removing...';
        if (messageEl) { messageEl.textContent = ''; }

        try {
          await window.CineJunction.ratingService.deleteRating(currentUserRating.id);
          currentUserRating = null;
          if (messageEl) { messageEl.textContent = 'Rating removed'; messageEl.style.color = '#4ade80'; }
          await loadRatingData(currentMovieId);
        } catch (error) {
          console.error('[MovieDetails] Remove Rating Error:', error);
          if (messageEl) { messageEl.textContent = error.message || 'Failed to remove rating.'; messageEl.style.color = 'var(--cj-primary)'; }
        } finally {
          isSubmittingRating = false;
          removeBtn.disabled = false;
          removeBtn.textContent = 'Remove';
        }
      });
    }
  }

  function renderReviews() {
    const reviewList = document.getElementById('review-list');
    if (reviewList) {
      reviewList.innerHTML = '<p class="body-text" style="text-align: center; color: var(--text-secondary);">No reviews available.</p>';
    }
  }

  function renderInsights() {
    const insightGrid = document.querySelector('.insight-grid');
    if (insightGrid) {
      insightGrid.innerHTML = '<p class="body-text" style="grid-column: 1 / -1; text-align: center; color: var(--text-secondary);">No insights available.</p>';
    }
  }

  function renderStreaming() {
    const availabilityGrid = document.querySelector('.availability-grid');
    if (availabilityGrid) {
      availabilityGrid.innerHTML = '<p class="body-text" style="grid-column: 1 / -1; text-align: center; color: var(--text-secondary);">No streaming information available.</p>';
    }
  }

  function renderRelatedMovies() {
    const cardRow = document.querySelector('.card-row');
    if (cardRow) {
      cardRow.innerHTML = '<p class="body-text" style="text-align: center; color: var(--text-secondary);">No related movies available.</p>';
    }
  }

  function formatDate(dateInput) {
    if (!dateInput) return 'N/A';
    const date = new Date(dateInput);
    if (isNaN(date.getTime())) return 'N/A';
    const options = { year: 'numeric', month: 'long' };
    return date.toLocaleDateString(undefined, options);
  }

  function formatRuntime(minutes) {
    if (!minutes) return 'N/A';
    const hours = Math.floor(minutes / 60);
    const mins = minutes % 60;
    if (hours > 0) {
      return hours + 'h ' + (mins > 0 ? mins + 'm' : '');
    }
    return mins + 'm';
  }

  async function initMovieDetails() {
    const movieId = getMovieId();
    if (!movieId) return;

    currentMovieId = movieId;

    const movie = await fetchMovie(movieId);
    if (!movie) return;

    currentMovie = movie;

    console.log('[MovieDetails] Rendering movie:', movie.title);

    renderHero(movie);
    renderHeroStats(movie, ratingStats);
    renderStoryline(movie);
    renderKeyFacts(movie);
    renderCast();
    renderMediaGallery();
    renderRatings(currentUserRating, ratingStats);
    renderReviews();
    renderInsights();
    renderStreaming();
    renderRelatedMovies();

    await loadRatingData(movieId);
    renderHeroStats(movie, ratingStats);
    renderRatings(currentUserRating, ratingStats);

    if (window.CineJunction.initGallery) {
      window.CineJunction.initGallery();
    }
    if (window.CineJunction.initReviews) {
      window.CineJunction.initReviews();
    }

    console.log('[MovieDetails] Completed');
  }

  window.CineJunction.initMovieDetails = initMovieDetails;

  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initMovieDetails);
  } else {
    initMovieDetails();
  }
})();
