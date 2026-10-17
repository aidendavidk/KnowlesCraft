package knowlescraft.mauler;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.*;

/** Meat heals and tames Maulers without changing stored experience. */
public final class KnowlesCraftMaulerCare implements ModInitializer {
 public void onInitialize(){UseEntityCallback.EVENT.register((player,world,hand,entity,hit)->feed(player,world,hand,entity));}
 private static boolean tagged(class_1799 stack,String id){return stack.method_31573(class_6862.method_40092(class_7924.field_41197,new class_2960(id)));}
 public static boolean isMeat(class_1799 stack){
  if(stack.method_7960())return false;
  var food=stack.method_7909().method_19264();
  if(food==null)return false;
  String id=class_7923.field_41178.method_10221(stack.method_7909()).toString();
  if(id.equals("minecraft:rotten_flesh"))return false;
  if(tagged(stack,"knowlescraft:mauler_food"))return true;
  if(food.method_19232())return true;
  // Common tags cover modded cuts and fish whose food component omits isMeat.
  if(tagged(stack,"c:foods/cooked_meats/cooked_eggs"))return false;
  return tagged(stack,"c:foods/raw_meats")||tagged(stack,"c:foods/cooked_meats")
   ||tagged(stack,"c:foods/raw_fishes")||tagged(stack,"c:foods/cooked_fishes")
   ||tagged(stack,"minecraft:fishes")||tagged(stack,"forge:raw_meats")||tagged(stack,"forge:cooked_meats");
 }
 public static class_1269 feed(class_1657 player,class_1937 world,class_1268 hand,class_1297 target){
  if(player.method_7325()||!(target instanceof class_1309 mob)||!class_7923.field_41177.method_10221(target.method_5864()).toString().equals("friendsandfoes:mauler"))return class_1269.field_5811;
  var stack=player.method_5998(hand);
  var pet=(PetMauler)mob;boolean wild=pet.knowlescraft$owner()==null;
  if(!mob.method_5805()||(!wild&&mob.method_6032()>=mob.method_6063())||!isMeat(stack))return class_1269.field_5811;
  if(!world.field_9236){
   mob.method_6025(Math.max(1,stack.method_7909().method_19264().method_19230()));
   if(wild){
    var mauler=(com.faboslav.friendsandfoes.common.entity.MaulerEntity)mob;
    boolean tamed=mob.method_6051().method_43048(3)==0;
    if(tamed)pet.knowlescraft$tame(player.method_5667());
    world.method_8421(mob,(byte)(tamed?7:6));
   }
   if(!player.method_31549().field_7477){
    var remainder=stack.method_7909().method_7858();stack.method_7934(1);
    if(remainder!=null){var returned=new class_1799(remainder);if(stack.method_7960())player.method_6122(hand,returned);else if(!player.method_31548().method_7394(returned))player.method_7328(returned,false);}
   }
   mob.method_5783(class_7923.field_41172.method_10223(new class_2960("friendsandfoes:entity.mauler.bite")),1,1);
  }
  return class_1269.method_29236(world.field_9236);
 }
}
