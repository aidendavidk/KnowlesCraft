package knowlescraft.radial.mixin;
import com.yamikhal.playeremotes.client.animation.EmotePlayback;
import com.yamikhal.playeremotes.anim.*;
import family.settings.KnowlesCraftEmoteMovement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
/** Let the native solver blend the emote upper body onto vanilla walking legs, on every client. */
@Mixin(value=EmotePlayback.Frame.class,remap=false)
public abstract class KnowlesCraftEmoteWalkPoseMixin {
 @Inject(method="sample",at=@At("TAIL"))
 private void knowlescraft$walking(Pose pose,CallbackInfo ci){
  var frame=(EmotePlayback.Frame)(Object)this;
  if(frame.link()!=null||frame.queries()==null||!KnowlesCraftEmoteMovement.gesture(frame.animation().name())||frame.queries().query(Query.GROUND_SPEED)<=0.1)return;
  var flags=(KnowlesCraftEmotePoseAccessor)(Object)pose;
  for(var part:new Part[]{Part.BODY,Part.RIGHT_LEG,Part.LEFT_LEG,Part.RIGHT_LOWER_LEG,Part.LEFT_LOWER_LEG}){
   int i=part.ordinal();flags.knowlescraft$rotations()[i]=false;flags.knowlescraft$positions()[i]=false;flags.knowlescraft$scales()[i]=false;
  }
 }
}
