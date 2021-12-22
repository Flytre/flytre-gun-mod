package net.flytre.fguns.gun;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.annotations.SerializedName;
import net.flytre.fguns.entity.Bullet;
import net.flytre.fguns.misc.Sounds;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

import java.util.UUID;

public class Minigun extends AbstractGun {

    private final Multimap<EntityAttribute, EntityAttributeModifier> attributeModifiers;


    private Minigun(Builder builder) {
        super(builder);
        ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> mapBuilder = ImmutableMultimap.builder();
        mapBuilder.put(EntityAttributes.GENERIC_MOVEMENT_SPEED, new EntityAttributeModifier(UUID.fromString("CB3F88D4-645B-4A38-C198-9C13A444A5CF"), "Weight modifier", builder.speedModifier, EntityAttributeModifier.Operation.MULTIPLY_BASE));
        this.attributeModifiers = mapBuilder.build();
    }

    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot) {
        return slot == EquipmentSlot.MAINHAND || slot == EquipmentSlot.OFFHAND ? this.attributeModifiers : super.getAttributeModifiers(slot);
    }

    @Override
    public void bulletSetup(World world, LivingEntity user, Hand hand, Bullet bullet) {
        bullet.setPos(user.getX(), user.getEyeY() - 0.8, user.getZ());
        super.bulletSetup(world, user, hand, bullet);
    }


    public static class Builder extends AbstractGun.Builder<Builder> {

        @SerializedName("speed_modifier")
        protected double speedModifier = -.25;

        public Builder() {
            super();
            this.fireSound = Sounds.RIFLE_FIRE_EVENT;
            this.scope = false;
            this.horizontalRecoil = 0.7;
            this.verticalRecoil = 0.6;
        }

        @Override
        protected Builder self() {
            return this;
        }

        public Builder speedModifier(double speedModifier) {
            this.speedModifier = speedModifier;
            return self();
        }

        @Override
        public Minigun build() {
            return new Minigun(this);
        }
    }
}