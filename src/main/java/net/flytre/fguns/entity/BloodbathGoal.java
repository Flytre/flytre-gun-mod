package net.flytre.fguns.entity;

import net.flytre.fguns.gun.AbstractGun;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

import java.util.EnumSet;

public class BloodbathGoal extends Goal {

    private final MobEntity hitman;
    private final double mobSpeed;
    private LivingEntity target;
    private int seenTargetTicks;

    public BloodbathGoal(HostileEntity hitman, double mobSpeed) {
        if (hitman == null) {
            throw new IllegalArgumentException("Hitman is NULL");
        } else {
            this.hitman = hitman;
            this.mobSpeed = mobSpeed;
            this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
        }
    }

    public boolean isHoldingGun() {
        return hitman.getMainHandStack().getItem() instanceof AbstractGun;
    }

    public AbstractGun getHeldGun() {
        Item item = hitman.getMainHandStack().getItem();
        assert item instanceof AbstractGun;
        return (AbstractGun) item;
    }

    public int getRange() {
        return getHeldGun().getRange();
    }

    public int getSquaredRange() {
        return getRange() * getRange();
    }

    public boolean canStart() {

        if (!isHoldingGun())
            return false;

        LivingEntity livingEntity = this.hitman.getTarget();
        if (livingEntity != null && livingEntity.isAlive()) {
            this.target = livingEntity;
            return true;
        } else {
            return false;
        }
    }

    public boolean shouldContinue() {
        return this.canStart() || !this.hitman.getNavigation().isIdle();
    }

    public void start() {
    }

    public void stop() {
        this.target = null;
        this.seenTargetTicks = 0;
    }

    public void tick() {
        double d = this.hitman.squaredDistanceTo(this.target.getX(), this.target.getY(), this.target.getZ());
        boolean bl = this.hitman.getVisibilityCache().canSee(this.target);

        tickGun();

        if (bl) {
            ++this.seenTargetTicks;
        } else {
            this.seenTargetTicks = 0;
        }

        if (d <= getSquaredRange() && this.seenTargetTicks >= 5) {
            this.hitman.getNavigation().stop();
        } else {
            this.hitman.getNavigation().startMovingTo(this.target, this.mobSpeed);
        }

        this.hitman.getLookControl().lookAt(this.target, 30.0F, 30.0F);

        if (bl) {
            ItemStack stack = hitman.getStackInHand(Hand.MAIN_HAND);
            AbstractGun.GunNBTSerializer serializer = new AbstractGun.GunNBTSerializer(stack.getOrCreateTag(), getHeldGun());
            if (serializer.clip != 0 || serializer.cooldown != 0)
                getHeldGun().action(hitman.world, hitman, Hand.MAIN_HAND, target, false);
        }
    }

    public void tickGun() {
        ItemStack stack = hitman.getMainHandStack();
        AbstractGun gun = getHeldGun();
        AbstractGun.GunNBTSerializer serializer = new AbstractGun.GunNBTSerializer(stack.getOrCreateTag(), gun);
        if (serializer.reload != -1 || serializer.clip == 0) {
            if (serializer.reload >= 0) {
                double ammoCalc = (double) gun.getClipSize() / ((int) (gun.getReloadTime() * 20));
                ammoCalc *= (int) (gun.getReloadTime() * 20) - serializer.reload;
                if (serializer.partialClip < Math.floor(ammoCalc)) {
                    serializer.partialClip = (int) Math.floor(ammoCalc);
                }
            }
            if (serializer.reload == 0) {
                serializer.clip = serializer.partialClip;
                serializer.reload = -1;
                serializer.partialClip = 0;
            } else if (serializer.reload != -1) {
                serializer.reload--;
            } else {
                serializer.reload = (int) (gun.getReloadTime() * 20);
                serializer.partialClip = serializer.clip;
                serializer.clip = 0;
            }

        }
        if (serializer.cooldown > 0)
            serializer.cooldown--;
        serializer.toTag(stack.getOrCreateTag());
    }
}
