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
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
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

        if(player == null || player.inventory == null)
            return;

        ItemStack stack = player.getOffHandStack();
        if(!(stack.getItem() instanceof GunItem))
            stack = player.getMainHandStack();

        if(!(stack.getItem() instanceof GunItem))
            return;

        GunItem gun = (GunItem) stack.getItem();

        int max = gun.getClipSize();
        CompoundTag tag = stack.getOrCreateTag();
        int curr = tag.contains("clip") ? tag.getInt("clip") : gun.getClipSize();
        int reload = tag.contains("reload") ? tag.getInt("reload") : -1;
        Text gunText;

        if(reload != -1) {
            gunText = Text.of(String.format("%.2f",reload/20.0) + "s");
        } else {
            gunText = Text.of(curr + " / " + max);
        }

        drawBundle(matrixStack,x,y,stack,gunText,itemRenderer,textRenderer);
        drawBundle(matrixStack,x,y + 30,new ItemStack(gun.getAmmoItem()),Text.of("" + countItem(gun.getAmmoItem(),player)),itemRenderer,textRenderer);

    }

    private void drawBundle(MatrixStack matrixStack, int x, int y, ItemStack stack, Text text, ItemRenderer itemRenderer, TextRenderer textRenderer) {
        DrawableHelper.fill(matrixStack,x - 5, y - 5, x + 75, y + 20, 0x88101747);
        itemRenderer.renderGuiItemIcon(stack, x, y);
        itemRenderer.renderGuiItemOverlay(textRenderer,stack,x, y);
        textRenderer.draw(matrixStack, text, x + 25, y + 3,11250603);
    }


    public int countItem(Item item, PlayerEntity player) {
        ArrayList<ItemStack> items = new ArrayList<>(player.inventory.main);
        items.addAll(player.inventory.armor);
        items.addAll(player.inventory.offHand);

        OptionalInt sum = items.stream().filter(i -> i.getItem() == item).mapToInt(ItemStack::getCount).reduce(Integer::sum);
        return sum.isPresent() ? sum.getAsInt() : 0;
    }

    @Environment(EnvType.CLIENT)
    private void itemHUD() {
        final PlayerEntity player = client.player;
        final TextRenderer textRenderer = client.textRenderer;
        final MatrixStack matrixStack = new MatrixStack();

        int x = client.getWindow().getScaledWidth();
        int y = client.getWindow().getScaledHeight();

        final ItemRenderer itemRenderer = client.getItemRenderer();
        if (player == null || player.inventory == null)
            return;

        ArrayList<ItemStack> items = new ArrayList<>(player.inventory.main);
        items.addAll(player.inventory.armor);
        items.addAll(player.inventory.offHand);

        HashMap<Item, Integer> id_to_count = new HashMap<>();
        HashMap<Item, ItemStack> id_to_itemstack = new HashMap<>();
        ArrayList<ItemStack> unstackables = new ArrayList<>();

        for (ItemStack item : items) {
            Item id = item.getItem();
            if (id == Items.AIR)
                continue;
            //If the item is damagable and either is damaged OR has other NBT besides damage display it separately
            if (id.isDamageable() &&
                    ((item.getTag() != null && item.getTag().contains("Damage") && ((IntTag)(item.getTag().get("Damage"))).getInt() > 0) ||
                            (item.getTag() != null && item.getTag().getKeys() != null && item.getTag().getKeys().size() > 1))) {
                unstackables.add(item);

            //Otherwise display it 'normally'
            } else if (!id_to_count.containsKey(id)) {
                id_to_count.put(id, item.getCount());
                id_to_itemstack.put(id, item);
            } else
                id_to_count.put(id, id_to_count.get(id) + item.getCount());
        }


        int y_offset = 0;

        //Normal items (no NBT)

        ArrayList<Item> keys = new ArrayList<>(id_to_count.keySet());
        keys.sort(Comparator.comparing(id_to_count::get));

        for (Item key :keys) {
            int ct = id_to_count.get(key);
            ItemStack item = id_to_itemstack.get(key);
            itemRenderer.renderGuiItemIcon(item, x - 30, 20 * y_offset);
            itemRenderer.renderGuiItemOverlay(textRenderer,item,x - 30,20 * y_offset++, String.valueOf(ct));
    }

        //Special Items (NBT and unstackable)
        unstackables.sort(Comparator.comparing( i -> 1 - (i.getDamage() / i.getMaxDamage())));
        for (ItemStack slot : unstackables) {
            itemRenderer.renderGuiItemIcon(slot, x - 30, 20 * y_offset);
            itemRenderer.renderGuiItemOverlay(textRenderer,slot,x - 30,20 * y_offset++);
        }

    }

}
