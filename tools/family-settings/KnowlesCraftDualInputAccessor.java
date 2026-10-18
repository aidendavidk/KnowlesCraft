package knowlescraft.radial.mixin;
import dev.isxander.controlify.ingame.DualInput;
import net.minecraft.class_744;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
@Mixin(value=DualInput.class,remap=false)
public interface KnowlesCraftDualInputAccessor {
 @Accessor("input1") class_744 knowlescraft$first();
 @Accessor("input2") class_744 knowlescraft$second();
}
