# Sobriety (fork)

Sobriety is a simple, minimal app to track how long you've been sober from anything you
want. It tells you how long it's been since you logged a relapse and the average time
between your most recent relapses, so you have more to work with in recovery.

This repository is a personal **fork** of
[seaglade/Sobriety](https://github.com/seaglade/Sobriety). The app, its design, and its
story belong entirely to [seaglade](https://github.com/seaglade) and the upstream
contributors. This fork only adds a handful of quality-of-life improvements for personal
use — it is not a remake or a rebrand.

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
- Same package id, and it reads existing Sobriety data files

## Upstream

The original app is maintained by seaglade and published on F-Droid:

https://f-droid.org/packages/com.katiearose.sobriety/

Fork builds are signed with a different key and are not published on F-Droid; they are
intended for personal testing and for contributing fixes back upstream.

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
