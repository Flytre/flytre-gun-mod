package net.flytre.fguns.entity;

import net.flytre.fguns.gun.BulletProperties;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3f;
import org.jetbrains.annotations.Nullable;

public class BulletEntityRenderer extends EntityRenderer<Bullet> {
    private final BulletModel model;


    public BulletEntityRenderer(EntityRendererFactory.Context ctx, BulletModel model) {
        super(ctx);
        this.shadowRadius = 0;
        this.shadowOpacity = 0;
        this.model = model;
    }

    public static int getOverlay(float whiteOverlayProgress) {
        return OverlayTexture.packUv(OverlayTexture.getU(whiteOverlayProgress), OverlayTexture.getV(false));
    }

    public void render(Bullet livingEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {

        matrixStack.push();

        if (!(livingEntity.getProperties() == BulletProperties.SNIPER)) {

            matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(livingEntity.getYaw() - 90.0F));
            matrixStack.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(livingEntity.getPitch()));
            matrixStack.translate(0, -0.7, 0);
            RenderLayer renderLayer = this.getRenderLayer(livingEntity);
            if (renderLayer != null) {
                VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(renderLayer);
                int r = getOverlay(0);
                this.model.render(matrixStack, vertexConsumer, i, r, 1.0F, 1.0F, 1.0F, 1.0F);
            }
        }

        matrixStack.pop();
        super.render(livingEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }

    @Nullable
    protected RenderLayer getRenderLayer(Bullet entity) {
        Identifier identifier = this.getTexture(entity);
        return this.model.getLayer(identifier);
    }

    public Identifier getTexture(Bullet tntEntity) {
        return new Identifier("fguns:textures/entity/bullet.png");
    }
}
