package knowlescraft.radial.mixin;
import family.settings.KnowlesCraftRadial;
import dev.isxander.controlify.api.bind.InputBinding;
import dev.isxander.controlify.controller.input.ControllerStateView;
import org.spongepowered.asm.mixin.*;

/** Preserve Controlify's short pulse, but publish its rising edge immediately. */
@Mixin(targets="dev.isxander.controlify.bindings.InputBindingImpl",remap=false)
public abstract class KnowlesCraftImmediateBindingMixin implements KnowlesCraftRadial.ImmediateBinding {
 @Shadow private int fakePressState;
 @Unique public void knowlescraft$pressNow(ControllerStateView state){
  var binding=(InputBinding)(Object)this;
  fakePressState=0;binding.pushState(state);
  fakePressState=1;binding.pushState(state);
 }
}
