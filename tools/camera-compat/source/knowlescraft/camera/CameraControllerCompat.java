package knowlescraft.camera;

import dev.isxander.controlify.Controlify;
import dev.isxander.controlify.api.ControlifyApi;
import dev.isxander.controlify.api.entrypoint.ControlifyEntrypoint;
import dev.isxander.controlify.api.event.ControlifyEvents;
import dev.isxander.controlify.bindings.ControlifyBindings;
import dev.isxander.controlify.controller.ControllerEntity;
import dev.isxander.controlify.controller.input.ControllerState;
import dev.isxander.controlify.controller.input.GamepadInputs;
import dev.isxander.controlify.screenop.ScreenProcessor;
import dev.isxander.controlify.screenop.ScreenProcessorProvider;
import dev.isxander.controlify.virtualmouse.VirtualMouseBehaviour;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.class_310;
import net.minecraft.class_437;
import java.util.Map;
import java.util.WeakHashMap;

/** Contextual controls only; this addon never changes a controller profile or sneak state. */
public final class CameraControllerCompat implements ControlifyEntrypoint {
    private static final Map<ControllerEntity, Gesture> GESTURES = new WeakHashMap<>();

    @Override public void onControlifyInit(ControlifyApi api) {
        if (FabricLoader.getInstance().isModLoaded("exposure")) {
            for (String name : new String[] {
                    "io.github.mortuusars.exposure.gui.screen.camera.CameraAttachmentsScreen",
                    "io.github.mortuusars.exposure.gui.screen.camera.ViewfinderControlsScreen",
                    "io.github.mortuusars.exposure.gui.screen.LightroomScreen",
                    "io.github.mortuusars.exposure.gui.screen.PhotographScreen",
                    "io.github.mortuusars.exposure.gui.screen.FilmFrameInspectScreen",
                    "io.github.mortuusars.exposure.gui.screen.ItemListScreen",
                    "io.github.mortuusars.exposure.gui.screen.ItemRenameScreen",
                    "io.github.mortuusars.exposure.gui.screen.album.AlbumScreen",
                    "io.github.mortuusars.exposure.gui.screen.album.AlbumPhotographScreen",
                    "io.github.mortuusars.exposure.gui.screen.album.AlbumSigningScreen",
                    "io.github.mortuusars.exposure.gui.screen.album.LecternAlbumScreen" }) registerMouseScreen(name);
            ControlifyEvents.CONTROLLER_STATE_UPDATE.register(event -> filterCameraInput(event.controller()));
        }
        if (FabricLoader.getInstance().isModLoaded("immersive_melodies")) {
            registerMouseScreen("immersive_melodies.client.gui.ImmersiveMelodiesScreen");
            registerMouseScreen("immersive_melodies.client.gui.MelodyUploadScreen");
            registerMouseScreen("immersive_melodies.client.gui.ImmersiveMelodiesFreePlayingScreen");
        }
        ControlifyEvents.CONTROLLER_DISCONNECTED.register(event -> GESTURES.remove(event.controller()));
    }

    @Override public void onControllersDiscovered(ControlifyApi api) {}

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void registerMouseScreen(String name) {
        try {
            Class<?> type = Class.forName(name);
            if (class_437.class.isAssignableFrom(type))
                ScreenProcessorProvider.registerProvider((Class)type, screen -> new MouseProcessor((class_437)screen));
        } catch (ClassNotFoundException absentOptionalScreen) {
            // Different optional mod versions can omit nonessential screens.
        }
    }

    public static final class MouseProcessor extends ScreenProcessor<class_437> {
        public MouseProcessor(class_437 screen) { super(screen); }
        @Override public VirtualMouseBehaviour virtualMouseBehaviour() { return VirtualMouseBehaviour.ENABLED; }
    }

    /** Called after bindings are sampled, before Controlify's gameplay/screen handlers. */
    public static void filterCameraInput(ControllerEntity controller) {
        if (controller.input().isEmpty()) return;
        if (!Controlify.instance().getCurrentController().filter(current -> current == controller).isPresent()
                || !Controlify.instance().currentInputMode().isController()) return;
        class_310 mc = class_310.method_1551();
        if (mc.field_1724 == null) { GESTURES.remove(controller); return; }
        var raw = controller.input().orElseThrow().rawStateNow();
        Gesture gesture = GESTURES.computeIfAbsent(controller, ignored -> new Gesture());
        boolean east = raw.isButtonDown(GamepadInputs.EAST_BUTTON);
        boolean west = raw.isButtonDown(GamepadInputs.WEST_BUTTON);
        boolean left = raw.isButtonDown(GamepadInputs.LEFT_SHOULDER_BUTTON);
        boolean right = raw.isButtonDown(GamepadInputs.RIGHT_SHOULDER_BUTTON);
        ExposureBridge.handle(mc, gesture, east, west, left, right, System.nanoTime());

        // Consume a camera action through release so it cannot also crouch, swap hands,
        // close the newly opened menu, or click its background in the same frame.
        if (gesture.blockEast) {
            ControlifyBindings.SNEAK.on(controller).pushState(ControllerState.EMPTY);
            ControlifyBindings.GUI_BACK.on(controller).pushState(ControllerState.EMPTY);
            if (!east) gesture.blockEast = false;
        }
        if (gesture.blockWest) {
            ControlifyBindings.SWAP_HANDS.on(controller).pushState(ControllerState.EMPTY);
            ControlifyBindings.VMOUSE_RCLICK.on(controller).pushState(ControllerState.EMPTY);
            if (!west) gesture.blockWest = false;
        }
        if (gesture.cameraContext) {
            ControlifyBindings.NEXT_SLOT.on(controller).pushState(ControllerState.EMPTY);
            ControlifyBindings.PREV_SLOT.on(controller).pushState(ControllerState.EMPTY);
        }
    }

    public static final class Gesture {
        boolean east, west, blockEast, blockWest, cameraContext;
        long nextZoom;
        /** Pure edge decision for focused tests; 1=exit, 2=controls. */
        public int action(boolean camera, boolean controls, boolean eastNow, boolean westNow) {
            int result = (camera || controls) && eastNow && !east ? 1
                    : camera && westNow && !west ? 2 : 0;
            east = eastNow;
            west = westNow;
            return result;
        }
    }

    /** Exposure classes are linked only when the optional mod is installed. */
    private static final class ExposureBridge {
        static void handle(class_310 mc, Gesture gesture, boolean east, boolean west,
                           boolean left, boolean right, long now) {
            boolean controls = mc.field_1755 instanceof io.github.mortuusars.exposure.gui.screen.camera.ViewfinderControlsScreen;
            boolean camera = mc.field_1755 == null
                    && io.github.mortuusars.exposure.camera.viewfinder.Viewfinder.isLookingThrough()
                    && io.github.mortuusars.exposure.camera.CameraClient.getCamera().isPresent();
            gesture.cameraContext = camera;
            int action = gesture.action(camera, controls, east, west);
            if (action == 1) {
                gesture.blockEast = true;
                if (controls) mc.field_1755.method_25419();
                else io.github.mortuusars.exposure.camera.CameraClient.deactivate(mc.field_1724);
            } else if (action == 2) {
                gesture.blockWest = true;
                io.github.mortuusars.exposure.gui.ClientGUI.openViewfinderControlsScreen();
            }
            if (camera && left != right && now >= gesture.nextZoom) {
                io.github.mortuusars.exposure.camera.viewfinder.Viewfinder.zoom(
                        right ? io.github.mortuusars.exposure.camera.infrastructure.ZoomDirection.IN
                                : io.github.mortuusars.exposure.camera.infrastructure.ZoomDirection.OUT, false);
                gesture.nextZoom = now + 100_000_000L;
            } else if (!left && !right) gesture.nextZoom = 0;
        }
    }
}
