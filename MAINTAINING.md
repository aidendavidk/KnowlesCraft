# Maintaining KnowlesCraft

Update releases are deliberate: validate on a client copy, recalculate file/index/pack hashes, then commit the complete manifest together. Clients check the main branch before launching. Minecraft and loader migrations require separate Prism changes.

Only mod-download references and selected settings belong in the manifest. Do not upload worlds, accounts, player data, personal maps, server access lists, or backups.

The fresh-install ZIP is generated automatically from `fresh-install/` plus the official packwiz bootstrapper already stored in `setup/`. It contains no Microsoft login, worlds, screenshots, or personal map history.

Release 1.0.2 adds a tiny client-only Family Settings patch. Before voice chat initializes, it changes only Plasmo Voice's `check_for_updates` preference to false, preserving microphone devices, volumes and all other voice preferences. Source is in `tools/family-settings/FamilySettings.java`. It does not change voice functionality or update the voice mod itself.

