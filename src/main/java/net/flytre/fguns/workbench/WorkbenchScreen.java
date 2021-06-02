package net.flytre.fguns.workbench;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.flytre.fguns.Packets;
import net.flytre.flytre_lib.client.gui.CoordinateProvider;
import net.flytre.flytre_lib.common.util.InventoryUtils;
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
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3f;
import java.util.Map;
import java.util.Objects;

public class WorkbenchScreen extends HandledScreen<WorkbenchScreenHandler> implements CoordinateProvider {

    private static final Identifier INVENTORY_TEXTURE = new Identifier("fguns:textures/gui/workbench_base.png");
    private static final Identifier PANEL_TEXTURE = new Identifier("fguns:textures/gui/workbench_side.png");

    private WorkbenchRecipe currentRecipe = null;
    private IngredientList ingredientList;
    private StatList statList;


    public WorkbenchScreen(WorkbenchScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    public WorkbenchRecipe getCurrentRecipe() {
        return currentRecipe;
    }

    public void setCurrentRecipe(WorkbenchRecipe currentRecipe) {
        this.currentRecipe = currentRecipe;
        ingredientList.refreshList();
        statList.refreshList();
    }

    @Override
    protected void drawForeground(MatrixStack matrices, int mouseX, int mouseY) {
        if (currentRecipe != null) {
            DrawableHelper.drawCenteredText(matrices, textRenderer, new TranslatableText(currentRecipe.getOutputItem().getTranslationKey()), this.backgroundWidth / 2, titleY, 4210752);
        }
    }

    @Override
    public void tick() {
        super.tick();
        assert client != null && client.player != null;
        Map<Item, Integer> map = InventoryUtils.countInventoryContents(client.player.inventory);
        for (IngredientList.IngredientEntry entry : ingredientList.children())
            entry.setGreen(map.getOrDefault(entry.getStack().getItem(), 0) >= entry.getStack().getCount());
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
        if (currentRecipe != null) {
            ItemStack stack = new ItemStack(currentRecipe.getOutputItem(), 1);
            renderStack(delta, stack);
        }
        this.client.getTextureManager().bindTexture(INVENTORY_TEXTURE);
        int i = this.x;
        int j = (this.height - this.backgroundHeight) / 2;
        this.drawTexture(matrices, i, j, 0, 0, this.backgroundWidth, this.backgroundHeight);
        this.client.getTextureManager().bindTexture(PANEL_TEXTURE);
        this.drawTexture(matrices, this.x + 95, this.y, 0, 0, this.backgroundWidth + 100, this.backgroundHeight);
        this.drawTexture(matrices, this.x - 205, this.y, 0, 0, this.backgroundWidth + 100, this.backgroundHeight);

    }

    private void renderStack(float delta, ItemStack stack) {
        assert client != null;
        ItemRenderer renderer = client.getItemRenderer();
        renderGuiItemModel(stack, this.x + 80, this.y + 42, renderer.getHeldItemModel(stack, null, null), delta);
    }


    //See ItemRenderer::renderInGUI
    @SuppressWarnings("deprecation")
    protected void renderGuiItemModel(ItemStack stack, int x, int y, BakedModel model, float delta) {
        RenderSystem.pushMatrix();
        assert client != null;
        ItemRenderer renderer = client.getItemRenderer();
        client.getTextureManager().bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);
        Objects.requireNonNull(client.getTextureManager().getTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE)).setFilter(false, false);
        RenderSystem.enableRescaleNormal();
        RenderSystem.enableAlphaTest();
        RenderSystem.defaultAlphaFunc();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.translatef((float) x, (float) y, 100.0F + renderer.zOffset);
        RenderSystem.translatef(8.0F, 8.0F, 0.0F);
        RenderSystem.scalef(1.0F, -1.0F, 1.0F);
        RenderSystem.scalef(16.0F, 16.0F, 16.0F);
        MatrixStack matrixStack = new MatrixStack();
        matrixStack.scale(3.3f, 3.3f, 3.3f);
        assert client.world != null;
        float rot = ((client.world.getTime() + delta) * 4) % 360;
        matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(rot));
        matrixStack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(-30));
        VertexConsumerProvider.Immediate immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
        boolean bl = !model.isSideLit();
        if (bl) {
            DiffuseLighting.disableGuiDepthLighting();
        }
        renderer.renderItem(stack, ModelTransformation.Mode.GUI, false, matrixStack, immediate, 15728880, OverlayTexture.DEFAULT_UV, model);
        immediate.draw();
        RenderSystem.enableDepthTest();
        if (bl) {
            DiffuseLighting.enableGuiDepthLighting();
        }

        RenderSystem.disableAlphaTest();
        RenderSystem.disableRescaleNormal();
        RenderSystem.popMatrix();
    }


    @Override
    protected void init() {
        super.init();
        addButton(new ButtonWidget(this.x, this.y, 10, 20, new LiteralText("\u276E"), (button) -> {
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            buf.writeInt(-1);
            ClientPlayNetworking.send(Packets.NEXT_RECIPE, buf);
            ClientPlayNetworking.send(Packets.REQUEST_RECIPE, PacketByteBufs.empty());
        }));
        addButton(new ButtonWidget(this.x + 166, this.y, 10, 20, new LiteralText("\u276F"), (button) -> {
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            buf.writeInt(1);
            ClientPlayNetworking.send(Packets.NEXT_RECIPE, buf);
            ClientPlayNetworking.send(Packets.REQUEST_RECIPE, PacketByteBufs.empty());
        }));

        addButton(new ButtonWidget(this.x + 198, this.y + 10, 80, 20, new TranslatableText("gui.fguns.assemble"), (button) -> {
            if (currentRecipe != null) {
                PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
                buf.writeIdentifier(currentRecipe.getId());
                ClientPlayNetworking.send(Packets.CRAFT_ITEM, buf);
            }
        }));

        if (ingredientList == null) {
            ingredientList = new IngredientList(client, 40, height, y + 100, this.y + 150, 20, this, x + 280);
        }

        if (statList == null) {
            statList = new StatList(client, 40, height, y + 5, this.y + 150, 25, this, x - 22);
        }

        ClientPlayNetworking.send(Packets.REQUEST_RECIPE, PacketByteBufs.empty());
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
