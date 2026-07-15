(function () {
  function closeModal() {
    const modal = document.getElementById('media-modal');
    if (!modal) return;
    modal.hidden = true;
    const content = modal.querySelector('.modal__content');
    if (content) {
      content.innerHTML = '';
    }
  }

  function openModal(trigger) {
    const modal = document.getElementById('media-modal');
    const content = modal?.querySelector('.modal__content');
    if (!modal || !content) return;

    const type = trigger.dataset.mediaType;
    const src = trigger.dataset.mediaSrc;
    const title = trigger.dataset.mediaTitle || 'Media';

    if (type === 'video') {
      content.innerHTML = `
        <div class="modal__frame">
          <iframe src="${src}" title="${title}" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture" allowfullscreen></iframe>
        </div>
      `;
    } else {
      content.innerHTML = `<img src="${src}" alt="${title}" />`;
    }

    modal.hidden = false;
    modal.setAttribute('aria-label', title);
  }

  function initGallery() {
    const buttons = Array.from(document.querySelectorAll('[data-media-type]'));
    const modal = document.getElementById('media-modal');
    const closeButton = modal?.querySelector('.modal__close');

    buttons.forEach((button) => {
      button.addEventListener('click', () => openModal(button));
    });

    closeButton?.addEventListener('click', closeModal);

    modal?.addEventListener('click', (event) => {
      if (event.target === modal) {
        closeModal();
      }
    });

    document.addEventListener('keydown', (event) => {
      if (event.key === 'Escape' && !modal?.hidden) {
        closeModal();
      }
    });
  }

  window.CineJunction = window.CineJunction || {};
  window.CineJunction.initGallery = initGallery;

  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initGallery);
  } else {
    initGallery();
  }
})();
