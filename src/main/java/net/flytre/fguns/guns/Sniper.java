package net.flytre.fguns.guns;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.flytre.fguns.entity.Bullet;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class Sniper extends GunItem {

    private final Multimap<EntityAttribute, EntityAttributeModifier> attributeModifiers;


    public Sniper(double damage, double armorPen, double rps, double dropoff, int spray, int range, int clipSize, double reloadTime, GunType gunType) {
        super(damage, armorPen, rps, dropoff, spray, range, clipSize, reloadTime, gunType);

        ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(EntityAttributes.GENERIC_MOVEMENT_SPEED, new EntityAttributeModifier(UUID.fromString("CB3F88D4-645B-4A38-C198-9C13A444A5CF"), "Weight modifier", -0.1, EntityAttributeModifier.Operation.MULTIPLY_BASE));
        this.attributeModifiers = builder.build();
    }


    private int getEffectiveSpray(PlayerEntity user) {
        return user.isSneaking() ? 0 : getSpray();

    }

    protected Vec3d getRotationVectorSpray(PlayerEntity user) {

        int spray = getEffectiveSpray(user);

        if (spray == 0)
            return getRotationVector(user.pitch, user.yaw);
        else
            return getRotationVector((float) (user.pitch + (Math.random() * (spray + 1)) - 1 - spray / 2.0), (float) (user.yaw + (Math.random() * (spray + 1)) - 1 - spray / 2.0));
    }


    @Override
    public void bulletSetup(World world, PlayerEntity user, Hand hand, Bullet bullet) {
        bullet.setProperties(GunType.SNIPER);
        bullet.setVelocity(bullet.getVelocity().multiply(3));
        super.bulletSetup(world, user, hand, bullet);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        tooltip.add(new TranslatableText("text.fguns.sniper.tip"));
    }

    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot) {
        return slot == EquipmentSlot.MAINHAND ? this.attributeModifiers : super.getAttributeModifiers(slot);
    }
}
