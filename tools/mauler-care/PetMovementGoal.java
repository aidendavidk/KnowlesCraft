package knowlescraft.mauler;
import net.minecraft.*;
import java.util.EnumSet;
public final class PetMovementGoal extends class_1352 {
 private final class_1314 mob;private final PetMauler pet;private class_1657 lure;private int returnTicks;
 public PetMovementGoal(class_1314 mob){this.mob=mob;pet=(PetMauler)mob;method_6265(EnumSet.of(class_4134.field_18405,class_4134.field_18406));}
 public boolean method_6264(){if(mob.method_5968()!=null||mob.method_5934())return false;lure=pet.knowlescraft$lure();return lure!=null||pet.knowlescraft$returning();}
 public boolean method_6266(){return method_6264();}
 public void method_6269(){
  returnTicks=0;
  if(lure!=null){var mauler=(com.faboslav.friendsandfoes.common.entity.MaulerEntity)mob;mauler.setBurrowedDown(false);mauler.setBurrowingDownAnimationProgress(0);}
 }
 public void method_6268(){
  if(lure!=null){
   mob.method_5988().method_6226(lure,30,30);
   if(mob.method_5858(lure)>6.25)mob.method_5942().method_6335(lure,0.65);else mob.method_5942().method_6340();
  }else if(pet.knowlescraft$home()!=null){
   var home=pet.knowlescraft$home();
   if(mob.method_19538().method_1025(home)<4||++returnTicks>200){pet.knowlescraft$returned();mob.method_5942().method_6340();}
   else mob.method_5942().method_6337(home.field_1352,home.field_1351,home.field_1350,0.6);
  }
 }
 public void method_6270(){lure=null;mob.method_5942().method_6340();}
}
