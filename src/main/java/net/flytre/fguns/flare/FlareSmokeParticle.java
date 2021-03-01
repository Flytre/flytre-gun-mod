package net.flytre.fguns.flare;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;

@Environment(EnvType.CLIENT)
public class FlareSmokeParticle extends SpriteBillboardParticle {
    public FlareSmokeParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        super(world, x, y, z);
        this.scale(3.0F);
        this.setBoundingBoxSpacing(0.25F, 0.25F);
        this.maxAge = this.random.nextInt(50) + 220;
        this.gravityStrength = 3.0E-6F;
        this.velocityX = velocityX / 2;
        this.velocityY = velocityY + (double) (this.random.nextFloat() / 500.0F);
        this.velocityZ = velocityZ / 2;
    }

    public void buildGeometry(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
        this.setColorAlpha(1.0F - ((float) this.age + tickDelta - 1.0F) * 0.25F * 0.5F * 0.1356f);
        super.buildGeometry(vertexConsumer, camera, tickDelta);
    }

    public void tick() {
        this.prevPosX = this.x;
        this.prevPosY = this.y;
        this.prevPosZ = this.z;
        if (this.age++ < this.maxAge && !(this.colorAlpha <= 0.0F)) {
            this.velocityX += this.random.nextFloat() / 20000.0F * (float) (this.random.nextBoolean() ? 1 : -1);
            this.velocityZ += this.random.nextFloat() / 20000.0F * (float) (this.random.nextBoolean() ? 1 : -1);
            this.velocityY -= this.gravityStrength;
            this.move(this.velocityX, this.velocityY, this.velocityZ);
//            if (this.age >= this.maxAge - 60 && this.colorAlpha > 0.01F) {
//                this.colorAlpha -= 0.009F;
//            }

        } else {
            this.markDead();
        }
    }

    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Environment(EnvType.CLIENT)
    public static class SignalSmokeFactory implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider spriteProvider;

        public SignalSmokeFactory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            FlareSmokeParticle campfireSmokeParticle = new FlareSmokeParticle(clientWorld, d, e, f, g, h, i);
            campfireSmokeParticle.setColorAlpha(0.6F);
            campfireSmokeParticle.setColor(1.0f, 0.0f, (float) (0.6f + Math.random() * 0.2f));
            campfireSmokeParticle.setSprite(this.spriteProvider);
            return campfireSmokeParticle;
        }
    }

}

