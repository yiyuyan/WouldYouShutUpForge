package cn.ksmcbrigade.sufyf.mixin;

import cn.ksmcbrigade.sufyf.ShutUpFuckYouForge;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(Minecraft.class)
public class ChatHandleMixin {
    @Inject(method = "isBlocked",at = @At("RETURN"), cancellable = true)
    public void onChatAdd(UUID p_91247_, CallbackInfoReturnable<Boolean> cir){
        if (ShutUpFuckYouForge.fuckLists.contains(p_91247_)) cir.setReturnValue(true);
    }
}
