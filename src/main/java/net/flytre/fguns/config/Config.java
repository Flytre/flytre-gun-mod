package net.flytre.fguns.config;

import com.google.gson.annotations.SerializedName;
import net.minecraft.block.Block;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Config {

    @SerializedName("player_damage_modifier")
    private final float playerDamageModifier;

    @SerializedName("mob_damage_modifier")
    private final float mobDamageModifier;

    @SerializedName("mob_gun_spawn_chance")
    private final float mobGunSpawnChance;

    @SerializedName("breakable_blocks")
    private final String[] breakableBlocks;


    private transient Set<Block> breakableBlockCache;


    public Config() {
        playerDamageModifier = 1.0f;
        mobDamageModifier = 0.25f;
        mobGunSpawnChance = 0.02f;
        breakableBlocks = new String[]{"#minecraft:impermeable",
                "glass_pane",
                "white_stained_glass_pane",
                "orange_stained_glass_pane",
                "magenta_stained_glass_pane",
                "light_blue_stained_glass_pane",
                "yellow_stained_glass_pane",
                "lime_stained_glass_pane",
                "pink_stained_glass_pane",
                "gray_stained_glass_pane",
                "light_gray_stained_glass_pane",
                "cyan_stained_glass_pane",
                "purple_stained_glass_pane",
                "blue_stained_glass_pane",
                "brown_stained_glass_pane",
                "green_stained_glass_pane",
                "red_stained_glass_pane",
                "black_stained_glass_pane"
        };
    }

    public float getPlayerDamageModifier() {
        return playerDamageModifier;
    }

    public float getEntityDamageModifier() {
        return mobDamageModifier;
    }

    public float getMobGunSpawnChance() {
        return mobGunSpawnChance;
    }


    private Set<Block> genBreakableBlocks() {
        Set<Block> result = new HashSet<>();
        for (String str : breakableBlocks) {
            if (str.startsWith("#")) {
                result.addAll(Objects.requireNonNull(BlockTags.getTagGroup().getTag(new Identifier(str.substring(1)))).values());
            } else
                result.add(Registry.BLOCK.get(new Identifier(str)));
        }
        return result;
    }

    public Set<Block> getBreakableBlocks() {
        if (breakableBlockCache == null)
            breakableBlockCache = genBreakableBlocks();
        return breakableBlockCache;
    }
}
