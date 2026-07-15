(function () {
  function initReviews() {
    const reviewList = document.getElementById('review-list');
    const filterButtons = Array.from(document.querySelectorAll('[data-filter]'));
    const sortSelect = document.getElementById('review-sort');
    const reviews = Array.from(reviewList?.querySelectorAll('.review-card') || []);

    if (!reviewList || reviews.length === 0) {
      return;
    }

    function applyFilters() {
      const activeFilter = document.querySelector('[data-filter].is-active')?.dataset.filter || 'all';
      const sortValue = sortSelect?.value || 'helpful';

      const visibleReviews = reviews
        .filter((review) => {
          const type = review.dataset.type;
          return activeFilter === 'all' || type === activeFilter;
        })
        .sort((left, right) => {
          const leftHelpful = Number(left.dataset.helpful || 0);
          const rightHelpful = Number(right.dataset.helpful || 0);
          const leftRating = Number(left.dataset.rating || 0);
          const rightRating = Number(right.dataset.rating || 0);

          if (sortValue === 'rating') {
            return rightRating - leftRating;
          }

          if (sortValue === 'recent') {
            return rightHelpful - leftHelpful;
          }

          return rightHelpful - leftHelpful;
        });

      reviews.forEach((review) => {
        review.style.display = 'none';
      });

      visibleReviews.forEach((review) => {
        review.style.display = 'grid';
      });
    }

    filterButtons.forEach((button) => {
      button.addEventListener('click', () => {
        filterButtons.forEach((item) => item.classList.remove('is-active'));
        button.classList.add('is-active');
        applyFilters();
      });
    });

    sortSelect?.addEventListener('change', applyFilters);

    reviewList.querySelectorAll('.review-card__toggle').forEach((button) => {
      button.addEventListener('click', () => {
        const card = button.closest('.review-card');
        card?.classList.toggle('is-spoiler');
        button.textContent = card?.classList.contains('is-spoiler') ? 'Hide spoiler' : 'Reveal spoiler';
      });
    });

    reviewList.querySelectorAll('[data-vote]').forEach((button) => {
      button.addEventListener('click', () => {
        button.classList.toggle('is-active');
      });
    });

    applyFilters();
  }

  window.CineJunction = window.CineJunction || {};
  window.CineJunction.initReviews = initReviews;

  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initReviews);
  } else {
    initReviews();
  }
})();
