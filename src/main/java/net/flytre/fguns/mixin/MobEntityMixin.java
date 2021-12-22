package net.flytre.fguns.mixin;

import net.flytre.fguns.FlytreGuns;
import net.flytre.fguns.config.Config;
import net.flytre.fguns.gun.AbstractGun;
import net.flytre.flytre_lib.api.config.reference.entity.ConfigEntity;
import net.minecraft.entity.*;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mobs can spawn holding guns
 */
@Mixin(MobEntity.class)
public abstract class MobEntityMixin extends LivingEntity {

    protected MobEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Shadow
    public abstract void equipStack(EquipmentSlot slot, ItemStack stack);


    @Inject(method = "initialize", at = @At("TAIL"))
    public void fguns$holdGun(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, EntityData entityData, NbtCompound entityNbt, CallbackInfoReturnable<EntityData> cir) {
        Config cfg = FlytreGuns.CONFIG.getConfig();
        if (ConfigEntity.contains(cfg.mobGunWhitelist,this.getType(),world.toServerWorld())) {
            AbstractGun item = AbstractGun.getRandomEquipmentGun();
            if (item == null)
                return;
            ItemStack stack = new ItemStack(item, 1);
            equipStack(EquipmentSlot.MAINHAND, stack);
        }
    }
}
