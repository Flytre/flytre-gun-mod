package net.flytre.fguns.mixin;

import net.flytre.fguns.entity.Bullet;
import net.flytre.fguns.entity.BulletDamageSource;
import net.minecraft.entity.DamageUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Armor pierce formula for guns
 */
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Shadow
    protected abstract void damageArmor(DamageSource source, float amount);

    @Shadow
    public abstract int getArmor();

    @Shadow
    public abstract double getAttributeValue(EntityAttribute attribute);

    @Inject(method = "applyArmorToDamage", at = @At("HEAD"), cancellable = true)
    public void fguns$armorPen(DamageSource source, float amount, CallbackInfoReturnable<Float> cir) {
        if (!(source instanceof BulletDamageSource))
            return;
        BulletDamageSource src = (BulletDamageSource) source;
        Bullet bullet = src.getSource();

        if (bullet == null)
            return;

        damageArmor(source, amount);
        amount = DamageUtil.getDamageLeft(amount, (float) (getArmor() * (1 - bullet.getArmorPen())), (float) (this.getAttributeValue(EntityAttributes.GENERIC_ARMOR_TOUGHNESS) * (1 - bullet.getArmorPen())));
        cir.setReturnValue(amount);
    }
}
