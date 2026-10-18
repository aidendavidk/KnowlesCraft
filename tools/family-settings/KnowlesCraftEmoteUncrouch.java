package family.settings;
import net.minecraft.*;
import java.util.*;
import dev.isxander.controlify.controller.ControllerEntity;
import dev.isxander.controlify.bindings.ControlifyBindings;
import knowlescraft.radial.mixin.KnowlesCraftDualInputAccessor;
import knowlescraft.radial.mixin.KnowlesCraftSneakKeyAccessor;
/** Reset crouch only once playback actually starts; opening/browsing a wheel is harmless. */
public final class KnowlesCraftEmoteUncrouch {
 private static final Set<ControllerEntity> held=Collections.newSetFromMap(new WeakHashMap<>());
 public static void clear(class_744 input){
  if(input==null)return;input.field_3903=false;
  if(input instanceof KnowlesCraftDualInputAccessor dual){clear(dual.knowlescraft$first());clear(dual.knowlescraft$second());}
 }
 public static boolean suppressHeld(ControllerEntity controller){
  if(!held.contains(controller))return false;
  if(!ControlifyBindings.SNEAK.on(controller).digitalNow()){held.remove(controller);return false;}
  return true;
 }
 public static void start(class_310 mc){
  var player=mc.field_1724;if(player==null)return;
  var key=mc.field_1690.field_1832;
  if(key instanceof KnowlesCraftSneakKeyAccessor sticky)sticky.knowlescraft$resetToggle();else key.method_23481(false);
  clear(player.field_3913);player.method_5660(false);
  dev.isxander.controlify.Controlify.instance().getCurrentController().ifPresent(c->{if(ControlifyBindings.SNEAK.on(c).digitalNow())held.add(c);});
 }
}
