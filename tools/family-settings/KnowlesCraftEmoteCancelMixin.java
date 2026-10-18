package knowlescraft.radial.mixin;
import net.minecraft.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
/** Route movement and crouch cancellation through the unified input policy; a toggled state must not block future emotes. */
@Mixin(targets="com.yamikhal.playeremotes.client.LocalEmotes",remap=false)
public abstract class KnowlesCraftEmoteCancelMixin {
 @Redirect(method="shouldCancel",at=@At(value="INVOKE",target="Lnet/minecraft/class_304;method_1434()Z"))
 private static boolean knowlescraft$direction(class_304 key){
  var o=class_310.method_1551().field_1690;
  return key!=o.field_1894&&key!=o.field_1881&&key!=o.field_1913&&key!=o.field_1849&&key.method_1434();
 }
 @Redirect(method="shouldCancel",at=@At(value="INVOKE",target="Lnet/minecraft/class_746;method_5715()Z"))
 private static boolean knowlescraft$crouchIsAnAction(class_746 player){return false;}

 @Inject(method="started",at=@At("TAIL"))
 private static void knowlescraft$standForEmote(class_310 mc,CallbackInfo ci){family.settings.KnowlesCraftEmoteUncrouch.start(mc);}
 @Inject(method="partnerStarted",at=@At("TAIL"))
 private static void knowlescraft$standForPartner(int partnership,CallbackInfo ci){family.settings.KnowlesCraftEmoteUncrouch.start(class_310.method_1551());}

}
