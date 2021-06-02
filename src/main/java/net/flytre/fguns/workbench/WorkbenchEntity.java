package net.flytre.fguns.workbench;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.flytre.fguns.FlytreGuns;
import net.flytre.fguns.Packets;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public class WorkbenchEntity extends BlockEntity {

    private int currentIndex;

    public WorkbenchEntity(BlockPos pos, BlockState state) {
        super(FlytreGuns.WORKBENCH_ENTITY, pos, state);
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
        ServerPlayNetworking.send((ServerPlayerEntity) client, Packets.RECEIVE_RECIPE, buf);
    }

    @Override
    public NbtCompound writeNbt(NbtCompound tag) {
        tag.putInt("cI", currentIndex);
        return super.writeNbt(tag);
    }

    @Override
    public void readNbt(NbtCompound tag) {
        currentIndex = tag.getInt("cI");
        super.readNbt(tag);
    }
}
