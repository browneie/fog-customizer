# Fog Customizer

A Minecraft 1.8.9 client mod for changing fog, with settings in OneConfig. Made for
[OneClient](https://polyfrost.org)'s 1.8.9 profile, which runs on [Ornithe](https://ornithemc.net).

## Features

Open OneConfig (Right Shift) and find **Fog Customizer**:

- **Fog**: turn distance fog off completely.
- **Fog Start / Fog End**: where fog begins and where it is fully thick, as a percent of render distance
  (vanilla: 75% / 100%).
- **Nether Fog**: turn off the Nether's thick fog.
- **Water Fog / Lava Fog**: how thick fog is underwater and in lava, 0–100% of vanilla.
- **Custom Fog Color**: pick your own fog color.

Blindness fog is never changed.

## Install

1. Download the jar from [Releases](../../releases).
2. **OneClient:** open your 1.8.9 profile and drag the jar onto the window.
   **Other launchers:** put it in the `mods` folder of a 1.8.9 Ornithe instance with OneConfig.

## Building

```bash
./gradlew build
```

The jar is written to `build/libs/`.

## License

[MIT](LICENSE)
