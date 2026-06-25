@echo off
setlocal
cd /d "%~dp0"

call build_debug_apk.bat
if errorlevel 1 exit /b 1

if "%ANDROID_HOME%"=="" (
  set "ANDROID_HOME=%LOCALAPPDATA%\Android\Sdk"
)
set "PATH=%ANDROID_HOME%\platform-tools;%PATH%"

where adb >nul 2>nul
if errorlevel 1 (
  echo Ошибка: adb не найден. Проверьте Android SDK platform-tools.
  exit /b 1
)

adb devices
adb install -r app\build\outputs\apk\debug\app-debug.apk
endlocal
