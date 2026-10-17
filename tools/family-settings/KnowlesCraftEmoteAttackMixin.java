package knowlescraft.radial.mixin;
import family.settings.KnowlesCraftEmotes;
import net.minecraft.class_310;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

/** Cancel before vanilla attack handling; never consume, defer or replace the attack. */
@Mixin(value=class_310.class,remap=false)
public abstract class KnowlesCraftEmoteAttackMixin {
 @Inject(method="method_1536",at=@At("HEAD"))
 private void knowlescraft$attack(CallbackInfoReturnable<Boolean> ci){KnowlesCraftEmotes.cancelForAttack();}
 @Inject(method="method_1590",at=@At("HEAD"))
 private void knowlescraft$mine(boolean attacking,CallbackInfo ci){if(attacking)KnowlesCraftEmotes.cancelForAttack();}
}
