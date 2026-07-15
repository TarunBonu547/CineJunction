(function () {
  window.CineJunction = window.CineJunction || {};

  const FEED_STORAGE_KEY = 'cinejunction.community.posts';

  function getStoredPosts() {
    const raw = window.localStorage.getItem(FEED_STORAGE_KEY);
    if (raw) {
      try { return JSON.parse(raw); } catch (e) {}
    }
    // Initialize with mock data posts
    const mockPosts = window.CineJunction.mockData.communityPosts;
    window.localStorage.setItem(FEED_STORAGE_KEY, JSON.stringify(mockPosts));
    return mockPosts;
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
      
      const linkTag = post.movieSlug 
        ? `<span class="meta-pill" style="cursor: pointer;" data-movie-slug="${post.movieSlug}">Discussing: ${post.movie}</span>`
        : '';

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
            ${linkTag}
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

    // Attach Click handlers for linked movies in post chips
    container.querySelectorAll('[data-movie-slug]').forEach(chip => {
      chip.addEventListener('click', () => {
        const slug = chip.dataset.movieSlug;
        window.location.href = `movie-details.html?id=${slug}`;
      });
    });

    // Attach Click handlers for liking posts
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

    // Attach comment prompts
    container.querySelectorAll('.comment-btn').forEach(btn => {
      btn.addEventListener('click', () => {
        const card = btn.closest('.review-card');
        const postId = card.dataset.postId;
        const reply = prompt("Add your comment:");
        if (reply && reply.trim()) {
          const posts = getStoredPosts();
          const post = posts.find(p => p.id === postId);
          const currentUser = window.CineJunction.getAuthState() || { name: "Guest" };
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

    // Change main section to a dynamic grid page
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
        <!-- Left Side: Community Feed -->
        <div class="discovery-content" style="display: grid; gap: 24px;">
          <!-- Post Creator -->
          <div class="info-card" style="padding: 20px;">
            <h3 class="movie-title" style="font-size: var(--fs-lg); margin-bottom: 12px;">Share your thoughts</h3>
            <form id="create-post-form">
              <div class="input-shell" style="margin-bottom: 12px; padding: 10px 14px; border-radius: 16px;">
                <textarea id="post-content" placeholder="What are you watching recently? Share an analysis or short thought..." style="width: 100%; min-height: 84px; background: transparent; border: 0; outline: 0; color: var(--text-primary); resize: vertical; font-family: inherit; font-size: inherit; line-height: 1.5;" required></textarea>
              </div>
              <div style="display: flex; justify-content: space-between; align-items: center; gap: 12px; flex-wrap: wrap;">
                <label class="sort-shell" style="margin: 0; display: flex; align-items: center; gap: 8px;">
                  <span class="input-label" style="font-size: var(--fs-xs); color: var(--text-secondary);">Link Movie:</span>
                  <select id="post-movie-select" class="input-shell" style="min-width: 180px; padding: 6px 12px; border-radius: 999px; background: rgba(255,255,255,0.04); color: var(--text-primary); border: 1px solid var(--border);">
                    <option value="">-- Optional --</option>
                  </select>
                </label>
                <button class="btn btn-primary" type="submit">Post to Feed</button>
              </div>
            </form>
          </div>

          <!-- Feed Feed List -->
          <div class="review-list" id="community-feed"></div>
        </div>

        <!-- Right Side: Sidebar -->
        <div class="discovery-sidebar" style="display: grid; gap: 24px; align-self: start;">
          <!-- Trending Discussions -->
          <div class="info-card" style="padding: 20px;">
            <h3 class="movie-title" style="font-size: var(--fs-lg); margin-bottom: 12px;">Trending Discussions</h3>
            <ul class="score-list" id="trending-discussions" style="display: grid; gap: 12px; padding: 0; margin: 0; list-style: none;"></ul>
          </div>

          <!-- Popular Lists -->
          <div class="info-card" style="padding: 20px;">
            <h3 class="movie-title" style="font-size: var(--fs-lg); margin-bottom: 12px;">Popular Lists</h3>
            <ul class="score-list" id="popular-lists" style="display: grid; gap: 12px; padding: 0; margin: 0; list-style: none;"></ul>
          </div>

          <!-- Active Users -->
          <div class="info-card" style="padding: 20px;">
            <h3 class="movie-title" style="font-size: var(--fs-lg); margin-bottom: 12px;">Active Users</h3>
            <div class="person-grid" id="active-users-grid" style="display: grid; grid-template-columns: repeat(2, 1fr); gap: 10px;"></div>
          </div>
        </div>
      </div>
    `;

    // Populate Linked Movies Dropdown
    const select = document.getElementById('post-movie-select');
    if (select) {
      const movies = window.CineJunction.mockData.movies;
      movies.forEach(m => {
        const opt = document.createElement('option');
        opt.value = m.id;
        opt.textContent = m.title;
        select.appendChild(opt);
      });
    }

    // Populate Sidebars
    const trendingList = document.getElementById('trending-discussions');
    if (trendingList) {
      const discussions = window.CineJunction.mockData.trendingDiscussions;
      trendingList.innerHTML = discussions.map(disc => `
        <li style="padding-bottom: 10px; border-bottom: 1px solid var(--border); font-size: var(--fs-sm); line-height: var(--lh-normal); color: var(--text-primary); cursor: pointer; transition: color 150ms;" onmouseover="this.style.color='var(--cj-primary)'" onmouseout="this.style.color='var(--text-primary)'">
          # ${disc}
        </li>
      `).join('');
    }

    const popLists = document.getElementById('popular-lists');
    if (popLists) {
      const lists = window.CineJunction.mockData.popularLists;
      popLists.innerHTML = lists.map(item => `
        <li style="display: flex; justify-content: space-between; align-items: center; padding-bottom: 10px; border-bottom: 1px solid var(--border); font-size: var(--fs-sm);">
          <div>
            <strong>${item.title}</strong>
            <div style="font-size: var(--fs-xs); color: var(--text-secondary);">by ${item.author}</div>
          </div>
          <span class="meta-pill" style="font-size: var(--fs-xs);">${item.count} titles</span>
        </li>
      `).join('');
    }

    const activeUsersGrid = document.getElementById('active-users-grid');
    if (activeUsersGrid) {
      const users = window.CineJunction.mockData.activeUsers;
      activeUsersGrid.innerHTML = users.map(user => `
        <div style="display: flex; flex-direction: column; align-items: center; padding: 12px; background: rgba(255,255,255,0.02); border: 1px solid var(--border); border-radius: 16px; text-align: center;">
          <div class="avatar avatar-medium" style="margin-bottom: 6px; width: 40px; height: 40px; display: flex; align-items: center; justify-content: center; font-weight: bold; font-size: var(--fs-sm);">${user.initials}</div>
          <div style="font-size: var(--fs-xs); font-weight: var(--weight-medium); color: var(--text-primary); white-space: nowrap; overflow: hidden; text-overflow: ellipsis; max-width: 100%;">${user.name}</div>
          <div style="display: flex; align-items: center; gap: 4px; font-size: 10px; color: var(--text-secondary); margin-top: 4px;">
            <span style="width: 6px; height: 6px; border-radius: 50%; background: ${user.status === 'online' ? '#22C55E' : '#F59E0B'}"></span>
            ${user.status}
          </div>
        </div>
      `).join('');
    }

    // Load and Render Feed Posts
    const posts = getStoredPosts();
    renderFeed(posts);

    // Bind Post Submission
    const postForm = document.getElementById('create-post-form');
    if (postForm) {
      postForm.addEventListener('submit', (e) => {
        e.preventDefault();
        const contentText = document.getElementById('post-content').value.trim();
        const movieSelect = document.getElementById('post-movie-select');
        const selectedMovieId = movieSelect.value;
        const selectedMovieText = selectedMovieId ? movieSelect.options[movieSelect.selectedIndex].text : "";

        if (!contentText) return;

        const currentUser = window.CineJunction.getAuthState() || { name: "Guest" };
        const newPost = {
          id: 'post-' + Date.now(),
          author: currentUser.name,
          avatar: currentUser.name.charAt(0).toUpperCase(),
          time: 'Just now',
          movie: selectedMovieText,
          movieSlug: selectedMovieId,
          content: contentText,
          likes: 0,
          liked: false,
          comments: []
        };

        const currentPosts = getStoredPosts();
        currentPosts.unshift(newPost);
        setStoredPosts(currentPosts);

        // Clear Form inputs
        document.getElementById('post-content').value = '';
        movieSelect.value = '';

        // Rerender feed
        renderFeed(currentPosts);

        // Show Toast
        if (window.CineJunction.showToast) {
          window.CineJunction.showToast("Post added to feed!");
        }

        // Increment reviews written counter in user stats
        const user = window.CineJunction.getAuthState();
        if (user) {
          user.stats = user.stats || { moviesWatched: 42, seriesWatched: 15, reviewsWritten: 8, watchTime: "112h" };
          user.stats.reviewsWritten++;
          window.localStorage.setItem('cinejunction.auth', JSON.stringify(user));
        }
      });
    }
  }

  function tryInit(attempts) {
    if (window.CineJunction?.mockData?.movies) {
      initCommunityPage();
    } else if (attempts > 0) {
      window.setTimeout(() => tryInit(attempts - 1), 50);
    }
  }

  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', () => tryInit(20));
  } else {
    tryInit(20);
  }
})();
