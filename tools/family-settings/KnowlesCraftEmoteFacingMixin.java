package knowlescraft.radial.mixin;
import com.yamikhal.playeremotes.client.animation.EmotePlayers;
import com.yamikhal.playeremotes.client.animation.EmotePlayback;
import net.minecraft.class_1657;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

/** Use current horizontal facing, preserving native paired-emote alignment and blending. */
@Mixin(value=EmotePlayers.class,remap=false)
public abstract class KnowlesCraftEmoteFacingMixin {
 @ModifyVariable(method="face",at=@At("STORE"),ordinal=0)
 private static float knowlescraft$currentFacing(float fixedYaw,class_1657 player,EmotePlayback playback){
  return playback.link()==null?player.method_36454():fixedYaw;
 }
}
