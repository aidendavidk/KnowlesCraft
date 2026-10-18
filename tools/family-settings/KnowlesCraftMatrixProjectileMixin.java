package knowlescraft.emotes.server.mixin;
import net.minecraft.*;
import java.util.function.Predicate;
import knowlescraft.emotes.rules.KnowlesCraftMatrixDodge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
/** Only projectile sweep collision changes: no global hitbox or damage immunity. */
@Mixin(value=class_1675.class,remap=false)
public abstract class KnowlesCraftMatrixProjectileMixin {
 @Redirect(method="method_37226",at=@At(value="INVOKE",target="Lnet/minecraft/class_1297;method_5829()Lnet/minecraft/class_238;"))
 private static class_238 knowlescraft$lean(class_1297 target,class_1937 world,class_1297 projectile,class_243 start,class_243 end,class_238 search,Predicate<class_1297> predicate,float margin){
  return KnowlesCraftMatrixDodge.projectileBox(target,projectile);
 }
}
