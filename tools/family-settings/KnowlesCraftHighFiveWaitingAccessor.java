package knowlescraft.emotes.server.mixin;
import java.util.UUID;
import net.minecraft.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
@Mixin(targets="com.yamikhal.playeremotes.server.PartnerEmotes$Waiting",remap=false)
public interface KnowlesCraftHighFiveWaitingAccessor {
 @Accessor("starter") UUID knowlescraft$starter();
 @Accessor("emote") class_2960 knowlescraft$emote();
 @Accessor("position") class_243 knowlescraft$position();
}
