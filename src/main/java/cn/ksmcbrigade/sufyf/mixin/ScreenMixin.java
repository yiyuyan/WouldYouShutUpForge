package cn.ksmcbrigade.sufyf.mixin;

import cn.ksmcbrigade.sufyf.ShutUpFuckYouForge;
import cn.ksmcbrigade.vmr.uitls.ModuleUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public class ScreenMixin {
    @Inject(method = "renderBackground",at = @At("HEAD"), cancellable = true)
    public void render(GuiGraphics p_283688_, int p_299421_, int p_298679_, float p_297268_, CallbackInfo ci){
        if(ModuleUtils.enabled("hack.name.no_back") && ShutUpFuckYouForge.renderCancel()){
            ci.cancel();
        }
    }
}
