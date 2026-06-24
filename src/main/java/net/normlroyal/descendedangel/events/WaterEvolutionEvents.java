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
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.normlroyal.descendedangel.DescendedAngel;
import net.normlroyal.descendedangel.content.entity.SeraphicMirageEntity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = DescendedAngel.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class WaterEvolutionEvents {
    private static final List<MistField> MIST_FIELDS = new ArrayList<>();
    private static final List<SerenityField> SERENITY_FIELDS = new ArrayList<>();

    private static final String TAG_SERENITY_PACIFIED_UNTIL = "da_serenity_pacified_until";

    public static void createMovingMistField(
            ServerLevel level,
            ServerPlayer owner,
            int durationTicks,
            double radius,
            float damage
    ) {
        long now = level.getGameTime();

        MIST_FIELDS.add(new MistField(
                level.dimension().location().toString(),
                owner.getUUID(),
                now + Math.max(40, durationTicks),
                radius,
                damage
        ));

        level.playSound(null, owner.blockPosition(), SoundEvents.BUBBLE_COLUMN_UPWARDS_AMBIENT, SoundSource.PLAYERS, 1.1F, 0.65F);

        level.sendParticles(
                ParticleTypes.CLOUD,
                owner.getX(),
                owner.getY() + 0.8D,
                owner.getZ(),
                64,
                radius * 0.45D,
                0.8D,
                radius * 0.45D,
                0.04D
        );
    }

    public static void createSeraphicMirage(
            ServerLevel level,
            ServerPlayer owner,
            int count,
            int durationTicks
    ) {
        Vec3 look = owner.getLookAngle();
        Vec3 flatLook = new Vec3(look.x, 0.0D, look.z);

        if (flatLook.lengthSqr() < 0.0001D) {
            flatLook = new Vec3(0.0D, 0.0D, 1.0D);
        }

        flatLook = flatLook.normalize();

        Vec3 right = new Vec3(-flatLook.z, 0.0D, flatLook.x);

        for (int i = 0; i < count; i++) {
            double spread = count == 1 ? 0.0D : i - ((count - 1) / 2.0D);
            Vec3 run = flatLook.add(right.scale(spread * 0.75D)).normalize();

            Vec3 spawn = owner.position()
                    .add(0.0D, 0.05D, 0.0D)
                    .add(right.scale(spread * 0.95D))
                    .add(flatLook.scale(1.15D));

            SeraphicMirageEntity mirage = SeraphicMirageEntity.create(
                    level,
                    owner,
                    spawn,
                    run,
                    durationTicks
            );

            if (mirage != null) {
                level.addFreshEntity(mirage);
            }
        }

        level.playSound(null, owner.blockPosition(), SoundEvents.ILLUSIONER_MIRROR_MOVE, SoundSource.PLAYERS, 1.0F, 1.25F);

        level.sendParticles(
                ParticleTypes.END_ROD,
                owner.getX(),
                owner.getY() + 1.0D,
                owner.getZ(),
                36,
                0.75D,
                0.8D,
                0.75D,
                0.04D
        );
    }

    public static void createDivineSerenity(
            ServerLevel level,
            ServerPlayer owner,
            int durationTicks,
            double radius
    ) {
        long now = level.getGameTime();
        Vec3 center = owner.position().add(0.0D, 0.6D, 0.0D);

        SERENITY_FIELDS.add(new SerenityField(
                level.dimension().location().toString(),
                center,
                owner.getUUID(),
                now + Math.max(60, durationTicks),
                radius
        ));

        level.playSound(null, owner.blockPosition(), SoundEvents.AMETHYST_BLOCK_RESONATE, SoundSource.PLAYERS, 1.0F, 1.45F);

        level.sendParticles(
                ParticleTypes.CLOUD,
                center.x,
                center.y,
                center.z,
                96,
                radius * 0.5D,
                0.65D,
                radius * 0.5D,
                0.025D
        );

        level.sendParticles(
                ParticleTypes.HAPPY_VILLAGER,
                center.x,
                center.y + 0.4D,
                center.z,
                32,
                radius * 0.35D,
                0.6D,
                radius * 0.35D,
                0.01D
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

        tickMistFields(level);
        tickSerenityFields(level);
        tickPacifiedMobs(level);
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

        long until = mob.getPersistentData().getLong(TAG_SERENITY_PACIFIED_UNTIL);

        if (until > level.getGameTime()) {
            event.setCanceled(true);
            mob.setTarget(null);
            mob.setAggressive(false);
            mob.getNavigation().stop();
        }
    }

    private static void tickMistFields(ServerLevel level) {
        long now = level.getGameTime();
        String dimension = level.dimension().location().toString();

        Iterator<MistField> it = MIST_FIELDS.iterator();

        while (it.hasNext()) {
            MistField field = it.next();

            if (!field.dimension.equals(dimension)) {
                continue;
            }

            ServerPlayer owner = level.getServer().getPlayerList().getPlayer(field.owner);

            if (owner == null || !owner.isAlive()) {
                it.remove();
                continue;
            }

            if (now >= field.expiresAt) {
                it.remove();
                continue;
            }

            applyMovingMist(level, owner, field, now);

            if (now % 3 == 0) {
                spawnMistParticles(level, owner, field.radius);
            }
        }
    }

    private static void applyMovingMist(ServerLevel level, ServerPlayer owner, MistField field, long now) {
        owner.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 30, 0, true, false, false));
        owner.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 30, 1, true, false, false));
        owner.clearFire();

        AABB cloud = owner.getBoundingBox().inflate(field.radius);

        for (Mob mob : level.getEntitiesOfClass(Mob.class, cloud, mob -> mob.isAlive() && mob instanceof Enemy)) {
            mob.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 30, 2, true, false, false));
            mob.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 30, 0, true, false, false));

            mob.setAirSupply(Math.min(mob.getAirSupply(), -20));

            if (now % 8 == 0) {
                DamageSource source = level.damageSources().drown();
                mob.hurt(source, field.damage);
            }

            if (now % 12 == 0) {
                mob.setTarget(null);
                mob.getNavigation().stop();
            }
        }
    }

    private static void tickSerenityFields(ServerLevel level) {
        long now = level.getGameTime();
        String dimension = level.dimension().location().toString();

        Iterator<SerenityField> it = SERENITY_FIELDS.iterator();

        while (it.hasNext()) {
            SerenityField field = it.next();

            if (!field.dimension.equals(dimension)) {
                continue;
            }

            if (now >= field.expiresAt) {
                it.remove();
                continue;
            }

            applySerenity(level, field, now);

            if (now % 4 == 0) {
                spawnSerenityParticles(level, field);
            }
        }
    }

    private static void applySerenity(ServerLevel level, SerenityField field, long now) {
        AABB aura = new AABB(
                field.center.x - field.radius,
                field.center.y - 2.0D,
                field.center.z - field.radius,
                field.center.x + field.radius,
                field.center.y + 3.0D,
                field.center.z + field.radius
        );

        for (Mob mob : level.getEntitiesOfClass(Mob.class, aura, mob -> mob.isAlive() && mob instanceof Enemy)) {
            pacifyMob(mob, now + 60L);

            if (now % 20 == 0) {
                wanderSoftly(mob, field.center);
            }
        }
    }

    private static void tickPacifiedMobs(ServerLevel level) {
        long now = level.getGameTime();

        if (now % 5 != 0) {
            return;
        }

        for (Entity entity : level.getAllEntities()) {
            if (!(entity instanceof Mob mob)) {
                continue;
            }

            long until = mob.getPersistentData().getLong(TAG_SERENITY_PACIFIED_UNTIL);

            if (until <= 0L) {
                continue;
            }

            if (now >= until) {
                mob.getPersistentData().remove(TAG_SERENITY_PACIFIED_UNTIL);
                continue;
            }

            mob.setTarget(null);
            mob.setAggressive(false);

            if (now % 20 == 0) {
                wanderSoftly(mob, mob.position());
            }
        }
    }

    private static void pacifyMob(Mob mob, long until) {
        mob.getPersistentData().putLong(TAG_SERENITY_PACIFIED_UNTIL, until);
        mob.setTarget(null);
        mob.setAggressive(false);
        mob.getNavigation().stop();

        mob.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 45, 2, true, false, false));
        mob.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 35, 0, true, false, false));
    }

    private static void wanderSoftly(Mob mob, Vec3 center) {
        double angle = mob.getRandom().nextDouble() * Math.PI * 2.0D;
        double dist = 2.0D + mob.getRandom().nextDouble() * 4.0D;

        double x = center.x + Math.cos(angle) * dist;
        double z = center.z + Math.sin(angle) * dist;
        double y = mob.getY();

        mob.getNavigation().moveTo(x, y, z, 0.55D);
    }

    private static void spawnMistParticles(ServerLevel level, ServerPlayer owner, double radius) {
        level.sendParticles(
                ParticleTypes.CLOUD,
                owner.getX(),
                owner.getY() + 0.8D,
                owner.getZ(),
                18,
                radius * 0.45D,
                0.55D,
                radius * 0.45D,
                0.015D
        );

        level.sendParticles(
                ParticleTypes.BUBBLE_POP,
                owner.getX(),
                owner.getY() + 0.5D,
                owner.getZ(),
                6,
                radius * 0.3D,
                0.35D,
                radius * 0.3D,
                0.01D
        );
    }

    private static void spawnSerenityParticles(ServerLevel level, SerenityField field) {
        level.sendParticles(
                ParticleTypes.CLOUD,
                field.center.x,
                field.center.y,
                field.center.z,
                20,
                field.radius * 0.45D,
                0.35D,
                field.radius * 0.45D,
                0.01D
        );

        if (level.getGameTime() % 12 == 0) {
            level.sendParticles(
                    ParticleTypes.HAPPY_VILLAGER,
                    field.center.x,
                    field.center.y + 0.35D,
                    field.center.z,
                    8,
                    field.radius * 0.35D,
                    0.35D,
                    field.radius * 0.35D,
                    0.01D
            );
        }
    }

    private record MistField(
            String dimension,
            UUID owner,
            long expiresAt,
            double radius,
            float damage
    ) {}

    private record SerenityField(
            String dimension,
            Vec3 center,
            UUID owner,
            long expiresAt,
            double radius
    ) {}
}