package knowlescraft.zoom.mixin;
import family.settings.KnowlesCraftController;
import net.minecraft.class_310;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Combine the controller toggle with Zoomify's unchanged keyboard behavior. */
@Mixin(targets="dev.isxander.zoomify.Zoomify",remap=false)
public abstract class KnowlesCraftZoomMixin {
 @Shadow private static boolean zooming;
 @Unique private boolean knowlescraft$keyboardZoom;
 @Inject(method="tick",at=@At("HEAD"))
 private void knowlescraft$restoreKeyboard(class_310 mc,CallbackInfo ci){zooming=knowlescraft$keyboardZoom;}
 @Inject(method="tick",at=@At(value="INVOKE",target="Ldev/isxander/zoomify/Zoomify;handleSpyglass(Lnet/minecraft/class_310;Z)V"))
 private void knowlescraft$combineZoom(class_310 mc,CallbackInfo ci){
  knowlescraft$keyboardZoom=zooming;zooming=zooming||KnowlesCraftController.controllerZoom();
 }
}
