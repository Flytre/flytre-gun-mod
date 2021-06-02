// Made with Blockbench 3.7.4
// Exported for Minecraft version 1.15
// Paste this class into your mod and generate all required imports

package net.flytre.fguns.entity;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;

import java.util.HashMap;
import java.util.List;

public class BulletModel extends EntityModel<Bullet> {
    private final ModelPart main;

    public BulletModel() {
        main = new ModelPart(List.of(new ModelPart.Cuboid(12, 0, -1.0F, -9.0F, 0.0F, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F, false, 16, 16)), new HashMap<>());
        main.setPivot(0.0F, 24.0F, 0.0F);
    }

    @Override
    public void setAngles(Bullet entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        //previously the render function, render code was moved to a method below
    }

    @Override
    public void render(MatrixStack matrixStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        main.render(matrixStack, buffer, packedLight, packedOverlay);
    }

}