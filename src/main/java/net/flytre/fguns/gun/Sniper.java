package net.flytre.fguns.gun;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.annotations.SerializedName;
import net.flytre.fguns.FlytreGuns;
import net.flytre.fguns.entity.Bullet;
import net.flytre.fguns.misc.Sounds;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class Sniper extends AbstractGun {

    private final Multimap<EntityAttribute, EntityAttributeModifier> attributeModifiers;

    private Sniper(Builder builder) {
        super(builder);
        ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> mapBuilder = ImmutableMultimap.builder();
        mapBuilder.put(EntityAttributes.GENERIC_MOVEMENT_SPEED, new EntityAttributeModifier(UUID.fromString("CB3F88D4-645B-4A38-C198-9C13A444A5CF"), "Weight modifier", builder.speedModifier, EntityAttributeModifier.Operation.MULTIPLY_BASE));
        this.attributeModifiers = mapBuilder.build();
    }


    @Override
    public int getEffectiveSpray(LivingEntity user) {
        return !(user instanceof PlayerEntity) ? 3 : user.isSneaking() ? 0 : getSpray();
    }


    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        tooltip.add(new TranslatableText("text.fguns.sniper.tip"));
    }

    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot) {
        return slot == EquipmentSlot.MAINHAND || slot == EquipmentSlot.OFFHAND ? this.attributeModifiers : super.getAttributeModifiers(slot);
    }


    public static class Builder extends AbstractGun.Builder<Builder> {

        @SerializedName("speed_modifier")
        protected double speedModifier = -.1;

        public Builder() {
            super();
            bulletProperties = BulletProperties.SNIPER;
            this.fireSound = Sounds.SNIPER_FIRE_EVENT;
            this.ammoItem = FlytreGuns.SNIPER_AMMO;
            this.scope = true;
            this.scopeZoom = 16;
            this.velocity = 3;
            this.dropoff = 0.0;
            this.spray = 17;
            this.horizontalRecoil = 2.9;
            this.verticalRecoil = 2.9;
        }

        @Override
        protected Builder self() {
            return this;
        }

        public void speedModifier(double speedModifier) {
            this.speedModifier = speedModifier;
        }

        @Override
        public Sniper build() {
            return new Sniper(this);
        }
    }
}
