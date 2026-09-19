/**
 * Клиент Kotler API — auth + чат для UI из Design Canvas.
 * Базовый URL: тот же origin (Spring Boot), либо window.KOTLER_API_BASE.
 */
(function (global) {
  const BASE = () => (global.KOTLER_API_BASE || "").replace(/\/$/, "");
  const TOKENS_KEY = "tokens";

  function getTokens() {
    try {
      const raw = localStorage.getItem(TOKENS_KEY);
      return raw ? JSON.parse(raw) : null;
    } catch (_) {
      return null;
    }
  }

  function saveTokens(data, username) {
    const payload = {
      accessToken: data.token,
      refreshToken: data.refreshToken,
      id: data.id,
      username: username || getTokens()?.username || null
    };
    localStorage.setItem(TOKENS_KEY, JSON.stringify(payload));
    return payload;
  }

  function clearTokens() {
    localStorage.removeItem(TOKENS_KEY);
  }

  function isLoggedIn() {
    return !!(getTokens()?.accessToken);
  }

  async function request(path, options = {}) {
    const headers = Object.assign({ "Content-Type": "application/json" }, options.headers || {});
    const tokens = getTokens();
    if (tokens?.accessToken) {
      headers.Authorization = "Bearer " + tokens.accessToken;
    }
    const res = await fetch(BASE() + path, Object.assign({}, options, { headers }));
    if (res.status === 401 && tokens?.refreshToken && path !== "/auth/refresh") {
      const refreshed = await refresh();
      if (refreshed) {
        headers.Authorization = "Bearer " + getTokens().accessToken;
        return fetch(BASE() + path, Object.assign({}, options, { headers }));
      }
    }
    return res;
  }

  async function login(username, password) {
    const res = await fetch(BASE() + "/auth/login", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ username, password })
    });
    if (!res.ok) {
      throw new Error(await friendlyAuthError(res, "Неверный логин или пароль"));
    }
    const data = await res.json();
    return saveTokens(data, username);
  }

  async function register(username, email, password) {
    const res = await fetch(BASE() + "/auth/reg", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ username, email, password })
    });
    if (!res.ok) {
      throw new Error(await friendlyAuthError(res, "Не удалось зарегистрироваться"));
    }
    const data = await res.json();
    return saveTokens(data, username);
  }

  async function friendlyAuthError(res, fallback) {
    if (res.status === 405) {
      return "Сервер входа недоступен. Обновите страницу или зайдите позже.";
    }
    if (res.status >= 500) {
      return "Сервис временно недоступен. Попробуйте через минуту.";
    }
    const err = (await res.text()).trim();
    if (!err || err.startsWith("<!") || err.startsWith("{")) {
      try {
        const j = JSON.parse(err);
        return j.message || j.error || fallback;
      } catch (_) {
        return fallback;
      }
    }
    return err.length > 180 ? fallback : err;
  }

  async function refresh() {
    const tokens = getTokens();
    if (!tokens?.refreshToken) return null;
    const res = await fetch(BASE() + "/auth/refresh", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ refreshToken: tokens.refreshToken })
    });
    if (!res.ok) {
      clearTokens();
      return null;
    }
    const data = await res.json();
    return saveTokens(data, tokens.username);
  }

  async function ensureChat(chatName) {
    const listRes = await request("/api/chats");
    if (!listRes.ok) throw new Error("Не удалось получить чаты");
    const chats = await listRes.json();
    const name = chatName || "Kotler";
    if (Array.isArray(chats)) {
      const found = chats.find((c) => c && c.chatName === name);
      if (found) return found;
    }
    const createRes = await request("/api/chats", {
      method: "POST",
      body: JSON.stringify({ chatName: name })
    });
    if (!createRes.ok) throw new Error("Не удалось создать чат");
    return createRes.json();
  }

  async function askKotler(text, chatName) {
    const chat = await ensureChat(chatName);
    const res = await request("/api/chats/" + chat.id + "/messages", {
      method: "POST",
      body: JSON.stringify({
        messageText: text,
        messageType: "USER_MESSAGE"
      })
    });
    if (!res.ok) {
      const err = await res.text();
      throw new Error(err || "Ошибка ответа Kotler");
    }
    const msg = await res.json();
    return msg.messageText || msg.text || "";
  }

  global.KotlerApi = {
    getTokens,
    saveTokens,
    clearTokens,
    isLoggedIn,
    login,
    register,
    refresh,
    ensureChat,
    askKotler
  };
})(window);
