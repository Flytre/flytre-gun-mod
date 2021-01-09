package net.flytre.fguns.mixin;

import net.flytre.fguns.entity.Bullet;
import net.minecraft.entity.DamageUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.ProjectileDamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Shadow
    protected abstract void damageArmor(DamageSource source, float amount);

    @Shadow
    public abstract int getArmor();

    @Shadow
    public abstract double getAttributeValue(EntityAttribute attribute);

    @Inject(method = "applyArmorToDamage", at = @At("HEAD"), cancellable = true)
    public void armorPen(DamageSource source, float amount, CallbackInfoReturnable<Float> cir) {
        if (!(source instanceof ProjectileDamageSource))
            return;
        ProjectileDamageSource src = (ProjectileDamageSource) source;

        if (!(source.getSource() instanceof Bullet))
            return;

        Bullet bullet = (Bullet) source.getSource();

        damageArmor(source, amount);
        amount = DamageUtil.getDamageLeft(amount, (float) (getArmor() * (1 - bullet.getArmorPen())), (float) (this.getAttributeValue(EntityAttributes.GENERIC_ARMOR_TOUGHNESS) * (1 - bullet.getArmorPen())));
        cir.setReturnValue(amount);
    }
}
