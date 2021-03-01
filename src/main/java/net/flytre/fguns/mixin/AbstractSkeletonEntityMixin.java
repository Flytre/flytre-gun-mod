package net.flytre.fguns.mixin;

import net.flytre.fguns.FlytreGuns;
import net.flytre.fguns.entity.BloodbathGoal;
import net.flytre.fguns.gun.AbstractGun;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.BowAttackGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractSkeletonEntity.class)
public abstract class AbstractSkeletonEntityMixin extends HostileEntity {

    private final BloodbathGoal bloodbathGoal = new BloodbathGoal(this, 1.0D);
    @Shadow
    @Final
    private MeleeAttackGoal meleeAttackGoal;
    @Shadow
    @Final
    private BowAttackGoal<AbstractSkeletonEntity> bowAttackGoal;

    protected AbstractSkeletonEntityMixin(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }


    @Inject(method = "updateAttackType", at = @At("HEAD"), cancellable = true)
    public void fguns$gunAttackType(CallbackInfo ci) {

        if (!FlytreGuns.MOB_AI_RELEASED)
            return;

        if (this.world != null && !this.world.isClient) {

            boolean bl = getMainHandStack().getItem() instanceof AbstractGun;

            if (bl) {
                this.goalSelector.remove(this.meleeAttackGoal);
                this.goalSelector.remove(this.bowAttackGoal);
                this.goalSelector.add(4, this.bloodbathGoal);
                ci.cancel();
            } else {
                this.goalSelector.remove(this.bloodbathGoal);
            }

        }
    }
}
