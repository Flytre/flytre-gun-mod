package net.flytre.fguns.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.flytre.fguns.FlytreGuns;
import net.flytre.fguns.flare.FlareSmokeParticle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(ParticleManager.class)
public abstract class ParticleManagerMixin {

    @Shadow
    protected abstract <T extends ParticleEffect> void registerFactory(ParticleType<T> particleType, ParticleManager.SpriteAwareFactory<T> spriteAwareFactory);

    @Inject(method = "registerDefaultFactories", at = @At("RETURN"))
    public void fguns$registerParticle(CallbackInfo ci) {
        registerFactory(FlytreGuns.FLARE_PARTICLES, FlareSmokeParticle.SignalSmokeFactory::new);
    }
}
