package net.flytre.fguns.gun;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.flytre.fguns.Sounds;
import net.flytre.fguns.entity.Bullet;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.Item;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

import java.util.UUID;

public class Minigun extends AbstractGun {

    private final Multimap<EntityAttribute, EntityAttributeModifier> attributeModifiers;


    protected Minigun(double damage, double armorPen, double rps, double dropoff, int spray, int range, int clipSize, double reloadTime, BulletProperties bulletProperties, boolean scope, double scopeZoom, SoundEvent fireSound, Item ammoItem, double speedModifier, double recoilMultiplier) {
        super(damage, armorPen, rps, dropoff, spray, range, clipSize, reloadTime, bulletProperties, scope, scopeZoom, fireSound, ammoItem, recoilMultiplier);
        ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(EntityAttributes.GENERIC_MOVEMENT_SPEED, new EntityAttributeModifier(UUID.fromString("CB3F88D4-645B-4A38-C198-9C13A444A5CF"), "Weight modifier", speedModifier, EntityAttributeModifier.Operation.MULTIPLY_BASE));
        this.attributeModifiers = builder.build();
    }

    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot) {
        return slot == EquipmentSlot.MAINHAND || slot == EquipmentSlot.OFFHAND ? this.attributeModifiers : super.getAttributeModifiers(slot);
    }

    @Override
    public void bulletSetup(World world, LivingEntity user, Hand hand, Bullet bullet) {
        bullet.setPos(user.getX(), user.getEyeY() - 0.8, user.getZ());
        super.bulletSetup(world, user, hand, bullet);
    }


    public static class Builder extends AbstractGun.Builder<Minigun> {

        protected double speedModifier = -.25;

        public Builder() {
            super();
            this.fireSound = Sounds.RIFLE_FIRE_EVENT;
            this.scope = false;
            this.recoilMultiplier = 0.7;
        }

        public void setSpeedModifier(double speedModifier) {
            this.speedModifier = speedModifier;
        }

        @Override
        public Minigun build() {
            return new Minigun(damage, armorPen, rps, dropoff, spray, range, clipSize, reloadTime, bulletProperties, scope, scopeZoom, fireSound, ammoItem, speedModifier, recoilMultiplier);
        }
    }
}