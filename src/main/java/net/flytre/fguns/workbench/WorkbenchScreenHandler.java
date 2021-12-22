package net.flytre.fguns.workbench;

import net.flytre.fguns.FlytreGuns;
import net.flytre.flytre_lib.api.storage.upgrade.UpgradeHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

public class WorkbenchScreenHandler extends ScreenHandler {



    public WorkbenchScreenHandler(int syncId, PlayerInventory playerInventory) {
        super(FlytreGuns.WORKBENCH_SCREEN_HANDLER, syncId);
        ((UpgradeHandler)this).addInventorySlots(playerInventory);
    }


    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }

    @Override
    public ItemStack transferSlot(PlayerEntity player, int index) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasStack()) {
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
