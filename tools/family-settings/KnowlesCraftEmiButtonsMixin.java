package family.settings.mixin;

import dev.emi.emi.screen.EmiScreenManager;
import dev.emi.emi.screen.widget.SizedButtonWidget;
import net.minecraft.class_332;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value=SizedButtonWidget.class,remap=false)
public abstract class KnowlesCraftEmiButtonsMixin {
    @Inject(method="method_48579",at=@At("HEAD"),cancellable=true,remap=false)
    private void knowlescraft$hideBottomShortcuts(class_332 context,int mouseX,int mouseY,float delta,CallbackInfo ci){
        Object button=this;
        if(button==EmiScreenManager.emi||button==EmiScreenManager.tree)ci.cancel();
    }
}
