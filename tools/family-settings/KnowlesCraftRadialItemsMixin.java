package knowlescraft.radial.mixin;
import family.settings.KnowlesCraftRadial;
import dev.isxander.controlify.controller.ControllerEntity;
import dev.isxander.controlify.gui.screen.*;
import dev.isxander.controlify.api.bind.RadialIcon;
import net.minecraft.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value=RadialItems.class,remap=false)
public abstract class KnowlesCraftRadialItemsMixin {
 @Inject(method="getItemForBinding",at=@At("RETURN"),cancellable=true)
 private static void knowlescraft$rememberChoice(class_2960 id,ControllerEntity controller,CallbackInfoReturnable<RadialMenuScreen.RadialItem> ci){
  var item=ci.getReturnValue();if(item==RadialItems.EMPTY_ACTION)return;
  ci.setReturnValue(new RadialMenuScreen.RadialItem(){
   public class_2561 name(){return id.toString().equals("knowlescraft:toggle_hud")?class_2561.method_43470(class_310.method_1551().field_1690.field_1842?"Show HUD":"Hide HUD"):item.name();}
   public RadialIcon icon(){return item.icon();}
   public boolean playAction(){boolean played;if(id.toString().equals("knowlescraft:toggle_hud")){family.settings.KnowlesCraftController.toggleHud();played=true;}else played=item.playAction();if(played)KnowlesCraftRadial.remember(controller,id);return played;}
  });
 }
}
