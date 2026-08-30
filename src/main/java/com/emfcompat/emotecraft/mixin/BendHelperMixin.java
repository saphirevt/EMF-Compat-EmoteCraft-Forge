package com.emfcompat.emotecraft.mixin;

import com.emfcompat.emotecraft.BendBridge;
import dev.kosmx.playerAnim.core.util.Pair;
import dev.kosmx.playerAnim.impl.animation.BendHelper;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.core.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = BendHelper.class, remap = false)
public abstract class BendHelperMixin {
    @Inject(
            method = "initBend(Lnet/minecraft/client/model/geom/ModelPart;Lnet/minecraft/core/Direction;)V",
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private void emfCompat$initialize(ModelPart part, Direction direction, CallbackInfo callback) {
        if (BendBridge.isEmfPart(part)) {
            BendBridge.initialize(part, direction);
            callback.cancel();
        }
    }

    @Inject(
            method = "initCapeBend(Lnet/minecraft/client/model/geom/ModelPart;)V",
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private void emfCompat$initializeCape(ModelPart part, CallbackInfo callback) {
        if (BendBridge.isEmfPart(part)) {
            BendBridge.initializeCape(part);
            callback.cancel();
        }
    }

    @Inject(
            method = "bend(Lnet/minecraft/client/model/geom/ModelPart;Ldev/kosmx/playerAnim/core/util/Pair;)V",
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private void emfCompat$bendPair(ModelPart part, Pair<Float, Float> bend, CallbackInfo callback) {
        if (BendBridge.isEmfPart(part)) {
            BendBridge.apply(part, bend);
            callback.cancel();
        }
    }

    @Inject(
            method = "bend(Lnet/minecraft/client/model/geom/ModelPart;FF)V",
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private void emfCompat$bendValues(ModelPart part, float axis, float amount, CallbackInfo callback) {
        if (BendBridge.isEmfPart(part)) {
            BendBridge.apply(part, axis, amount);
            callback.cancel();
        }
    }
}