(function () {
  window.CineJunction = window.CineJunction || {};

  const LISTS = {
    favorites: 'cinejunction.lists.favorites',
    watchlist: 'cinejunction.lists.watchlist',
    recently: 'cinejunction.lists.recently',
    modifiedMovies: 'cinejunction.movies.modified'
  };

  function getQueryParam(param) {
    const urlParams = new URLSearchParams(window.location.search);
    return urlParams.get(param);
  }

  function getList(key) {
    const raw = window.localStorage.getItem(key);
    if (raw) {
      try { return JSON.parse(raw); } catch (e) {}
    }
    return [];
  }

  function setList(key, val) {
    window.localStorage.setItem(key, JSON.stringify(val));
  }

  function getModifiedMovies() {
    const raw = window.localStorage.getItem(LISTS.modifiedMovies);
    if (raw) {
      try { return JSON.parse(raw); } catch(e) {}
    }
    return {};
  }

  function saveModifiedMovie(movie) {
    const modified = getModifiedMovies();
    modified[movie.id] = movie;
    window.localStorage.setItem(LISTS.modifiedMovies, JSON.stringify(modified));
  }

  function loadMovieData(id) {
    const modified = getModifiedMovies();
    if (modified[id]) {
      return modified[id];
    }
    const allMovies = window.CineJunction.mockData.movies;
    return allMovies.find(m => m.id === id) || allMovies.find(m => m.id === "the-last-horizon");
  }

  function toggleMovieInList(listKey, movieId) {
    let list = getList(listKey);
    const index = list.indexOf(movieId);
    let added = false;
    if (index === -1) {
      list.push(movieId);
      added = true;
    } else {
      list.splice(index, 1);
    }
    setList(listKey, list);
    return added;
  }

  function renderMovieDetails() {
    const movieId = getQueryParam('id') || "the-last-horizon";
    const movie = loadMovieData(movieId);

    if (!movie) return;

    // 1. Update Hero Backdrop and Poster
    const backdropImg = document.querySelector('.movie-hero__backdrop img');
    if (backdropImg && movie.backdropUrl) {
      backdropImg.src = movie.backdropUrl;
      backdropImg.alt = `${movie.title} backdrop`;
    }
    const posterImg = document.querySelector('.movie-hero__poster img');
    if (posterImg && movie.posterUrl) {
      posterImg.src = movie.posterUrl;
      posterImg.alt = `${movie.title} poster`;
    }

    // 2. Update Basic Metadata
    const title = document.querySelector('[data-hero-title]');
    const tagline = document.querySelector('[data-hero-tagline]');
    const year = document.querySelector('[data-hero-year]');
    const runtime = document.querySelector('[data-hero-runtime]');
    const genre = document.querySelector('[data-hero-genre]');
    const language = document.querySelector('[data-hero-language]');
    const rating = document.querySelector('[data-hero-rating]');

    if (title) title.textContent = movie.title;
    if (tagline) tagline.textContent = movie.tagline || "";
    if (year) year.textContent = movie.year;
    if (runtime) runtime.textContent = movie.runtime;
    if (genre) genre.textContent = movie.genres ? movie.genres[0] : movie.genre;
    if (language) language.textContent = movie.language;
    if (rating) rating.textContent = movie.ageRating;

    // 3. Update Hero Stats
    const stats = document.querySelectorAll('.hero-stat strong');
    if (stats.length >= 4) {
      stats[0].textContent = movie.imdbRating || "N/A";
      stats[1].textContent = movie.cjRating || "N/A";
      stats[2].textContent = movie.popularityScore || "N/A";
      stats[3].textContent = movie.director || "N/A";
    }

    // 4. Update Storyline & Key Details
    const storylineArticle = document.querySelector('[aria-labelledby="story-title"], #story-title')?.closest('article') || document.querySelector('.info-card');
    const storyText = storylineArticle?.querySelector('.body-text');
    if (storyText) storyText.textContent = movie.synopsis || movie.description;

    const detailList = document.querySelector('.detail-list');
    if (detailList) {
      detailList.innerHTML = `
        <div role="listitem"><span>Director</span><strong>${movie.director || 'N/A'}</strong></div>
        <div role="listitem"><span>Writers</span><strong>${movie.writers || 'N/A'}</strong></div>
        <div role="listitem"><span>Producers</span><strong>${movie.producers || 'N/A'}</strong></div>
        <div role="listitem"><span>Production Company</span><strong>${movie.productionCompany || 'N/A'}</strong></div>
        <div role="listitem"><span>Budget</span><strong>${movie.budget || 'N/A'}</strong></div>
        <div role="listitem"><span>Box Office</span><strong>${movie.boxOffice || 'N/A'}</strong></div>
        <div role="listitem"><span>Country</span><strong>${movie.country || 'N/A'}</strong></div>
        <div role="listitem"><span>Languages</span><strong>${movie.languages || 'N/A'}</strong></div>
        <div role="listitem"><span>Awards</span><strong>${movie.awards || 'N/A'}</strong></div>
        <div role="listitem"><span>Filming Locations</span><strong>${movie.filmingLocations || 'N/A'}</strong></div>
      `;
    }

    // 5. Update Key Facts Sidebar
    const facts = document.querySelectorAll('.fact-list strong');
    if (facts.length >= 6) {
      facts[0].textContent = movie.genres ? movie.genres.join(' • ') : movie.genre;
      facts[1].textContent = movie.year;
      facts[2].textContent = movie.runtime;
      facts[3].textContent = "4K Dolby Atmos";
      facts[4].textContent = movie.ageRating || 'N/A';
      facts[5].textContent = movie.language || 'N/A';
    }

    // 6. Update Cast
    const personGrid = document.querySelector('.person-grid');
    if (personGrid && movie.cast) {
      personGrid.innerHTML = movie.cast.map(c => `
        <article class="person-card">
          <img src="${c.imageUrl || 'https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?auto=format&fit=crop&w=500&q=80'}" alt="Portrait of ${c.name}" />
          <div class="person-card__body">
            <h3 class="movie-title" style="font-size: var(--fs-md);">${c.name}</h3>
            <p class="body-text" style="font-size: var(--fs-xs);">${c.role}</p>
            ${c.characterName ? `<span class="meta-pill" style="font-size: 10px; margin-top: 4px;">${c.characterName}</span>` : ''}
          </div>
        </article>
      `).join('');
    }

    // 7. Update Media Gallery
    const galleryGrid = document.querySelector('.gallery-grid');
    if (galleryGrid && movie.mediaGallery) {
      galleryGrid.innerHTML = movie.mediaGallery.map(g => `
        <button class="gallery-card" type="button" data-media-type="${g.type}" data-media-src="${g.src}" data-media-title="${g.title}">
          <img src="${g.type === 'video' ? 'https://images.unsplash.com/photo-1478720568477-152d9b164e26?auto=format&fit=crop&w=800&q=80' : g.src}" alt="${g.title}" />
          <div class="gallery-card__body">
            <h3 class="movie-title" style="font-size: var(--fs-md);">${g.title}</h3>
            <p class="body-text" style="font-size: var(--fs-xs);">${g.type === 'video' ? 'Video Player' : 'Key art'}</p>
          </div>
        </button>
      `).join('');
    }

    // 8. Update Streaming Availability
    const availabilityGrid = document.querySelector('.availability-grid');
    if (availabilityGrid && movie.streaming) {
      availabilityGrid.innerHTML = movie.streaming.map(s => `
        <article class="availability-card">
          <h3 class="movie-title" style="font-size: var(--fs-md);">${s.name}</h3>
          <p class="body-text" style="font-size: var(--fs-xs); color: var(--text-secondary);">${s.status}</p>
        </article>
      `).join('');
    }

    // 9. Update Reviews
    const reviewList = document.getElementById('review-list');
    if (reviewList && movie.reviews) {
      reviewList.innerHTML = movie.reviews.map(r => `
        <article class="review-card" data-type="${r.type}" data-helpful="${r.helpful}" data-rating="${r.rating}" data-spoiler="${r.spoiler || false}">
          <div class="review-card__meta">
            <div>
              <h3 class="movie-title" style="font-size: var(--fs-md);">${r.author}</h3>
              <p class="body-text" style="font-size: var(--fs-xs); color: var(--text-secondary);">${r.type === 'critic' ? 'Critic Review' : 'Verified Viewer'}</p>
            </div>
            <span class="meta-pill">${'★'.repeat(r.rating) + '☆'.repeat(5 - r.rating)}</span>
          </div>
          <p class="body-text" style="font-size: var(--fs-md);">${r.text}</p>
          ${r.spoiler ? `<button class="btn btn-outline review-card__toggle" type="button" style="margin-top: 6px;">Reveal spoiler</button>` : ''}
          <div class="review-card__actions" style="margin-top: 8px;">
            <div class="review-card__controls">
              <button class="btn btn-outline" type="button" data-vote="helpful">Helpful</button>
              <button class="btn btn-outline" type="button" data-vote="dislike">Dislike</button>
            </div>
            <span class="meta-pill">${r.helpful} helpful</span>
          </div>
        </article>
      `).join('');
    }

    // 10. Setup Toggle states
    const actionButtons = document.querySelectorAll('.hero-actions button, .hero-actions a');
    const watchlistBtn = actionButtons[1];
    const watchedBtn = actionButtons[2];
    const favoriteBtn = actionButtons[3];
    const rateBtn = actionButtons[5];

    function updateButtonStates() {
      const watchlist = getList(LISTS.watchlist);
      const recently = getList(LISTS.recently);
      const favorites = getList(LISTS.favorites);

      const inWatchlist = watchlist.includes(movie.id);
      const inRecently = recently.includes(movie.id);
      const inFavorites = favorites.includes(movie.id);

      if (watchlistBtn) {
        watchlistBtn.classList.toggle('is-active', inWatchlist);
        watchlistBtn.textContent = inWatchlist ? '✓ In Watchlist' : 'Add to Watchlist';
        watchlistBtn.className = inWatchlist ? 'btn btn-outline is-active' : 'btn btn-secondary';
      }

      if (watchedBtn) {
        watchedBtn.classList.toggle('is-active', inRecently);
        watchedBtn.textContent = inRecently ? '✓ Watched' : 'Mark as Watched';
      }

      if (favoriteBtn) {
        favoriteBtn.classList.toggle('is-active', inFavorites);
        favoriteBtn.textContent = inFavorites ? '★ Favorited' : 'Favorite';
      }
    }

    updateButtonStates();

    // Bind Action Button click listeners
    if (watchlistBtn) {
      watchlistBtn.addEventListener('click', () => {
        const added = toggleMovieInList(LISTS.watchlist, movie.id);
        updateButtonStates();
        if (window.CineJunction.showToast) {
          window.CineJunction.showToast(added ? "Added to Watchlist" : "Removed from Watchlist");
        }
      });
    }

    if (watchedBtn) {
      watchedBtn.addEventListener('click', () => {
        const added = toggleMovieInList(LISTS.recently, movie.id);
        updateButtonStates();
        
        // Update user movies watched stat
        const user = window.CineJunction.getAuthState();
        if (user) {
          user.stats = user.stats || { moviesWatched: 42, seriesWatched: 15, reviewsWritten: 8, watchTime: "112h" };
          if (added) user.stats.moviesWatched++;
          else user.stats.moviesWatched = Math.max(0, user.stats.moviesWatched - 1);
          window.localStorage.setItem('cinejunction.auth', JSON.stringify(user));
        }

        if (window.CineJunction.showToast) {
          window.CineJunction.showToast(added ? "Marked as Watched" : "Removed from History");
        }
      });
    }

    if (favoriteBtn) {
      favoriteBtn.addEventListener('click', () => {
        const added = toggleMovieInList(LISTS.favorites, movie.id);
        updateButtonStates();
        if (window.CineJunction.showToast) {
          window.CineJunction.showToast(added ? "Added to Favorites" : "Removed from Favorites");
        }
      });
    }

    // Connect Trailer Action to First Gallery Video Item if available
    const trailerBtn = actionButtons[0];
    if (trailerBtn && movie.mediaGallery) {
      const firstVideo = movie.mediaGallery.find(g => g.type === 'video');
      if (firstVideo) {
        trailerBtn.href = "#";
        trailerBtn.addEventListener('click', (e) => {
          e.preventDefault();
          // Create a mock trigger structure for gallery.js openModal
          const mockTrigger = document.createElement('button');
          mockTrigger.dataset.mediaType = 'video';
          mockTrigger.dataset.mediaSrc = firstVideo.src;
          mockTrigger.dataset.mediaTitle = firstVideo.title;
          
          const modal = document.getElementById('media-modal');
          const content = modal?.querySelector('.modal__content');
          if (modal && content) {
            content.innerHTML = `
              <div class="modal__frame">
                <iframe src="${firstVideo.src}" title="${firstVideo.title}" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture" allowfullscreen></iframe>
              </div>
            `;
            modal.hidden = false;
            modal.setAttribute('aria-label', firstVideo.title);
          }
        });
      }
    }

    // 11. Write Review handlers
    function promptReview() {
      const stars = prompt("Rate this movie from 1 to 5 stars (numbers only):", "5");
      const ratingVal = parseInt(stars);
      if (isNaN(ratingVal) || ratingVal < 1 || ratingVal > 5) {
        alert("Please enter a valid rating between 1 and 5.");
        return;
      }

      const text = prompt("Write a short review comment:");
      if (!text || !text.trim()) {
        alert("Review text cannot be empty.");
        return;
      }

      const user = window.CineJunction.getAuthState() || { name: "Guest" };
      const newReview = {
        author: user.name,
        type: "user",
        rating: ratingVal,
        helpful: 0,
        text: text.trim(),
        spoiler: false
      };

      // Add review to movie
      movie.reviews = movie.reviews || [];
      movie.reviews.unshift(newReview);
      
      // Update CJ rating score (weighted average)
      const avg = (movie.reviews.reduce((acc, curr) => acc + curr.rating, 0) / movie.reviews.length * 2).toFixed(1);
      movie.cjRating = String(avg);

      // Save modified movie
      saveModifiedMovie(movie);

      // Increment reviews written counter in user stats
      const loggedUser = window.CineJunction.getAuthState();
      if (loggedUser) {
        loggedUser.stats = loggedUser.stats || { moviesWatched: 42, seriesWatched: 15, reviewsWritten: 8, watchTime: "112h" };
        loggedUser.stats.reviewsWritten++;
        window.localStorage.setItem('cinejunction.auth', JSON.stringify(loggedUser));
      }

      // Re-initialize details rendering
      renderMovieDetails();

      if (window.CineJunction.showToast) {
        window.CineJunction.showToast("Review submitted successfully!");
      }
    }

    if (rateBtn) {
      rateBtn.addEventListener('click', promptReview);
    }
    const sectionWriteReview = document.querySelector('.section-link[href="#"]');
    if (sectionWriteReview) {
      // Find the one in section 6
      const headingText = sectionWriteReview.previousElementSibling?.querySelector('h2');
      if (headingText && headingText.textContent.includes('Reviews')) {
        sectionWriteReview.addEventListener('click', (e) => {
          e.preventDefault();
          promptReview();
        });
      }
    }

    // Trigger sub-Gallery and sub-Reviews initialization
    if (window.CineJunction.initGallery) {
      window.CineJunction.initGallery();
    }
    if (window.CineJunction.initReviews) {
      window.CineJunction.initReviews();
    }
  }

  function tryInit(attempts) {
    if (window.CineJunction?.mockData?.movies) {
      renderMovieDetails();
    } else if (attempts > 0) {
      window.setTimeout(() => tryInit(attempts - 1), 50);
    }
  }

  // Overwrite details initializer
  window.CineJunction.initMovieDetails = renderMovieDetails;

  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', () => tryInit(20));
  } else {
    tryInit(20);
  }
})();
