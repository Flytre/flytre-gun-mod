package net.flytre.fguns.workbench;

import net.flytre.fguns.guns.GunItem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class StatList extends EntryListWidget<StatList.StatEntry> {
    private final WorkbenchScreen screen;


    public StatList(MinecraftClient client, int width, int height, int top, int bottom, int itemHeight, WorkbenchScreen screen, int left) {
        super(client, width, height, top, bottom, itemHeight);
        this.screen = screen;
        this.left = left;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        int k = getRowLeft();
        if (this.getScrollAmount() > getMaxScroll()) {
            this.setScrollAmount(this.getMaxScroll());
        }
        int l = this.top + 4 - (int) getScrollAmount();
        renderList(matrices, k, l, mouseX, mouseY, delta);
    }


    public void refreshList() {
        clearEntries();
        WorkbenchRecipe recipe = screen.getCurrentRecipe();
        if (recipe != null) {
            ItemStack out = recipe.getOutput();

            List<Text> texts;
            if (!(out.getItem() instanceof GunItem)) {
                texts = new ArrayList<>();
            } else {
                texts = ((GunItem) out.getItem()).sidebarInfo();
            }
            texts.forEach(i -> addEntry(new StatEntry(client, i)));
        }
    }

    static class StatEntry extends EntryListWidget.Entry<StatList.StatEntry> {

        private final MinecraftClient client;
        private final String key;
        private final String value;

        public StatEntry(MinecraftClient client, Text text) {
            String string = text.getString();
            String[] parts = string.split(": ");
            if (parts.length == 0) {
                key = string;
                value = "";
            } else {
                key = parts[0].replace("§c", "§f").replace("§7", "§f");
                value = parts[1].replace("§c", "§f").replace("§7", "§f");
            }
            this.client = client;
        }


        @Override
        public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            client.textRenderer.draw(matrices, key, x, y, 0xffffff);
            client.textRenderer.draw(matrices, value, x, y + 12, 0xAAffffff);
        }
    }
}
