package knowlescraft.radial.mixin;
import family.settings.KnowlesCraftRadial;
import dev.isxander.controlify.api.bind.InputBinding;
import dev.isxander.controlify.controller.ControllerEntity;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(targets="dev.isxander.controlify.ingame.InGameInputHandler",remap=false)
public abstract class KnowlesCraftRadialInputMixin {
 @Shadow @Final private ControllerEntity controller;
 @Redirect(method="handleKeybinds",at=@At(value="INVOKE",target="Ldev/isxander/controlify/api/bind/InputBinding;justPressed()Z"))
 private boolean knowlescraft$tapOrHold(InputBinding binding){return binding==dev.isxander.controlify.bindings.ControlifyBindings.RADIAL_MENU.on(controller)?KnowlesCraftRadial.shouldOpen(controller,binding):binding.justPressed();}
}
