package knowlescraft.radial.mixin;
import net.minecraft.class_746;
import family.settings.KnowlesCraftEmoteMovement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
@Mixin(value=class_746.class,remap=false)
public abstract class KnowlesCraftEmoteMovementMixin {
 @Inject(method="method_6007",at=@At(value="INVOKE",target="Lnet/minecraft/class_744;method_3129(ZF)V",shift=At.Shift.AFTER))
 private void knowlescraft$movement(CallbackInfo ci){KnowlesCraftEmoteMovement.input((class_746)(Object)this);}
}
