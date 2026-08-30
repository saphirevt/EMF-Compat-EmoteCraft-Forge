package com.emfcompat.emotecraft.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.kosmx.bendylib.MutableCuboid;
import io.github.kosmx.bendylib.impl.ICuboid;
import net.minecraft.util.Tuple;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(
        targets = "traben.entity_model_features.models.parts.EMFModelPartCustom$EMFCube",
        remap = false
)
public abstract class EmfCubeMixin {

    // Заменено имя метода с "compile" на SRG-имя "m_171332_"
    @Inject(
            method = "m_171332_(Lcom/mojang/blaze3d/vertex/PoseStack$Pose;Lcom/mojang/blaze3d/vertex/VertexConsumer;IIFFFF)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void emfCompat$renderActiveMutator(
            PoseStack.Pose pose,
            VertexConsumer consumer,
            int packedLight,
            int packedOverlay,
            float red,
            float green,
            float blue,
            float alpha,
            CallbackInfo callback
    ) {
        MutableCuboid mutable = (MutableCuboid) (Object) this;
        Tuple<String, ICuboid> active = mutable.getActiveMutator();
        if (active == null) {
            return;
        }

        ICuboid cuboid = active.getB();
        cuboid.render(pose, consumer, red, green, blue, alpha, packedLight, packedOverlay);

        if (cuboid.disableAfterDraw()) {
            mutable.getAndActivateMutator(null);
        }
        callback.cancel();
    }
}