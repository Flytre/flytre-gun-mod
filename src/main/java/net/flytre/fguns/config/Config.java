package net.flytre.fguns.config;

import com.google.gson.annotations.SerializedName;
import jdk.jfr.Description;
import net.flytre.flytre_lib.api.config.reference.block.BlockReference;
import net.flytre.flytre_lib.api.config.reference.block.ConfigBlock;
import net.flytre.flytre_lib.api.config.reference.entity.ConfigEntity;
import net.flytre.flytre_lib.api.config.reference.entity.EntityReference;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.util.registry.Registry;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.minecraft.entity.EntityType.*;

public class Config {

    @SerializedName("breakable_blocks")
    @Description("The blocks guns can break")
    public Set<ConfigBlock> breakableBlocks = Registry.BLOCK
            .stream()
            .map(Registry.BLOCK::getId)
            .filter(i -> i.getNamespace().equals("minecraft") && i.getPath().contains("glass"))
            .map(Registry.BLOCK::get)
            .map(BlockReference::new)
            .collect(Collectors.toSet());

    @SerializedName("mobs_that_can_spawn_with_guns")
    @Description("Mobs that can spawn holding guns")
    public Set<ConfigEntity> mobGunWhitelist = Stream
            .of(
                    ZOMBIE, SKELETON, WITHER_SKELETON, STRAY,
                    PIGLIN, ZOMBIFIED_PIGLIN, DROWNED, HUSK,
                    PIGLIN_BRUTE, PILLAGER, WITCH, VINDICATOR
            ).map(EntityReference::new).collect(Collectors.toSet());

    @Description("The multiplier to gun damage when fired by players")
    @SerializedName("player_damage_modifier")
    public float playerDamageModifier = 1.0f;


    @Description("The multiplier to gun damage when fired by mobs")
    @SerializedName("mob_damage_modifier")
    public float mobDamageModifier = 0.25f;


    @Description("The chance a mob spawns with a gun")
    @SerializedName("mob_gun_spawn_chance")
    public float mobGunSpawnChance = 0.02f;


    @Description("The power of rocket launcher explosions")
    @SerializedName("rocket_explosion_power")
    public float rocketExplosionPower = 3f;


    @Description("Whether bullets on fire should set blocks on fire")
    @SerializedName("flammable_griefing")
    public boolean flammableGriefing = true;
}
