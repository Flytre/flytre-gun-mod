package net.flytre.fguns.entity;

import net.flytre.fguns.guns.GunType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class BulletEntityRenderer extends EntityRenderer<Bullet> {
    private final BulletModel model;


    public BulletEntityRenderer(EntityRenderDispatcher entityRenderDispatcher, BulletModel model) {
        super(entityRenderDispatcher);
        this.shadowRadius = 0;
        this.shadowOpacity = 0;
        BlockState block = Blocks.STONE_BUTTON.getDefaultState();
        this.model = model;
    }


    public void render(Bullet livingEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {

        matrixStack.push();

        if(!(livingEntity.getProperties() == GunType.SNIPER)) {

            matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(livingEntity.yaw - 90.0F));
            matrixStack.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(livingEntity.pitch));
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

    public static int getOverlay(float whiteOverlayProgress) {
        return OverlayTexture.packUv(OverlayTexture.getU(whiteOverlayProgress), OverlayTexture.getV(false));
    }
}
