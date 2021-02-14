package net.flytre.fguns.workbench;

import net.flytre.flytre_lib.common.recipe.QuantifiedIngredient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.TranslatableText;

public class IngredientList extends EntryListWidget<IngredientList.IngredientEntry> {

    private final WorkbenchScreen screen;

    public IngredientList(MinecraftClient client, int width, int height, int top, int bottom, int itemHeight, WorkbenchScreen screen, int left) {
        super(client, width, height, top, bottom, itemHeight);
        this.screen = screen;
        this.left = left;
    }


    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.top = this.bottom - children().size() * 20;
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
            for (QuantifiedIngredient ingredient : recipe.getIngredients()) {
                ItemStack[] stacks = ingredient.getMatchingStacksClient();
                if (stacks.length > 0)
                    addEntry(new IngredientEntry(stacks[0], client, this));

            }
        }
    }

    static class IngredientEntry extends EntryListWidget.Entry<IngredientEntry> {

        private final ItemStack stack;
        private final MinecraftClient client;
        private final IngredientList list;
        private boolean green;
        private boolean init;

        public IngredientEntry(ItemStack stack, MinecraftClient client, IngredientList list) {
            this.stack = stack;
            this.client = client;
            this.list = list;
            this.init = false;
        }

        public void setGreen(boolean green) {
            this.green = green;
            this.init = true;
        }

        public ItemStack getStack() {
            return stack;
        }

        @Override
        public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {

            if (!init)
                list.screen.tick();

            DrawableHelper.fill(matrices, x, y - 1, x + 93, y + entryHeight + 1, green ? 0xAA009e0b : 0xAA9e0300);
            client.getItemRenderer().renderGuiItemOverlay(client.textRenderer, stack, x, y, "" + stack.getCount());
            client.getItemRenderer().renderGuiItemIcon(stack, x, y);
            client.textRenderer.draw(matrices, new TranslatableText(stack.getTranslationKey()), x + 20, y + 2, 0xffffff);
        }
    }
}
