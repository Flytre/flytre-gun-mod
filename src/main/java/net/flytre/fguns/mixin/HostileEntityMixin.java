package net.flytre.fguns.mixin;

import net.flytre.fguns.entity.BloodbathGoal;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Gun AI for hostile mobs in general
 */
@Mixin(HostileEntity.class)
public abstract class HostileEntityMixin extends MobEntity {


    protected HostileEntityMixin(EntityType<? extends MobEntity> entityType, World world) {
        super(entityType, world);
    }

    @SuppressWarnings("ConstantConditions")
    @Inject(method = "<init>", at = @At("TAIL"))
    public void fguns$boom(CallbackInfo ci) {

        HostileEntity me = (HostileEntity) (Object) this;
        if (world != null && !world.isClient && !(me instanceof AbstractSkeletonEntity) && !(me instanceof ZombieEntity))
            this.goalSelector.add(3, new BloodbathGoal(me, 1.0D));
    }
}
