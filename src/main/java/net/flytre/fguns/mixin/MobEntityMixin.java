package net.flytre.fguns.mixin;

import net.flytre.fguns.FlytreGuns;
import net.flytre.fguns.gun.AbstractGun;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MobEntity.class)
public abstract class MobEntityMixin extends LivingEntity {

    @Unique
    private boolean gunAdded = false;

    protected MobEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Shadow
    public abstract void equipStack(EquipmentSlot slot, ItemStack stack);

    @Inject(method = "readCustomDataFromTag", at = @At("HEAD"))
    public void fguns$readData(CompoundTag tag, CallbackInfo ci) {
        gunAdded = tag.getBoolean("gunAdded");
    }

    @Inject(method = "writeCustomDataToTag", at = @At("HEAD"))
    public void fguns$writeData(CompoundTag tag, CallbackInfo ci) {
        tag.putBoolean("gunAdded", gunAdded);
    }


    @SuppressWarnings("ConstantConditions")
    @Inject(method = "tick", at = @At("TAIL"))
    public void fguns$holdGun(CallbackInfo ci) {
        if (!FlytreGuns.MOB_AI_RELEASED)
            return;

        if (!gunAdded) {
            AbstractGun item = AbstractGun.getRandomEquipmentGun();
            if (item == null || !((Object) this instanceof HostileEntity))
                return;
            ItemStack stack = new ItemStack(item, 1);
            equipStack(EquipmentSlot.MAINHAND, stack);
            gunAdded = true;
        }
    }
}
