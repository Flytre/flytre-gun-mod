package net.flytre.fguns.workbench;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.flytre.fguns.FlytreGuns;
import net.flytre.fguns.network.CraftGunC2SPacket;
import net.flytre.flytre_lib.api.base.util.InventoryUtils;
import net.flytre.flytre_lib.api.gui.CoordinateProvider;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3f;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static net.flytre.fguns.client.TempClientData.currentRecipeIndex;


public class WorkbenchScreen extends HandledScreen<WorkbenchScreenHandler> implements CoordinateProvider {

    private static final Identifier INVENTORY_TEXTURE = new Identifier("fguns:textures/gui/workbench_base.png");
    private static final Identifier PANEL_TEXTURE = new Identifier("fguns:textures/gui/workbench_side.png");

    private final List<WorkbenchRecipe> recipes;

    private IngredientList ingredientList;
    private StatList statList;


    public WorkbenchScreen(WorkbenchScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        recipes = Objects.requireNonNull(MinecraftClient.getInstance().getNetworkHandler()).getRecipeManager().listAllOfType(FlytreGuns.WORKBENCH_RECIPE);
    }

    public WorkbenchRecipe getCurrentRecipe() {
        return recipes.get(currentRecipeIndex);
    }

    public void setCurrentRecipe(int index) {

        if (index == -1)
            index = recipes.size() - 1;

        index %= recipes.size();
        currentRecipeIndex = index;
        ingredientList.refreshList();
        statList.refreshList();
    }

    @Override
    protected void drawForeground(MatrixStack matrices, int mouseX, int mouseY) {
        if (getCurrentRecipe() != null) {
            DrawableHelper.drawCenteredText(matrices, textRenderer, new TranslatableText(getCurrentRecipe().getOutputItem().getTranslationKey()), this.backgroundWidth / 2, titleY, 4210752);
        }
    }

    @Override
    protected void handledScreenTick() {
        assert client != null && client.player != null;
        Map<Item, Integer> map = InventoryUtils.countInventoryContents(client.player.getInventory());
        for (IngredientList.IngredientEntry entry : ingredientList.children())
            entry.setGreen(Arrays.stream(entry.getStacks()).anyMatch(i -> map.getOrDefault(i.getItem(),0) >= i.getCount()));
    }

    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        ingredientList.render(matrices, mouseX, mouseY, delta);
        statList.render(matrices, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(matrices, mouseX, mouseY);
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        assert this.client != null;
        if (getCurrentRecipe() != null) {
            ItemStack stack = new ItemStack(getCurrentRecipe().getOutputItem(), 1);
            renderStack(delta, stack);
        }

        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, INVENTORY_TEXTURE);
        int i = this.x;
        int j = (this.height - this.backgroundHeight) / 2;
        this.drawTexture(matrices, i, j, 0, 0, this.backgroundWidth, this.backgroundHeight);

        RenderSystem.setShaderTexture(0, PANEL_TEXTURE);
        this.drawTexture(matrices, this.x + 95, this.y, 0, 0, this.backgroundWidth + 100, this.backgroundHeight);
        this.drawTexture(matrices, this.x - 205, this.y, 0, 0, this.backgroundWidth + 100, this.backgroundHeight);

    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (ingredientList.isMouseOver(mouseX, mouseY))
            return ingredientList.mouseScrolled(mouseX, mouseY, amount);
        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    private void renderStack(float delta, ItemStack stack) {
        assert client != null;
        ItemRenderer renderer = client.getItemRenderer();
        renderGuiItemModel(stack, this.x + 80, this.y + 42, renderer.getHeldItemModel(stack, null, null, 0), delta);
    }


    //See ItemRenderer::renderInGUI
    @SuppressWarnings("deprecation")
    protected void renderGuiItemModel(ItemStack stack, int x, int y, BakedModel model, float delta) {
        assert client != null && client.world != null : "Null client or world found";
        TextureManager textureManager = client.getTextureManager();
        ItemRenderer renderer = client.getItemRenderer();

        textureManager.getTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE).setFilter(false, false);
        RenderSystem.setShaderTexture(0, SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        MatrixStack matrixStack = RenderSystem.getModelViewStack();
        matrixStack.push();
        matrixStack.translate(x, y, 100.0F + renderer.zOffset);
        matrixStack.translate(8.0D, 8.0D, 0.0D);
        matrixStack.scale(1.0F, -1.0F, 1.0F);
        matrixStack.scale(16.0F, 16.0F, 16.0F);
        matrixStack.scale(3.0f, 3.0f, 3.0f);
        RenderSystem.applyModelViewMatrix();
        MatrixStack matrixStack2 = new MatrixStack();
        float rot = ((client.world.getTime() + delta) * 4) % 360;
        matrixStack2.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(rot));
        matrixStack2.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(-30));
        VertexConsumerProvider.Immediate immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
        boolean bl = !model.isSideLit();
        if (bl) {
            DiffuseLighting.disableGuiDepthLighting();
        }
        assert client.world != null;
        itemRenderer.renderItem(stack, ModelTransformation.Mode.GUI, false, matrixStack2, immediate, 15728880, OverlayTexture.DEFAULT_UV, model);
        immediate.draw();
        RenderSystem.enableDepthTest();
        if (bl) {
            DiffuseLighting.enableGuiDepthLighting();
        }

        matrixStack.pop();
        RenderSystem.applyModelViewMatrix();
    }


    @Override
    protected void init() {
        super.init();
        addDrawableChild(new ButtonWidget(this.x, this.y, 10, 20, new LiteralText("\u276E"), (button) -> setCurrentRecipe(currentRecipeIndex - 1)));
        addDrawableChild(new ButtonWidget(this.x + 166, this.y, 10, 20, new LiteralText("\u276F"), (button) -> setCurrentRecipe(currentRecipeIndex + 1)));

        addDrawableChild(new ButtonWidget(this.x + 198, this.y + 10, 80, 20, new TranslatableText("gui.fguns.assemble"), (button) -> {
            if (getCurrentRecipe() != null) {
                assert client != null;
                Objects.requireNonNull(client.getNetworkHandler()).sendPacket(new CraftGunC2SPacket(getCurrentRecipe().getId()));
            }
        }));

        ingredientList = new IngredientList(client, 95, height, y + 45, this.y + 145, 20, this, x + 188);

        statList = new StatList(client, 40, height, y + 5, this.y + 150, 25, this, x - 22);


        setCurrentRecipe(currentRecipeIndex);
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }
}
