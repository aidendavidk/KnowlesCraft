package knowlescraft.radial.mixin;
import com.yamikhal.playeremotes.anim.Pose;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
@Mixin(value=Pose.class,remap=false)
public interface KnowlesCraftEmotePoseAccessor {
 @Accessor("hasRotation") boolean[] knowlescraft$rotations();
 @Accessor("hasPosition") boolean[] knowlescraft$positions();
 @Accessor("hasScale") boolean[] knowlescraft$scales();
}
