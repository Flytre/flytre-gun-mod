package net.flytre.fguns.network;

import net.flytre.fguns.workbench.WorkbenchRecipe;
import net.flytre.fguns.workbench.WorkbenchScreenHandler;
import net.flytre.flytre_lib.api.base.util.InventoryUtils;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.recipe.Recipe;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class CraftGunC2SPacket implements Packet<ServerPlayPacketListener> {

    private final Identifier recipeId;

    public CraftGunC2SPacket(Identifier recipeId) {
        this.recipeId = recipeId;
    }

    public CraftGunC2SPacket(PacketByteBuf buf) {
        this.recipeId = buf.readIdentifier();
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeIdentifier(recipeId);
    }

    @Override
    public void apply(ServerPlayPacketListener listener) {
        ServerPlayerEntity player = ((ServerPlayNetworkHandler)listener).player;
        MinecraftServer server = player.getServer();
        assert server != null;
        server.execute(() -> execute(player, server));

    }

    private void execute(ServerPlayerEntity player, MinecraftServer server) {
        ScreenHandler screenHandler = player.currentScreenHandler;
        if(!(screenHandler instanceof WorkbenchScreenHandler))
            return;
        Recipe<?> possibleRecipe = server.getRecipeManager().get(recipeId).orElse(null);
        if (!(possibleRecipe instanceof WorkbenchRecipe))
            return;
        WorkbenchRecipe recipe = (WorkbenchRecipe) possibleRecipe;
        if (recipe.matches(player.getInventory(), player.world)) {
            ItemStack stack = recipe.craft(player.getInventory());
            stack = InventoryUtils.putStackInInventory(stack, player.getInventory(), 0, 36);
            if (!stack.isEmpty())
                player.world.spawnEntity(new ItemEntity(player.world, player.getX(), player.getY(), player.getZ(), stack));
        }
    }
}
