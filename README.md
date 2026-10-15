# Family Minecraft pack updates

Pinned client updates for Minecraft 1.20.1 / Fabric 0.15.7.

## Enable updates in an existing Prism instance

[Download the small one-time setup ZIP](https://raw.githubusercontent.com/aidendavidk/family-minecraft-pack/main/setup/FamilyEdition-Enable-AutoUpdates.zip).

1. Close Minecraft. In Prism select the existing Family Edition instance and click Folder. Open `.minecraft` (or `minecraft`).
2. Copy `packwiz-installer-bootstrap.jar` from the ZIP into that folder, next to `options.txt`.
3. Edit the Prism instance, open Settings > Custom Commands, and enable Custom Commands.
4. Paste this into **Pre-launch command**:

```
"$INST_JAVA" -jar packwiz-installer-bootstrap.jar -g -s client https://raw.githubusercontent.com/aidendavidk/family-minecraft-pack/main/pack.toml
```

5. Launch normally. Existing matching mods are validated and reused. Do not import a replacement instance or independently update individual mods.

## Current release: 1.0.1

Preserves the tested 78-mod set and disables new automatic JourneyMap death markers. Existing death markers are not deleted. Personal maps, manual waypoint files, saves, screenshots, account login, and graphics/control options are outside the update manifest. The JourneyMap waypoint-settings file is managed by this pack.

## Publishing

Update releases are deliberate: validate on a client copy, recalculate file/index/pack hashes, then commit the complete manifest together. Clients check the main branch before launching. Minecraft and loader migrations require separate Prism changes.

Only mod-download references and selected settings belong in the manifest. Do not upload worlds, accounts, player data, personal maps, server access lists, or backups. The setup ZIP contains the official packwiz bootstrapper and instructions; the manifest points to official mod download services.
