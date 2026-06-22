package net.normlroyal.descendedangel.events;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.FlyingMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.normlroyal.descendedangel.DescendedAngel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = DescendedAngel.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class WindEvolutionEvents {
    private static final List<Vortex> VORTICES = new ArrayList<>();
    private static final List<Downdraft> DOWNDRAFTS = new ArrayList<>();

    private static final String TAG_VORTEX_CRASH_ARMED = "da_vortex_crash_armed";
    private static final String TAG_VORTEX_OWNER = "da_vortex_owner";

    private static final String TAG_ZEPHYR_ARMOR_UNTIL = "da_zephyr_armor_until";
    private static final UUID ZEPHYR_SHRED_UUID = UUID.fromString("74c616bd-55f1-45d7-b6d6-91a79ef7b5e7");

    private static final String TAG_DOWNDRAFT_PIN_UNTIL = "da_downdraft_pin_until";

    public static void scheduleVortex(
            ServerLevel level,
            Vec3 center,
            ServerPlayer owner,
            int durationTicks,
            double radius,
            float tickDamage,
            double launchPower
    ) {
        long now = level.getGameTime();

        VORTICES.add(new Vortex(
                level.dimension().location().toString(),
                center,
                owner.getUUID(),
                now + Math.max(20, durationTicks),
                radius,
                tickDamage,
                launchPower,
                false
        ));

        level.playSound(null, center.x, center.y, center.z, SoundEvents.TRIDENT_RIPTIDE_2, SoundSource.PLAYERS, 1.0F, 0.8F);

        level.sendParticles(
                ParticleTypes.CLOUD,
                center.x,
                center.y,
                center.z,
                64,
                radius * 0.35D,
                0.6D,
                radius * 0.35D,
                0.08D
        );
    }

    public static void schedulePlayerVortex(
            ServerLevel level,
            ServerPlayer owner,
            int durationTicks,
            double radius,
            float tickDamage,
            double launchPower
    ) {
        long now = level.getGameTime();
        Vec3 center = owner.position().add(0.0D, owner.getBbHeight() * 0.5D, 0.0D);

        VORTICES.add(new Vortex(
                level.dimension().location().toString(),
                center,
                owner.getUUID(),
                now + Math.max(20, durationTicks),
                radius,
                tickDamage,
                launchPower,
                true
        ));

        level.playSound(null, owner.getX(), owner.getY(), owner.getZ(), SoundEvents.TRIDENT_RIPTIDE_2, SoundSource.PLAYERS, 1.0F, 0.8F);

        level.sendParticles(
                ParticleTypes.CLOUD,
                center.x,
                center.y,
                center.z,
                64,
                radius * 0.35D,
                0.6D,
                radius * 0.35D,
                0.08D
        );
    }

    public static void castZephyrScythes(
            ServerLevel level,
            ServerPlayer owner,
            double range,
            double width,
            float damage,
            double armorShred,
            int shredDurationTicks
    ) {
        Vec3 eye = owner.getEyePosition();
        Vec3 look = owner.getLookAngle().normalize();
        Vec3 end = eye.add(look.scale(range));

        spawnScytheLine(level, eye, look, range);

        AABB search = owner.getBoundingBox()
                .expandTowards(look.scale(range))
                .inflate(width + 1.0D);

        List<LivingEntity> targets = level.getEntitiesOfClass(
                LivingEntity.class,
                search,
                e -> e.isAlive() && !e.getUUID().equals(owner.getUUID())
        );

        targets.sort(Comparator.comparingDouble(e -> e.distanceToSqr(owner)));

        int hits = 0;

        for (LivingEntity target : targets) {
            Vec3 targetCenter = target.position().add(0.0D, target.getBbHeight() * 0.5D, 0.0D);
            Vec3 fromEye = targetCenter.subtract(eye);

            double projected = fromEye.dot(look);

            if (projected < 0.0D || projected > range) {
                continue;
            }

            Vec3 closest = eye.add(look.scale(projected));
            double distanceToLine = targetCenter.distanceTo(closest);

            if (distanceToLine > width + target.getBbWidth() * 0.5D) {
                continue;
            }

            if (hurtWind(level, target, owner, damage)) {
                applyZephyrShred(level, target, armorShred, shredDurationTicks);

                Vec3 push = look.scale(0.45D);
                target.setDeltaMovement(target.getDeltaMovement().add(push.x, 0.12D, push.z));
                target.hurtMarked = true;

                hits++;

                level.sendParticles(
                        ParticleTypes.SWEEP_ATTACK,
                        target.getX(),
                        target.getY() + target.getBbHeight() * 0.5D,
                        target.getZ(),
                        2,
                        0.25D,
                        0.25D,
                        0.25D,
                        0.0D
                );
            }

            if (hits >= 8) {
                break;
            }
        }

        level.playSound(null, owner.getX(), owner.getY(), owner.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 1.0F, 1.55F);
    }

    public static void scheduleDowndraft(
            ServerLevel level,
            Vec3 center,
            ServerPlayer owner,
            int durationTicks,
            double radius,
            float damage
    ) {
        long now = level.getGameTime();

        DOWNDRAFTS.add(new Downdraft(
                level.dimension().location().toString(),
                center,
                owner.getUUID(),
                now + Math.max(30, durationTicks),
                radius,
                damage
        ));

        level.playSound(null, center.x, center.y, center.z, SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.PLAYERS, 0.6F, 1.55F);

        level.sendParticles(
                ParticleTypes.CLOUD,
                center.x,
                center.y + 7.0D,
                center.z,
                96,
                radius * 0.45D,
                0.35D,
                radius * 0.45D,
                0.04D
        );
    }

    @SubscribeEvent
    public static void onLevelTick(TickEvent.LevelTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        if (!(event.level instanceof ServerLevel level)) {
            return;
        }

        tickVortices(level);
        tickDowndrafts(level);
        tickZephyrShredExpiry(level);
        tickDowndraftPins(level);
    }

    @SubscribeEvent
    public static void onLivingFall(LivingFallEvent event) {
        LivingEntity entity = event.getEntity();

        if (!(entity.level() instanceof ServerLevel level)) {
            return;
        }

        if (!entity.getPersistentData().getBoolean(TAG_VORTEX_CRASH_ARMED)) {
            return;
        }

        UUID ownerId = entity.getPersistentData().hasUUID(TAG_VORTEX_OWNER)
                ? entity.getPersistentData().getUUID(TAG_VORTEX_OWNER)
                : null;

        entity.getPersistentData().remove(TAG_VORTEX_CRASH_ARMED);
        entity.getPersistentData().remove(TAG_VORTEX_OWNER);

        ServerPlayer owner = ownerId == null ? null : level.getServer().getPlayerList().getPlayer(ownerId);

        crashBurst(level, entity, owner);
    }

    private static void tickVortices(ServerLevel level) {
        long now = level.getGameTime();
        String dimension = level.dimension().location().toString();

        Iterator<Vortex> it = VORTICES.iterator();

        while (it.hasNext()) {
            Vortex vortex = it.next();

            if (!vortex.dimension.equals(dimension)) {
                continue;
            }

            ServerPlayer owner = level.getServer().getPlayerList().getPlayer(vortex.owner);

            if (owner == null || !owner.isAlive()) {
                it.remove();
                continue;
            }

            Vec3 center = getVortexCenter(vortex, owner);

            if (now >= vortex.expiresAt) {
                launchVortex(level, vortex, owner, center);
                it.remove();
                continue;
            }

            if (now % 2 == 0) {
                spawnVortexParticles(level, vortex, center);
            }

            pullVortexTargets(level, vortex, owner, center, now);
        }
    }

    private static void pullVortexTargets(ServerLevel level, Vortex vortex, ServerPlayer owner, Vec3 center, long now) {
        AABB box = new AABB(
                center.x - vortex.radius,
                center.y - vortex.radius,
                center.z - vortex.radius,
                center.x + vortex.radius,
                center.y + vortex.radius,
                center.z + vortex.radius
        );

        for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class, box, LivingEntity::isAlive)) {
            if (target.getUUID().equals(owner.getUUID())) {
                continue;
            }

            Vec3 targetCenter = target.position().add(0.0D, target.getBbHeight() * 0.5D, 0.0D);
            Vec3 toCenter = center.subtract(targetCenter);

            if (toCenter.lengthSqr() < 0.05D) {
                target.setDeltaMovement(target.getDeltaMovement().scale(0.35D));
                target.hurtMarked = true;
            } else {
                Vec3 pull = toCenter.normalize().scale(0.58D);

                target.setDeltaMovement(
                        target.getDeltaMovement()
                                .scale(0.45D)
                                .add(pull.x, 0.08D, pull.z)
                );

                target.hurtMarked = true;
            }

            if (now % 7 == 0) {
                hurtWind(level, target, owner, vortex.tickDamage);
            }
        }
    }

    private static void launchVortex(ServerLevel level, Vortex vortex, ServerPlayer owner, Vec3 center) {
        AABB box = new AABB(
                center.x - vortex.radius - 1.0D,
                center.y - vortex.radius,
                center.z - vortex.radius - 1.0D,
                center.x + vortex.radius + 1.0D,
                center.y + vortex.radius,
                center.z + vortex.radius + 1.0D
        );

        boolean launchedAny = false;

        for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class, box, LivingEntity::isAlive)) {
            if (target.getUUID().equals(owner.getUUID())) {
                continue;
            }

            target.setDeltaMovement(
                    target.getDeltaMovement().x * 0.15D,
                    vortex.launchPower,
                    target.getDeltaMovement().z * 0.15D
            );

            target.getPersistentData().putBoolean(TAG_VORTEX_CRASH_ARMED, true);
            target.getPersistentData().putUUID(TAG_VORTEX_OWNER, owner.getUUID());

            target.hurtMarked = true;
            launchedAny = true;

            level.sendParticles(
                    ParticleTypes.CLOUD,
                    target.getX(),
                    target.getY() + 0.3D,
                    target.getZ(),
                    18,
                    0.35D,
                    0.15D,
                    0.35D,
                    0.06D
            );
        }

        level.playSound(null, center.x, center.y, center.z, SoundEvents.GENERIC_EXPLODE, SoundSource.PLAYERS, launchedAny ? 0.9F : 0.55F, 1.7F);

        level.sendParticles(
                ParticleTypes.EXPLOSION,
                center.x,
                center.y,
                center.z,
                launchedAny ? 10 : 4,
                1.0D,
                0.75D,
                1.0D,
                0.05D
        );
    }

    private static void crashBurst(ServerLevel level, LivingEntity landingEntity, ServerPlayer owner) {
        Vec3 pos = landingEntity.position();

        level.playSound(null, pos.x, pos.y, pos.z, SoundEvents.GENERIC_EXPLODE, SoundSource.PLAYERS, 0.9F, 1.7F);

        level.sendParticles(
                ParticleTypes.EXPLOSION,
                pos.x,
                pos.y + 0.15D,
                pos.z,
                6,
                0.65D,
                0.25D,
                0.65D,
                0.02D
        );

        level.sendParticles(
                ParticleTypes.CLOUD,
                pos.x,
                pos.y + 0.1D,
                pos.z,
                42,
                1.35D,
                0.15D,
                1.35D,
                0.05D
        );

        AABB box = landingEntity.getBoundingBox().inflate(3.25D);

        for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class, box, LivingEntity::isAlive)) {
            if (owner != null && target.getUUID().equals(owner.getUUID())) {
                continue;
            }

            float damage = target == landingEntity ? 10.0F : 6.0F;

            if (hurtWind(level, target, owner, damage)) {
                knockAway(target, pos, target == landingEntity ? 0.35D : 0.85D);
            }
        }
    }

    private static void tickDowndrafts(ServerLevel level) {
        long now = level.getGameTime();
        String dimension = level.dimension().location().toString();

        Iterator<Downdraft> it = DOWNDRAFTS.iterator();

        while (it.hasNext()) {
            Downdraft downdraft = it.next();

            if (!downdraft.dimension.equals(dimension)) {
                continue;
            }

            if (now >= downdraft.expiresAt) {
                it.remove();
                continue;
            }

            ServerPlayer owner = level.getServer().getPlayerList().getPlayer(downdraft.owner);

            if (now % 2 == 0) {
                spawnDowndraftParticles(level, downdraft);
            }

            applyDowndraft(level, downdraft, owner, now);
        }
    }

    private static void applyDowndraft(ServerLevel level, Downdraft downdraft, ServerPlayer owner, long now) {
        AABB box = new AABB(
                downdraft.center.x - downdraft.radius,
                downdraft.center.y,
                downdraft.center.z - downdraft.radius,
                downdraft.center.x + downdraft.radius,
                downdraft.center.y + 12.0D,
                downdraft.center.z + downdraft.radius
        );

        for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class, box, LivingEntity::isAlive)) {
            if (owner != null && target.getUUID().equals(owner.getUUID())) {
                continue;
            }

            target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 25, 2, true, false, false));

            boolean airTarget = isAirTarget(target);

            if (airTarget || !target.onGround()) {
                Vec3 velocity = target.getDeltaMovement();

                target.setDeltaMovement(
                        velocity.x * 0.25D,
                        -1.35D,
                        velocity.z * 0.25D
                );

                target.getPersistentData().putLong(TAG_DOWNDRAFT_PIN_UNTIL, now + 45L);
                target.hurtMarked = true;
            }

            if (now % 5 == 0) {
                hurtWind(level, target, owner, airTarget ? downdraft.damage + 4.0F : downdraft.damage);
            }
        }
    }

    private static void tickDowndraftPins(ServerLevel level) {
        long now = level.getGameTime();

        for (Entity entity : level.getAllEntities()) {
            if (!(entity instanceof LivingEntity target)) {
                continue;
            }

            long until = target.getPersistentData().getLong(TAG_DOWNDRAFT_PIN_UNTIL);

            if (until <= 0L) {
                continue;
            }

            if (now > until) {
                target.getPersistentData().remove(TAG_DOWNDRAFT_PIN_UNTIL);
                continue;
            }

            target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 10, 3, true, false, false));

            Vec3 velocity = target.getDeltaMovement();

            if (target.onGround()) {
                target.setDeltaMovement(velocity.x * 0.15D, Math.min(velocity.y, 0.0D), velocity.z * 0.15D);
            } else {
                target.setDeltaMovement(velocity.x * 0.25D, Math.min(velocity.y, -0.45D), velocity.z * 0.25D);
            }

            target.hurtMarked = true;
        }
    }

    private static void tickZephyrShredExpiry(ServerLevel level) {
        long now = level.getGameTime();

        if (now % 10 != 0) {
            return;
        }

        for (Entity entity : level.getAllEntities()) {
            if (!(entity instanceof LivingEntity target)) {
                continue;
            }

            long until = target.getPersistentData().getLong(TAG_ZEPHYR_ARMOR_UNTIL);

            if (until <= 0L) {
                continue;
            }

            if (now <= until) {
                continue;
            }

            removeZephyrShred(target);
        }
    }

    private static void applyZephyrShred(ServerLevel level, LivingEntity target, double armorShred, int durationTicks) {
        AttributeInstance armor = target.getAttribute(Attributes.ARMOR);

        if (armor == null) {
            return;
        }

        if (armor.getModifier(ZEPHYR_SHRED_UUID) != null) {
            armor.removeModifier(ZEPHYR_SHRED_UUID);
        }

        armor.addTransientModifier(new AttributeModifier(
                ZEPHYR_SHRED_UUID,
                "Zephyr Scythes armor shred",
                -Math.abs(armorShred),
                AttributeModifier.Operation.ADDITION
        ));

        target.getPersistentData().putLong(TAG_ZEPHYR_ARMOR_UNTIL, level.getGameTime() + Math.max(20, durationTicks));

        level.sendParticles(
                ParticleTypes.CRIT,
                target.getX(),
                target.getY() + target.getBbHeight() * 0.5D,
                target.getZ(),
                12,
                0.35D,
                0.35D,
                0.35D,
                0.04D
        );
    }

    private static void removeZephyrShred(LivingEntity target) {
        AttributeInstance armor = target.getAttribute(Attributes.ARMOR);

        if (armor != null && armor.getModifier(ZEPHYR_SHRED_UUID) != null) {
            armor.removeModifier(ZEPHYR_SHRED_UUID);
        }

        target.getPersistentData().remove(TAG_ZEPHYR_ARMOR_UNTIL);
    }

    private static void spawnVortexParticles(ServerLevel level, Vortex vortex, Vec3 center) {
        long now = level.getGameTime();

        for (int i = 0; i < 28; i++) {
            double angle = (now * 0.35D) + (i * Math.PI * 2.0D / 28.0D);
            double ring = vortex.radius * (0.25D + (i % 4) * 0.18D);

            double x = center.x + Math.cos(angle) * ring;
            double z = center.z + Math.sin(angle) * ring;
            double y = center.y - 0.4D + ((i % 6) * 0.24D);

            level.sendParticles(
                    ParticleTypes.CLOUD,
                    x,
                    y,
                    z,
                    1,
                    0.03D,
                    0.03D,
                    0.03D,
                    0.0D
            );
        }

        level.sendParticles(
                ParticleTypes.POOF,
                center.x,
                center.y,
                center.z,
                5,
                0.35D,
                0.25D,
                0.35D,
                0.02D
        );
    }

    private static void spawnScytheLine(ServerLevel level, Vec3 eye, Vec3 look, double range) {
        for (double d = 1.0D; d <= range; d += 0.85D) {
            Vec3 point = eye.add(look.scale(d));

            level.sendParticles(
                    ParticleTypes.SWEEP_ATTACK,
                    point.x,
                    point.y,
                    point.z,
                    1,
                    0.0D,
                    0.0D,
                    0.0D,
                    0.0D
            );

            level.sendParticles(
                    ParticleTypes.CLOUD,
                    point.x,
                    point.y,
                    point.z,
                    1,
                    0.04D,
                    0.04D,
                    0.04D,
                    0.0D
            );
        }
    }

    private static void spawnDowndraftParticles(ServerLevel level, Downdraft downdraft) {
        long now = level.getGameTime();

        for (int i = 0; i < 18; i++) {
            double angle = (Math.PI * 2.0D * i / 18.0D) + now * 0.08D;
            double ring = downdraft.radius * (0.35D + (i % 3) * 0.2D);

            double x = downdraft.center.x + Math.cos(angle) * ring;
            double z = downdraft.center.z + Math.sin(angle) * ring;
            double y = downdraft.center.y + 8.0D - ((i % 6) * 1.15D);

            level.sendParticles(ParticleTypes.CLOUD, x, y, z, 1, 0.02D, -0.25D, 0.02D, 0.02D);
        }
    }

    private static boolean hurtWind(ServerLevel level, LivingEntity target, ServerPlayer owner, float amount) {
        DamageSource source = owner != null
                ? owner.damageSources().indirectMagic(owner, owner)
                : level.damageSources().magic();

        return target.hurt(source, amount);
    }

    private static boolean isAirTarget(LivingEntity target) {
        return target instanceof FlyingMob
                || target instanceof Phantom
                || target instanceof Blaze
                || !target.onGround();
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

    private static Vec3 getVortexCenter(Vortex vortex, ServerPlayer owner) {
        if (vortex.followsOwner) {
            return owner.position().add(0.0D, owner.getBbHeight() * 0.5D, 0.0D);
        }

        return vortex.center;
    }

    private record Vortex(
            String dimension,
            Vec3 center,
            UUID owner,
            long expiresAt,
            double radius,
            float tickDamage,
            double launchPower,
            boolean followsOwner
    ) {}

    private record Downdraft(
            String dimension,
            Vec3 center,
            UUID owner,
            long expiresAt,
            double radius,
            float damage
    ) {}
}