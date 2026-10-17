package knowlescraft.mauler.mixin;
import java.util.*;
import net.minecraft.*;
import knowlescraft.mauler.*;
import com.faboslav.friendsandfoes.common.entity.MaulerEntity;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MaulerEntity.class)
public abstract class MaulerPetMixin extends class_1314 implements PetMauler,class_6025 {
 @Unique private UUID knowlescraft$owner;
 @Unique private int knowlescraft$lastHurt=-1,knowlescraft$chaseTicks;
 @Unique private class_243 knowlescraft$origin;
 @Unique private UUID knowlescraft$disengaged;
 @Unique private int knowlescraft$ignoreUntil;
 @Unique private boolean knowlescraft$returning,knowlescraft$installed;
 protected MaulerPetMixin(class_1299<? extends class_1314> type,class_1937 world){super(type,world);}
 public UUID knowlescraft$owner(){return knowlescraft$owner;}
 public UUID method_6139(){return knowlescraft$owner;}
 public class_1924 method_48926(){return method_37908();}
 @Override public void method_5711(byte status){
  if(status==6||status==7){
   var particle=status==7?class_2398.field_11201:class_2398.field_11251;
   for(int i=0;i<7;i++)method_37908().method_8406(particle,method_23322(1),method_23319()+0.5,method_23325(1),method_6051().method_43059()*0.02,method_6051().method_43059()*0.02,method_6051().method_43059()*0.02);
  }else super.method_5711(status);
 }
 @Inject(method="<init>",at=@At("TAIL"))
 private void knowlescraft$addLure(class_1299<?> type,class_1937 world,CallbackInfo ci){field_6201.method_6277(1,new PetMovementGoal(this));}
 public void knowlescraft$tame(UUID owner){
  knowlescraft$owner=owner;method_5971();
  var player=method_37908().method_18470(owner);knowlescraft$lastHurt=player==null?-1:player.method_6117();
  knowlescraft$install();
 }
 public void knowlescraft$untame(){
  if(knowlescraft$owner==null)return;
  knowlescraft$owner=null;knowlescraft$installed=false;knowlescraft$origin=null;knowlescraft$returning=false;knowlescraft$lastHurt=-1;
  field_6201.method_35113(goal->true);field_6185.method_35113(goal->true);method_5959();field_6201.method_6277(1,new PetMovementGoal(this));
  method_5980(null);method_6015(null);var self=(MaulerEntity)(Object)this;self.method_29509();self.setTicksUntilNextBurrowingDown(1200);
 }
 @Unique private void knowlescraft$install(){
  if(knowlescraft$installed)return;knowlescraft$installed=true;
  var self=(MaulerEntity)(Object)this;
  field_6185.method_35113(goal->true);
  field_6201.method_6280(self.burrowDownGoal);
  self.method_29509();method_5980(null);method_6015(null);
  self.setBurrowedDown(false);self.setBurrowingDownAnimationProgress(0);self.setTicksUntilNextBurrowingDown(Integer.MAX_VALUE);
 }
 @Inject(method="method_5652",at=@At("TAIL"))
 private void knowlescraft$save(class_2487 nbt,CallbackInfo ci){if(knowlescraft$owner!=null)nbt.method_25927("KnowlesCraftOwner",knowlescraft$owner);}
 @Inject(method="method_5749",at=@At("TAIL"))
 private void knowlescraft$load(class_2487 nbt,CallbackInfo ci){if(nbt.method_25928("KnowlesCraftOwner"))knowlescraft$tame(nbt.method_25926("KnowlesCraftOwner"));else knowlescraft$untame();}
 @Inject(method="method_6007",at=@At("HEAD"))
 private void knowlescraft$tick(CallbackInfo ci){knowlescraft$petTick();}
 @Inject(method="method_6121",at=@At("HEAD"),cancellable=true)
 private void knowlescraft$noFriendlyFire(class_1297 target,CallbackInfoReturnable<Boolean> ci){
  if(knowlescraft$owner!=null&&(!(target instanceof class_1309 living)||!knowlescraft$safeEnemy(living)))ci.setReturnValue(false);
 }
 public void knowlescraft$petTick(){
  if(knowlescraft$owner==null||method_37908().field_9236)return;
  knowlescraft$install();var self=(MaulerEntity)(Object)this;
  self.setBurrowedDown(false);self.setBurrowingDownAnimationProgress(0);self.setTicksUntilNextBurrowingDown(Integer.MAX_VALUE);
  if(method_5968()!=null){
   var enemy=method_5968();
   if(!knowlescraft$safeEnemy(enemy)||knowlescraft$origin==null||enemy.method_19538().method_1025(knowlescraft$origin)>256||method_19538().method_1025(knowlescraft$origin)>256||++knowlescraft$chaseTicks>600){
    knowlescraft$disengaged=enemy.method_5667();knowlescraft$ignoreUntil=field_6012+200;
    method_5980(null);self.method_29509();knowlescraft$returning=knowlescraft$origin!=null;method_5942().method_6340();
   }
  }else if(knowlescraft$origin!=null&&!knowlescraft$returning)knowlescraft$returning=true;
  if(method_6065() instanceof class_1657){method_6015(null);if(method_5968()==null)self.method_29509();}
  if(method_5968()!=null||knowlescraft$returning||field_6012%5!=0)return;
  class_1309 enemy=null;double nearest=256;
  for(var entity:method_37908().method_8333(this,method_5829().method_1014(16),candidate->candidate instanceof class_1309&&candidate instanceof class_1569)){
   var candidate=(class_1309)entity;double distance=method_5858(candidate);
   if(knowlescraft$safeEnemy(candidate)&&distance<nearest&&!(candidate.method_5667().equals(knowlescraft$disengaged)&&field_6012<knowlescraft$ignoreUntil)){enemy=candidate;nearest=distance;}
  }
  if(enemy!=null){
   knowlescraft$origin=method_19538();knowlescraft$returning=false;knowlescraft$chaseTicks=0;method_5980(enemy);
  }
 }
 @Unique private boolean knowlescraft$safeEnemy(class_1309 enemy){
  return enemy instanceof class_1569&&enemy instanceof class_1308&&!(enemy instanceof class_1548)&&!(enemy instanceof class_1571)&&enemy!=this&&enemy.method_5805()&&!(enemy instanceof class_1321 pet&&pet.method_6181())&&!(enemy instanceof PetMauler pet&&pet.knowlescraft$owner()!=null);
 }
 /** Shared MOVE control keeps luring, returning, combat and wandering mutually exclusive. */
 @Unique public class_1657 knowlescraft$lure(){
  class_1657 nearest=null;double distance=100;
  for(var player:method_37908().method_18456())if(!player.method_7325()&&player.method_5805()&&(KnowlesCraftMaulerCare.isMeat(player.method_6047())||KnowlesCraftMaulerCare.isMeat(player.method_6079()))){
   double d=method_5858(player);if(d<distance){distance=d;nearest=player;}
  }
  return nearest;
 }
 @Unique public boolean knowlescraft$returning(){return knowlescraft$returning;}
 @Unique public class_243 knowlescraft$home(){return knowlescraft$origin;}
 @Unique public void knowlescraft$returned(){knowlescraft$returning=false;knowlescraft$origin=null;}
}
