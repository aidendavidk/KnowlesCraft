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
 @Unique private boolean knowlescraft$keepSneak;
 @Unique private boolean knowlescraft$wasSneaking;
 @Inject(method="method_3129",at=@At("HEAD"))
 private void knowlescraft$rememberCrouch(boolean slow,float factor,CallbackInfo ci){
  var screen=net.minecraft.class_310.method_1551().field_1755;
  knowlescraft$keepSneak=screen instanceof family.settings.KnowlesCraftEmotes.Screen||screen instanceof com.yamikhal.playeremotes.client.gui.EmoteScreen;
  knowlescraft$wasSneaking=((class_744)(Object)this).field_3903;
 }
 @Inject(method="method_3129",at=@At("RETURN"))
 private void knowlescraft$releaseFirst(boolean slow,float factor,CallbackInfo ci){if(knowlescraft$keepSneak)((class_744)(Object)this).field_3903=knowlescraft$wasSneaking;
  if(KnowlesCraftEmoteUncrouch.suppressHeld(controller))((class_744)(Object)this).field_3903=false;}
}
