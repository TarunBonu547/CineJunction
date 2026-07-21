(function () {
  window.CineJunction = window.CineJunction || {};

  function getMovies(params) {
    params = params || {};
    var query = new URLSearchParams();
    if (params.sort) query.set('sort', params.sort);
    if (params.genre) query.set('genre', params.genre);
    if (params.language) query.set('language', params.language);
    if (params.year) query.set('year', String(params.year));
    if (params.page !== undefined) query.set('page', String(params.page));
    if (params.size) query.set('size', String(params.size));
    var qs = query.toString();
    return window.CineJunction.apiFetch('/api/v1/movies' + (qs ? '?' + qs : ''));
  }

  function getMovieById(id) {
    return window.CineJunction.apiFetch('/api/v1/movies/' + encodeURIComponent(id));
  }

  function searchMovies(keyword, page, size) {
    page = page || 0;
    size = size || 20;
    var query = new URLSearchParams();
    query.set('keyword', keyword);
    query.set('page', String(page));
    query.set('size', String(size));
    return window.CineJunction.apiFetch('/api/v1/movies/search?' + query.toString());
  }

  window.CineJunction.movieService = {
    getMovies: getMovies,
    getMovieById: getMovieById,
    searchMovies: searchMovies
  };
})();
