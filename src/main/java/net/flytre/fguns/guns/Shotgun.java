package net.flytre.fguns.guns;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class Shotgun extends GunItem {

    public Shotgun() {
        super(4, .40, 3, 0.06, 16, 12, 2, 2.2, GunType.SHOTGUN);
    }

    @Override
    protected void fireBullet(World world, PlayerEntity user, Hand hand) {

        for(int i = 0; i < 5; i++)
            super.fireBullet(world, user, hand);
    }
}
