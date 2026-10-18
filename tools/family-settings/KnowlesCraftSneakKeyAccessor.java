package knowlescraft.radial.mixin;
import net.minecraft.class_4666;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
@Mixin(value=class_4666.class,remap=false)
public interface KnowlesCraftSneakKeyAccessor {
 @Invoker("method_52232") void knowlescraft$resetToggle();
}
