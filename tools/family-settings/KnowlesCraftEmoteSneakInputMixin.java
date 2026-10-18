package knowlescraft.radial.mixin;
import dev.isxander.controlify.ingame.ControllerPlayerMovement;
import dev.isxander.controlify.controller.ControllerEntity;
import family.settings.KnowlesCraftEmoteUncrouch;
import net.minecraft.class_744;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(value=ControllerPlayerMovement.class,remap=false)
public abstract class KnowlesCraftEmoteSneakInputMixin {
 @Shadow @Final private ControllerEntity controller;
 @Inject(method="method_3129",at=@At("TAIL"))
 private void knowlescraft$releaseFirst(boolean slow,float factor,CallbackInfo ci){if(KnowlesCraftEmoteUncrouch.suppressHeld(controller))((class_744)(Object)this).field_3903=false;}
}
