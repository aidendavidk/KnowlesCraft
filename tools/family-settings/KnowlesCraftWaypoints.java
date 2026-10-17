package family.settings;
import net.minecraft.*;
import journeymap.client.waypoint.*;
import java.util.*;

/** One action per press. Waypoint targeting compares horizontal bearing only. */
public final class KnowlesCraftWaypoints {
 private boolean wasDown;
 public boolean pressed(boolean down,boolean inGame){boolean fire=down&&!wasDown&&inGame;wasDown=down;return fire;}
 public static Waypoint target(Collection<Waypoint> waypoints,class_243 eye,class_243 look,String dimension){
  double headingLength=Math.hypot(look.field_1352,look.field_1350);
  if(headingLength<0.0001)return null;
  Waypoint best=null;double bestAngle=Math.cos(Math.toRadians(2)),bestDistance=Double.MAX_VALUE;
  for(var waypoint:waypoints){
   if(!waypoint.isEnable()||!waypoint.isPersistent()||waypoint.isDeathPoint()||!waypoint.getDimensions().contains(dimension)||(waypoint.getGroup()!=null&&!waypoint.getGroup().isEnable()))continue;
   double dx=waypoint.getBlockCenteredX()-eye.field_1352,dz=waypoint.getBlockCenteredZ()-eye.field_1350,distance=Math.hypot(dx,dz);
   if(distance<0.5)continue;
   double alignment=(dx*look.field_1352+dz*look.field_1350)/(distance*headingLength);
   if(alignment>bestAngle+1e-9||(Math.abs(alignment-bestAngle)<1e-9&&distance<bestDistance)){
    best=waypoint;bestAngle=alignment;bestDistance=distance;
   }
  }
  return best;
 }
 public void tick(class_310 mc,boolean down){
  boolean inGame=mc.field_1755==null&&mc.field_1724!=null&&mc.field_1687!=null;
  if(!pressed(down,inGame))return;
  var store=WaypointStore.INSTANCE;
  String dimension=journeymap.common.helper.DimensionHelper.getDimKeyName(mc.field_1687.method_27983());
  var selected=target(store.getAll(),mc.field_1724.method_33571(),mc.field_1724.method_5828(1),dimension);
  if(selected!=null){store.remove(selected,true);mc.field_1724.method_7353(class_2561.method_43470("Waypoint removed: "+selected.getName()),true);return;}
  var hit=mc.field_1724.method_5745(128,1,false);
  if(!(hit instanceof class_3965 block)||hit.method_17783()!=class_239.class_240.field_1332){mc.field_1724.method_7353(class_2561.method_43470("Aim at a block to place a waypoint"),true);return;}
  var waypoint=Waypoint.at(block.method_17777(),Waypoint.Type.Normal,dimension).setPersistent(true);
  store.save(waypoint,true);
  mc.field_1724.method_7353(class_2561.method_43470("Waypoint placed: "+waypoint.getName()),true);
 }
}
