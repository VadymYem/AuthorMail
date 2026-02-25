# ✉️ AuthorMail(YOU CAN USE SOFTWARE WITH TAG OF ME IN SOURCES CODE)

<div align="center">

![AuthorMail](https://img.shields.io/badge/AuthorMail-v1.0.0-1A3557?style=for-the-badge&logo=gmail&logoColor=white)
![Kotlin](https://img.shields.io/badge/Kotlin-2.0.0-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)
![Compose](https://img.shields.io/badge/Jetpack%20Compose-MD3-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)
![License](https://img.shields.io/badge/License-MIT-2D7D5A?style=for-the-badge)

**A premium Android email client scaffold with AI-powered spam detection**  
**Преміум Android шаблон поштового клієнта з ШІ-захистом від спаму**

[English](#english) • [Українська](#українська)

---

*Built by [Vadim Yemelianov (AuthorChe)](https://authorche.top)*

</div>

---

# English

## 📋 Table of Contents
- [What is AuthorMail?](#what-is-authormail)
- [Current Status](#current-status)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Features](#features)
- [Build Instructions](#build-instructions)
- [OAuth Setup](#oauth-setup)
- [Gemini AI Setup](#gemini-ai-setup)
- [Roadmap](#roadmap)
- [Contributing](#contributing)
- [Author](#author)

---

## What is AuthorMail?

AuthorMail is an **open-source Android email client scaffold** built with modern Android development best practices. It serves as a solid, well-architected foundation that any developer can fork and extend into a fully functional email application.

The project demonstrates:
- Clean Architecture + MVVM in a real-world app scenario
- Material Design 3 with Dynamic Color, dark/light themes
- Google Gemini AI integration for spam detection
- OAuth2 authentication flow (Gmail & Outlook) — same approach as Thunderbird
- DataStore-backed settings and whitelist management

> **⚠️ This is a scaffold / template project.** The UI is fully navigable as a preview. Real IMAP email fetching and SMTP sending require connecting your own credentials. See [Roadmap](#roadmap).

---

## Current Status

| Feature | Status |
|---|---|
| UI shell — Navigation Rail, all screens | ✅ Complete |
| Material Design 3 — Dynamic Color, Dark/Light theme | ✅ Complete |
| AI spam detection — Gemini API | ✅ Complete |
| Spam whitelist manager | ✅ Complete |
| Settings — API key, threshold slider, auto-block | ✅ Complete |
| OAuth2 — Gmail & Outlook, UI + token logic | ✅ Complete |
| About / Author screen | ✅ Complete |
| Demo email list | ✅ Complete |
| Real IMAP email fetching | 🔧 Scaffold ready, needs credentials |
| Real SMTP email sending | 🔧 Not yet implemented |
| Compose new email screen | 🔧 Not yet implemented |
| Email detail view | 🔧 Not yet implemented |
| Push notifications | 🔧 Not yet implemented |
| Multiple accounts | 🔧 Not yet implemented |
| Swipe actions (delete / archive) | 🔧 Not yet implemented |

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin 2.0 |
| UI Framework | Jetpack Compose + Material Design 3 |
| Architecture | MVVM + Clean Architecture |
| Dependency Injection | Hilt 2.51 |
| Navigation | Navigation Compose |
| Local Storage | DataStore Preferences |
| Networking | OkHttp 4.12 |
| Serialization | Kotlinx Serialization JSON |
| Image Loading | Coil 2.7 |
| Email Protocol | JavaMail for Android (scaffold) |
| AI | Google Gemini 2.0 Flash (REST API) |
| OAuth | Chrome Custom Tab + OkHttp token exchange |
| Min SDK | API 26 (Android 8.0 Oreo) |
| Target SDK | API 35 (Android 15) |
| Build System | Gradle 8.9 + Version Catalogs |

---

## Project Structure

```
AuthorMail/
├── build_authormail.sh                   ← One-command build script (Linux/macOS)
├── build.gradle.kts                      ← Root Gradle config
├── settings.gradle.kts                   ← Project settings + repos
├── gradle/
│   ├── libs.versions.toml               ← Version Catalog (all dependencies)
│   └── wrapper/
│       └── gradle-wrapper.properties
│
└── app/
    ├── build.gradle.kts                  ← App-level Gradle config
    └── src/main/
        ├── AndroidManifest.xml
        ├── res/
        │   └── values/
        │       ├── strings.xml
        │       ├── themes.xml
        │       └── font_certs.xml        ← Google Fonts certificates
        │
        └── kotlin/top/authorche/authormail/
            │
            ├── AuthorMailApp.kt          ← @HiltAndroidApp application class
            ├── MainActivity.kt           ← Edge-to-edge single activity
            │
            ├── di/
            │   └── AppModule.kt          ← Hilt module: OkHttpClient
            │
            ├── domain/
            │   └── model/
            │       └── Models.kt         ← Email, EmailAccount, SpamAnalysisResult,
            │                                OAuthProvider (data classes + enums)
            ├── data/
            │   ├── spam/
            │   │   ├── GeminiSpamProvider.kt  ← Gemini REST call + JSON parsing
            │   │   ├── SpamDetector.kt        ← Pipeline orchestrator
            │   │   │                            (whitelist → AI → SpamAction)
            │   │   ├── WhitelistManager.kt    ← DataStore-backed trusted senders
            │   │   └── SettingsRepository.kt  ← All settings via DataStore
            │   │
            │   ├── oauth/
            │   │   └── OAuthManager.kt        ← Gmail & Outlook OAuth2
            │   │                                openAuthBrowser() / exchangeCode()
            │   │                                / refreshToken()
            │   └── repository/
            │       └── EmailRepository.kt     ← JavaMail IMAP scaffold
            │                                    (spam pipeline integrated)
            └── ui/
                ├── theme/
                │   └── Theme.kt               ← MD3 color schemes, dynamic color
                │
                ├── navigation/
                │   └── AppNavigation.kt       ← NavHost: main / settings / about /
                │                                account
                ├── viewmodel/
                │   └── MainViewModel.kt       ← UiState, folder selection,
                │                                settings mutations, whitelist CRUD
                └── screen/
                    ├── MainScreen.kt          ← Navigation Rail + email card list
                    ├── AiSettingsScreen.kt    ← Gemini key input, AI toggles,
                    │                            disclaimer dialog, whitelist UI
                    ├── AccountSetupScreen.kt  ← OAuth tab + Manual/App Password tab
                    └── AboutScreen.kt         ← Author brand page with links
```

---

## Features

### 🎨 Material Design 3 UI

- **Dynamic Color** — adapts to wallpaper palette on Android 12+
- **Dark / Light mode** — follows system setting automatically
- **Navigation Rail** — folder navigation: Inbox, Sent, Drafts, Spam, Trash, Starred
- **Card-based email list** — unread indicators, AI spam badges, avatar initials with color hashing
- **Deep Navy `#1A3557` + Emerald `#2D7D5A` + Gold `#A07000`** — static palette for Android < 12

### 🤖 AI Spam Detection

The spam pipeline works as follows:

```
New email arrives
       │
       ▼
Is sender whitelisted? ──YES──► Deliver to Inbox (AI skipped)
       │ NO
       ▼
Is AI enabled + API key set? ──NO──► Deliver to Inbox
       │ YES
       ▼
POST to Gemini 2.0 Flash API
(sender, subject, first 500 chars of body)
       │
       ▼
Parse JSON response
{ is_spam, confidence, reason, category }
       │
       ├── confidence < threshold ──────────► Deliver normally
       ├── is_spam + conf ≥ threshold
       │   + auto-blocking OFF ────────────► FLAG → Move to Spam folder
       └── is_spam + conf ≥ threshold
           + auto-blocking ON ─────────────► TRASH → Move to Trash
```

**Key points:**
- API key stored only on-device in DataStore — never sent to AuthorMail servers
- Configurable threshold slider (50%–99%)
- Full MD3 AlertDialog disclaimer before enabling Auto-Block:
  - False positive risk
  - Data sent to Google
  - No liability
  - Whitelist tip

### 🛡️ Whitelist Manager

- Add trusted sender addresses via the Settings screen
- Whitelisted addresses **bypass all AI analysis** — always land in Inbox
- Stored in a separate DataStore instance
- Full add / remove UI

### 🔐 OAuth2 (like Thunderbird)

- Opens **Chrome Custom Tab** — the same trusted browser approach as Thunderbird
- AuthorMail never sees your email password
- Supports **Gmail** (Google Cloud Console) and **Outlook** (Azure Portal)
- Full token exchange and refresh logic in `OAuthManager.kt`
- **Manual / App Password** fallback tab for other providers

### ⚙️ Settings Screen

- Gemini API key field (masked, toggle visibility, save button)
- Inline privacy notice on the key field
- AI spam detection toggle
- Confidence threshold slider
- Auto-block toggle with mandatory disclaimer dialog
- Full whitelist management

### 👤 About / Author Screen

- Animated gradient hero header
- Links to all AuthorChe resources

---

## Build Instructions

### Requirements

| Tool | Notes |
|---|---|
| OS | Ubuntu 20.04+ or macOS 12+ |
| Java 17 | Auto-installed by script if missing |
| Android SDK | Auto-downloaded by script |
| Gradle | Auto-installed by script |
| Internet | ~2 GB on first run |

### Option A — One-Command Build Script (Linux / macOS)

```bash
# Clone the repository
git clone https://github.com/VadymYem/AuthorMail.git
cd AuthorMail

# Run the build script
bash build_authormail.sh
```

The script performs these steps automatically:

| Step | Action |
|---|---|
| 1 | Install Java 17 via `apt` if missing |
| 2 | Download Android SDK command-line tools |
| 3 | Accept SDK licenses + install `platforms;android-35`, `build-tools;35.0.0` |
| 4 | Generate Gradle Wrapper via system `gradle wrapper` (real jar) |
| 5 | Create full project directory structure |
| 6 | Write all Gradle config files (`settings.gradle.kts`, `libs.versions.toml`, etc.) |
| 7 | Write all Kotlin source files |
| 8 | Write `AndroidManifest.xml` and resource files |
| 9 | Run `./gradlew assembleDebug` |
| 10 | Print APK path on success |

**First run:** 15–30 minutes (Gradle + Maven dependency downloads)  
**Subsequent runs:** ~30 seconds (incremental build)

**Output:**
```
~/AuthorMail/app/build/outputs/apk/debug/app-debug.apk
```

### Option B — Android Studio

1. Clone the repo
2. Open **Android Studio Ladybug** (2024.2.1+) or newer
3. **File → Open** → select the `AuthorMail/` folder
4. Wait for Gradle sync to complete
5. **Build → Build Bundle(s) / APK(s) → Build APK(s)**

### Install on Device

```bash
# Enable USB Debugging on your phone (Settings → Developer Options)
adb install ~/AuthorMail/app/build/outputs/apk/debug/app-debug.apk

# Or transfer the APK file to your phone and tap to install
```

---

## OAuth Setup

> This step is only needed if you want to enable real Gmail or Outlook sign-in.

### Gmail

1. Go to [console.cloud.google.com](https://console.cloud.google.com)
2. Create a project → **APIs & Services → Library** → enable **Gmail API**
3. **Credentials → Create Credentials → OAuth 2.0 Client ID**
4. Application type: **Android**
5. Package name: `top.authorche.authormail`
6. Add redirect URI: `top.authorche.authormail://oauth2callback`
7. Copy the **Client ID** (and Client Secret if shown)
8. Open `app/src/main/kotlin/.../data/oauth/OAuthManager.kt`
9. Replace the placeholders:
   ```kotlin
   const val GMAIL_CLIENT_ID     = "YOUR_CLIENT_ID.apps.googleusercontent.com"
   const val GMAIL_CLIENT_SECRET = "YOUR_CLIENT_SECRET"
   ```
10. Rebuild the app

### Outlook / Microsoft

1. Go to [portal.azure.com](https://portal.azure.com) → **App registrations → New registration**
2. Name: `AuthorMail` | Supported accounts: **Personal Microsoft accounts**
3. Redirect URI: `top.authorche.authormail://oauth2callback` (Mobile and desktop)
4. **API Permissions → Add a permission → Microsoft Graph → Delegated:**
   - `IMAP.AccessAsUser.All`
   - `offline_access`
5. Copy the **Application (client) ID**
6. In `OAuthManager.kt` replace:
   ```kotlin
   const val OUTLOOK_CLIENT_ID = "YOUR_OUTLOOK_CLIENT_ID"
   ```
7. Rebuild

---

## Gemini AI Setup

1. Go to [aistudio.google.com](https://aistudio.google.com)
2. Sign in with Google → **Get API Key → Create API key in new project**
3. Copy the key (starts with `AIza...`)
4. In AuthorMail → tap **Settings** (gear icon in Navigation Rail)
5. Paste into **"Your Gemini API Key"** → tap Save
6. Enable **"AI Spam Detection"** toggle

### The Internal System Prompt

Every spam check sends this hidden instruction alongside the email data:

```
You are a professional email security analyst.
Analyze the email below and determine if it is spam, phishing, or unwanted content.

Consider: suspicious links, manipulative language, money promises, unknown senders,
credential requests, urgent fake warnings.

Reply ONLY with a valid JSON object — no markdown, no text outside JSON:
{"is_spam": true/false, "confidence": 0.0-1.0, "reason": "short reason", "category": "spam|phishing|promo|personal|unknown"}
```

---

## Roadmap

- [ ] Real IMAP fetch (JavaMail with OAuth access token)
- [ ] Real SMTP send
- [ ] Compose new email screen
- [ ] Reply / Forward
- [ ] Full email detail view (HTML body rendering)
- [ ] Multiple accounts
- [ ] Background sync + Push notifications (WorkManager + FCM)
- [ ] Attachment support (view + send)
- [ ] Full-text search
- [ ] Swipe actions (archive / delete / spam)
- [ ] Signed release APK + Play Store listing
- [ ] Tablet / foldable adaptive layout (NavigationDrawer on wide screens)

---

## Contributing

Pull requests are welcome!

```bash
# Fork and clone
git clone https://github.com/VadymYem/AuthorMail.git

# Create a feature branch
git checkout -b feature/real-imap-fetch

# Make changes, then commit
git commit -m "feat: implement real IMAP fetching with OAuth token"

# Push and open a Pull Request
git push origin feature/real-imap-fetch
```

Please keep PRs focused on one feature at a time.

---

## Author

**Vadim Yemelianov** (AuthorChe)

| | |
|---|---|
| 🌐 Main Site | [authorche.top](https://authorche.top) |
| 🎶 Music | [authorche.top/music](https://authorche.top/music) |
| ✍️ Poems | [authorche.top/poems](https://authorche.top/poems) |
| 🌎 All Links | [authorche.top/links](https://authorche.top/links) |
| 💼 Dev Services | [authorche.top/dev](https://authorche.top/dev) |

---

## License

MIT License — free to use, modify, and distribute.

---
---

# Українська

## 📋 Зміст
- [Що таке AuthorMail?](#що-таке-authormail)
- [Поточний стан](#поточний-стан)
- [Технологічний стек](#технологічний-стек)
- [Структура проєкту](#структура-проєкту)
- [Функціонал](#функціонал)
- [Інструкція зі збірки](#інструкція-зі-збірки)
- [Налаштування OAuth](#налаштування-oauth)
- [Налаштування Gemini AI](#налаштування-gemini-ai)
- [Плани розвитку](#плани-розвитку)
- [Автор](#автор)

---

## Що таке AuthorMail?

AuthorMail — це **відкритий шаблон Android поштового клієнта**, побудований за сучасними стандартами розробки. Це добре архітектурована основа, яку будь-який розробник може форкнути і розширити до повноцінного поштового застосунку.

Проєкт демонструє:
- Clean Architecture + MVVM у реальному сценарії
- Material Design 3 з Dynamic Color і темною/світлою темами
- Інтеграцію з Google Gemini AI для виявлення спаму
- OAuth2 авторизацію (Gmail і Outlook) — такий самий підхід, як у Thunderbird
- Управління налаштуваннями і білим списком через DataStore

> **⚠️ Це шаблон / каркас проєкту.** Інтерфейс повністю працює як попередній перегляд. Реальне отримання листів через IMAP і відправка через SMTP потребують підключення облікових даних. Дивіться [Плани розвитку](#плани-розвитку).

---

## Поточний стан

| Функція | Статус |
|---|---|
| UI оболонка — Navigation Rail, всі екрани | ✅ Готово |
| Material Design 3 — Dynamic Color, темна/світла тема | ✅ Готово |
| ШІ-виявлення спаму — Gemini API | ✅ Готово |
| Менеджер білого списку | ✅ Готово |
| Налаштування — ключ API, слайдер порогу, автоблок | ✅ Готово |
| OAuth2 — Gmail і Outlook, UI + логіка токенів | ✅ Готово |
| Екран "Про автора" | ✅ Готово |
| Демо-список листів | ✅ Готово |
| Реальне отримання листів через IMAP | 🔧 Каркас є, потрібні облікові дані |
| Реальна відправка листів через SMTP | 🔧 Не реалізовано |
| Екран написання листа | 🔧 Не реалізовано |
| Детальний перегляд листа | 🔧 Не реалізовано |
| Push-сповіщення | 🔧 Не реалізовано |
| Кілька акаунтів | 🔧 Не реалізовано |
| Свайп-дії (видалити / архівувати) | 🔧 Не реалізовано |

---

## Технологічний стек

| Шар | Технологія |
|---|---|
| Мова | Kotlin 2.0 |
| UI | Jetpack Compose + Material Design 3 |
| Архітектура | MVVM + Clean Architecture |
| DI | Hilt 2.51 |
| Навігація | Navigation Compose |
| Локальне сховище | DataStore Preferences |
| Мережа | OkHttp 4.12 |
| Серіалізація | Kotlinx Serialization JSON |
| Завантаження зображень | Coil 2.7 |
| Поштовий протокол | JavaMail for Android (каркас) |
| ШІ | Google Gemini 2.0 Flash (REST API) |
| OAuth | Chrome Custom Tab + обмін токенів через OkHttp |
| Мін. SDK | API 26 (Android 8.0 Oreo) |
| Цільовий SDK | API 35 (Android 15) |
| Система збірки | Gradle 8.9 + Version Catalogs |

---

## Структура проєкту

```
AuthorMail/
├── build_authormail.sh                   ← Скрипт збірки однією командою
├── build.gradle.kts                      ← Кореневий Gradle конфіг
├── settings.gradle.kts                   ← Налаштування проєкту + репозиторії
├── gradle/
│   ├── libs.versions.toml               ← Version Catalog (всі залежності)
│   └── wrapper/gradle-wrapper.properties
│
└── app/
    ├── build.gradle.kts                  ← Gradle конфіг застосунку
    └── src/main/
        ├── AndroidManifest.xml
        ├── res/values/
        │   ├── strings.xml
        │   ├── themes.xml
        │   └── font_certs.xml            ← Сертифікати Google Fonts
        │
        └── kotlin/top/authorche/authormail/
            │
            ├── AuthorMailApp.kt          ← @HiltAndroidApp клас застосунку
            ├── MainActivity.kt           ← Edge-to-edge єдина активність
            │
            ├── di/
            │   └── AppModule.kt          ← Hilt модуль: OkHttpClient
            │
            ├── domain/model/
            │   └── Models.kt             ← Email, EmailAccount, SpamAnalysisResult,
            │                                OAuthProvider
            ├── data/
            │   ├── spam/
            │   │   ├── GeminiSpamProvider.kt  ← REST запит до Gemini + парсинг
            │   │   ├── SpamDetector.kt        ← Оркестратор (білий список → ШІ → дія)
            │   │   ├── WhitelistManager.kt    ← Білий список через DataStore
            │   │   └── SettingsRepository.kt  ← Всі налаштування через DataStore
            │   │
            │   ├── oauth/
            │   │   └── OAuthManager.kt        ← OAuth2 для Gmail і Outlook
            │   │
            │   └── repository/
            │       └── EmailRepository.kt     ← Каркас JavaMail IMAP
            │
            └── ui/
                ├── theme/Theme.kt             ← Кольорові схеми MD3, dynamic color
                ├── navigation/AppNavigation.kt← NavHost з усіма екранами
                ├── viewmodel/MainViewModel.kt ← Стан UI, мутації налаштувань
                └── screen/
                    ├── MainScreen.kt          ← Navigation Rail + список листів
                    ├── AiSettingsScreen.kt    ← Ключ Gemini, перемикачі, білий список
                    ├── AccountSetupScreen.kt  ← OAuth + ручне налаштування
                    └── AboutScreen.kt         ← Сторінка автора з посиланнями
```

---

## Функціонал

### 🎨 Інтерфейс Material Design 3

- **Dynamic Color** — адаптується до кольору шпалер на Android 12+
- **Темна / Світла тема** — автоматично слідує системним налаштуванням
- **Navigation Rail** — навігація по папках (Вхідні, Відправлені, Чернетки, Спам, Кошик, Зірочки)
- **Картки листів** — індикатори непрочитаних, значки спаму, кольорові аватари з ініціалами
- **Deep Navy `#1A3557` + Emerald `#2D7D5A` + Gold `#A07000`** — статична палітра для Android < 12

### 🤖 ШІ-виявлення спаму

Пайплайн обробки спаму:

```
Надходить новий лист
       │
       ▼
Відправник у білому списку? ──ТАК──► Доставити у Вхідні (ШІ пропущено)
       │ НІ
       ▼
ШІ увімкнено + є ключ API? ──НІ──► Доставити у Вхідні
       │ ТАК
       ▼
POST до Gemini 2.0 Flash API
(відправник, тема, перші 500 символів тіла)
       │
       ▼
Парсинг JSON відповіді
{ is_spam, confidence, reason, category }
       │
       ├── довіра < поріг ──────────────────► Доставити нормально
       ├── is_spam + довіра ≥ поріг
       │   + автоблок ВИМКНЕНО ──────────────► ПОЗНАЧИТИ → перемістити у Спам
       └── is_spam + довіра ≥ поріг
           + автоблок УВІМКНЕНО ─────────────► КОШИК → перемістити в Кошик
```

**Ключові моменти:**
- API ключ зберігається лише на пристрої в DataStore — не передається на сервери AuthorMail
- Настроюваний поріг впевненості (слайдер 50%–99%)
- Повний MD3 AlertDialog з попередженням перед увімкненням автоблоку:
  - Ризик хибних спрацювань
  - Дані передаються Google
  - Відмова від гарантій
  - Підказка про білий список

### 🛡️ Менеджер білого списку

- Додайте довірені адреси через екран Налаштувань
- Адреси з білого списку **повністю обходять аналіз ШІ** — завжди потрапляють у Вхідні
- Зберігається в окремому екземплярі DataStore
- Повний UI для додавання та видалення

### 🔐 OAuth2 (як у Thunderbird)

- Відкриває **Chrome Custom Tab** — той самий підхід, що й у Thunderbird
- AuthorMail ніколи не бачить ваш пароль
- Підтримка **Gmail** (Google Cloud Console) і **Outlook** (Azure Portal)
- Повна логіка обміну токенів і оновлення в `OAuthManager.kt`
- Вкладка **Ручне налаштування / Пароль застосунку** для інших провайдерів

### ⚙️ Екран налаштувань

- Поле для ключа Gemini API (маскований, кнопка показу, кнопка збереження)
- Вбудоване повідомлення про конфіденційність
- Перемикач ШІ-аналізу спаму
- Слайдер порогу впевненості
- Перемикач автоблокування з обов'язковим діалогом-попередженням
- Повне управління білим списком

### 👤 Екран "Про автора"

- Анімований градієнтний заголовок-герой
- Картки-посилання на всі ресурси AuthorChe

---

## Інструкція зі збірки

### Вимоги

| Інструмент | Примітки |
|---|---|
| ОС | Ubuntu 20.04+ або macOS 12+ |
| Java 17 | Скрипт встановлює автоматично |
| Android SDK | Скрипт завантажує автоматично |
| Gradle | Скрипт встановлює автоматично |
| Інтернет | ~2 ГБ при першому запуску |

### Варіант А — Скрипт збірки однією командою (Linux / macOS)

```bash
# Клонуй репозиторій
git clone https://github.com/VadymYem/AuthorMail.git
cd AuthorMail

# Запусти скрипт
bash build_authormail.sh
```

Скрипт виконує ці кроки автоматично:

| Крок | Дія |
|---|---|
| 1 | Встановлює Java 17 через `apt` якщо відсутня |
| 2 | Завантажує Android SDK command-line tools |
| 3 | Приймає ліцензії SDK, встановлює `platforms;android-35` і `build-tools;35.0.0` |
| 4 | Генерує Gradle Wrapper через системний `gradle wrapper` (справжній jar) |
| 5 | Створює повну структуру директорій проєкту |
| 6 | Записує всі Gradle конфігураційні файли |
| 7 | Записує весь Kotlin вихідний код |
| 8 | Записує `AndroidManifest.xml` і ресурси |
| 9 | Запускає `./gradlew assembleDebug` |
| 10 | Виводить шлях до APK при успіху |

**Перший запуск:** 15–30 хвилин  
**Наступні запуски:** ~30 секунд

**Результат:**
```
~/AuthorMail/app/build/outputs/apk/debug/app-debug.apk
```

### Варіант Б — Android Studio

1. Клонуй репозиторій
2. Відкрий **Android Studio Ladybug** (2024.2.1+) або новіший
3. **File → Open** → вибери папку `AuthorMail/`
4. Зачекай завершення синхронізації Gradle
5. **Build → Build Bundle(s) / APK(s) → Build APK(s)**

### Встановлення на пристрій

```bash
# Увімкни налагодження USB на телефоні
# (Налаштування → Для розробників → Налагодження USB)
adb install ~/AuthorMail/app/build/outputs/apk/debug/app-debug.apk

# Або скопіюй APK на телефон і натисни для встановлення
```

---

## Налаштування OAuth

> Цей крок потрібен лише для реального входу через Gmail або Outlook.

### Gmail

1. Відкрий [console.cloud.google.com](https://console.cloud.google.com)
2. Створи проєкт → **APIs & Services → Library** → увімкни **Gmail API**
3. **Credentials → Create Credentials → OAuth 2.0 Client ID**
4. Тип: **Android** | Package name: `top.authorche.authormail`
5. Redirect URI: `top.authorche.authormail://oauth2callback`
6. Скопіюй **Client ID**
7. Відкрий `data/oauth/OAuthManager.kt`, встав:
   ```kotlin
   const val GMAIL_CLIENT_ID     = "твій-id.apps.googleusercontent.com"
   const val GMAIL_CLIENT_SECRET = "твій-секрет"
   ```
8. Перезбери застосунок

### Outlook

1. Відкрий [portal.azure.com](https://portal.azure.com) → **App registrations → New registration**
2. Назва: `AuthorMail` | Тип: особисті Microsoft акаунти
3. Redirect URI: `top.authorche.authormail://oauth2callback` (Mobile and desktop)
4. **API Permissions → Add → Microsoft Graph → Delegated:**
   - `IMAP.AccessAsUser.All`
   - `offline_access`
5. Скопіюй **Application (client) ID**, встав в `OAuthManager.kt`:
   ```kotlin
   const val OUTLOOK_CLIENT_ID = "твій-outlook-client-id"
   ```
6. Перезбери застосунок

---

## Налаштування Gemini AI

1. Відкрий [aistudio.google.com](https://aistudio.google.com)
2. Увійди з Google акаунтом → **Get API Key → Create API key**
3. Скопіюй ключ (починається з `AIza...`)
4. У застосунку: **Settings** (іконка шестерні) → поле "Your Gemini API Key" → Save
5. Увімкни перемикач **"AI Spam Detection"**

> **Конфіденційність:** Ключ зберігається лише в DataStore на твоєму пристрої, ніколи не передається на сервери AuthorMail. Google отримує: адресу відправника, тему і перші 500 символів листа.

### Внутрішній системний промт

```
You are a professional email security analyst.
Analyze the email below and determine if it is spam, phishing, or unwanted content.

Consider: suspicious links, manipulative language, money promises, unknown senders,
credential requests, urgent fake warnings.

Reply ONLY with a valid JSON object — no markdown, no text outside JSON:
{"is_spam": true/false, "confidence": 0.0-1.0, "reason": "short reason", "category": "spam|phishing|promo|personal|unknown"}
```

---

## Плани розвитку

- [ ] Реальне отримання листів через IMAP (JavaMail з OAuth токеном)
- [ ] Реальна відправка через SMTP
- [ ] Екран написання нового листа
- [ ] Відповідь / Переслати
- [ ] Повний перегляд листа (рендеринг HTML тіла)
- [ ] Кілька акаунтів
- [ ] Фонова синхронізація + Push-сповіщення (WorkManager + FCM)
- [ ] Підтримка вкладень
- [ ] Пошук по листах
- [ ] Свайп-дії (архів / видалити / спам)
- [ ] Підписаний release APK + публікація в Google Play
- [ ] Адаптивний макет для планшетів (NavigationDrawer на широких екранах)

---

## Автор

**Вадим Ємельянов** (AuthorChe)

| | |
|---|---|
| 🌐 Сайт | [authorche.top](https://authorche.top) |
| 🎶 Музика | [authorche.top/music](https://authorche.top/music) |
| ✍️ Поезія | [authorche.top/poems](https://authorche.top/poems) |
| 🌎 Посилання | [authorche.top/links](https://authorche.top/links) |
| 💼 Послуги | [authorche.top/dev](https://authorche.top/dev) |

---

## Ліцензія

```
MIT License — вільне використання, модифікація та розповсюдження.
```
