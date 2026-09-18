#!/usr/bin/env bash
# Локальный запуск Kotler. Нужен JDK 17. Секреты — из Ai/.env
set -euo pipefail
cd "$(dirname "$0")"

load_env() {
  if [[ ! -f .env ]]; then
    echo "Нет файла .env — скопируйте:  cp .env.example .env"
    echo "Заполните DEEPSEEK_API_KEY, POSTGRES_PASSWORD, TOKEN_SIGNING_KEY"
    exit 1
  fi
  set -a
  # shellcheck disable=SC1091
  source .env
  set +a
}

pick_java17() {
  if [[ -n "${JAVA_HOME:-}" && -x "$JAVA_HOME/bin/java" ]]; then
    ver="$("$JAVA_HOME/bin/java" -version 2>&1 | head -1 || true)"
    if echo "$ver" | grep -q '"17\.'; then
      return 0
    fi
  fi
  if command -v /usr/libexec/java_home >/dev/null 2>&1; then
    if home="$(/usr/libexec/java_home -v 17 2>/dev/null)"; then
      export JAVA_HOME="$home"
      export PATH="$JAVA_HOME/bin:$PATH"
      return 0
    fi
  fi
  for cand in \
    /opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home \
    /usr/local/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home
  do
    if [[ -x "$cand/bin/java" ]]; then
      export JAVA_HOME="$cand"
      export PATH="$JAVA_HOME/bin:$PATH"
      return 0
    fi
  done
  return 1
}

load_env

if ! pick_java17; then
  echo "Нужен JDK 17. Установите: brew install openjdk@17"
  echo "Потом: export JAVA_HOME=\"\$(/usr/libexec/java_home -v 17)\""
  exit 1
fi

if [[ -z "${TOKEN_SIGNING_KEY:-}" ]]; then
  echo "В .env пустой TOKEN_SIGNING_KEY"
  exit 1
fi
if [[ -z "${POSTGRES_PASSWORD:-}" ]]; then
  echo "В .env пустой POSTGRES_PASSWORD"
  exit 1
fi

echo "Java: $(java -version 2>&1 | head -1)"
echo "Секреты загружены из .env (DeepSeek key: ${DEEPSEEK_API_KEY:+задан}${DEEPSEEK_API_KEY:-НЕ ЗАДАН})"
echo "Откройте http://localhost:8080"
echo "Postgres: docker compose up -d postgres-tables"

exec ./mvnw spring-boot:run "$@"
