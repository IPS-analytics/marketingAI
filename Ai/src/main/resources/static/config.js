/** Базовый URL API.
 *  Пусто = тот же origin (UI с Spring на Render / localhost).
 *  На Vercel UI ходит на Render — иначе /auth/* даёт 405 на статике.
 *  Переопределение: window.KOTLER_API_BASE = "https://ваш-сервис.onrender.com" до этого скрипта.
 */
(function () {
  if (window.KOTLER_API_BASE) return;
  var host = (typeof location !== "undefined" && location.hostname) || "";
  var sameOrigin =
    host === "localhost" ||
    host === "127.0.0.1" ||
    /\.onrender\.com$/i.test(host);
  window.KOTLER_API_BASE = sameOrigin ? "" : "https://kotler-api.onrender.com";
})();
