(function () {
  window.CineJunction = window.CineJunction || {};

  const FEED_STORAGE_KEY = 'cinejunction.community.posts';

  function getStoredPosts() {
    const raw = window.localStorage.getItem(FEED_STORAGE_KEY);
    if (raw) {
      try { return JSON.parse(raw); } catch (e) {}
    }
    const seed = [
      {
        id: 'post-1',
        author: 'CineJunction',
        time: '2 hours ago',
        movie: '',
        movieSlug: '',
        content: 'Welcome to the community hub. Share your thoughts on cinema.',
        likes: 0,
        liked: false,
        comments: []
      }
    ];
    window.localStorage.setItem(FEED_STORAGE_KEY, JSON.stringify(seed));
    return seed;
  }

  function setStoredPosts(posts) {
    window.localStorage.setItem(FEED_STORAGE_KEY, JSON.stringify(posts));
  }

  function renderFeed(posts) {
    const container = document.getElementById('community-feed');
    if (!container) return;

    if (!posts || posts.length === 0) {
      container.innerHTML = `
        <div class="empty-state">
          <h4>No conversations yet</h4>
          <p>Be the first to share your thoughts on CineJunction.</p>
        </div>
      `;
      return;
    }

    container.innerHTML = posts.map(post => {
      const isLiked = post.liked;
      const initial = post.avatar || (post.author ? post.author.charAt(0).toUpperCase() : 'A');

      const commentsHtml = post.comments && post.comments.length > 0
        ? `<div class="comments-section" style="margin-top: 12px; padding: 12px; background: rgba(255,255,255,0.02); border-radius: 12px; font-size: var(--fs-sm); display: grid; gap: 8px;">
            ${post.comments.map(c => `<div><strong>${c.author}:</strong> <span style="color: var(--text-secondary);">${c.text}</span></div>`).join('')}
           </div>`
        : '';

      return `
        <article class="review-card" data-post-id="${post.id}">
          <div class="review-card__meta">
            <div>
              <div style="display: flex; align-items: center; gap: 10px;">
                <div class="avatar avatar-medium" style="margin-bottom: 0; width: 36px; height: 36px; display: flex; align-items: center; justify-content: center; font-weight: bold; font-size: var(--fs-sm);">${initial}</div>
                <div>
                  <h3 class="movie-title" style="font-size: var(--fs-md);">${post.author}</h3>
                  <p class="body-text" style="font-size: var(--fs-xs); color: var(--text-secondary); margin: 0;">${post.time}</p>
                </div>
              </div>
            </div>
          </div>
          <p class="body-text" style="font-size: var(--fs-md); margin-top: 6px;">${post.content}</p>
          <div class="review-card__actions" style="margin-top: 8px;">
            <div class="review-card__controls">
              <button class="btn btn-outline like-btn ${isLiked ? 'is-active' : ''}" type="button">
                ${isLiked ? '❤️' : '♡'} ${post.likes} Likes
              </button>
              <button class="btn btn-outline comment-btn" type="button">💬 Comment</button>
            </div>
          </div>
          ${commentsHtml}
        </article>
      `;
    }).join('');

    container.querySelectorAll('.like-btn').forEach(btn => {
      btn.addEventListener('click', (e) => {
        const card = e.target.closest('.review-card');
        if (!card) return;
        const postId = card.dataset.postId;

        const posts = getStoredPosts();
        const post = posts.find(p => p.id === postId);
        if (post) {
          if (post.liked) {
            post.liked = false;
            post.likes--;
          } else {
            post.liked = true;
            post.likes++;
          }
          setStoredPosts(posts);
          renderFeed(posts);
        }
      });
    });

    container.querySelectorAll('.comment-btn').forEach(btn => {
      btn.addEventListener('click', () => {
        const card = btn.closest('.review-card');
        const postId = card.dataset.postId;
        const reply = prompt("Add your comment:");
        if (reply && reply.trim()) {
          const posts = getStoredPosts();
          const post = posts.find(p => p.id === postId);
          const currentUser = window.CineJunction.getAuthState ? window.CineJunction.getAuthState() : { name: "Guest" };
          if (post) {
            post.comments = post.comments || [];
            post.comments.push({
              author: currentUser.name,
              text: reply.trim()
            });
            setStoredPosts(posts);
            renderFeed(posts);
            if (window.CineJunction.showToast) {
              window.CineJunction.showToast("Comment added!");
            }
          }
        }
      });
    });
  }

  function initCommunityPage() {
    const main = document.querySelector('main');
    if (!main) return;

    main.className = "page-shell container content-section";
    main.innerHTML = `
      <section class="info-card" style="margin-bottom: 24px; padding: 24px;">
        <div class="section-heading" style="margin-bottom: 0;">
          <div>
            <p class="section-label">Community Hub</p>
            <h1 class="section-title" style="margin: 0 0 8px;">The conversation is open.</h1>
            <p class="body-text" style="margin: 0;">Connect with fellow cinephiles, share reviews, and join discussions on curated cinema.</p>
          </div>
        </div>
      </section>

      <div class="movie-info-grid">
        <div class="discovery-content" style="display: grid; gap: 24px;">
          <div class="info-card" style="padding: 20px;">
            <h3 class="movie-title" style="font-size: var(--fs-lg); margin-bottom: 12px;">Share your thoughts</h3>
            <form id="create-post-form">
              <div class="input-shell" style="margin-bottom: 12px; padding: 10px 14px; border-radius: 16px;">
                <textarea id="post-content" placeholder="What are you watching recently? Share an analysis or short thought..." style="width: 100%; min-height: 84px; background: transparent; border: 0; outline: 0; color: var(--text-primary); resize: vertical; font-family: inherit; font-size: inherit; line-height: 1.5;" required></textarea>
              </div>
              <button class="btn btn-primary" type="submit">Post to Feed</button>
            </form>
          </div>

          <div class="review-list" id="community-feed"></div>
        </div>

        <div class="discovery-sidebar" style="display: grid; gap: 24px; align-self: start;">
          <div class="info-card" style="padding: 20px;">
            <h3 class="movie-title" style="font-size: var(--fs-lg); margin-bottom: 12px;">Trending Discussions</h3>
            <ul class="score-list" id="trending-discussions" style="display: grid; gap: 12px; padding: 0; margin: 0; list-style: none;"></ul>
          </div>

          <div class="info-card" style="padding: 20px;">
            <h3 class="movie-title" style="font-size: var(--fs-lg); margin-bottom: 12px;">Popular Lists</h3>
            <ul class="score-list" id="popular-lists" style="display: grid; gap: 12px; padding: 0; margin: 0; list-style: none;"></ul>
          </div>

          <div class="info-card" style="padding: 20px;">
            <h3 class="movie-title" style="font-size: var(--fs-lg); margin-bottom: 12px;">Active Users</h3>
            <div class="person-grid" id="active-users-grid" style="display: grid; grid-template-columns: repeat(2, 1fr); gap: 10px;"></div>
          </div>
        </div>
      </div>
    `;

    const posts = getStoredPosts();
    renderFeed(posts);

    const postForm = document.getElementById('create-post-form');
    if (postForm) {
      postForm.addEventListener('submit', (e) => {
        e.preventDefault();
        const contentText = document.getElementById('post-content').value.trim();

        if (!contentText) return;

        const currentUser = window.CineJunction.getAuthState ? window.CineJunction.getAuthState() : { name: "Guest" };
        const newPost = {
          id: 'post-' + Date.now(),
          author: currentUser.name,
          avatar: currentUser.name.charAt(0).toUpperCase(),
          time: 'Just now',
          movie: '',
          movieSlug: '',
          content: contentText,
          likes: 0,
          liked: false,
          comments: []
        };

        const currentPosts = getStoredPosts();
        currentPosts.unshift(newPost);
        setStoredPosts(currentPosts);

        document.getElementById('post-content').value = '';

        renderFeed(currentPosts);

        if (window.CineJunction.showToast) {
          window.CineJunction.showToast("Post added to feed!");
        }
      });
    }
  }

  function initCommunityPageDirect() {
    initCommunityPage();
  }

  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initCommunityPageDirect);
  } else {
    initCommunityPageDirect();
  }
})();
