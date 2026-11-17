# Champ Auto Mine

A compact **client-side tunnel miner** for **Minecraft 1.21.11** on **Fabric**, with a native Minecraft GUI.

![Champ Auto Mine GUI](https://raw.githubusercontent.com/ingmarltd11-cyber/champ-auto-mine/main/screenshots/gui.png)

Champ Auto Mine is a small automation mod for digging a straight tunnel. It locks your yaw, holds a configurable pitch, keeps forward and attack pressed, and mines the block directly ahead. If lava or fire is detected in front, it briefly strafes to the safer side. There is no ImGui: the interface uses normal Minecraft buttons and sliders.

Everything runs on the client. The mod has no API-key code, no accounts, no analytics, no telemetry and no network requests.

## Features

- Straight one-block-wide tunnel mining.
- Yaw lock and configurable pitch lock.
- Holds forward and attack for you.
- Lava and fire detection up to 8 blocks ahead.
- Automatic short strafe to the safer side.
- Compact native Minecraft GUI for GUI scale 2.
- Settings saved in `config/champ-auto-mine.json`.
- Fabric and Java 21, client side only.
- Source code and a sources jar are included.

## How to use

1. Install Fabric Loader for Minecraft 1.21.11 and Fabric API.
2. Put `champ-auto-mine-1.0.0.jar` in your `mods` folder.
3. Join a world and face the direction you want to tunnel.
4. Hold the correct pickaxe in your main hand.
5. Press `F6` to open the panel.
6. Press `Auto Mine: OFF` or press `F7` to start.
7. Press `F7` again to stop.

Closing the panel with `Escape` does not stop the miner. Change the keybinds in Minecraft's Controls screen if `F6` or `F7` conflicts with another mod.

## Options

| Option | Default | Description |
| --- | --- | --- |
| Lock pitch | On | Holds the configured pitch while mining. |
| Pitch | 0.0 | The pitch to hold. 0 looks straight ahead. |
| Lock yaw | On | Keeps the direction from when mining started. |
| Auto forward | On | Holds the forward key. |
| Auto break | On | Holds attack and mines ahead. |
| Avoid lava | On | Scans for lava and fire and strafes away. |
| Scan range | 4 | Hazard scan distance from 1 to 8 blocks. |

## Compatibility

| | |
| --- | --- |
| Minecraft | 1.21.11 |
| Loader | Fabric Loader 0.19.0+ |
| Dependency | Fabric API |
| Java | 21 |
| Environment | Client |
| License | MIT |

## Important notes

- Use this only on servers where automation and client mods are allowed. Many servers prohibit macros or automation.
- The miner only mines the block directly ahead; it is not a full quarry.
- It does not switch tools, place torches, collect drops or fight mobs.
- Hazard avoidance is a short reactive strafe, not full pathfinding. Lava can still be dangerous.
- The server remains in control. The mod does not bypass anti-cheat, permissions or reach limits.

## Privacy and security

The mod contains no API-key handling, no tokens, no analytics and no outbound HTTP or WebSocket code. It writes only its local config file. The public GitHub repository has no collaborators and is read-only for visitors.

## License

MIT. See the repository's `LICENSE` file.

---

## Modrinth project settings

| Field | Value |
| --- | --- |
| Title | Champ Auto Mine |
| Slug | champ-auto-mine |
| Summary | A compact client-side tunnel miner for Fabric 1.21.11 with a native Minecraft GUI. No ImGui, no API keys, no telemetry. |
| Categories | Utility, Game Mechanics |
| Loader | Fabric |
| Game versions | 1.21.11 |
| Environment | Client |
| License | MIT |
| Source | https://github.com/ingmarltd11-cyber/champ-auto-mine |
| Issues | https://github.com/ingmarltd11-cyber/champ-auto-mine/issues |
| Icon | `icon.png` |
| Gallery | `screenshots/gui.png` |
