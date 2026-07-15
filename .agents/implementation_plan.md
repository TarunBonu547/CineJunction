# Implementation Plan — CineJunction Sprint 2: Complete Frontend Integration

This plan outlines the design and integration of CineJunction's frontend. The goal is to connect all pages, fix broken functionality, implement localStorage-backed lists, expand user profile features, construct the community page with interactive mock data, and unify navigation using shared components, all while preserving the existing visual appearance.

## User Review Required

> [!IMPORTANT]
> - **Unified Script Architecture**: We will standardize script tags across all pages to ensure authentication guards, dynamic headers/footers, card click listeners, and toast notifications function reliably.
> - **Relative Link Prefixing**: Because pages are divided between the root directory (`/`) and the `pages/` directory, the shared JS navigation script will dynamically compute relative paths (e.g., prepending `pages/` or `../` as needed) based on the location of the page.
> - **No Styling Reset**: We will strictly reuse existing design tokens, grid patterns, and component styling. Appending styling adjustments where necessary (e.g., custom bio fields or post inputs) will be done in pages stylesheet files.

## Proposed Changes

We will group our work into the following phases:

---

### Phase 1: Shared Mock Data & Reusable Components

#### [NEW] [mock-data.js](file:///c:/Users/Admin/Desktop/CineJunction%20-%20The%20New%20Way/frontend/js/mock-data.js)
- Define `window.CineJunction.mockData` containing:
  - **Movies, TV Shows, Anime & Trending** (at least 10 titles complete with metadata, storylines, cast lists, images, ratings, streaming details, and sample reviews).
  - **Community Posts** (sample feed items with authors, likes, and comments).
  - **User Profile** (default profile with name, email, bio, stats, and favorite genres).
  - **Lists** (pre-populated favorites, watch later, recently watched).

#### [MODIFY] [navigation.js](file:///c:/Users/Admin/Desktop/CineJunction%20-%20The%20New%20Way/frontend/js/navigation.js)
- Dynamically inject the premium visual **Navbar** and **Footer** into any page with `<header class="site-header">` and `<footer class="site-footer">`.
- Render the search suggest field, profile picture shortcut dropdown (with "Profile", "Watchlist", "Settings", and a "Logout" action), and navigation lists.
- Support responsive toggle drawer behavior (the hamburger menu) and keyboard access.
- Pre-highlight the active menu item based on the current window location.

#### [MODIFY] [utils.js](file:///c:/Users/Admin/Desktop/CineJunction%20-%20The%20New%20Way/frontend/js/utils.js)
- Implement a global event listener to capture click events on any `.movie-card`.
- Read the movie title from the clicked card, map it to the corresponding slug, and navigate dynamically to `movie-details.html?id=slug`.
- Set up a standard global page transition effect.

#### [MODIFY] [auth.js](file:///c:/Users/Admin/Desktop/CineJunction%20-%20The%20New%20Way/frontend/js/auth.js)
- Ensure that unauthenticated users visiting protected pages are redirected to the Login page.
- Link login/register form submissions directly to local storage initialization and homepage redirection.

---

### Phase 2: Page Integration & Interactive Logic

#### [NEW] [community-page.js](file:///c:/Users/Admin/Desktop/CineJunction%20-%20The%20New%20Way/frontend/js/community-page.js)
- Render the community feed (mock posts + user submissions stored in localStorage).
- Include an interactive post creator (textarea + dropdown of movies to link) to submit new comments in real-time.
- Render trending discussions, popular lists, and active users in the sidebar.

#### [NEW] [watchlist-page.js](file:///c:/Users/Admin/Desktop/CineJunction%20-%20The%20New%20Way/frontend/js/watchlist-page.js)
- Render tab navigation for **Favorites**, **Watch Later** (Watchlist), **Recently Watched**, **Top Rated**, and **Custom Lists**.
- Display lists as a grid of movie cards.
- Add support for creating custom lists (e.g. via prompt dialog), adding movies, and deleting lists or list items.

#### [NEW] [profile-page.js](file:///c:/Users/Admin/Desktop/CineJunction%20-%20The%20New%20Way/frontend/js/profile-page.js)
- Render user metrics (Movies Watched, Time Spent, Reviews Written).
- Provide form fields to update name, username, bio, and favorite genres (multiselect chips).
- Display lists preview (recently watched and watchlist).
- Wire up settings navigation and the logout button.

#### [NEW] [search-page.js](file:///c:/Users/Admin/Desktop/CineJunction%20-%20The%20New%20Way/frontend/js/search-page.js)
- Retrieve `q` query parameter from the URL.
- Match search query against titles, directors, actors, and genres.
- Render results in a `.movie-grid` layout matching the design system, or fallback to the empty state.

#### [MODIFY] [movie-details.js](file:///c:/Users/Admin/Desktop/CineJunction%20-%20The%20New%20Way/frontend/js/movie-details.js)
- Check URL for movie `id`. If present, render all details (storyline, cast, trailer, ratings, reviews, streaming providers) dynamically from mock-data.
- Bind clicks for "Add to Watchlist", "Mark as Watched", and "Favorite" to persist state in localStorage and toggle active classes.
- Support writing user reviews dynamically: add user reviews directly to the page feed and recalculate the breakdown and average rating.

---

### Phase 3: Placeholder Pages & HTML Alignment

#### [NEW] Footer Placeholders
Create simple pages mirroring the layout and theme of the main shell for:
- [about.html](file:///c:/Users/Admin/Desktop/CineJunction%20-%20The%20New%20Way/frontend/pages/about.html)
- [contact.html](file:///c:/Users/Admin/Desktop/CineJunction%20-%20The%20New%20Way/frontend/pages/contact.html)
- [privacy.html](file:///c:/Users/Admin/Desktop/CineJunction%20-%20The%20New%20Way/frontend/pages/privacy.html)
- [terms.html](file:///c:/Users/Admin/Desktop/CineJunction%20-%20The%20New%20Way/frontend/pages/terms.html)
- [help.html](file:///c:/Users/Admin/Desktop/CineJunction%20-%20The%20New%20Way/frontend/pages/help.html)

#### [MODIFY] HTML Headers & Footers
- Audit HTML files to include script tags for `mock-data.js`, `auth.js`, `utils.js`, `navigation.js`, and `search.js` in a standardized sequence.
- Ensure all pages are properly connected.

---

## Verification Plan

### Automated Tests
- None are currently configured in this vanilla workspace.

### Manual Verification
- Deploy a local development server or open pages directly in a browser.
- Perform the complete navigation loop: Login -> Home -> Movies -> Movie Details -> Watchlist -> Profile -> Settings -> Search -> Trending -> Community -> Logout.
- Verify adding to watchlist, editing user bio, submitting custom lists, and posting comments on the community feed.
- Ensure the visual layout, color contrast, and styling remain exactly as designed in `design-system.html`.
