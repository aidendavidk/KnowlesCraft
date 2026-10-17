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
 @Unique private int knowlescraft$lastHurt=-1,knowlescraft$lastAttack=-1,knowlescraft$lastSelfHurt=-1;
 @Unique private class_243 knowlescraft$origin;
 @Unique private UUID knowlescraft$disengaged;
 @Unique private class_243 knowlescraft$disengageOrigin;
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
  knowlescraft$lastAttack=player==null?-1:player.method_6083();knowlescraft$lastSelfHurt=method_6117();
 }
 public void knowlescraft$untame(){
  if(knowlescraft$owner==null)return;
  knowlescraft$owner=null;knowlescraft$installed=false;knowlescraft$origin=null;knowlescraft$returning=false;knowlescraft$lastHurt=-1;
  field_6201.method_35113(goal->true);field_6185.method_35113(goal->true);method_5959();field_6201.method_6277(1,new PetMovementGoal(this));
  method_5980(null);method_6015(null);var self=(MaulerEntity)(Object)this;self.method_29514(0);self.method_29513(null);self.setTicksUntilNextBurrowingDown(1200);
 }
 @Unique private void knowlescraft$install(){
  if(knowlescraft$installed)return;knowlescraft$installed=true;
  var self=(MaulerEntity)(Object)this;
  field_6185.method_35113(goal->true);
  field_6201.method_6280(self.burrowDownGoal);
  self.method_29514(0);self.method_29513(null);method_5980(null);method_6015(null);
  self.setBurrowedDown(false);self.setBurrowingDownAnimationProgress(0);self.setTicksUntilNextBurrowingDown(Integer.MAX_VALUE);
 }
 @Inject(method="method_5652",at=@At("TAIL"))
 private void knowlescraft$save(class_2487 nbt,CallbackInfo ci){if(knowlescraft$owner!=null)nbt.method_25927("KnowlesCraftOwner",knowlescraft$owner);}
 @Inject(method="method_5749",at=@At("TAIL"))
 private void knowlescraft$load(class_2487 nbt,CallbackInfo ci){if(nbt.method_25928("KnowlesCraftOwner"))knowlescraft$tame(nbt.method_25926("KnowlesCraftOwner"));else knowlescraft$untame();}
 @Inject(method="method_6007",at=@At("HEAD"))
 private void knowlescraft$tick(CallbackInfo ci){knowlescraft$petTick();}
 @Inject(method="tryToInteractWithEnhancedItem",at=@At("RETURN"))
 private void knowlescraft$magicTrust(class_1657 player,class_1268 hand,class_1799 stack,CallbackInfoReturnable<Boolean> ci){
  if(ci.getReturnValue())KnowlesCraftMaulerCare.offerTrust(player,this);
 }
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
   boolean outside=knowlescraft$origin!=null&&(enemy.method_19538().method_1025(knowlescraft$origin)>256||method_19538().method_1025(knowlescraft$origin)>256);
   if(!knowlescraft$safeEnemy(enemy)||knowlescraft$origin==null||outside){
    if(outside){knowlescraft$disengaged=enemy.method_5667();knowlescraft$disengageOrigin=knowlescraft$origin;}
    method_5980(null);self.method_29514(0);self.method_29513(null);knowlescraft$returning=knowlescraft$origin!=null;method_5942().method_6340();
   }
  }else if(knowlescraft$origin!=null&&!knowlescraft$returning)knowlescraft$returning=true;
  var owner=method_37908().method_18470(knowlescraft$owner);
  class_1309 candidate=null;
  if(owner!=null){
   int hurt=owner.method_6117(),attack=owner.method_6083();
   if(attack!=knowlescraft$lastAttack&&owner.field_6012-attack<=100&&method_5858(owner)<=256)candidate=owner.method_6052();
   if(hurt!=knowlescraft$lastHurt&&owner.field_6012-hurt<=100&&method_5858(owner)<=256)candidate=owner.method_6065();
   knowlescraft$lastHurt=hurt;knowlescraft$lastAttack=attack;
  }
  int hurt=method_6117();
  if(hurt!=knowlescraft$lastSelfHurt&&field_6012-hurt<=100&&knowlescraft$safeEnemy(method_6065()))candidate=method_6065();
  knowlescraft$lastSelfHurt=hurt;
  if(candidate!=null&&knowlescraft$safeEnemy(candidate)&&method_5858(candidate)<=256){
   boolean outsideOldBoundary=candidate.method_5667().equals(knowlescraft$disengaged)&&knowlescraft$disengageOrigin!=null&&(candidate.method_19538().method_1025(knowlescraft$disengageOrigin)>256||method_19538().method_1025(knowlescraft$disengageOrigin)>256);
   if(!outsideOldBoundary){
    if(knowlescraft$origin==null||knowlescraft$returning)knowlescraft$origin=method_19538();
    knowlescraft$returning=false;method_5980(candidate);
   }
  }
  if(method_5968()==null){self.method_29514(0);self.method_29513(null);}
  if(method_6065()!=null&&method_6065().method_5667().equals(knowlescraft$owner))method_6015(null);
 }
 @Unique private boolean knowlescraft$safeEnemy(class_1309 enemy){
  if(enemy==null||enemy==this||!enemy.method_5805()||enemy.method_5667().equals(knowlescraft$owner))return false;
  if(enemy instanceof class_1657 player&&(player.method_7325()||player.method_31549().field_7477))return false;
  if(enemy instanceof class_6025 pet&&knowlescraft$owner.equals(pet.method_6139()))return false;
  var owner=method_37908().method_18470(knowlescraft$owner);
  return owner==null||!owner.method_5722(enemy);
 }
 /** Shared MOVE control keeps luring, returning, combat and wandering mutually exclusive. */
 @Unique public class_1657 knowlescraft$lure(){
  class_1657 nearest=null;double distance=100;
  for(var player:method_37908().method_18456())if(!player.method_7325()&&player.method_5805()&&(KnowlesCraftMaulerCare.isLure(player.method_6047())||KnowlesCraftMaulerCare.isLure(player.method_6079()))){
   double d=method_5858(player);if(d<distance){distance=d;nearest=player;}
  }
  return nearest;
 }
 @Unique public boolean knowlescraft$returning(){return knowlescraft$returning;}
 @Unique public class_243 knowlescraft$home(){return knowlescraft$origin;}
 @Unique public void knowlescraft$returned(){knowlescraft$returning=false;knowlescraft$origin=null;}
}
