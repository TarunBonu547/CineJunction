(function () {
  window.CineJunction = window.CineJunction || {};

  async function getMovieRatings(movieId) {
    return window.CineJunction.apiFetch('/api/v1/ratings/movie/' + encodeURIComponent(movieId));
  }

  async function getMovieRatingStats(movieId) {
    return window.CineJunction.apiFetch('/api/v1/ratings/movie/' + encodeURIComponent(movieId) + '/stats');
  }

  async function getUserRatings(userId, page, size) {
    page = page || 0;
    size = size || 20;
    return window.CineJunction.apiFetch('/api/v1/ratings/user/' + encodeURIComponent(userId) + '?page=' + page + '&size=' + size);
  }

  async function createRating(data) {
    return window.CineJunction.apiFetch('/api/v1/ratings', {
      method: 'POST',
      body: data
    });
  }

  async function updateRating(ratingId, rating) {
    return window.CineJunction.apiFetch('/api/v1/ratings/' + encodeURIComponent(ratingId), {
      method: 'PUT',
      body: { rating: rating }
    });
  }

  async function deleteRating(ratingId) {
    return window.CineJunction.apiFetch('/api/v1/ratings/' + encodeURIComponent(ratingId), {
      method: 'DELETE'
    });
  }

  window.CineJunction.ratingService = {
    getMovieRatings: getMovieRatings,
    getMovieRatingStats: getMovieRatingStats,
    getUserRatings: getUserRatings,
    createRating: createRating,
    updateRating: updateRating,
    deleteRating: deleteRating
  };
})();
