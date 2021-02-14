package net.flytre.fguns.mixin;

import net.flytre.fguns.FlytreGuns;
import net.flytre.fguns.entity.BloodbathGoal;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ZombieEntity.class)
public abstract class ZombieEntityMixin extends HostileEntity {

    protected ZombieEntityMixin(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }


    @Inject(method = "initGoals", at = @At("HEAD"), cancellable = true)
    public void fguns$gunAttackType(CallbackInfo ci) {

        if (!FlytreGuns.MOB_AI_RELEASED)
            return;

        this.goalSelector.add(1, new BloodbathGoal(this, 1.0D));
    }
}
