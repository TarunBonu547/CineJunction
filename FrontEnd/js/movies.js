document.addEventListener('DOMContentLoaded', () => {
  const searchInputs = Array.from(document.querySelectorAll('input[type="search"]'));
  const chips = Array.from(document.querySelectorAll('.chip'));
  const buttons = Array.from(document.querySelectorAll('.btn-outline, .btn-icon'));

  searchInputs.forEach((input) => {
    input.addEventListener('focus', () => input.parentElement?.classList.add('is-focused'));
    input.addEventListener('blur', () => input.parentElement?.classList.remove('is-focused'));
  });

  chips.forEach((chip) => {
    chip.addEventListener('click', () => {
      const group = chip.closest('.chip-row');
      if (!group) return;
      group.querySelectorAll('.chip').forEach((item) => item.classList.remove('is-active'));
      chip.classList.add('is-active');
    });
  });

  buttons.forEach((button) => {
    button.addEventListener('click', () => {
      button.classList.toggle('is-active');
    });
  });
});
