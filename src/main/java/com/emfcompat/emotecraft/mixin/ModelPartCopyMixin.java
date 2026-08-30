package com.emfcompat.emotecraft.mixin;

import com.emfcompat.emotecraft.BendBridge;
import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ModelPart.class)
public abstract class ModelPartCopyMixin {
    // Явно указываем, что метод принимает (Lnet/minecraft/client/model/geom/ModelPart;) и возвращает void (V)
    @Inject(method = "copyFrom(Lnet/minecraft/client/model/geom/ModelPart;)V", at = @At("TAIL"))
    private void emfCompat$copyBendState(ModelPart source, CallbackInfo callback) {
        BendBridge.copy(source, (ModelPart) (Object) this);
    }
}