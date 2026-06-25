#!/usr/bin/env bash
set -euo pipefail

PROJECT_DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$PROJECT_DIR"

if [ -z "${ANDROID_HOME:-}" ]; then
  if [ -d "$HOME/Android/Sdk" ]; then
    export ANDROID_HOME="$HOME/Android/Sdk"
  else
    echo "Ошибка: задайте ANDROID_HOME, например: export ANDROID_HOME=\$HOME/Android/Sdk"
    exit 1
  fi
fi

export PATH="$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools:$PATH"

if ! command -v sdkmanager >/dev/null 2>&1; then
  echo "Ошибка: sdkmanager не найден. Установите Android SDK Command-Line Tools и добавьте cmdline-tools/latest/bin в PATH."
  exit 1
fi

if ! command -v gradle >/dev/null 2>&1; then
  echo "Ошибка: gradle не найден. Установите Gradle 8.7 или новее."
  exit 1
fi

echo "Устанавливаю нужные Android SDK-пакеты..."
yes | sdkmanager --licenses >/dev/null || true
sdkmanager "platform-tools" "platforms;android-35" "build-tools;34.0.0"

echo "Собираю debug APK..."
gradle assembleDebug --no-daemon

echo "Готово: app/build/outputs/apk/debug/app-debug.apk"
