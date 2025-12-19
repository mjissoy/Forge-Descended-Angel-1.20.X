package net.normlroyal.descendedangel.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.particle.ParticleRenderType;

public class VoidTouchedParticle extends TextureSheetParticle {

    protected VoidTouchedParticle(ClientLevel level, double x, double y, double z,
                                  double xd, double yd, double zd, SpriteSet sprites) {
        super(level, x, y, z, xd, yd, zd);

        this.quadSize *= 1.10f;

        this.lifetime = 30 + level.random.nextInt(15);
        this.gravity = 0.0f;
        this.friction = 0.90f;


        this.rCol = 0.10f;
        this.gCol = 0.10f;
        this.bCol = 0.20f;

        this.alpha = 0.9f;

        this.pickSprite(sprites);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.age > this.lifetime - 10) {
            this.alpha *= 0.85f;
        }
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }
}
