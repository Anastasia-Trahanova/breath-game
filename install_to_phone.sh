#!/usr/bin/env bash
set -euo pipefail

PROJECT_DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$PROJECT_DIR"

./build_debug_apk.sh

if [ -z "${ANDROID_HOME:-}" ] && [ -d "$HOME/Android/Sdk" ]; then
  export ANDROID_HOME="$HOME/Android/Sdk"
fi
export PATH="${ANDROID_HOME:-$HOME/Android/Sdk}/platform-tools:$PATH"

if ! command -v adb >/dev/null 2>&1; then
  echo "Ошибка: adb не найден. Проверьте Android SDK platform-tools."
  exit 1
fi

adb devices
adb install -r app/build/outputs/apk/debug/app-debug.apk
