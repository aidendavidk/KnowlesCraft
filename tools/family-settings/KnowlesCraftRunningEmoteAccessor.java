package knowlescraft.emotes.server.mixin;
import net.minecraft.class_2960;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
@Mixin(targets="com.yamikhal.playeremotes.server.EmoteTracker$Running",remap=false)
public interface KnowlesCraftRunningEmoteAccessor {
 @Accessor("emote") class_2960 knowlescraft$emote();
 @Accessor("animation") class_2960 knowlescraft$animation();
 @Accessor("startTick") int knowlescraft$start();
}
