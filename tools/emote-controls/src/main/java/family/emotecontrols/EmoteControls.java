package family.emotecontrols;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBinding;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Gives the PC wheel key the same tap/hold gesture as KnowlesCraft's controller binding:
 * tap plays the most recent emote; hold for 250 ms opens the wheel.
 */
public final class EmoteControls implements ClientModInitializer {
    private static final long HOLD_NANOS = 250_000_000L;

    private static KeyBinding wheelKey;
    private static boolean down;
    private static boolean opened;
    private static boolean blocked;
    private static boolean failureReported;
    private static long started;

    @Override
    public void onInitializeClient() {
        try {
            Class<?> playerEmotes = Class.forName("com.yamikhal.playeremotes.client.PlayerEmotesClient");
            wheelKey = (KeyBinding) playerEmotes.getField("OPEN_WHEEL").get(null);
        } catch (ReflectiveOperationException | LinkageError e) {
            reportFailure("Could not connect to the Player Emotes wheel key", e);
            return;
        }

        // Player Emotes consumes this key at END_CLIENT_TICK. Drain its click queue first,
        // while still using the binding's held state and release event for the wheel itself.
        ClientTickEvents.START_CLIENT_TICK.register(EmoteControls::tick);
    }

    private static void tick(MinecraftClient client) {
        if (wheelKey == null) {
            return;
        }

        boolean clicked = drainClicks();
        boolean pressed = wheelKey.isPressed();
        boolean allowed = client.currentScreen == null
                && client.player != null
                && client.player == client.getCameraEntity();

        if (!allowed) {
            if (pressed || down) {
                blocked = true;
            }
            down = pressed;
            opened = false;
            return;
        }

        if (blocked) {
            if (!pressed) {
                blocked = false;
                down = false;
            }
            return;
        }

        long now = System.nanoTime();
        if (pressed && !down) {
            down = true;
            opened = false;
            started = now;
        }

        if (pressed && down && !opened && now - started >= HOLD_NANOS) {
            opened = openWheel(client);
            return;
        }

        if (!pressed && down) {
            down = false;
            boolean tap = !opened && now - started < HOLD_NANOS;
            opened = false;
            if (tap) {
                playMostRecent();
            }
            return;
        }

        // A very fast press and release can both happen between client ticks.
        if (!pressed && !down && clicked) {
            playMostRecent();
        }
    }

    private static boolean drainClicks() {
        boolean clicked = false;
        try {
            while (wheelKey.wasPressed()) {
                clicked = true;
            }
        } catch (ReflectiveOperationException | RuntimeException e) {
            reportFailure("Could not read the Player Emotes wheel key", e);
        }
        return clicked;
    }

    private static boolean openWheel(MinecraftClient client) {
        try {
            Class<?> wheelClass = Class.forName("com.yamikhal.playeremotes.client.gui.QuickWheelScreen");
            Constructor<?> constructor = wheelClass.getConstructor(Screen.class, boolean.class);
            Screen screen = (Screen) constructor.newInstance(null, true);
            client.setScreen(screen);
            return true;
        } catch (ReflectiveOperationException | LinkageError e) {
            reportFailure("Could not open the Player Emotes wheel", e);
            return true;
        }
    }

    private static void playMostRecent() {
        try {
            Class<?> playerEmotes = Class.forName("com.yamikhal.playeremotes.client.PlayerEmotesClient");
            Object config = playerEmotes.getMethod("config").invoke(null);
            Object recentValue = config.getClass().getField("recent").get(config);
            if (!(recentValue instanceof List<?> recent) || recent.isEmpty()) {
                return;
            }

            Object id = recent.get(0);
            Class<?> registry = Class.forName("com.yamikhal.playeremotes.client.emote.EmoteRegistry");
            Object emote = findCompatibleMethod(registry, "get", id).invoke(null, id);
            if (emote == null) {
                return;
            }

            findCompatibleMethod(playerEmotes, "play", emote).invoke(null, emote);
        } catch (ReflectiveOperationException | RuntimeException | LinkageError e) {
            reportFailure("Could not play the most recent emote", e);
        }
    }

    private static Method findCompatibleMethod(Class<?> owner, String name, Object argument)
            throws NoSuchMethodException {
        for (Method method : owner.getMethods()) {
            if (method.getName().equals(name)
                    && method.getParameterCount() == 1
                    && method.getParameterTypes()[0].isInstance(argument)) {
                return method;
            }
        }
        throw new NoSuchMethodException(owner.getName() + "." + name);
    }

    private static void reportFailure(String message, Throwable error) {
        if (!failureReported) {
            failureReported = true;
            System.err.println("[KnowlesCraft] " + message + ": " + error.getClass().getSimpleName());
        }
    }
}
