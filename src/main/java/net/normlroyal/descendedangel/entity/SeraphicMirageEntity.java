package net.normlroyal.descendedangel.entity;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;
import java.util.UUID;

public class SeraphicMirageEntity extends PathfinderMob {
    private static final EntityDataAccessor<Optional<UUID>> DATA_OWNER_UUID =
            SynchedEntityData.defineId(SeraphicMirageEntity.class, EntityDataSerializers.OPTIONAL_UUID);

    private int lifeTicks = 120;
    private double runX;
    private double runZ;
    private boolean flashed;

    public SeraphicMirageEntity(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
        this.xpReward = 0;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 8.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.42D)
                .add(Attributes.FOLLOW_RANGE, 24.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.25D);
    }

    public static SeraphicMirageEntity create(
            ServerLevel level,
            ServerPlayer owner,
            Vec3 position,
            Vec3 runDirection,
            int lifeTicks
    ) {
        SeraphicMirageEntity mirage = ModEntities.SERAPHIC_MIRAGE.get().create(level);

        if (mirage == null) {
            return null;
        }

        mirage.setOwnerUUID(owner.getUUID());
        mirage.lifeTicks = lifeTicks;

        Vec3 flat = new Vec3(runDirection.x, 0.0D, runDirection.z);

        if (flat.lengthSqr() < 0.0001D) {
            flat = owner.getLookAngle();
            flat = new Vec3(flat.x, 0.0D, flat.z);
        }

        if (flat.lengthSqr() < 0.0001D) {
            flat = new Vec3(1.0D, 0.0D, 0.0D);
        }

        flat = flat.normalize();

        mirage.runX = flat.x;
        mirage.runZ = flat.z;

        mirage.moveTo(
                position.x,
                position.y,
                position.z,
                owner.getYRot(),
                owner.getXRot()
        );

        mirage.setCustomName(owner.getDisplayName());
        mirage.setCustomNameVisible(false);
        mirage.setPersistenceRequired();

        return mirage;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_OWNER_UUID, Optional.empty());
    }

    public UUID getOwnerUUID() {
        return this.entityData.get(DATA_OWNER_UUID).orElse(null);
    }

    public void setOwnerUUID(UUID uuid) {
        this.entityData.set(DATA_OWNER_UUID, Optional.ofNullable(uuid));
    }

    @Override
    protected void registerGoals() {
        // Movement and aggro are handled manually.
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if (this.level().isClientSide) {
            return;
        }

        if (!(this.level() instanceof ServerLevel level)) {
            return;
        }

        lifeTicks--;

        if (lifeTicks <= 0 || !this.isAlive()) {
            flashAndDiscard(level);
            return;
        }

        moveLikeSplinter();
        pullAggro(level);

        if (this.tickCount % 4 == 0) {
            level.sendParticles(
                    ParticleTypes.CLOUD,
                    this.getX(),
                    this.getY() + 1.0D,
                    this.getZ(),
                    4,
                    0.25D,
                    0.4D,
                    0.25D,
                    0.01D
            );

            level.sendParticles(
                    ParticleTypes.END_ROD,
                    this.getX(),
                    this.getY() + 1.0D,
                    this.getZ(),
                    2,
                    0.2D,
                    0.35D,
                    0.2D,
                    0.01D
            );
        }
    }

    private void moveLikeSplinter() {
        Vec3 run = new Vec3(runX, 0.0D, runZ).normalize();

        this.setDeltaMovement(
                this.getDeltaMovement()
                        .scale(0.72D)
                        .add(run.x * 0.16D, this.onGround() ? 0.04D : -0.01D, run.z * 0.16D)
        );

        if (this.horizontalCollision && this.onGround()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0D, 0.35D, 0.0D));
        }

        this.hurtMarked = true;
    }

    private void pullAggro(ServerLevel level) {
        if (this.tickCount % 5 != 0) {
            return;
        }

        AABB box = this.getBoundingBox().inflate(14.0D);

        for (Mob mob : level.getEntitiesOfClass(Mob.class, box, mob -> mob.isAlive() && mob instanceof Enemy)) {
            mob.setTarget(this);
            mob.setAggressive(true);
            mob.getNavigation().moveTo(this, 1.25D);
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (this.level().isClientSide) {
            return super.hurt(source, amount);
        }

        boolean hurt = super.hurt(source, amount);

        if (hurt && this.getHealth() <= 0.0F && this.level() instanceof ServerLevel level) {
            flashAndDiscard(level);
        }

        return hurt;
    }

    @Override
    public void die(DamageSource source) {
        if (!this.level().isClientSide && this.level() instanceof ServerLevel level) {
            flashAndDiscard(level);
        } else {
            super.die(source);
        }
    }

    private void flashAndDiscard(ServerLevel level) {
        if (flashed) {
            this.discard();
            return;
        }

        flashed = true;

        level.playSound(
                null,
                this.getX(),
                this.getY(),
                this.getZ(),
                SoundEvents.FIREWORK_ROCKET_BLAST,
                SoundSource.PLAYERS,
                0.9F,
                1.65F
        );

        level.sendParticles(
                ParticleTypes.FLASH,
                this.getX(),
                this.getY() + 1.0D,
                this.getZ(),
                1,
                0.0D,
                0.0D,
                0.0D,
                0.0D
        );

        level.sendParticles(
                ParticleTypes.END_ROD,
                this.getX(),
                this.getY() + 1.0D,
                this.getZ(),
                48,
                0.8D,
                0.8D,
                0.8D,
                0.08D
        );

        AABB blindZone = this.getBoundingBox().inflate(5.0D);

        for (Mob mob : level.getEntitiesOfClass(Mob.class, blindZone, mob -> mob.isAlive() && mob instanceof Enemy)) {
            mob.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 80, 0, true, false, false));
            mob.setTarget(null);
            mob.setAggressive(false);
            mob.getNavigation().stop();
        }

        this.discard();
    }

    @Override
    protected void dropAllDeathLoot(DamageSource source) {
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);

        UUID owner = getOwnerUUID();

        if (owner != null) {
            tag.putUUID("Owner", owner);
        }

        tag.putInt("LifeTicks", lifeTicks);
        tag.putDouble("RunX", runX);
        tag.putDouble("RunZ", runZ);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);

        if (tag.hasUUID("Owner")) {
            setOwnerUUID(tag.getUUID("Owner"));
        }

        lifeTicks = tag.getInt("LifeTicks");
        runX = tag.getDouble("RunX");
        runZ = tag.getDouble("RunZ");
    }
}