package knowlescraft.emotes.server.mixin;
import java.util.*;
import com.yamikhal.playeremotes.server.EmoteTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
@Mixin(value=EmoteTracker.class,remap=false)
public interface KnowlesCraftEmoteTrackerAccessor {
 @Accessor("RUNNING") static Map<UUID,?> knowlescraft$running(){throw new AssertionError("Mixin not applied");}
}
