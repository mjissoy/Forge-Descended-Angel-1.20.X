package net.normlroyal.descendedangel.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;

public class AngelFlightParticle extends TextureSheetParticle {

    private final float flutterOffset;

    protected AngelFlightParticle(ClientLevel level, double x, double y, double z,
                                  double xd, double yd, double zd, SpriteSet sprites) {
        super(level, x, y, z, xd, yd, zd);

        this.quadSize = 0.06f + level.random.nextFloat() * 0.02f;
        this.lifetime = 14 + level.random.nextInt(10);

        this.gravity = 0.012f;
        this.friction = 0.96f;

        this.xd = xd;
        this.yd = yd;
        this.zd = zd;

        float tint = level.random.nextFloat() * 0.08f;
        this.rCol = 0.92f + tint;
        this.gCol = 0.92f + tint;
        this.bCol = 0.96f + tint;

        this.alpha = 0.85f;
        this.flutterOffset = level.random.nextFloat() * ((float) Math.PI * 2f);

        this.pickSprite(sprites);
    }

    @Override
    public void tick() {
        super.tick();

        float flutter = (float) Math.sin((this.age * 0.45f) + this.flutterOffset) * 0.008f;
        this.xd += flutter;
        this.zd -= flutter * 0.6f;

        if (this.age > this.lifetime * 0.65f) {
            this.alpha *= 0.88f;
        }
    }

    @Override
    protected int getLightColor(float partialTick) {
        return 0xF000F0;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }
}