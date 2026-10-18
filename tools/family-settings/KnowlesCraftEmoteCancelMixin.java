package knowlescraft.radial.mixin;
import net.minecraft.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
/** Replace only the native keyboard-only directional cancellation with the unified input policy. */
@Mixin(targets="com.yamikhal.playeremotes.client.LocalEmotes",remap=false)
public abstract class KnowlesCraftEmoteCancelMixin {
 @Redirect(method="shouldCancel",at=@At(value="INVOKE",target="Lnet/minecraft/class_304;method_1434()Z"))
 private static boolean knowlescraft$direction(class_304 key){
  var o=class_310.method_1551().field_1690;
  return key!=o.field_1894&&key!=o.field_1881&&key!=o.field_1913&&key!=o.field_1849&&key.method_1434();
 }
}
