package net.flytre.fguns.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.flytre.fguns.FlytreGuns;
import net.flytre.fguns.flare.FlareSmokeParticle;
import net.flytre.flytre_lib.api.base.registry.ParticleManagerRegistry;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Register particles!
 *
 * No access widener needed due to flytre lib taking it on for us!
 */
@Environment(EnvType.CLIENT)
@Mixin(ParticleManager.class)
public abstract class ParticleManagerMixin {
    @Inject(method = "registerDefaultFactories", at = @At("RETURN"))
    public void fguns$registerParticle(CallbackInfo ci) {
        ((ParticleManagerRegistry)this).altRegister(FlytreGuns.FLARE_PARTICLES, FlareSmokeParticle.SignalSmokeFactory::new);
    }
}
