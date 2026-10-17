package family.settings;
import java.nio.file.*;
import java.util.*;
import net.minecraft.*;
import dev.isxander.controlify.controller.ControllerEntity;
import dev.isxander.controlify.api.bind.InputBinding;
import dev.isxander.controlify.gui.screen.RadialMenuScreen;

/** Delays the native wheel and reuses its own action dispatch for a quick tap. */
public final class KnowlesCraftRadial {
 public static final class Gesture {
  private boolean down,opened,blocked;private long started;
  public int update(boolean pressed,boolean allowed,long now){
   if(!allowed){down=pressed;blocked=pressed;opened=false;return 0;}
   if(blocked){if(!pressed){blocked=false;down=false;}return 0;}
   if(pressed&&!down){down=true;opened=false;started=now;return 0;}
   if(pressed&&down&&!opened&&now-started>=250_000_000L){opened=true;return 2;}
   if(!pressed&&down){down=false;boolean tap=!opened&&now-started<250_000_000L;opened=false;return tap?1:0;}
   return 0;
  }
 }
 private static final Map<ControllerEntity,Gesture> gestures=new WeakHashMap<>();
 private static final Map<String,String> last=new HashMap<>();private static boolean loaded;
 private static Path file(){return net.fabricmc.loader.api.FabricLoader.getInstance().getConfigDir().resolve("knowlescraft/radial-last-action.json");}
 private static void load(){
  if(loaded)return;loaded=true;
  try{if(Files.exists(file())){var json=com.google.gson.JsonParser.parseString(Files.readString(file())).getAsJsonObject();for(var e:json.entrySet())if(e.getValue().isJsonPrimitive())last.put(e.getKey(),e.getValue().getAsString());}}catch(Exception e){System.err.println("[KnowlesCraft] Could not load radial shortcut.");}
 }
 public static void remember(ControllerEntity controller,class_2960 id){
  load();if(id.toString().equals(last.put(controller.uid(),id.toString())))return;
  try{Files.createDirectories(file().getParent());var json=new com.google.gson.JsonObject();last.forEach(json::addProperty);FamilySettings.atomicWrite(file(),new com.google.gson.GsonBuilder().setPrettyPrinting().create().toJson(json));}catch(Exception e){System.err.println("[KnowlesCraft] Could not save radial shortcut.");}
 }
 public static String last(ControllerEntity controller){load();return last.get(controller.uid());}
 public static void frame(class_310 mc,ControllerEntity controller){
  if(mc.field_1724==null||(mc.field_1755!=null&&!(mc.field_1755 instanceof RadialMenuScreen))){
   var input=controller.input().orElseThrow();var bind=dev.isxander.controlify.bindings.ControlifyBindings.RADIAL_MENU.on(controller);
   gestures.computeIfAbsent(controller,c->new Gesture()).update(bind.boundInput().state(input.rawStateNow())>=input.confObj().buttonActivationThreshold,false,System.nanoTime());
  }
 }
 public static void forget(ControllerEntity controller){gestures.remove(controller);}
 public static boolean shouldOpen(ControllerEntity controller,InputBinding bind){
  var input=controller.input().orElseThrow();var mc=class_310.method_1551();
  boolean pressed=bind.boundInput().state(input.rawStateNow())>=input.confObj().buttonActivationThreshold;
  int action=gestures.computeIfAbsent(controller,c->new Gesture()).update(pressed,mc.field_1755==null&&mc.field_1724!=null,System.nanoTime());
  if(action==2)return true;
  if(action==1){
   String id=last(controller);class_2960 key=null;
   try{if(id!=null)key=new class_2960(id);}catch(Exception ignored){}
   if(key!=null&&Arrays.asList(input.confObj().radialActions).contains(key)){
    var binding=input.getBinding(key);if(binding!=null&&binding.radialIcon().isPresent()){binding.fakePress();return false;}
   }
   mc.field_1724.method_7353(class_2561.method_43470("Hold D-pad Right to choose a radial shortcut"),true);
  }
  return false;
 }
}
