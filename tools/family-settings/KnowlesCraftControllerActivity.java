package family.settings;
import dev.isxander.controlify.controller.input.ControllerStateView;

/** Report intentional, deadzone-filtered controller input to WATUT's normal idle timer. */
public final class KnowlesCraftControllerActivity {
 public static boolean active(ControllerStateView state){
  for(var button:state.getButtons())if(state.isButtonDown(button))return true;
  for(var hat:state.getHats())if(!state.getHatState(hat).isCentered())return true;
  for(var axis:state.getAxes())if(Math.abs(state.getAxisState(axis)-state.getAxisResting(axis))>0.05f)return true;
  return false;
 }
 public static void report(dev.isxander.controlify.controller.ControllerEntity controller){
  var mc=net.minecraft.class_310.method_1551();
  if(mc.field_1724!=null&&mc.method_1569()&&active(controller.input().orElseThrow().stateNow()))com.corosus.watut.WatutMod.getPlayerStatusManagerClient().onAction();
 }
}
