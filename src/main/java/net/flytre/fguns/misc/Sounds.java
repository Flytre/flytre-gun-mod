package net.flytre.fguns.misc;

import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class Sounds {
    public static final Identifier SNIPER_FIRE = new Identifier("fguns:sniper.fire");
    public static final SoundEvent SNIPER_FIRE_EVENT = new SoundEvent(SNIPER_FIRE);

    public static final Identifier PISTOL_FIRE = new Identifier("fguns:pistol.fire");
    public static final SoundEvent PISTOL_FIRE_EVENT = new SoundEvent(PISTOL_FIRE);

    public static final Identifier RIFLE_FIRE = new Identifier("fguns:rifle.fire");
    public static final SoundEvent RIFLE_FIRE_EVENT = new SoundEvent(RIFLE_FIRE);

    public static final Identifier DRY_FIRE = new Identifier("fguns:dry_fire");
    public static final SoundEvent DRY_FIRE_EVENT = new SoundEvent(DRY_FIRE);

    public static final Identifier SHOTGUN_FIRE = new Identifier("fguns:shotgun.fire");
    public static final SoundEvent SHOTGUN_FIRE_EVENT = new SoundEvent(SHOTGUN_FIRE);

    public static final Identifier SLIME_FIRE = new Identifier("fguns:slime.fire");
    public static final SoundEvent SLIME_FIRE_EVENT = new SoundEvent(SLIME_FIRE);

    public static final Identifier ROCKET_FIRE = new Identifier("fguns:rocket.fire");
    public static final SoundEvent ROCKET_FIRE_EVENT = new SoundEvent(ROCKET_FIRE);

    public static final Identifier SHOCKER_FIRE = new Identifier("fguns:shocker.fire");
    public static final SoundEvent SHOCKER_FIRE_EVENT = new SoundEvent(SHOCKER_FIRE);

    public static final Identifier FLARE_BURN = new Identifier("fguns:flare.burn");
    public static final SoundEvent FLARE_BURN_EVENT = new SoundEvent(FLARE_BURN);


    public static void init() {
        Registry.register(Registry.SOUND_EVENT, SNIPER_FIRE, SNIPER_FIRE_EVENT);
        Registry.register(Registry.SOUND_EVENT, PISTOL_FIRE, PISTOL_FIRE_EVENT);
        Registry.register(Registry.SOUND_EVENT, RIFLE_FIRE, RIFLE_FIRE_EVENT);
        Registry.register(Registry.SOUND_EVENT, DRY_FIRE, DRY_FIRE_EVENT);
        Registry.register(Registry.SOUND_EVENT, SHOTGUN_FIRE, SHOTGUN_FIRE_EVENT);
        Registry.register(Registry.SOUND_EVENT, SLIME_FIRE, SLIME_FIRE_EVENT);
        Registry.register(Registry.SOUND_EVENT, ROCKET_FIRE, ROCKET_FIRE_EVENT);
        Registry.register(Registry.SOUND_EVENT, SHOCKER_FIRE, SHOCKER_FIRE_EVENT);
        Registry.register(Registry.SOUND_EVENT, FLARE_BURN, FLARE_BURN_EVENT);
    }
}
