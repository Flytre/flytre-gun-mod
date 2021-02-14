package net.flytre.fguns.workbench;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.flytre.fguns.FlytreGuns;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.List;

public class WorkbenchEntity extends BlockEntity {

    private int currentIndex;

    public WorkbenchEntity() {
        super(FlytreGuns.WORKBENCH_ENTITY);
    }

    public void nextRecipe() {
        currentIndex++;
    }

    public void previousRecipe() {
        currentIndex--;
    }

    public void sendRecipe(PlayerEntity client) {
        assert world != null;
        if (world.isClient)
            throw new AssertionError("World cannot be client");

        List<WorkbenchRecipe> recipeList = world.getRecipeManager().listAllOfType(FlytreGuns.WORKBENCH_RECIPE);
        if (currentIndex < 0)
            currentIndex = recipeList.size() - 1;
        if (currentIndex >= recipeList.size())
            currentIndex = 0;
        if (recipeList.size() == 0)
            return;
        WorkbenchRecipe recipe = recipeList.get(currentIndex);
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeIdentifier(recipe.getId());
        FlytreGuns.WORKBENCH_SERIALIZER.write(buf, recipe);
        ServerPlayNetworking.send((ServerPlayerEntity) client, FlytreGuns.RECEIVE_RECIPE_PACKET_ID, buf);
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        tag.putInt("cI", currentIndex);
        return super.toTag(tag);
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        currentIndex = tag.getInt("cI");
        super.fromTag(state, tag);
    }
}
