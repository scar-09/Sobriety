# Sobriety

[![Download latest](https://img.shields.io/badge/download-latest-blue)](https://github.com/scar-09/Sobriety/releases)
[![License: GPL-3.0](https://img.shields.io/badge/license-GPL--3.0-blue)](LICENSE)

Sobriety is a simple, minimal app for tracking how long you have been sober from
anything you choose. It records relapses so you can see your current streak, your
recent average, and how your progress changes over time.

This is a **standalone fork** of [seaglade/Sobriety](https://github.com/seaglade/Sobriety).
The app, its design, and its story belong entirely to
[seaglade](https://github.com/seaglade) and the upstream contributors. The original
maintainer confirmed in
[this PR thread](https://github.com/seaglade/Sobriety/pull/118#issuecomment-6249704536)
that she can no longer publish builds, so this fork provides signed releases.

*Last updated: 21 October 2026 · current version: v10.1.1 (versionCode 24)*

## Features

- **Hero streak timer** — the ongoing streak is the large number on each card,
  shown in a compact, stable format (with a full spoken phrase for screen readers)
- **Log a relapse at a previous date/time** instead of only "now"
- **Rename addictions** at any time
- **Peek the recent average** — tap the ⓘ icon on a card to show it briefly
- **Multiple daily notes per day**, grouped by day with search
- **Summary insights** — longest streak and total clean time
- **"Track now" accepts a previous date/time** for stopped or future addictions
- **Safer import/export** — merge or replace, with an automatic backup first
- **Data safety** — atomic cache writes and recovery from a `.bak` backup
- **Crash fixes** for invalid savings/milestone amounts, milestone list updates,
  and zero-progress summaries
- **Visual polish** — clearer typography, card layout, theme contrast, and
  Material You support

## What stays the same

- 100% offline: no network permission, no trackers, no accounts, no analytics
- Every feature is free
- Your data stays on your device
- Compatible with your existing Sobriety data (via export/import)

## Installing

Signed builds are available on the
[Releases page](https://github.com/scar-09/Sobriety/releases).

This fork uses its own package id (`io.github.scar09.sobriety`) and signing key, so
it installs **alongside** the original app — it does not replace or update it.
Coming from another Sobriety install? Export your data there, then use Import here
(a backup is made automatically).

## Building

The project uses Gradle 7.5, Android Gradle Plugin 7.3.1, and JDK 17.

Debug build:

```sh
./gradlew :app:assembleDebug
```

Release build:

```sh
./gradlew :app:assembleRelease
```

Gradle produces the release APK **unsigned**. Sign it with `zipalign` and
`apksigner` from the Android build-tools before distributing it. For example
(Windows paths shown; replace the keystore and alias with your own):

```powershell
$bt = "$env:ANDROID_HOME\build-tools\35.0.0"
& "$bt\zipalign.exe" -p -f 4 "app\build\outputs\apk\release\Sobriety v10.1.1-release-unsigned.apk" aligned.apk
& "$bt\apksigner.bat" sign --ks your-key.jks --ks-key-alias your-alias --out signed.apk aligned.apk
& "$bt\apksigner.bat" verify --print-certs signed.apk
```

## Upstream

The original app by seaglade is available on F-Droid:

https://f-droid.org/packages/com.katiearose.sobriety/

Credit for the original app, its design, and years of work goes entirely to
seaglade and the upstream contributors. See the upstream README's
[Project Status](https://github.com/seaglade/Sobriety#project-status) section for
background.

## License

GPL-3.0 — same as upstream. See [LICENSE](LICENSE).
