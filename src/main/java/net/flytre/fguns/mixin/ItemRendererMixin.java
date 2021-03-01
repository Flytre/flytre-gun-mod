package net.flytre.fguns.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.flytre.fguns.gun.AbstractGun;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(value = ItemRenderer.class, priority = 500)
public abstract class ItemRendererMixin {

    @Shadow
    protected abstract void renderGuiQuad(BufferBuilder buffer, int x, int y, int width, int height, int red, int green, int blue, int alpha);

    @Inject(method = "renderGuiItemOverlay(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V", at = @At("RETURN"))
    public void fguns$gunDamage(TextRenderer renderer, ItemStack stack, int x, int y, String countLabel, CallbackInfo ci) {

        Item item = stack.getItem();

        if (item instanceof AbstractGun) {

            AbstractGun gun = (AbstractGun) item;
            CompoundTag tag = stack.getOrCreateTag();

            int clip = tag.contains("clip") ? tag.getInt("clip") : -1;
            int reload = tag.contains("reload") ? tag.getInt("reload") : -1;
            int cooldown = tag.contains("cooldown") ? tag.getInt("cooldown") : 0;


            if (clip != -1 && reload == -1 && clip < gun.getClipSize()) {
                float f = (float) gun.getClipSize() - clip;
                float g = (float) gun.getClipSize();
                renderBar(f, g, x, y);
            }

            if (reload != -1) {
                renderBar((float) reload, (float) (gun.getReloadTime() * 20), x, y);
            }

            if (cooldown > 0) {
                int max = (int) (20 / gun.getRps() - 1);

                if (max > 3)
                    renderCooldown((double) cooldown / max, x, y);
            }
        }
    }


    @Unique
    private void renderCooldown(double k, int x, int y) {
        RenderSystem.disableDepthTest();
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        Tessellator tessellator2 = Tessellator.getInstance();
        BufferBuilder bufferBuilder2 = tessellator2.getBuffer();
        this.renderGuiQuad(bufferBuilder2, x, y + MathHelper.floor(16.0F * (1.0F - k)), 16, MathHelper.ceil(16.0F * k), 255, 255, 255, 127);
        RenderSystem.enableTexture();
        RenderSystem.enableDepthTest();
    }

    @Unique
    @SuppressWarnings("deprecation")
    private void renderBar(float curr, float max, int x, int y) {
        RenderSystem.disableDepthTest();
        RenderSystem.disableTexture();
        RenderSystem.disableAlphaTest();
        RenderSystem.disableBlend();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        float h = Math.max(0.0F, (max - curr) / max);
        int i = Math.round(13.0F - curr * 13.0F / max);
        int j = MathHelper.hsvToRgb(h / 3.0F, 1.0F, 1.0F);
        renderGuiQuad(bufferBuilder, x + 2, y + 13, 13, 2, 0, 0, 0, 255);
        renderGuiQuad(bufferBuilder, x + 2, y + 13, i, 1, j >> 16 & 255, j >> 8 & 255, j & 255, 255);
        RenderSystem.enableBlend();
        RenderSystem.enableAlphaTest();
        RenderSystem.enableTexture();
        RenderSystem.enableDepthTest();
    }
}
