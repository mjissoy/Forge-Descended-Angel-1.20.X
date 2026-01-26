package net.normlroyal.descendedangel.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.gameevent.GameEvent;
import net.normlroyal.descendedangel.events.useful.HaloUndeadScalingTarget;
import net.normlroyal.descendedangel.particle.ModParticles;
import net.normlroyal.descendedangel.util.HaloUtils;

public class VoidAnomalyEntity extends Zombie implements HaloUndeadScalingTarget {
    private int teleportCooldown = 0;

    public VoidAnomalyEntity(EntityType<? extends Zombie> type, Level level) {
        super(type, level);
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if (!level().isClientSide) {
            if (random.nextFloat() < 0.02f && this.level() instanceof ServerLevel sl) {
                sl.sendParticles(ModParticles.VOID_TOUCHED.get(),
                        this.getX(), this.getY() + 1.0, this.getZ(),
                        2,
                        0.35, 0.5, 0.35,
                        0.0);
            }
            if (teleportCooldown > 0) {
                teleportCooldown--;
            } else if (this.getTarget() != null && random.nextFloat() < 0.05f) {
                attemptShortTeleport();
                teleportCooldown = 200;
            }
        }
    }

    private void attemptShortTeleport() {
        double range = 5.0;

        double fromX = this.getX();
        double fromY = this.getY();
        double fromZ = this.getZ();

        double dx = this.getX() + (random.nextDouble() - 0.5) * 2.0 * range;
        double dy = this.getY() + (double)(random.nextInt(3) - 1);
        double dz = this.getZ() + (random.nextDouble() - 0.5) * 2.0 * range;

        spawnTeleportParticles(fromX, fromY, fromZ);

        boolean success = this.randomTeleport(dx, dy, dz, false);
        if (success) {
            spawnTeleportParticles(this.getX(), this.getY(), this.getZ());
            level().gameEvent(GameEvent.TELEPORT, this.position(), GameEvent.Context.of(this));
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        Entity direct = source.getDirectEntity();
        Entity attacker = source.getEntity();

        if (attacker instanceof Player player) {
            return HaloUtils.findEquippedHalo(player).isPresent() && super.hurt(source, amount);
        }

        return false;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Zombie.createAttributes();
    }

    @Override
    public MobType getMobType() {
        return MobType.UNDEFINED;
    }

    private void spawnTeleportParticles(double x, double y, double z) {
        if (this.level() instanceof ServerLevel sl) {
            for (int i = 0; i < 48; i++) {
                double vx = (random.nextDouble() - 0.5) * 2.0;
                double vy = (random.nextDouble() - 0.5) * 2.0;
                double vz = (random.nextDouble() - 0.5) * 2.0;

                sl.sendParticles(ModParticles.BLUE_PORTAL.get(),
                        x, y + 1.0, z,
                        1, vx, vy, vz, 0.0);
            }
        }
    }

    @Override
    protected boolean isSunSensitive() {
        return false;
    }

    public static boolean canSpawnHere(
            EntityType<VoidAnomalyEntity> type,
            ServerLevelAccessor level,
            MobSpawnType reason,
            BlockPos pos,
            RandomSource random
    ) {
        if (level.getDifficulty() == Difficulty.PEACEFUL) return false;

        if (level.getRawBrightness(pos, 0) > 7) return false;
        if (pos.getY() > 0) return false;

        return level.getBlockState(pos.below())
                .isValidSpawn(level, pos.below(), type);
    }
}