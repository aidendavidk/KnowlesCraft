package family.settings.mixin;

import family.settings.KnowlesCraftLayout;

import net.minecraft.class_437;
import net.minecraft.class_332;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Render after the complete screen, including recipe-book and EMI layers. */
@Mixin(class_437.class)
public abstract class KnowlesCraftGuideMixin {
    @Inject(method="method_47413", at=@At("TAIL"))
    private void knowlescraft$drawHints(class_332 context,int mouseX,int mouseY,float delta,CallbackInfo ci){
        var screen=(class_437)(Object)this;
        KnowlesCraftLayout.layout(screen,true);
        context.method_51448().method_22903();
        context.method_51448().method_46416(0,0,400);
        KnowlesCraftLayout.renderGuides(screen,context,delta);
        context.method_51452();
        context.method_51448().method_22909();
    }
}
