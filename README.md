# 🎮 GameGlish

![Build Status](https://github.com/vasilon/GameGlish/actions/workflows/android-ci.yml/badge.svg)
![License](https://img.shields.io/github/license/vasilon/GameGlish)

## 📋 Tabla de contenidos
- [📱 Descripción](#descripción)  
- [🚀 Instalación y Quick-start](#instalación-y-quick-start)  
- [🎮 Uso](#uso)  
- [🗂 Estructura del proyecto](#estructura-del-proyecto)  
- [📝 Documentación](#documentación)  
- [🤝 Contribuir](#contribuir)  
- [⚖️ Licencia](#licencia)  
- [🎉 Créditos](#créditos)

---

## 📱 Descripción
GameGlish es una app educativa para Android que combina mecánicas de juego y gamificación para aprender vocabulario, gramática y pronunciación de inglés de forma motivadora y competitiva.

---

## 🚀 Instalación y Quick-start

Si sólo quieres probar la app sin compilarla tú mismo, descarga la última versión estable desde nuestras Releases:  
[⬇️ Descargar APK (v0.1.0)](https://github.com/vasilon/GameGlish/releases)

---

### 📦 Descarga la APK
Descarga la última versión estable desde [GitHub Releases](https://github.com/vasilon/GameGlish/releases).

---

### 🛠️ Compilar e instalar localmente

---

```bash
# Clona el repositorio
git clone https://github.com/JavierHuelamo/GameGlish.git
cd GameGlish

# Asegúrate de tener configurado `local.properties` con la ruta de tu SDK de Android

# Ensancha y compila
./gradlew clean assembleDebug

# Instala en tu emulador o dispositivo conectado
./gradlew installDebug

```

---

## 🎮 Uso

Registro / Login

Autenticación con Firebase Auth.
Modo Individual

Responde preguntas de vocabulario y gramática por niveles.
Modo Competitivo

Desafía a otros usuarios en tiempo real vía Firebase Realtime Database.
Sistema de Puntuación y Logros

Gana puntos y avanza niveles según tu rendimiento.
Retroalimentación Personalizada

Estadísticas y recomendaciones basadas en tu historial de juego.

---

## 🗂 Estructura del proyecto



```text
GameGlish/
├─ .github/                   ← Plantillas de issues/PRs y workflows de CI
│   ├─ ISSUE_TEMPLATE/
│   └─ workflows/
│       └─ android-ci.yml
├─ app/                       ← Módulo Android principal (MVVM)
│   ├─ src/
│   │   ├─ main/java/         ← Código Kotlin & Java  
│   │   ├─ main/res/          ← Recursos UI (layouts, drawables, strings)  
│   │   ├─ androidTest/       ← Instrumented tests  
│   │   └─ test/              ← Unit tests (>80% cobertura)  
│   ├─ build.gradle.kts       ← Configuración Gradle (ktlint, detekt…)  
│   └─ proguard-rules.pro
├─ docs/                      ← Documentación, diagramas y demos
│   ├─ demo/                  ← GIFs y capturas de pantalla
│   └─ architecture/          ← UML, flujos, casos de uso
├─ scripts/                   ← Scripts de ayuda (generación de datos…)
├─ build.gradle.kts           ← Configuración raíz de Gradle
├─ settings.gradle.kts
├─ gradlew*
├─ CHANGELOG.md
├─ CONTRIBUTING.md
├─ LICENSE
└─ README.md
```

---

## 📝 Documentación
docs/: diagramas de arquitectura, flujo de pantallas, casos de uso.

Wiki GitHub: guía de estilos, decisiones de diseño, roadmap.

GitHub Pages: sitio estático con tutoriales y API docs.

---

## 🤝 Contribuir
Lee primero CONTRIBUTING.md para pautas de código, ramas y procesar PRs. Usa ktlint y detekt antes de enviar tu PR.

---

## ⚖️ Licencia
Este proyecto está bajo licencia MIT. Consulta LICENSE.

---

## 🎉 Créditos
Android Studio y Kotlin


Firebase Auth & Realtime Database

SQLite / Room

APIs de Reconocimiento de Voz de Android

Figma para diseño UI/UX

Inspiración en Duolingo y Babbel
