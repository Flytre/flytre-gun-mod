package net.flytre.fguns.workbench;

import net.flytre.fguns.workbench.IngredientList.IngredientEntry;
import net.flytre.flytre_lib.api.storage.recipe.QuantifiedIngredient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;

public class IngredientList extends EntryListWidget<IngredientEntry> {

    private final WorkbenchScreen screen;
    private final int accTop;

    public IngredientList(MinecraftClient client, int width, int height, int top, int bottom, int itemHeight, WorkbenchScreen screen, int left) {
        super(client, width, height, top, bottom, itemHeight);
        accTop = top;
        this.screen = screen;
        this.left = left;
        this.right = left + width;
        setRenderBackground(false);
        setRenderHeader(false, 0);
    }


    @Override
    protected int getScrollbarPositionX() {
        return this.right;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.top = Math.max(accTop, this.bottom - children().size() * 20);
        int k = getRowLeft();
        if (this.getScrollAmount() > getMaxScroll()) {
            this.setScrollAmount(this.getMaxScroll());
        }
        int l = this.top + 4 - (int) getScrollAmount();
        renderList(matrices, k, l, mouseX, mouseY, delta);
    }

    @Override
    public int getRowWidth() {
        return right - left;
    }

    public void refreshList() {
        clearEntries();
        WorkbenchRecipe recipe = screen.getCurrentRecipe();
        if (recipe != null) {
            for (QuantifiedIngredient ingredient : recipe.getQuantifiedIngredients()) {
                ItemStack[] stacks = ingredient.getMatchingStacks();
                if (stacks.length > 0)
                    addEntry(new IngredientEntry(stacks, client, this));

            }
        }
    }


    //Narrator support
    @Override
    public void appendNarrations(NarrationMessageBuilder builder) {
    }

    static class IngredientEntry extends Entry<IngredientEntry> {

        private final ItemStack[] stacks;
        private final MinecraftClient client;
        private final IngredientList list;
        private boolean green;
        private boolean init;

        public IngredientEntry(ItemStack[] stacks, MinecraftClient client, IngredientList list) {
            this.stacks = stacks;
            this.client = client;
            this.list = list;
            this.init = false;
        }

        private ItemStack currentStack() {
            long second = (System.currentTimeMillis()) / 1000;
            return stacks[(int) (second % stacks.length)];
        }

        public void setGreen(boolean green) {
            this.green = green;
            this.init = true;
        }

        public ItemStack[] getStacks() {
            return stacks;
        }

        @Override
        public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {

            if (!init)
                list.screen.tick();

            ItemStack stack = currentStack();

            DrawableHelper.fill(matrices, x, y - 1, x + entryWidth, y + entryHeight + 1, green ? 0xAA009e0b : 0xAA9e0300);

            int stackY = y + entryHeight / 2 - 8;

            client.getItemRenderer().renderGuiItemOverlay(client.textRenderer, stack, x, stackY, "" + stack.getCount());
            client.getItemRenderer().renderGuiItemIcon(stack, x, stackY);

            float textY = y + entryHeight / 2.0f - client.textRenderer.fontHeight / 2.0f + 1;

            String text = client.textRenderer.trimToWidth(I18n.translate(stack.getTranslationKey()), entryWidth - 20);
            client.textRenderer.draw(matrices, text, x + 20, textY, 0xffffff);
        }
    }
}
