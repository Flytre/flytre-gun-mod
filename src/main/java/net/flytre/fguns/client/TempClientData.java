package net.flytre.fguns.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.flytre.fguns.gun.AbstractGun;

@Environment(EnvType.CLIENT)
public class TempClientData {

    public static AbstractGun gun = null;
    public static int shiftTime = 0;
}
