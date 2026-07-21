(function () {
  window.CineJunction = window.CineJunction || {};

  let currentProfile = null;
  let isSaving = false;
  let isChangingPassword = false;

  function getAuth() {
    return window.CineJunction.getAuthState ? window.CineJunction.getAuthState() : null;
  }

  function showMainMessage(message, isError) {
    var main = document.querySelector('main');
    if (!main) return;
    var color = isError ? 'var(--cj-primary)' : '#4ade80';
    main.innerHTML = `
      <section class="info-card" style="margin-bottom: 24px; padding: 24px;">
        <p class="body-text" style="color: ${color};">${message}</p>
      </section>
    `;
  }

  function renderLoading() {
    var main = document.querySelector('main');
    if (!main) return;
    main.innerHTML = `
      <section class="info-card" style="margin-bottom: 24px; padding: 24px;">
        <p class="body-text section-loading">Loading profile...</p>
      </section>
    `;
  }

  function renderProfile(profile) {
    var main = document.querySelector('main');
    if (!main) return;

    currentProfile = profile;
    var auth = getAuth();
    var displayName = profile.username || 'User';
    var initial = displayName.charAt(0).toUpperCase();
    var createdAt = profile.createdAt ? new Date(profile.createdAt).toLocaleDateString(undefined, { year: 'numeric', month: 'long', day: 'numeric' }) : 'N/A';
    var roleLabel = profile.role ? profile.role.replace('ROLE_', '').toLowerCase() : 'User';

    main.innerHTML = `
      <section class="info-card" style="margin-bottom: 24px; padding: 24px;">
        <div class="section-heading" style="margin-bottom: 0;">
          <div>
            <p class="section-label">Account Identity</p>
            <h1 class="section-title" style="margin: 0 0 8px;">Your Cinematic Profile</h1>
            <p class="body-text" style="margin: 0;">Manage your preferences, view stats, and customize your recommendations.</p>
          </div>
        </div>
      </section>

      <div class="movie-info-grid">
        <div class="discovery-content" style="display: grid; gap: 24px;">
          <div class="info-card" style="padding: 24px;">
            <h3 class="movie-title" style="font-size: var(--fs-lg); margin-bottom: 18px;">Edit Profile</h3>
            <form id="edit-profile-form" style="display: grid; gap: 16px;">
              <div class="field-group" style="display: grid; gap: 6px;">
                <label class="input-label" style="font-size: var(--fs-xs); color: var(--text-secondary);">Username</label>
                <div class="input-shell" style="padding: 0 14px; border-radius: 999px;">
                  <input type="text" id="edit-username" value="${profile.username || ''}" style="width: 100%; min-height: 44px; border: 0; outline: 0; background: transparent; color: var(--text-primary);" required />
                </div>
              </div>

              <div class="field-group" style="display: grid; gap: 6px;">
                <label class="input-label" style="font-size: var(--fs-xs); color: var(--text-secondary);">Email</label>
                <div class="input-shell" style="padding: 0 14px; border-radius: 999px;">
                  <input type="email" id="edit-email" value="${profile.email || ''}" style="width: 100%; min-height: 44px; border: 0; outline: 0; background: transparent; color: var(--text-primary);" required />
                </div>
              </div>

              <button class="btn btn-primary" type="submit" id="save-profile-btn" style="justify-self: start; margin-top: 10px;">Save Profile Changes</button>
              <p id="profile-message" class="body-text" style="margin: 0; font-size: var(--fs-sm);"></p>
            </form>
          </div>

          <div class="info-card" style="padding: 24px;">
            <h3 class="movie-title" style="font-size: var(--fs-lg); margin-bottom: 18px;">Change Password</h3>
            <form id="change-password-form" style="display: grid; gap: 16px;">
              <div class="field-group" style="display: grid; gap: 6px;">
                <label class="input-label" style="font-size: var(--fs-xs); color: var(--text-secondary);">Current Password</label>
                <div class="input-shell" style="padding: 0 14px; border-radius: 999px;">
                  <input type="password" id="current-password" style="width: 100%; min-height: 44px; border: 0; outline: 0; background: transparent; color: var(--text-primary);" required />
                </div>
              </div>

              <div class="field-group" style="display: grid; gap: 6px;">
                <label class="input-label" style="font-size: var(--fs-xs); color: var(--text-secondary);">New Password</label>
                <div class="input-shell" style="padding: 0 14px; border-radius: 999px;">
                  <input type="password" id="new-password" style="width: 100%; min-height: 44px; border: 0; outline: 0; background: transparent; color: var(--text-primary);" required minlength="8" />
                </div>
              </div>

              <div class="field-group" style="display: grid; gap: 6px;">
                <label class="input-label" style="font-size: var(--fs-xs); color: var(--text-secondary);">Confirm New Password</label>
                <div class="input-shell" style="padding: 0 14px; border-radius: 999px;">
                  <input type="password" id="confirm-password" style="width: 100%; min-height: 44px; border: 0; outline: 0; background: transparent; color: var(--text-primary);" required minlength="8" />
                </div>
              </div>

              <button class="btn btn-primary" type="submit" id="change-password-btn" style="justify-self: start; margin-top: 10px;">Change Password</button>
              <p id="password-message" class="body-text" style="margin: 0; font-size: var(--fs-sm);"></p>
            </form>
          </div>
        </div>

        <div class="discovery-sidebar" style="display: grid; gap: 24px; align-self: start;">
          <div class="info-card" style="padding: 24px; text-align: center; display: grid; justify-items: center; gap: 12px;">
            <div class="avatar" style="width: 80px; height: 80px; font-size: 2.2rem; font-weight: bold; color: var(--text-primary); border: 2px solid var(--cj-primary); border-radius: 50%; display: flex; align-items: center; justify-content: center; background: linear-gradient(135deg, var(--cj-primary), rgba(127,29,29,1));">
              ${initial}
            </div>
            <div>
              <h2 class="movie-title" style="font-size: var(--fs-2xl); margin: 0;">${displayName}</h2>
              <p class="body-text" style="font-size: var(--fs-sm); color: var(--text-secondary); margin: 4px 0 0;">@${profile.username}</p>
            </div>
            <p class="body-text" style="font-size: var(--fs-xs); color: var(--text-secondary); margin: 0; word-break: break-all;">${profile.email}</p>
            <p class="body-text" style="font-size: var(--fs-sm); line-height: 1.5; color: var(--text-primary); margin: 6px 0 0;">Role: ${roleLabel}</p>
            <p class="body-text" style="font-size: var(--fs-xs); color: var(--text-secondary); margin: 0;">Joined: ${createdAt}</p>
          </div>

          <div class="info-card" style="padding: 20px; display: grid; gap: 8px;">
            <button class="btn btn-outline" id="profile-logout-btn" style="width: 100%;">Sign Out</button>
          </div>
        </div>
      </div>
    `;

    bindEvents();
  }

  function bindEvents() {
    var editForm = document.getElementById('edit-profile-form');
    if (editForm) {
      editForm.addEventListener('submit', function (e) {
        e.preventDefault();
        if (isSaving) return;

        var username = document.getElementById('edit-username').value.trim();
        var email = document.getElementById('edit-email').value.trim();
        var messageEl = document.getElementById('profile-message');
        var saveBtn = document.getElementById('save-profile-btn');

        if (!username || !email) {
          if (messageEl) { messageEl.textContent = 'Please fill in all fields.'; messageEl.style.color = 'var(--cj-primary)'; }
          return;
        }

        isSaving = true;
        if (saveBtn) { saveBtn.disabled = true; saveBtn.textContent = 'Saving...'; }
        if (messageEl) { messageEl.textContent = ''; }

        window.CineJunction.profileService.updateProfile({ username: username, email: email })
          .then(function (updated) {
            currentProfile = response.data;
            if (messageEl) { messageEl.textContent = 'Profile updated successfully'; messageEl.style.color = '#4ade80'; }
            if (window.CineJunction.showToast) window.CineJunction.showToast('Profile updated successfully');
            renderProfile(response.data);
          })
          .catch(function (error) {
            if (messageEl) { messageEl.textContent = error.message || 'Failed to update profile.'; messageEl.style.color = 'var(--cj-primary)'; }
          })
          .finally(function () {
            isSaving = false;
            if (saveBtn) { saveBtn.disabled = false; saveBtn.textContent = 'Save Profile Changes'; }
          });
      });
    }

    var passwordForm = document.getElementById('change-password-form');
    if (passwordForm) {
      passwordForm.addEventListener('submit', function (e) {
        e.preventDefault();
        if (isChangingPassword) return;

        var oldPassword = document.getElementById('current-password').value;
        var newPassword = document.getElementById('new-password').value;
        var confirmPassword = document.getElementById('confirm-password').value;
        var messageEl = document.getElementById('password-message');
        var changeBtn = document.getElementById('change-password-btn');

        if (!oldPassword || !newPassword || !confirmPassword) {
          if (messageEl) { messageEl.textContent = 'Please fill in all password fields.'; messageEl.style.color = 'var(--cj-primary)'; }
          return;
        }

        if (newPassword !== confirmPassword) {
          if (messageEl) { messageEl.textContent = 'New passwords do not match.'; messageEl.style.color = 'var(--cj-primary)'; }
          return;
        }

        if (newPassword.length < 8) {
          if (messageEl) { messageEl.textContent = 'New password must be at least 8 characters long.'; messageEl.style.color = 'var(--cj-primary)'; }
          return;
        }

        isChangingPassword = true;
        if (changeBtn) { changeBtn.disabled = true; changeBtn.textContent = 'Changing...'; }
        if (messageEl) { messageEl.textContent = ''; }

        window.CineJunction.profileService.changePassword({ oldPassword: oldPassword, newPassword: newPassword })
          .then(function () {
            if (messageEl) { messageEl.textContent = 'Password changed successfully'; messageEl.style.color = '#4ade80'; }
            if (window.CineJunction.showToast) window.CineJunction.showToast('Password changed successfully');
            passwordForm.reset();
          })
          .catch(function (error) {
            if (messageEl) { messageEl.textContent = error.message || 'Failed to change password.'; messageEl.style.color = 'var(--cj-primary)'; }
          })
          .finally(function () {
            isChangingPassword = false;
            if (changeBtn) { changeBtn.disabled = false; changeBtn.textContent = 'Change Password'; }
          });
      });
    }

    var logoutBtn = document.getElementById('profile-logout-btn');
    if (logoutBtn) {
      logoutBtn.addEventListener('click', function () {
        if (window.CineJunction.logout) {
          window.CineJunction.logout();
        }
      });
    }
  }

  async function loadProfile() {
    if (!window.CineJunction.isAuthenticated || !window.CineJunction.isAuthenticated()) {
      window.location.replace(window.CineJunction.getLoginPath ? window.CineJunction.getLoginPath() : 'login.html');
      return;
    }

    renderLoading();

    try {
      var response = await window.CineJunction.profileService.getCurrentUser();
      renderProfile(response.data);
    } catch (error) {
      console.error('[Profile] API Error:', error);
      if (error.status === 401) {
        window.location.replace(window.CineJunction.getLoginPath ? window.CineJunction.getLoginPath() : 'login.html');
      } else {
        showMainMessage(error.message || 'Failed to load profile. Please try again.', true);
      }
    }
  }

  function initProfilePage() {
    loadProfile();
  }

  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initProfilePage);
  } else {
    initProfilePage();
  }
})();
