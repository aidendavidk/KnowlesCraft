package family.settings;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;

/** Changes only the Plasmo Voice update-notification preference before mods initialize. */
public final class FamilySettings implements PreLaunchEntrypoint {
    @Override
    public void onPreLaunch() {
        try {
            apply(FabricLoader.getInstance().getConfigDir().resolve("plasmovoice/client.toml"));
        } catch (Exception e) {
            System.err.println("[Family Settings] Could not disable voice update notices: " + e.getMessage());
        }
    }

    public static void apply(Path path) throws Exception {
        Files.createDirectories(path.getParent());
        String before = Files.exists(path) ? Files.readString(path, StandardCharsets.UTF_8) : "";
        String after;
        if (java.util.regex.Pattern.compile("(?m)^\\h*check_for_updates\\h*=").matcher(before).find()) {
            after = before.replaceAll("(?m)^(\\h*check_for_updates\\h*=\\h*)true(?=\\h*(?:#|$))", "$1false");
        } else {
            after = "check_for_updates = false\n" + before;
        }
        if (!after.equals(before)) Files.writeString(path, after, StandardCharsets.UTF_8);
    }

    /** Used only by local verification; Minecraft calls onPreLaunch instead. */
    public static void main(String[] args) throws Exception {
        apply(Path.of(args[0]));
    }
}
