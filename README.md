# KnowlesCraft

Pinned client setup for Minecraft 1.20.1 / Fabric 0.15.7.

## New install - start here

**[Download the fresh-install Prism ZIP](https://raw.githubusercontent.com/aidendavidk/family-minecraft-pack/main/dist/KnowlesCraft-Fresh-Install.zip)**

This is the file for someone who does **not** already have KnowlesCraft installed. It is a Prism-importable ZIP; the tested mods and managed settings download automatically from this repository on first launch.

1. Install Prism Launcher and sign in with the Microsoft account that owns Minecraft Java Edition.
2. Download the ZIP above. **Do not unzip it.**
3. In Prism choose **Add Instance > Import** and select `KnowlesCraft-Fresh-Install.zip`.
4. Launch normally. The first launch can take a few minutes while the current tested pack downloads.
5. Open **Multiplayer** and join the included **KnowlesCraft** entry.

Do **not** use GitHub's **Code > Download ZIP** button; that downloads the repository source, not the Minecraft instance.

## Current release: 1.0.3

Preserves the tested 78-mod set and disables new automatic JourneyMap death markers. Existing death markers are not deleted. Personal maps, manual waypoint files, saves, screenshots, account login, and graphics/control options are outside the update manifest. The JourneyMap waypoint-settings file is managed by this pack.

Fresh installs also start with the family-friendly client defaults: 4 GB Prism memory limit, lighter graphics, muted voice microphone, and TreeChop disabled until a player deliberately enables it.

## Publishing

Update releases are deliberate: validate on a client copy, recalculate file/index/pack hashes, then commit the complete manifest together. Clients check the main branch before launching. Minecraft and loader migrations require separate Prism changes.

Only mod-download references and selected settings belong in the manifest. Do not upload worlds, accounts, player data, personal maps, server access lists, or backups.

The fresh-install ZIP is generated automatically from `fresh-install/` plus the official packwiz bootstrapper already stored in `setup/`. It contains no Microsoft login, worlds, screenshots, or personal map history.

Release 1.0.2 adds a tiny client-only Family Settings patch. Before voice chat initializes, it changes only Plasmo Voice's `check_for_updates` preference to false, preserving microphone devices, volumes and all other voice preferences. Source is in `tools/family-settings/FamilySettings.java`. It does not change voice functionality or update the voice mod itself.

## KnowlesCraft 1.0.3

The pack and built-in server are now KnowlesCraft. Existing installs receive the saved server automatically on launch: `knowlescraft.duckdns.org`. The small client settings patch preserves other servers, local/home entries, maps, saves, graphics, microphone settings and server icons. Prism may need to be reopened to display its updated instance label. No reimport is required.
