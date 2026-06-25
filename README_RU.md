# BreathGameAndroid — сборка без Android Studio

Это минимальный Android-проект для вашей игры. Его можно собрать без Android Studio:

1. локально из терминала;
2. в облаке через GitHub Actions.

## Вариант 1: локальная сборка из терминала

### Что нужно установить один раз

- JDK 17
- Gradle 8.7 или новее
- Android SDK Command-Line Tools

После установки Android SDK задайте переменную `ANDROID_HOME`.

Примеры путей:

- Windows: `C:\Users\ВАШ_ПОЛЬЗОВАТЕЛЬ\AppData\Local\Android\Sdk`
- macOS/Linux: `$HOME/Android/Sdk`

В `PATH` желательно добавить:

- `$ANDROID_HOME/cmdline-tools/latest/bin`
- `$ANDROID_HOME/platform-tools`

### Команды macOS/Linux

```bash
cd BreathGameAndroid_CLI
chmod +x build_debug_apk.sh install_to_phone.sh
./build_debug_apk.sh
```

APK появится здесь:

```text
app/build/outputs/apk/debug/app-debug.apk
```

Установка на подключённый телефон:

```bash
./install_to_phone.sh
```

На телефоне должны быть включены “Параметры разработчика” и “Отладка по USB”.

### Команды Windows PowerShell / CMD

```bat
cd BreathGameAndroid_CLI
build_debug_apk.bat
```

APK появится здесь:

```text
app\build\outputs\apk\debug\app-debug.apk
```

Установка на подключённый телефон:

```bat
install_to_phone.bat
```

## Вариант 2: сборка APK в облаке через GitHub Actions

Это самый удобный способ, если не хочется ставить Android SDK локально.

1. Создайте пустой репозиторий на GitHub.
2. Загрузите туда содержимое этой папки.
3. Откройте вкладку **Actions**.
4. Запустите workflow **Build APK** вручную через **Run workflow**.
5. После завершения откройте запуск workflow и скачайте artifact `breathgame-debug-apk`.
6. Внутри будет `app-debug.apk`.

Workflow уже лежит в:

```text
.github/workflows/build-apk.yml
```

## Что внутри проекта

- `app/src/main/java/com/example/breathgame/GameView.java` — игровая логика.
- `app/src/main/java/com/example/breathgame/MainActivity.java` — стартовый экран.
- `app/src/main/res/layout/activity_main.xml` — разметка.
- `app/src/main/AndroidManifest.xml` — регистрация приложения и разрешение на вибрацию.
- `build.gradle`, `settings.gradle`, `app/build.gradle` — Gradle-сборка.

## Полезно знать

Проект использует:

- `compileSdk 35`
- `targetSdk 35`
- `minSdk 23`
- Android Gradle Plugin `8.6.1`

Для Android Gradle Plugin 8.6 нужна Java 17 и Gradle 8.7 или новее.
