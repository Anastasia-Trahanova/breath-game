@echo off
setlocal
cd /d "%~dp0"

if "%ANDROID_HOME%"=="" (
  set "ANDROID_HOME=%LOCALAPPDATA%\Android\Sdk"
)

set "PATH=%ANDROID_HOME%\cmdline-tools\latest\bin;%ANDROID_HOME%\platform-tools;%PATH%"

where sdkmanager >nul 2>nul
if errorlevel 1 (
  echo Ошибка: sdkmanager не найден. Установите Android SDK Command-Line Tools и добавьте cmdline-tools\latest\bin в PATH.
  exit /b 1
)

where gradle >nul 2>nul
if errorlevel 1 (
  echo Ошибка: gradle не найден. Установите Gradle 8.7 или новее.
  exit /b 1
)

echo Устанавливаю нужные Android SDK-пакеты...
echo y|sdkmanager --licenses >nul
sdkmanager "platform-tools" "platforms;android-35" "build-tools;34.0.0"
if errorlevel 1 exit /b 1

echo Собираю debug APK...
gradle assembleDebug --no-daemon
if errorlevel 1 exit /b 1

echo Готово: app\build\outputs\apk\debug\app-debug.apk
endlocal
