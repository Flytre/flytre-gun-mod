package net.flytre.fguns.gun;

import com.google.gson.annotations.SerializedName;
import net.flytre.fguns.FlytreGuns;
import net.flytre.fguns.misc.Sounds;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class Shotgun extends AbstractGun {

    private final int pelletCount;


    private Shotgun(Builder builder) {
        super(builder);
        this.pelletCount = builder.pelletCount;
    }

    public int getPelletCount() {
        return pelletCount;
    }

    @Override
    public void fireBullet(World world, LivingEntity user, Hand hand, LivingEntity target, boolean semi) {

        for (int i = 0; i < pelletCount; i++)
            super.fireBullet(world, user, hand, target, false);
    }


    @Override
    public Text getDamageLine() {
        return new TranslatableText("text.fguns.tooltip.damage.shotgun", String.format("%.1f", getDamage()));
    }

    public static class Builder extends AbstractGun.Builder<Builder> {

        @SerializedName("pellet_count")
        protected int pelletCount = 5;

        public Builder() {
            super();
            this.fireSound = Sounds.RIFLE_FIRE_EVENT;
            this.ammoItem = FlytreGuns.SHOTGUN_SHELL;
            this.fireSound = Sounds.SHOTGUN_FIRE_EVENT;
            this.scope = false;
            this.horizontalRecoil = 1.4;
        }

        @Override
        protected Builder self() {
            return this;
        }

        public void pelletCount(int pelletCount) {
            this.pelletCount = pelletCount;
        }

        @Override
        public Shotgun build() {
            return new Shotgun(this);
        }
    }
}
