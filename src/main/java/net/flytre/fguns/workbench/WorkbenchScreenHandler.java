package net.flytre.fguns.workbench;

import net.flytre.fguns.FlytreGuns;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.Slot;

public class WorkbenchScreenHandler extends ScreenHandler {

    private WorkbenchEntity workbenchEntity;

    public WorkbenchScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, ScreenHandlerContext.EMPTY);
    }

    public WorkbenchScreenHandler(int syncId, PlayerInventory playerInventory, ScreenHandlerContext screenHandlerContext) {
        super(FlytreGuns.WORKBENCH_SCREEN_HANDLER, syncId);
        this.workbenchEntity = null;
        int m, l;
        for (m = 0; m < 3; ++m) {
            for (l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + m * 9 + 9, 8 + l * 18, 84 + m * 18));
            }
        }

        for (m = 0; m < 9; ++m) {
            this.addSlot(new Slot(playerInventory, m, 8 + m * 18, 142));
        }

        screenHandlerContext.run((world, pos) -> {
            if (!world.isClient) {
                BlockEntity entity = world.getBlockEntity(pos);
                if (entity instanceof WorkbenchEntity) {
                    this.workbenchEntity = (WorkbenchEntity) entity;
                }
            }
        });
    }

    public WorkbenchEntity getWorkbenchEntity() {
        return workbenchEntity;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }

    @Override
    public ItemStack transferSlot(PlayerEntity player, int index) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasStack()) {
            stack = slot.getStack();
            if (index <= 26) {
                if (!this.insertItem(stack, 27, 36, false))
                    return ItemStack.EMPTY;
            } else {
                if (!this.insertItem(stack, 0, 27, false))
                    return ItemStack.EMPTY;
            }
        }
        return stack;
    }
}
