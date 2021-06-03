package net.flytre.fguns.mixin.client;


import net.flytre.fguns.entity.HeldGunFeatureRenderer;
import net.minecraft.client.render.entity.*;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.mob.MobEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MobEntityRenderer.class)
public abstract class MobEntityRendererMixin<T extends MobEntity, M extends EntityModel<T>> extends LivingEntityRenderer<T, M> {

    public MobEntityRendererMixin(EntityRendererFactory.Context ctx, M model, float shadowRadius) {
        super(ctx, model, shadowRadius);
    }

    @SuppressWarnings("ConstantConditions")
    @Inject(method = "<init>", at = @At("TAIL"))
    public void fguns$guns(EntityRendererFactory.Context context, M entityModel, float f, CallbackInfo ci) {

        if (!((Object) this instanceof BipedEntityRenderer))
            this.addFeature(new HeldGunFeatureRenderer<>(this));

    }
}
