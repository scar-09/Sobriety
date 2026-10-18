# Sobriety (fork)

[![Latest release](https://img.shields.io/github/v/release/scar-09/Sobriety?label=release)](https://github.com/scar-09/Sobriety/releases)
[![License: GPL-3.0](https://img.shields.io/badge/license-GPL--3.0-blue)](LICENSE)

Sobriety is a simple, minimal app to track how long you've been sober from anything you
want. It tells you how long it's been since you logged a relapse and the average time
between your most recent relapses, so you have more to work with in recovery.

This is a **standalone fork** of
[seaglade/Sobriety](https://github.com/seaglade/Sobriety). The app, its design, and its
story belong entirely to [seaglade](https://github.com/seaglade) and the upstream
contributors. The original app's maintainer confirmed in
[this PR thread](https://github.com/seaglade/Sobriety/pull/118#issuecomment-6249704536)
that she can't publish new builds, so this fork provides them — signed APKs are
published on the [Releases page](https://github.com/scar-09/Sobriety/releases).

*Last updated: 18 October 2026*

## What this fork adds

- **Log a relapse at a previous date/time** instead of only "now"
- **Rename addictions** at any time
- **Multiple daily notes per day**, grouped by day with collapse/expand
- **Search** across daily notes
- **Summary insights**: longest streak and total clean time
- **"Track now" accepts a previous date/time** (stopped and future addictions)
- **Safer import/export**: merge or replace, with an automatic backup before changes
- **Data safety**: atomic cache writes and recovery from a `.bak` backup if the cache
  is ever corrupt
- **Crash fixes**: invalid savings and milestone amounts no longer crash the app, and
  milestone lists update correctly
- **Visual polish**: clearer typography hierarchy, card layout and theme contrast
  (including Material You)

## What stays the same

- 100% offline: no network permission, no trackers, no accounts, no analytics
- Every feature free
- Your data stays on your device
- Compatible with your existing Sobriety data (via export/import)

## Installing

- Signed builds are on the [Releases page](https://github.com/scar-09/Sobriety/releases).
- The fork has its own package id (`io.github.scar09.sobriety`) and signing key, so it
  installs **alongside** the original app — it does not replace or update it.

## Upstream

The original app by seaglade is available on F-Droid:

https://f-droid.org/packages/com.katiearose.sobriety/

Credit for the original app, its design and years of work goes entirely to seaglade and
the upstream contributors.

## Building

The project uses Gradle 7.5 and Android Gradle Plugin 7.3.1, which require JDK 17.

Debug build:

```
./gradlew :app:assembleDebug
```

Release build (Gradle produces it unsigned; sign with your own key before installing):

```
./gradlew :app:assembleRelease
```

## License

GPL-3.0 — same as upstream. See [LICENSE](LICENSE). All credit for the original project
goes to seaglade and its contributors.
