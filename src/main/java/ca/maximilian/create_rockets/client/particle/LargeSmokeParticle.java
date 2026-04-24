package ca.maximilian.create_rockets.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class LargeSmokeParticle extends TextureSheetParticle {

    private static final float INITIAL_SCALE = 5F;
    private static final int LIFETIME_TICKS = 160;
    private static final float SCALE_MULTIPLIER = 1.05F;
    private static final float MAX_SCALE = 5F;

    private static final double AIR_FRICTION = 0.98D;
    private static final double BUOYANCY = 0.0001D;
    private static final double GROUND_SPREAD_ACCELERATION = 2D;
    private static final float GROUND_RANDOM_PUSH = 2F;
    private static final double MAX_GROUND_SPEED_SQ = 64D;

    private final SpriteSet sprites;

    protected LargeSmokeParticle(ClientLevel level, double x, double y, double z,
                                 double dx, double dy, double dz,
                                 SpriteSet spriteSet) {
        super(level, x, y, z, dx, dy, dz);

        this.sprites = spriteSet;

        this.scale(INITIAL_SCALE);
        this.lifetime = LIFETIME_TICKS;

        this.hasPhysics = true;

        this.xd = dx;
        this.yd = dy;
        this.zd = dz;

        this.rCol = 1F;
        this.gCol = 1F;
        this.bCol = 1F;

        this.alpha = 0f;

        this.setSpriteFromAge(spriteSet);
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        if (++this.age >= this.lifetime) {
            this.remove();
            return;
        }

        this.alpha = Math.min(this.alpha + 0.1f, 1);

        this.setSpriteFromAge(this.sprites);

        this.quadSize *= SCALE_MULTIPLIER;
        if (this.quadSize > MAX_SCALE) this.quadSize = MAX_SCALE;

        this.yd -= 0.04D * this.gravity;
        this.yd += BUOYANCY;

        this.move(this.xd, this.yd, this.zd);

        if (this.onGround) {
            this.yd = 0.0D;

            this.xd *= GROUND_SPREAD_ACCELERATION;
            this.zd *= GROUND_SPREAD_ACCELERATION;

            double mx = this.xd;
            double mz = this.zd;

            if ((mx < 0 ? -mx : mx) + (mz < 0 ? -mz : mz) < 0.05D) {
                this.xd += (this.random.nextFloat() - 0.5F) * GROUND_RANDOM_PUSH;
                this.zd += (this.random.nextFloat() - 0.5F) * GROUND_RANDOM_PUSH;
            }

            double speedSq = this.xd * this.xd + this.zd * this.zd;

            if (speedSq > MAX_GROUND_SPEED_SQ) {
                double invLen = Mth.fastInvSqrt(speedSq);
                double scale = 8D * invLen;
                this.xd *= scale;
                this.zd *= scale;
            }
        } else {
            this.xd *= AIR_FRICTION;
            this.yd *= AIR_FRICTION;
            this.zd *= AIR_FRICTION;
        }
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {

        private final SpriteSet spriteSet;

        public Provider(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Override
        public Particle createParticle(SimpleParticleType type,
                                       ClientLevel level,
                                       double x, double y, double z,
                                       double dx, double dy, double dz) {
            return new LargeSmokeParticle(level, x, y, z, dx, dy, dz, this.spriteSet);
        }
    }
}