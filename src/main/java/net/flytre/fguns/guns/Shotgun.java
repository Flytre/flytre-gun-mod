package net.flytre.fguns.guns;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class Shotgun extends GunItem {

    public Shotgun(double damage, double armorPen, double rps, double dropoff, int spray, int range, int clipSize, double reloadTime) {
        super(damage, armorPen, rps, dropoff, spray, range, clipSize, reloadTime, GunType.SHOTGUN);
    }

    @Override
    protected void fireBullet(World world, PlayerEntity user, Hand hand) {

        for(int i = 0; i < 5; i++)
            super.fireBullet(world, user, hand);
    }
}
