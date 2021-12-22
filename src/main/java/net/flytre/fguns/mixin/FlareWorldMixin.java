package net.flytre.fguns.mixin;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.flytre.fguns.FlytreGuns;
import net.flytre.fguns.flare.FlareWorld;
import net.flytre.fguns.misc.Sounds;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

/**
 * VFX / SFX for flare thru FlareWorld implementation
 */
@Mixin(ServerWorld.class)
public abstract class FlareWorldMixin extends World implements FlareWorld {

    private final Map<BlockPos, Integer> flares = new HashMap<>();

    protected FlareWorldMixin(MutableWorldProperties properties, RegistryKey<World> registryRef, DimensionType dimensionType, Supplier<Profiler> profiler, boolean isClient, boolean debugWorld, long seed) {
        super(properties, registryRef, dimensionType, profiler, isClient, debugWorld, seed);
    }

    @Shadow
    public abstract <T extends ParticleEffect> boolean spawnParticles(ServerPlayerEntity viewer, T particle, boolean force, double x, double y, double z, int count, double deltaX, double deltaY, double deltaZ, double speed);

    @Override
    public void setFlare(BlockPos pos) {
        flares.put(pos, 200);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    public void fguns$spawnFlareParticles(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
        List<BlockPos> toRemove = new ArrayList<>();
        for (Map.Entry<BlockPos, Integer> entry : flares.entrySet()) {
            int i = entry.getValue() - 1;
            BlockPos pos = entry.getKey();

            if (i == 199) {
                playSound(
                        null,
                        pos,
                        Sounds.FLARE_BURN_EVENT,
                        SoundCategory.PLAYERS,
                        1f,
                        1f
                );
            }

            for (ServerPlayerEntity player : PlayerLookup.tracking((ServerWorld) (Object) this, entry.getKey())) {
                spawnParticles(player, FlytreGuns.FLARE_PARTICLES, true, pos.getX(), pos.getY(), pos.getZ(), 5, 0, 7, 0, 0.05);
            }


            if (i <= 0)
                toRemove.add(entry.getKey());
            else
                flares.put(entry.getKey(), i);
        }
        for (BlockPos pos : toRemove)
            flares.remove(pos);
    }

}
