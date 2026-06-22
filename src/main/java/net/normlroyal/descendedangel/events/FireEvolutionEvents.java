package net.normlroyal.descendedangel.events;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.normlroyal.descendedangel.DescendedAngel;
import net.normlroyal.descendedangel.entity.ModEntities;
import net.normlroyal.descendedangel.events.useful.HaloUndeadScalingTarget;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = DescendedAngel.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class FireEvolutionEvents {
    public static final String TAG_SACRED_FLARE_PROJECTILE = "da_sacred_flare_projectile";

    public static final String TAG_SOL_CORONA_UNTIL = "da_sol_corona_until";
    public static final String TAG_SOL_CORONA_CHARGES = "da_sol_corona_charges";

    private static final List<Pillar> PILLARS = new ArrayList<>();

    public static void schedulePillar(ServerLevel level, BlockPos center, ServerPlayer owner, int delayTicks, int durationTicks) {
        long now = level.getGameTime();

        PILLARS.add(new Pillar(
                level.dimension().location().toString(),
                center.immutable(),
                owner.getUUID(),
                now + Math.max(1, delayTicks),
                now + Math.max(1, delayTicks) + Math.max(20, durationTicks)
        ));

        level.playSound(null, center, SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 1.0F, 1.4F);

        level.sendParticles(
                ParticleTypes.END_ROD,
                center.getX() + 0.5,
                center.getY() + 0.15,
                center.getZ() + 0.5,
                32,
                1.5,
                0.05,
                1.5,
                0.02
        );
    }

    @SubscribeEvent
    public static void onSacredFlareImpact(ProjectileImpactEvent event) {
        Projectile projectile = event.getProjectile();

        if (!projectile.getPersistentData().getBoolean(TAG_SACRED_FLARE_PROJECTILE)) {
            return;
        }

        if (!(projectile.level() instanceof ServerLevel level)) {
            return;
        }

        event.setCanceled(true);

        Entity ownerEntity = projectile.getOwner();
        ServerPlayer owner = ownerEntity instanceof ServerPlayer sp ? sp : null;

        explodeSacredFlare(level, projectile.position(), owner, projectile);
        projectile.discard();
    }

    @SubscribeEvent
    public static void onLevelTick(TickEvent.LevelTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        if (!(event.level instanceof ServerLevel level)) {
            return;
        }

        tickSolCorona(level);
        tickPillars(level);
    }

    private static void explodeSacredFlare(ServerLevel level, Vec3 center, ServerPlayer owner, Entity direct) {
        level.playSound(null, center.x, center.y, center.z, SoundEvents.GENERIC_EXPLODE, SoundSource.PLAYERS, 1.6F, 1.25F);

        level.sendParticles(
                ParticleTypes.EXPLOSION_EMITTER,
                center.x,
                center.y,
                center.z,
                1,
                0,
                0,
                0,
                0
        );

        level.sendParticles(
                ParticleTypes.FLAME,
                center.x,
                center.y,
                center.z,
                96,
                2.5,
                1.8,
                2.5,
                0.08
        );

        level.sendParticles(
                ParticleTypes.END_ROD,
                center.x,
                center.y,
                center.z,
                72,
                2.25,
                1.5,
                2.25,
                0.05
        );

        double radius = 5.0D;
        AABB box = new AABB(
                center.x - radius,
                center.y - radius,
                center.z - radius,
                center.x + radius,
                center.y + radius,
                center.z + radius
        );

        for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class, box, LivingEntity::isAlive)) {
            if (owner != null && target.getUUID().equals(owner.getUUID())) {
                continue;
            }

            float damage = isHolyWeak(target) ? 20.0F : 12.0F;

            if (hurtHoly(level, target, owner, direct, damage)) {
                knockAway(target, center, 1.1D);
            }
        }
    }

    private static void tickSolCorona(ServerLevel level) {
        long now = level.getGameTime();

        for (ServerPlayer sp : level.getServer().getPlayerList().getPlayers()) {
            if (sp.level() != level) {
                continue;
            }

            long until = sp.getPersistentData().getLong(TAG_SOL_CORONA_UNTIL);
            int charges = sp.getPersistentData().getInt(TAG_SOL_CORONA_CHARGES);

            if (until <= now || charges <= 0) {
                clearCorona(sp);
                continue;
            }

            spawnCoronaOrbit(level, sp, charges, now);

            int remaining = charges;
            remaining = coronaInterceptProjectiles(level, sp, remaining);
            remaining = coronaDetonateMobs(level, sp, remaining);

            if (remaining <= 0) {
                clearCorona(sp);
            } else if (remaining != charges) {
                sp.getPersistentData().putInt(TAG_SOL_CORONA_CHARGES, remaining);
            }
        }
    }

    private static int coronaInterceptProjectiles(ServerLevel level, ServerPlayer owner, int charges) {
        if (charges <= 0) return 0;

        AABB box = owner.getBoundingBox().inflate(3.35D);

        for (Projectile projectile : level.getEntitiesOfClass(Projectile.class, box, p -> {
            if (!p.isAlive()) return false;

            Entity projectileOwner = p.getOwner();
            return projectileOwner == null || !projectileOwner.getUUID().equals(owner.getUUID());
        })) {
            burst(level, projectile.position());
            projectile.discard();

            charges--;
            if (charges <= 0) {
                return 0;
            }
        }

        return charges;
    }

    private static int coronaDetonateMobs(ServerLevel level, ServerPlayer owner, int charges) {
        if (charges <= 0) return 0;

        AABB box = owner.getBoundingBox().inflate(2.15D);

        for (Monster mob : level.getEntitiesOfClass(Monster.class, box, Monster::isAlive)) {
            burst(level, mob.position().add(0, mob.getBbHeight() * 0.5D, 0));

            float damage = isHolyWeak(mob) ? 12.0F : 8.0F;
            if (hurtHoly(level, mob, owner, owner, damage)) {
                knockAway(mob, owner.position(), 0.8D);
            }

            charges--;
            if (charges <= 0) {
                return 0;
            }
        }

        return charges;
    }

    private static void tickPillars(ServerLevel level) {
        long now = level.getGameTime();
        String dimension = level.dimension().location().toString();

        Iterator<Pillar> it = PILLARS.iterator();

        while (it.hasNext()) {
            Pillar pillar = it.next();

            if (!pillar.dimension.equals(dimension)) {
                continue;
            }

            if (now > pillar.expiresAt) {
                it.remove();
                continue;
            }

            ServerPlayer owner = level.getServer().getPlayerList().getPlayer(pillar.owner);

            if (now < pillar.warmupUntil) {
                if (now % 5 == 0) {
                    spawnPillarWarning(level, pillar.center);
                }
                continue;
            }

            if (now % 2 == 0) {
                spawnPillarFlames(level, pillar.center);
            }

            if (now % 5 == 0) {
                damagePillar(level, pillar.center, owner);
            }
        }
    }

    private static void damagePillar(ServerLevel level, BlockPos center, ServerPlayer owner) {
        AABB box = new AABB(
                center.getX() - 1.75D,
                center.getY(),
                center.getZ() - 1.75D,
                center.getX() + 2.75D,
                center.getY() + 9.0D,
                center.getZ() + 2.75D
        );

        for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class, box, LivingEntity::isAlive)) {
            if (owner != null && target.getUUID().equals(owner.getUUID())) {
                continue;
            }

            float damage = isHolyWeak(target) ? 8.0F : 5.0F;
            hurtHoly(level, target, owner, owner, damage);
        }
    }

    private static void spawnPillarWarning(ServerLevel level, BlockPos center) {
        level.sendParticles(
                ParticleTypes.END_ROD,
                center.getX() + 0.5,
                center.getY() + 0.15,
                center.getZ() + 0.5,
                18,
                1.35,
                0.05,
                1.35,
                0.02
        );
    }

    private static void spawnPillarFlames(ServerLevel level, BlockPos center) {
        for (int y = 0; y < 8; y++) {
            level.sendParticles(
                    ParticleTypes.FLAME,
                    center.getX() + 0.5,
                    center.getY() + y + 0.35,
                    center.getZ() + 0.5,
                    10,
                    0.7,
                    0.2,
                    0.7,
                    0.02
            );

            level.sendParticles(
                    ParticleTypes.END_ROD,
                    center.getX() + 0.5,
                    center.getY() + y + 0.35,
                    center.getZ() + 0.5,
                    5,
                    0.55,
                    0.15,
                    0.55,
                    0.01
            );
        }

        if (level.getGameTime() % 20 == 0) {
            level.playSound(
                    null,
                    center,
                    SoundEvents.FIRECHARGE_USE,
                    SoundSource.PLAYERS,
                    0.8F,
                    0.9F
            );
        }
    }

    private static void spawnCoronaOrbit(ServerLevel level, ServerPlayer owner, int charges, long now) {
        for (int i = 0; i < charges; i++) {
            double angle = (now * 0.28D) + ((Math.PI * 2.0D / 3.0D) * i);
            double x = owner.getX() + Math.cos(angle) * 1.15D;
            double z = owner.getZ() + Math.sin(angle) * 1.15D;
            double y = owner.getY() + owner.getBbHeight() + 0.35D;

            level.sendParticles(ParticleTypes.FLAME, x, y, z, 2, 0.03, 0.03, 0.03, 0.0);
            level.sendParticles(ParticleTypes.END_ROD, x, y, z, 1, 0.02, 0.02, 0.02, 0.0);
        }
    }

    private static void burst(ServerLevel level, Vec3 pos) {
        level.playSound(null, pos.x, pos.y, pos.z, SoundEvents.FIRECHARGE_USE, SoundSource.PLAYERS, 0.8F, 1.45F);

        level.sendParticles(
                ParticleTypes.FLAME,
                pos.x,
                pos.y,
                pos.z,
                24,
                0.4,
                0.4,
                0.4,
                0.05
        );

        level.sendParticles(
                ParticleTypes.END_ROD,
                pos.x,
                pos.y,
                pos.z,
                16,
                0.35,
                0.35,
                0.35,
                0.03
        );
    }

    private static boolean hurtHoly(ServerLevel level, LivingEntity target, ServerPlayer owner, Entity direct, float amount) {
        if (target instanceof ZombieVillager zv && target.getHealth() <= amount + 0.5F) {
            purifyZombieVillager(level, zv);

            level.sendParticles(
                    ParticleTypes.HAPPY_VILLAGER,
                    zv.getX(),
                    zv.getY() + 1.0,
                    zv.getZ(),
                    32,
                    0.45,
                    0.65,
                    0.45,
                    0.02
            );

            level.sendParticles(
                    ParticleTypes.END_ROD,
                    zv.getX(),
                    zv.getY() + 1.0,
                    zv.getZ(),
                    18,
                    0.35,
                    0.45,
                    0.35,
                    0.02
            );

            return true;
        }

        DamageSource source = owner != null
                ? owner.damageSources().indirectMagic(direct == null ? owner : direct, owner)
                : level.damageSources().magic();

        boolean hurt = target.hurt(source, amount);

        if (hurt) {
            target.setSecondsOnFire(4);
        }

        return hurt;
    }

    private static boolean isHolyWeak(LivingEntity entity) {
        return entity.getMobType() == MobType.UNDEAD
                || entity.getType() == ModEntities.VOID_ANOMALY.get()
                || entity instanceof HaloUndeadScalingTarget;
    }

    private static void knockAway(LivingEntity target, Vec3 center, double strength) {
        Vec3 dir = target.position().subtract(center);

        if (dir.lengthSqr() < 0.0001D) {
            dir = new Vec3(0.0D, 1.0D, 0.0D);
        }

        Vec3 push = dir.normalize().scale(strength);
        target.setDeltaMovement(target.getDeltaMovement().add(push.x, 0.35D, push.z));
        target.hurtMarked = true;
    }

    private static void clearCorona(ServerPlayer sp) {
        sp.getPersistentData().remove(TAG_SOL_CORONA_UNTIL);
        sp.getPersistentData().remove(TAG_SOL_CORONA_CHARGES);
    }

    private record Pillar(
            String dimension,
            BlockPos center,
            UUID owner,
            long warmupUntil,
            long expiresAt
    ) {}

    private static void purifyZombieVillager(ServerLevel level, ZombieVillager zv) {
        Villager villager = EntityType.VILLAGER.create(level);

        if (villager == null) {
            zv.discard();
            return;
        }

        villager.moveTo(
                zv.getX(),
                zv.getY(),
                zv.getZ(),
                zv.getYRot(),
                zv.getXRot()
        );

        villager.setVillagerData(zv.getVillagerData());
        villager.setHealth(Math.max(4.0F, villager.getMaxHealth() * 0.5F));

        if (zv.hasCustomName()) {
            villager.setCustomName(zv.getCustomName());
            villager.setCustomNameVisible(zv.isCustomNameVisible());
        }

        if (zv.isBaby()) {
            villager.setBaby(true);
        }

        villager.setInvulnerable(zv.isInvulnerable());
        villager.setNoAi(zv.isNoAi());
        villager.setPersistenceRequired();

        level.addFreshEntity(villager);
        zv.discard();
    }
}