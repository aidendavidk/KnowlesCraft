package knowlescraft.radial.mixin;
import dev.isxander.controlify.screenop.compat.vanilla.PauseScreenProcessor;
import net.minecraft.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
/** The displayed Disconnect shortcut activates the real button, instead of merely focusing it. */
@Mixin(value=PauseScreenProcessor.class,remap=false)
public abstract class KnowlesCraftPauseDisconnectMixin {
 @Redirect(method="handleButtons",at=@At(value="INVOKE",target="Lnet/minecraft/class_433;method_25395(Lnet/minecraft/class_364;)V"))
 private void knowlescraft$disconnect(class_433 screen,class_364 target){
  if(target instanceof class_4185 button){if(button.field_22763&&button.field_22764){button.method_25354(class_310.method_1551().method_1483());button.method_25306();}}
  else screen.method_25395(target);
 }
}
