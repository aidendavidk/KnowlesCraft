package knowlescraft.emotes.server.mixin;
import net.minecraft.*;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
/** Only an unpaired high-five offer can travel. Native paired distance checks stay intact. */
@Mixin(targets="com.yamikhal.playeremotes.server.PartnerEmotes",remap=false)
public abstract class KnowlesCraftHighFiveServerMixin {
 @Redirect(method="tick",at=@At(value="FIELD",target="Lcom/yamikhal/playeremotes/server/PartnerEmotes$Waiting;position:Lnet/minecraft/class_243;"))
 private static class_243 knowlescraft$offerPosition(@Coerce Object waiting,MinecraftServer server){
  var offer=(KnowlesCraftHighFiveWaitingAccessor)waiting;String id=offer.knowlescraft$emote().toString();
  if(id.equals("playeremotes:high_five")||id.equals("playeremotes:high_five_sport")){
   var player=server.method_3760().method_14602(offer.knowlescraft$starter());if(player!=null)return player.method_19538();
  }
  return offer.knowlescraft$position();
 }
}
