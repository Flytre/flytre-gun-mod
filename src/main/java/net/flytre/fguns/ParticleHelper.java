package net.flytre.fguns;

import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.mob.*;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;

public class ParticleHelper {
    public static ParticleEffect ZOMBIFIED_PIG_PARTICLE_DATA = new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.LIME_TERRACOTTA.getDefaultState());
    public static ParticleEffect PHANTOM_PARTICLE_DATA = new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.GRAY_STAINED_GLASS.getDefaultState());
    public static ParticleEffect BLAZE_PARTICLE_DATA = ParticleTypes.FLAME, SLIME_PARTICLE_DATA = ParticleTypes.ITEM_SLIME;
    public static ParticleEffect MAGMA_CUBE_PARTICLE_DATA = new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.MAGMA_BLOCK.getDefaultState());
    public static ParticleEffect ENDER_PARTICLE_DATA = new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.PURPLE_CONCRETE.getDefaultState());
    public static ParticleEffect DEFAULT_PARTICLE_DATA = new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.REDSTONE_BLOCK.getDefaultState());


    public static ParticleEffect getParticle(Entity entity) {
        if (entity instanceof ZombifiedPiglinEntity || entity instanceof ZoglinEntity)
            return ZOMBIFIED_PIG_PARTICLE_DATA;
        else if (entity instanceof PhantomEntity)
            return PHANTOM_PARTICLE_DATA;
        else if (entity instanceof BlazeEntity)
            return BLAZE_PARTICLE_DATA;
        else if (entity instanceof MagmaCubeEntity)
            return MAGMA_CUBE_PARTICLE_DATA;
        else if (entity instanceof SlimeEntity)
            return SLIME_PARTICLE_DATA;
        else if (entity instanceof EndermanEntity || entity instanceof EnderDragonEntity || entity instanceof EndermiteEntity)
            return ENDER_PARTICLE_DATA;
        else
            return DEFAULT_PARTICLE_DATA;
    }

}
