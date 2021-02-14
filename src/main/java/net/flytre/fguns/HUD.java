package net.flytre.fguns;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.flytre.fguns.guns.GunItem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

import java.util.ArrayList;
import java.util.OptionalInt;

public class HUD {

    private final MinecraftClient client;


    public HUD() {
        client = MinecraftClient.getInstance();
        HudRenderCallback.EVENT.register((__, ___) -> this.render());
    }

    @Environment(EnvType.CLIENT)
    private void render() {
        gunHUD();
    }

    @Environment(EnvType.CLIENT)
    private void gunHUD() {
        final PlayerEntity player = client.player;
        final TextRenderer textRenderer = client.textRenderer;
        final MatrixStack matrixStack = new MatrixStack();
        final ItemRenderer itemRenderer = client.getItemRenderer();


        int x = client.getWindow().getScaledWidth() - 100;
        int y = client.getWindow().getScaledHeight() - 70;

        if (player == null || player.inventory == null)
            return;

        ItemStack stack = player.getOffHandStack();
        if (!(stack.getItem() instanceof GunItem))
            stack = player.getMainHandStack();

        if (!(stack.getItem() instanceof GunItem))
            return;

        GunItem gun = (GunItem) stack.getItem();

        int max = gun.getClipSize();
        CompoundTag tag = stack.getOrCreateTag();
        int curr = tag.contains("clip") ? tag.getInt("clip") : gun.getClipSize();
        int reload = tag.contains("reload") ? tag.getInt("reload") : -1;
        int mode = tag.contains("mode") ? tag.getInt("mode") : 0;
        Text modeText;
        switch (mode) {
            case 1:
                modeText = new TranslatableText("text.fguns.burst");
                break;
            case 2:
                modeText = new TranslatableText("text.fguns.semi");
                break;
            default:
                modeText = new TranslatableText("text.fguns.auto");

        }
        Text gunText;

        if (reload != -1) {
            gunText = Text.of(String.format("%.2f", reload / 20.0) + "s");
        } else {
            gunText = Text.of(curr + " / " + max);
        }

        drawTwoLineBundle(matrixStack, x, y - 5, stack, gunText, modeText, itemRenderer, textRenderer);
        drawBundle(matrixStack, x, y + 30, new ItemStack(gun.getAmmoItem()), Text.of("" + countItem(gun.getAmmoItem(), player)), itemRenderer, textRenderer);

    }

    private void drawBundle(MatrixStack matrixStack, int x, int y, ItemStack stack, Text text, ItemRenderer itemRenderer, TextRenderer textRenderer) {
        DrawableHelper.fill(matrixStack, x - 5, y - 5, x + 75, y + 20, 0x88101747);
        itemRenderer.renderGuiItemIcon(stack, x, y);
        itemRenderer.renderGuiItemOverlay(textRenderer, stack, x, y);
        textRenderer.draw(matrixStack, text, x + 25, y + 3, 11250603);
    }

    private void drawTwoLineBundle(MatrixStack matrixStack, int x, int y, ItemStack stack, Text text, Text text2, ItemRenderer itemRenderer, TextRenderer textRenderer) {
        DrawableHelper.fill(matrixStack, x - 5, y - 5, x + 75, y + 25, 0x88101747);
        itemRenderer.renderGuiItemIcon(stack, x, y);
        itemRenderer.renderGuiItemOverlay(textRenderer, stack, x, y);
        textRenderer.draw(matrixStack, text, x + 25, y + 2, 11250603);
        textRenderer.draw(matrixStack, text2, x + 25, y + 14, 11250603);

    }


    public int countItem(Item item, PlayerEntity player) {
        ArrayList<ItemStack> items = new ArrayList<>(player.inventory.main);
        items.addAll(player.inventory.armor);
        items.addAll(player.inventory.offHand);

        OptionalInt sum = items.stream().filter(i -> i.getItem() == item).mapToInt(ItemStack::getCount).reduce(Integer::sum);
        return sum.isPresent() ? sum.getAsInt() : 0;
    }

}
