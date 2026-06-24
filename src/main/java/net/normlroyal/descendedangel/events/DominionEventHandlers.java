package net.normlroyal.descendedangel.events;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.normlroyal.descendedangel.DescendedAngel;
import net.normlroyal.descendedangel.content.block.ModBlocks;
import net.normlroyal.descendedangel.content.entity.ModEntities;
import net.normlroyal.descendedangel.events.useful.HaloUndeadScalingTarget;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = DescendedAngel.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DominionEventHandlers {
    private static final List<AstralLance> ASTRAL_LANCES = new ArrayList<>();
    private static final List<HeavensMap> HEAVENS_MAPS = new ArrayList<>();
    private static final List<SacredSilence> SACRED_SILENCES = new ArrayList<>();

    private static final String TAG_SACRED_SILENCE_UNTIL = "da_sacred_silence_until";

    private static final ResourceKey<DamageType> RESONANCE_DAMAGE = ResourceKey.create(
            Registries.DAMAGE_TYPE,
            new ResourceLocation(DescendedAngel.MOD_ID, "resonance")
    );

    public static void scheduleAstralLances(
            ServerLevel level,
            ServerPlayer owner,
            Vec3 center,
            int count,
            int delayTicks,
            float damage
    ) {
        for (int i = 0; i < count; i++) {
            double spread = count == 1 ? 0.0D : 1.75D + (0.25D * count);
            double x = center.x + (level.random.nextDouble() - 0.5D) * spread * 2.0D;
            double z = center.z + (level.random.nextDouble() - 0.5D) * spread * 2.0D;

            Vec3 lanceCenter = new Vec3(x, center.y, z);

            ASTRAL_LANCES.add(new AstralLance(
                    level.dimension().location().toString(),
                    lanceCenter,
                    owner.getUUID(),
                    level.getGameTime() + Math.max(5, delayTicks + i * 5L),
                    damage
            ));

            level.sendParticles(
                    ParticleTypes.END_ROD,
                    lanceCenter.x,
                    lanceCenter.y + 0.1D,
                    lanceCenter.z,
                    18,
                    0.45D,
                    0.05D,
                    0.45D,
                    0.02D
            );
        }

        level.playSound(null, center.x, center.y, center.z, SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 1.0F, 1.65F);
    }

    public static void createHeavensMap(
            ServerLevel level,
            ServerPlayer owner,
            int durationTicks,
            double radius,
            double sacredBlockRadius
    ) {
        HEAVENS_MAPS.add(new HeavensMap(
                level.dimension().location().toString(),
                owner.getUUID(),
                level.getGameTime() + Math.max(80, durationTicks),
                radius,
                sacredBlockRadius
        ));

        owner.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, durationTicks, 0, true, false, false));

        level.playSound(null, owner.blockPosition(), SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 1.0F, 1.45F);

        level.sendParticles(
                ParticleTypes.END_ROD,
                owner.getX(),
                owner.getY() + 1.2D,
                owner.getZ(),
                64,
                1.0D,
                0.8D,
                1.0D,
                0.04D
        );
    }

    public static void castResonancePulse(
            ServerLevel level,
            ServerPlayer owner,
            double radius,
            float damage,
            int interruptTicks
    ) {
        Vec3 center = owner.position().add(0.0D, owner.getBbHeight() * 0.5D, 0.0D);
        AABB area = owner.getBoundingBox().inflate(radius);

        level.playSound(null, owner.blockPosition(), SoundEvents.AMETHYST_BLOCK_RESONATE, SoundSource.PLAYERS, 1.3F, 0.65F);

        spawnResonanceRing(level, center, radius);

        for (Projectile projectile : level.getEntitiesOfClass(Projectile.class, area, Projectile::isAlive)) {
            Entity projectileOwner = projectile.getOwner();

            if (projectileOwner != null && projectileOwner.getUUID().equals(owner.getUUID())) {
                continue;
            }

            level.sendParticles(
                    ParticleTypes.POOF,
                    projectile.getX(),
                    projectile.getY(),
                    projectile.getZ(),
                    10,
                    0.15D,
                    0.15D,
                    0.15D,
                    0.03D
            );

            projectile.discard();
        }

        for (Mob mob : level.getEntitiesOfClass(Mob.class, area, mob -> mob.isAlive() && mob instanceof Enemy)) {
            mob.setTarget(null);
            mob.setAggressive(false);
            mob.getNavigation().stop();

            mob.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, interruptTicks, 3, true, false, false));
            mob.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, interruptTicks, 1, true, false, false));

            mob.invulnerableTime = 0;
            mob.hurt(resonanceDamage(level, owner), damage);

            Vec3 dir = mob.position().subtract(center);

            if (dir.lengthSqr() > 0.0001D) {
                dir = dir.normalize().scale(0.45D);
                mob.setDeltaMovement(mob.getDeltaMovement().add(dir.x, 0.15D, dir.z));
                mob.hurtMarked = true;
            }
        }
    }

    public static void createSacredSilence(
            ServerLevel level,
            ServerPlayer owner,
            int durationTicks,
            double radius
    ) {
        SacredSilence silence = new SacredSilence(
                level.dimension().location().toString(),
                owner.position().add(0.0D, 0.6D, 0.0D),
                owner.getUUID(),
                level.getGameTime() + Math.max(60, durationTicks),
                radius
        );

        SACRED_SILENCES.add(silence);

        level.playSound(null, owner.blockPosition(), SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 1.1F, 0.55F);

        level.sendParticles(
                ParticleTypes.CLOUD,
                silence.center.x,
                silence.center.y,
                silence.center.z,
                72,
                radius * 0.45D,
                0.35D,
                radius * 0.45D,
                0.015D
        );

        level.sendParticles(
                ParticleTypes.END_ROD,
                silence.center.x,
                silence.center.y + 0.25D,
                silence.center.z,
                40,
                radius * 0.35D,
                0.45D,
                radius * 0.35D,
                0.02D
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

        tickAstralLances(level);
        tickHeavensMaps(level);
        tickSacredSilences(level);
        tickSilencedMobs(level);
    }

    @SubscribeEvent
    public static void onLivingAttack(LivingAttackEvent event) {
        Entity attacker = event.getSource().getEntity();

        if (!(attacker instanceof Mob mob)) {
            return;
        }

        if (!(mob.level() instanceof ServerLevel level)) {
            return;
        }

        long until = mob.getPersistentData().getLong(TAG_SACRED_SILENCE_UNTIL);

        if (until > level.getGameTime()) {
            event.setCanceled(true);
            mob.setTarget(null);
            mob.setAggressive(false);
            mob.getNavigation().stop();
        }
    }

    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }

        if (!(event.getEntity() instanceof Projectile projectile)) {
            return;
        }

        Entity owner = projectile.getOwner();

        if (!(owner instanceof Mob mob)) {
            return;
        }

        long until = mob.getPersistentData().getLong(TAG_SACRED_SILENCE_UNTIL);

        if (until > level.getGameTime()) {
            event.setCanceled(true);
            projectile.discard();
        }
    }

    private static void tickAstralLances(ServerLevel level) {
        long now = level.getGameTime();
        String dimension = level.dimension().location().toString();

        Iterator<AstralLance> it = ASTRAL_LANCES.iterator();

        while (it.hasNext()) {
            AstralLance lance = it.next();

            if (!lance.dimension.equals(dimension)) {
                continue;
            }

            if (now < lance.strikeAt) {
                if (now % 4 == 0) {
                    level.sendParticles(
                            ParticleTypes.END_ROD,
                            lance.center.x,
                            lance.center.y + 0.15D,
                            lance.center.z,
                            8,
                            0.35D,
                            0.05D,
                            0.35D,
                            0.01D
                    );
                }

                continue;
            }

            strikeAstralLance(level, lance);
            it.remove();
        }
    }

    private static void strikeAstralLance(ServerLevel level, AstralLance lance) {
        ServerPlayer owner = level.getServer().getPlayerList().getPlayer(lance.owner);

        Vec3 center = lance.center;

        level.playSound(null, center.x, center.y, center.z, SoundEvents.BEACON_POWER_SELECT, SoundSource.PLAYERS, 1.2F, 1.85F);

        for (int y = 0; y < 14; y++) {
            level.sendParticles(
                    ParticleTypes.END_ROD,
                    center.x,
                    center.y + y,
                    center.z,
                    10,
                    0.22D,
                    0.08D,
                    0.22D,
                    0.02D
            );
        }

        level.sendParticles(
                ParticleTypes.FLASH,
                center.x,
                center.y + 1.0D,
                center.z,
                1,
                0.0D,
                0.0D,
                0.0D,
                0.0D
        );

        AABB strike = new AABB(
                center.x - 1.3D,
                center.y - 1.0D,
                center.z - 1.3D,
                center.x + 1.3D,
                center.y + 10.0D,
                center.z + 1.3D
        );

        for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class, strike, LivingEntity::isAlive)) {
            if (owner != null && target.getUUID().equals(owner.getUUID())) {
                continue;
            }

            float damage = isCelestialWeak(target) ? lance.damage * 1.45F : lance.damage;

            DamageSource source = owner != null
                    ? owner.damageSources().indirectMagic(owner, owner)
                    : level.damageSources().magic();

            target.invulnerableTime = 0;

            if (target.hurt(source, damage)) {
                target.addEffect(new MobEffectInstance(MobEffects.GLOWING, 120, 0, true, false, false));
            }
        }
    }

    private static void tickHeavensMaps(ServerLevel level) {
        long now = level.getGameTime();
        String dimension = level.dimension().location().toString();

        Iterator<HeavensMap> it = HEAVENS_MAPS.iterator();

        while (it.hasNext()) {
            HeavensMap map = it.next();

            if (!map.dimension.equals(dimension)) {
                continue;
            }

            ServerPlayer owner = level.getServer().getPlayerList().getPlayer(map.owner);

            if (owner == null || !owner.isAlive()) {
                it.remove();
                continue;
            }

            if (now >= map.expiresAt) {
                it.remove();
                continue;
            }

            revealEntities(level, owner, map);

            if (now % 20 == 0) {
                revealSacredBlocks(level, owner, map.sacredBlockRadius);
            }

            if (now % 8 == 0) {
                spawnHeavensMapParticles(level, owner, map.radius);
            }
        }
    }

    private static void revealEntities(ServerLevel level, ServerPlayer owner, HeavensMap map) {
        AABB normal = owner.getBoundingBox().inflate(map.radius);
        AABB extended = owner.getBoundingBox().inflate(map.radius * 1.55D);

        for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, extended, LivingEntity::isAlive)) {
            if (entity.getUUID().equals(owner.getUUID())) {
                continue;
            }

            boolean special = isCelestialWeak(entity);
            boolean inNormalRange = entity.distanceToSqr(owner) <= map.radius * map.radius;

            if (special || inNormalRange && entity instanceof Enemy) {
                entity.addEffect(new MobEffectInstance(MobEffects.GLOWING, 35, 0, true, false, false));
            }
        }

        owner.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 40, 0, true, false, false));
    }

    private static void revealSacredBlocks(ServerLevel level, ServerPlayer owner, double radius) {
        int r = (int)Math.min(18, Math.max(4, radius));
        BlockPos center = owner.blockPosition();

        for (int i = 0; i < 72; i++) {
            int x = center.getX() + level.random.nextInt(r * 2 + 1) - r;
            int y = center.getY() + level.random.nextInt(17) - 8;
            int z = center.getZ() + level.random.nextInt(r * 2 + 1) - r;

            BlockPos pos = new BlockPos(x, y, z);

            if (!isSacredBlock(level, pos)) {
                continue;
            }

            level.sendParticles(
                    ParticleTypes.END_ROD,
                    pos.getX() + 0.5D,
                    pos.getY() + 0.8D,
                    pos.getZ() + 0.5D,
                    6,
                    0.25D,
                    0.35D,
                    0.25D,
                    0.02D
            );
        }
    }

    private static void tickSacredSilences(ServerLevel level) {
        long now = level.getGameTime();
        String dimension = level.dimension().location().toString();

        Iterator<SacredSilence> it = SACRED_SILENCES.iterator();

        while (it.hasNext()) {
            SacredSilence silence = it.next();

            if (!silence.dimension.equals(dimension)) {
                continue;
            }

            if (now >= silence.expiresAt) {
                it.remove();
                continue;
            }

            applySacredSilence(level, silence, now);

            if (now % 5 == 0) {
                spawnSacredSilenceParticles(level, silence);
            }
        }
    }

    private static void applySacredSilence(ServerLevel level, SacredSilence silence, long now) {
        AABB area = new AABB(
                silence.center.x - silence.radius,
                silence.center.y - 3.0D,
                silence.center.z - silence.radius,
                silence.center.x + silence.radius,
                silence.center.y + 4.0D,
                silence.center.z + silence.radius
        );

        for (Mob mob : level.getEntitiesOfClass(Mob.class, area, mob -> mob.isAlive() && mob instanceof Enemy)) {
            mob.getPersistentData().putLong(TAG_SACRED_SILENCE_UNTIL, now + 30L);
            mob.setTarget(null);
            mob.setAggressive(false);
            mob.getNavigation().stop();

            mob.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 35, 2, true, false, false));
            mob.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 35, 1, true, false, false));
        }

        for (Projectile projectile : level.getEntitiesOfClass(Projectile.class, area.inflate(1.5D), Projectile::isAlive)) {
            Entity projectileOwner = projectile.getOwner();

            if (projectileOwner instanceof ServerPlayer sp && sp.getUUID().equals(silence.owner)) {
                continue;
            }

            level.sendParticles(
                    ParticleTypes.POOF,
                    projectile.getX(),
                    projectile.getY(),
                    projectile.getZ(),
                    8,
                    0.15D,
                    0.15D,
                    0.15D,
                    0.02D
            );

            projectile.discard();
        }
    }

    private static void tickSilencedMobs(ServerLevel level) {
        long now = level.getGameTime();

        if (now % 10 != 0) {
            return;
        }

        for (Entity entity : level.getAllEntities()) {
            if (!(entity instanceof Mob mob)) {
                continue;
            }

            long until = mob.getPersistentData().getLong(TAG_SACRED_SILENCE_UNTIL);

            if (until <= 0L) {
                continue;
            }

            if (now >= until) {
                mob.getPersistentData().remove(TAG_SACRED_SILENCE_UNTIL);
                continue;
            }

            mob.setTarget(null);
            mob.setAggressive(false);
        }
    }

    private static void spawnResonanceRing(ServerLevel level, Vec3 center, double radius) {
        double[] rings = new double[] {
                radius * 0.35D,
                radius * 0.65D,
                radius
        };

        for (double r : rings) {
            int points = Math.max(10, (int)(r * 3.0D));

            for (int i = 0; i < points; i++) {
                double angle = Math.PI * 2.0D * i / points;

                double x = center.x + Math.cos(angle) * r;
                double z = center.z + Math.sin(angle) * r;

                level.sendParticles(
                        ParticleTypes.SONIC_BOOM,
                        x,
                        center.y,
                        z,
                        1,
                        0.0D,
                        0.0D,
                        0.0D,
                        0.0D
                );
            }
        }

        level.sendParticles(
                ParticleTypes.NOTE,
                center.x,
                center.y + 0.25D,
                center.z,
                12,
                0.6D,
                0.25D,
                0.6D,
                0.0D
        );
    }

    private static void spawnHeavensMapParticles(ServerLevel level, ServerPlayer owner, double radius) {
        double y = owner.getY() + 3.4D;

        for (int i = 0; i < 24; i++) {
            double angle = Math.PI * 2.0D * i / 24.0D + level.getGameTime() * 0.025D;
            double r = 2.35D + (i % 4) * 0.45D;

            level.sendParticles(
                    ParticleTypes.END_ROD,
                    owner.getX() + Math.cos(angle) * r,
                    y + Math.sin(level.getGameTime() * 0.06D + i) * 0.12D,
                    owner.getZ() + Math.sin(angle) * r,
                    1,
                    0.01D,
                    0.01D,
                    0.01D,
                    0.0D
            );
        }

        if (level.getGameTime() % 16 == 0) {
            level.sendParticles(
                    ParticleTypes.ENCHANT,
                    owner.getX(),
                    y - 0.15D,
                    owner.getZ(),
                    18,
                    1.8D,
                    0.08D,
                    1.8D,
                    0.01D
            );
        }
    }

    private static void spawnSacredSilenceParticles(ServerLevel level, SacredSilence silence) {
        level.sendParticles(
                ParticleTypes.CLOUD,
                silence.center.x,
                silence.center.y,
                silence.center.z,
                14,
                silence.radius * 0.4D,
                0.25D,
                silence.radius * 0.4D,
                0.01D
        );

        level.sendParticles(
                ParticleTypes.END_ROD,
                silence.center.x,
                silence.center.y + 0.15D,
                silence.center.z,
                8,
                silence.radius * 0.32D,
                0.25D,
                silence.radius * 0.32D,
                0.01D
        );
    }

    private static boolean isSacredBlock(ServerLevel level, BlockPos pos) {
        return level.getBlockState(pos).is(ModBlocks.SACRED_ORE.get())
                || level.getBlockState(pos).is(ModBlocks.SACRED_INGOT_BLOCK.get())
                || level.getBlockState(pos).is(ModBlocks.ALTAR.get())
                || level.getBlockState(pos).is(ModBlocks.BAPTISMAL_FONT.get())
                || level.getBlockState(pos).is(Blocks.AMETHYST_BLOCK);
    }

    private static boolean isCelestialWeak(LivingEntity entity) {
        return entity.getMobType() == MobType.UNDEAD
                || entity.getType() == ModEntities.VOID_ANOMALY.get()
                || entity instanceof HaloUndeadScalingTarget;
    }

    private static DamageSource resonanceDamage(ServerLevel level, ServerPlayer owner) {
        var holder = level.registryAccess()
                .registryOrThrow(Registries.DAMAGE_TYPE)
                .getHolderOrThrow(RESONANCE_DAMAGE);

        return owner == null
                ? new DamageSource(holder)
                : new DamageSource(holder, owner);
    }

    private record AstralLance(
            String dimension,
            Vec3 center,
            UUID owner,
            long strikeAt,
            float damage
    ) {}

    private record HeavensMap(
            String dimension,
            UUID owner,
            long expiresAt,
            double radius,
            double sacredBlockRadius
    ) {}

    private record SacredSilence(
            String dimension,
            Vec3 center,
            UUID owner,
            long expiresAt,
            double radius
    ) {}
}