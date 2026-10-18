package knowlescraft.emotes.rules;
import net.minecraft.*;
import knowlescraft.emotes.server.mixin.*;
/** Projectile-only vertical collision proxy, following the shipped Matrix lean tracks. */
public final class KnowlesCraftMatrixDodge {
 private static final double[][] BODY={{0,0},{.4333,-.09},{.6667,-1.25},{1.7333,-13.46},{2.2,-16},{2.7667,-15.37},{3,-12.83},{3.7667,-1.19},{3.9667,-.03},{4.3,0}};
 private static final double[][] TORSO={{0,0},{.3667,-.24},{.5,-2.02},{.6333,-5.31},{.9,-15.33},{1.4667,-42.87},{1.7,-52.78},{1.9333,-59.65},{2.0667,-61.63},{2.1667,-62},{2.6,-62},{2.7333,-60.18},{2.8333,-56.72},{2.9667,-49.99},{3.4667,-16.07},{3.6,-8.38},{3.7,-3.95},{3.8,-1.04},{3.8667,-.12},{4.3,0}};
 private static double sample(double[][] keys,double time){
  for(int i=1;i<keys.length;i++)if(time<=keys[i][0]){double t=(time-keys[i-1][0])/(keys[i][0]-keys[i-1][0]);return keys[i-1][1]+Math.max(0,Math.min(1,t))*(keys[i][1]-keys[i-1][1]);}
  return 0;
 }
 public static class_238 at(class_238 box,double seconds){
  if(seconds<=0||seconds>=4.3)return box;
  double lean=Math.abs(sample(BODY,seconds)+sample(TORSO,seconds));
  double height=(box.field_1325-box.field_1322)*(.42+.58*Math.cos(Math.toRadians(lean)));
  return new class_238(box.field_1323,box.field_1322,box.field_1321,box.field_1320,box.field_1322+height,box.field_1324);
 }
 public static class_238 projectileBox(class_1297 target,class_1297 projectile){
  var box=target.method_5829();
  if(!(projectile instanceof class_1676)||!(target instanceof class_3222 player))return box;
  var running=KnowlesCraftEmoteTrackerAccessor.knowlescraft$running().get(player.method_5667());
  if(!(running instanceof KnowlesCraftRunningEmoteAccessor emote)||!emote.knowlescraft$emote().toString().equals("playeremotes:matrix_dodge")||!emote.knowlescraft$animation().toString().equals("playeremotes:matrix_dodge"))return box;
  return at(box,(player.field_13995.method_3780()-emote.knowlescraft$start())/20.0);
 }
}
