package net.flytre.fguns;

import net.flytre.fguns.entity.Bullet;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.EntityDamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.jetbrains.annotations.Nullable;

public class BulletDamageSource extends EntityDamageSource {

    private final Entity attacker;

    public BulletDamageSource(Bullet projectile, @Nullable Entity attacker) {
        super("shot", projectile);
        this.attacker = attacker;
    }


    @Nullable
    public Entity getSource() {
        return this.source;
    }

    @Nullable
    public Entity getAttacker() {
        return this.attacker;
    }

    public Text getDeathMessage(LivingEntity entity) {
        Text text = this.attacker == null ? this.source.getDisplayName() : this.attacker.getDisplayName();
        ItemStack itemStack = this.attacker instanceof LivingEntity ? ((LivingEntity)this.attacker).getMainHandStack() : ItemStack.EMPTY;
        String string = "death.attack." + "shot";
        String string2 = string + ".item";
        return !itemStack.isEmpty() && itemStack.hasCustomName() ? new TranslatableText(string2, entity.getDisplayName(), text, itemStack.toHoverableText()) : new TranslatableText(string, entity.getDisplayName(), text);
    }

}
