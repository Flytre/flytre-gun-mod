package net.flytre.fguns.gun;

import net.flytre.fguns.FlytreGuns;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class MysteryGun extends Item {
    public MysteryGun() {
        super(new Settings().group(FlytreGuns.TAB).maxCount(1));
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if(entity instanceof PlayerEntity) {
            PlayerEntity living = (PlayerEntity) entity;
            living.getInventory().setStack(slot, new ItemStack(AbstractGun.randomGun(), 1));
        }
        super.inventoryTick(stack, world, entity, slot, selected);
    }
}
