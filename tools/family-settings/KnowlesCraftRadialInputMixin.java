package knowlescraft.radial.mixin;
import family.settings.KnowlesCraftRadial;
import dev.isxander.controlify.api.bind.InputBinding;
import dev.isxander.controlify.controller.ControllerEntity;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets="dev.isxander.controlify.ingame.InGameInputHandler",remap=false)
public abstract class KnowlesCraftRadialInputMixin {
 @Shadow @Final private ControllerEntity controller;
 @Unique private boolean knowlescraft$openWheel;
 @Inject(method="handleKeybinds",at=@At("HEAD"))
 private void knowlescraft$pollBeforeActions(CallbackInfo ci){
  knowlescraft$openWheel=KnowlesCraftRadial.shouldOpen(controller,dev.isxander.controlify.bindings.ControlifyBindings.RADIAL_MENU.on(controller));
 }
 @Redirect(method="handleKeybinds",at=@At(value="INVOKE",target="Ldev/isxander/controlify/api/bind/InputBinding;justPressed()Z"))
 private boolean knowlescraft$tapOrHold(InputBinding binding){return binding==dev.isxander.controlify.bindings.ControlifyBindings.RADIAL_MENU.on(controller)?knowlescraft$openWheel:binding.justPressed();}
}
