package family.settings;
import java.util.*;
import net.minecraft.*;
import com.yamikhal.playeremotes.client.PlayerEmotesClient;
import com.yamikhal.playeremotes.client.animation.*;

/** One input policy for native keyboard and Controlify input; never roots the player. */
public final class KnowlesCraftEmoteMovement {
 public enum Rule {STATIONARY,WALK,SIDEWAYS,RUN,OFFER}
 public static final Set<String> WALK=Set.of("wave","slim_wave","salute","shrug","yawn","facepalm","arms_crossed","laugh","clap","cry","hands_up");
 public static Rule rule(String id,boolean paired){
  if(!id.startsWith("playeremotes:"))return Rule.STATIONARY;
  String name=id.substring(13);
  if(name.equals("backflip"))return Rule.WALK;
  if(name.equals("naruto_run"))return Rule.RUN;
  if(name.equals("side_step_shuffle"))return Rule.SIDEWAYS;
  if(name.equals("high_five")||name.equals("high_five_sport"))return paired?Rule.STATIONARY:Rule.OFFER;
  if(WALK.contains(name))return Rule.WALK;
  return Rule.STATIONARY;
 }
 public static boolean cancels(Rule rule,float forward,float sideways,boolean jump,boolean sprint){
  boolean moving=Math.abs(forward)>0.001f||Math.abs(sideways)>0.001f;
  return (jump&&rule!=Rule.RUN)||(sprint&&moving&&rule!=Rule.RUN)||(rule==Rule.STATIONARY&&moving)||(rule==Rule.SIDEWAYS&&Math.abs(forward)>0.001f);
 }

 public static boolean jumpingAllowed(class_746 player){
  if(player==null)return false;
  var active=EmotePlayers.get(player.method_5667());
  return active!=null&&!active.isStopping()&&rule(active.emoteId().toString(),active.link()!=null)==Rule.RUN;
 }
 public static boolean gesture(String name){return WALK.contains(name)||name.equals("high_five_intro")||name.equals("high_five_sport_intro");}
 public static void apply(Rule rule,class_744 in,boolean sprint,Runnable stop){
  if(cancels(rule,in.field_3905,in.field_3907,in.field_3904,sprint)){stop.run();return;}
 }
 private static final Map<EmotePlayback,Boolean> crouchStates=new WeakHashMap<>();
 public static void input(class_746 player){
  var active=EmotePlayers.get(player.method_5667());if(active==null||active.isStopping())return;
  boolean crouched=player.method_5715();Boolean previous=crouchStates.put(active,crouched);
  if(previous!=null&&!previous&&crouched){PlayerEmotesClient.stop();return;}
  var in=player.field_3913;var rule=rule(active.emoteId().toString(),active.link()!=null);
  boolean sprint=player.method_5624();
  for(var key:class_310.method_1551().field_1690.field_1839)if(key.method_1431().equals("key.sprint")&&key.method_1434())sprint=true;
  apply(rule,in,sprint,PlayerEmotesClient::stop);
 }
}
