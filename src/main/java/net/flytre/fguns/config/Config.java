package net.flytre.fguns.config;

public class Config {

    private float player_damage_modifier;
    private float mob_damage_modifier;
    private float mob_gun_spawn_chance;


    public Config() {
        player_damage_modifier = 1.0f;
        mob_damage_modifier = 0.25f;
        mob_gun_spawn_chance = 0.02f;
    }

    public float getPlayerDamageModifier() {
        return player_damage_modifier;
    }

    public float getEntityDamageModifier() {
        return mob_damage_modifier;
    }

    public float getMobGunSpawnChance() {
        return mob_gun_spawn_chance;
    }


}
