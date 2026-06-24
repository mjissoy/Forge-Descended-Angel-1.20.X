package net.normlroyal.descendedangel.events;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.normlroyal.descendedangel.DescendedAngel;
import net.normlroyal.descendedangel.content.block.ModBlocks;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = DescendedAngel.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EarthEvolutionEvents {
    private static final List<TempStructure> STRUCTURES = new ArrayList<>();
    private static final List<AegisPillar> AEGIS_PILLARS = new ArrayList<>();
    private static final List<Chrysalis> CHRYSALISES = new ArrayList<>();

    private static final UUID AEGIS_ARMOR_UUID = UUID.fromString("40e5481a-b7c0-446f-80a8-3ad89ef02a1d");
    private static final String TAG_AEGIS_ARMOR_UNTIL = "da_aegis_armor_until";

    public static void createHolyBastion(
            ServerLevel level,
            ServerPlayer owner,
            int width,
            int height,
            int thickness,
            int durationTicks
    ) {
        Vec3 look = owner.getLookAngle();
        Vec3 forward = new Vec3(look.x, 0.0D, look.z);

        if (forward.lengthSqr() < 0.0001D) {
            forward = new Vec3(0.0D, 0.0D, 1.0D);
        }

        forward = forward.normalize();

        Vec3 right = new Vec3(-forward.z, 0.0D, forward.x);

        BlockPos base = owner.blockPosition().offset(
                (int)Math.round(forward.x * 3.0D),
                0,
                (int)Math.round(forward.z * 3.0D)
        );

        List<StoredBlock> blocks = new ArrayList<>();
        Set<BlockPos> used = new HashSet<>();
        BlockState holy = ModBlocks.TEMP_HOLY_BLOCK.get().defaultBlockState();

        int half = width / 2;

        for (int t = 0; t < thickness; t++) {
            for (int w = -half; w <= half; w++) {
                BlockPos columnBase = base.offset(
                        (int)Math.round(right.x * w + forward.x * t),
                        0,
                        (int)Math.round(right.z * w + forward.z * t)
                );

                for (int h = 0; h < height; h++) {
                    BlockPos pos = columnBase.above(h);
                    placeTemporaryBlock(level, blocks, used, pos, holy);
                }
            }
        }

        long expiresAt = level.getGameTime() + Math.max(40, durationTicks);

        STRUCTURES.add(new TempStructure(
                level.dimension().location().toString(),
                blocks,
                ModBlocks.TEMP_HOLY_BLOCK.get(),
                expiresAt
        ));

        level.playSound(null, base, SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 1.0F, 0.75F);

        level.sendParticles(
                ParticleTypes.END_ROD,
                base.getX() + 0.5D,
                base.getY() + height * 0.5D,
                base.getZ() + 0.5D,
                48,
                width * 0.35D,
                height * 0.35D,
                0.4D,
                0.04D
        );
    }

    public static void createAegisPillar(
            ServerLevel level,
            ServerPlayer owner,
            BlockPos base,
            int durationTicks
    ) {
        List<StoredBlock> blocks = new ArrayList<>();
        Set<BlockPos> used = new HashSet<>();
        BlockState holy = ModBlocks.TEMP_HOLY_BLOCK.get().defaultBlockState();

        placeTemporaryBlock(level, blocks, used, base, holy);
        placeTemporaryBlock(level, blocks, used, base.above(), holy);
        placeTemporaryBlock(level, blocks, used, base.above(2), holy);
        placeTemporaryBlock(level, blocks, used, base.above(3), holy);

        placeTemporaryBlock(level, blocks, used, base.north(), holy);
        placeTemporaryBlock(level, blocks, used, base.south(), holy);
        placeTemporaryBlock(level, blocks, used, base.east(), holy);
        placeTemporaryBlock(level, blocks, used, base.west(), holy);

        long expiresAt = level.getGameTime() + Math.max(80, durationTicks);

        AEGIS_PILLARS.add(new AegisPillar(
                level.dimension().location().toString(),
                Vec3.atCenterOf(base).add(0.0D, 1.5D, 0.0D),
                owner.getUUID(),
                blocks,
                ModBlocks.TEMP_HOLY_BLOCK.get(),
                expiresAt,
                3.5D
        ));

        level.playSound(null, base, SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 1.2F, 0.75F);

        level.sendParticles(
                ParticleTypes.END_ROD,
                base.getX() + 0.5D,
                base.getY() + 1.5D,
                base.getZ() + 0.5D,
                42,
                0.45D,
                1.15D,
                0.45D,
                0.05D
        );
    }

    public static void createCrystalChrysalis(
            ServerLevel level,
            ServerPlayer owner,
            int durationTicks
    ) {
        BlockPos base = owner.blockPosition();
        List<StoredBlock> blocks = new ArrayList<>();
        Set<BlockPos> used = new HashSet<>();
        BlockState holy = ModBlocks.TEMP_HOLY_BLOCK.get().defaultBlockState();

        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = 0; dy <= 2; dy++) {
                for (int dz = -1; dz <= 1; dz++) {
                    boolean insidePlayerSpace = dx == 0 && dz == 0 && (dy == 0 || dy == 1);

                    if (insidePlayerSpace) {
                        continue;
                    }

                    placeTemporaryBlock(level, blocks, used, base.offset(dx, dy, dz), holy);
                }
            }
        }

        long expiresAt = level.getGameTime() + Math.max(40, durationTicks);

        CHRYSALISES.add(new Chrysalis(
                level.dimension().location().toString(),
                owner.getUUID(),
                base,
                owner.position(),
                blocks,
                ModBlocks.TEMP_HOLY_BLOCK.get(),
                expiresAt
        ));

        owner.setDeltaMovement(Vec3.ZERO);
        owner.fallDistance = 0.0F;
        owner.clearFire();

        level.playSound(null, base, SoundEvents.AMETHYST_BLOCK_RESONATE, SoundSource.PLAYERS, 1.1F, 0.65F);

        level.sendParticles(
                ParticleTypes.END_ROD,
                owner.getX(),
                owner.getY() + 1.0D,
                owner.getZ(),
                56,
                0.7D,
                0.9D,
                0.7D,
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

        tickStructures(level);
        tickAegisPillars(level);
        tickChrysalises(level);
        tickAegisArmorExpiry(level);
    }

    @SubscribeEvent
    public static void onLivingAttack(LivingAttackEvent event) {
        if (event.getEntity() instanceof ServerPlayer sp && isProtectedByChrysalis(sp)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (event.getEntity() instanceof ServerPlayer sp && isProtectedByChrysalis(sp)) {
            event.setAmount(0.0F);
            event.setCanceled(true);
        }
    }

    private static void tickStructures(ServerLevel level) {
        long now = level.getGameTime();
        String dimension = level.dimension().location().toString();

        Iterator<TempStructure> it = STRUCTURES.iterator();

        while (it.hasNext()) {
            TempStructure structure = it.next();

            if (!structure.dimension.equals(dimension)) {
                continue;
            }

            if (now >= structure.expiresAt) {
                restoreBlocks(level, structure.blocks, structure.placedBlock);
                it.remove();
            }
        }
    }

    private static void tickAegisPillars(ServerLevel level) {
        long now = level.getGameTime();
        String dimension = level.dimension().location().toString();

        Iterator<AegisPillar> it = AEGIS_PILLARS.iterator();

        while (it.hasNext()) {
            AegisPillar pillar = it.next();

            if (!pillar.dimension.equals(dimension)) {
                continue;
            }

            if (now >= pillar.expiresAt) {
                restoreBlocks(level, pillar.blocks, pillar.placedBlock);
                it.remove();
                continue;
            }

            ServerPlayer owner = level.getServer().getPlayerList().getPlayer(pillar.owner);

            if (owner == null) {
                restoreBlocks(level, pillar.blocks, pillar.placedBlock);
                it.remove();
                continue;
            }

            applyAegisAura(level, pillar, owner, now);

            if (now % 5 == 0) {
                spawnAegisParticles(level, pillar);
            }
        }
    }

    private static void tickChrysalises(ServerLevel level) {
        long now = level.getGameTime();
        String dimension = level.dimension().location().toString();

        Iterator<Chrysalis> it = CHRYSALISES.iterator();

        while (it.hasNext()) {
            Chrysalis chrysalis = it.next();

            if (!chrysalis.dimension.equals(dimension)) {
                continue;
            }

            ServerPlayer owner = level.getServer().getPlayerList().getPlayer(chrysalis.owner);

            if (owner == null) {
                restoreBlocks(level, chrysalis.blocks, chrysalis.placedBlock);
                it.remove();
                continue;
            }

            if (now >= chrysalis.expiresAt) {
                endChrysalis(level, chrysalis, owner);
                it.remove();
                continue;
            }

            maintainChrysalis(owner);

            if (now % 4 == 0) {
                level.sendParticles(
                        ParticleTypes.END_ROD,
                        owner.getX(),
                        owner.getY() + 1.0D,
                        owner.getZ(),
                        6,
                        0.45D,
                        0.65D,
                        0.45D,
                        0.015D
                );
            }
        }
    }

    private static void applyAegisAura(ServerLevel level, AegisPillar pillar, ServerPlayer owner, long now) {
        AABB aura = new AABB(
                pillar.center.x - pillar.radius,
                pillar.center.y - 2.0D,
                pillar.center.z - pillar.radius,
                pillar.center.x + pillar.radius,
                pillar.center.y + 3.0D,
                pillar.center.z + pillar.radius
        );

        for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, aura, LivingEntity::isAlive)) {
            if (isAlly(entity, owner)) {
                entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 30, 1, true, false, false));
                applyAegisArmor(level, entity, now + 35L);
            } else if (entity instanceof Enemy) {
                entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 35, 3, true, false, false));
            }
        }
    }

    private static void applyAegisArmor(ServerLevel level, LivingEntity entity, long until) {
        AttributeInstance armor = entity.getAttribute(Attributes.ARMOR);

        if (armor == null) {
            return;
        }

        if (armor.getModifier(AEGIS_ARMOR_UUID) != null) {
            armor.removeModifier(AEGIS_ARMOR_UUID);
        }

        armor.addTransientModifier(new AttributeModifier(
                AEGIS_ARMOR_UUID,
                "Aegis Pillar armor protection",
                6.0D,
                AttributeModifier.Operation.ADDITION
        ));

        entity.getPersistentData().putLong(TAG_AEGIS_ARMOR_UNTIL, until);
    }

    private static void tickAegisArmorExpiry(ServerLevel level) {
        long now = level.getGameTime();

        if (now % 10 != 0) {
            return;
        }

        for (Entity entity : level.getAllEntities()) {
            if (!(entity instanceof LivingEntity living)) {
                continue;
            }

            long until = living.getPersistentData().getLong(TAG_AEGIS_ARMOR_UNTIL);

            if (until <= 0L || now <= until) {
                continue;
            }

            AttributeInstance armor = living.getAttribute(Attributes.ARMOR);

            if (armor != null && armor.getModifier(AEGIS_ARMOR_UUID) != null) {
                armor.removeModifier(AEGIS_ARMOR_UUID);
            }

            living.getPersistentData().remove(TAG_AEGIS_ARMOR_UNTIL);
        }
    }

    private static void maintainChrysalis(ServerPlayer owner) {
        owner.setDeltaMovement(Vec3.ZERO);
        owner.fallDistance = 0.0F;
        owner.clearFire();
        owner.setAirSupply(owner.getMaxAirSupply());

        owner.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 25, 2, true, false, false));
        owner.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 25, 4, true, false, false));
        owner.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 25, 10, true, false, false));
    }

    private static void endChrysalis(ServerLevel level, Chrysalis chrysalis, ServerPlayer owner) {
        restoreBlocks(level, chrysalis.blocks, chrysalis.placedBlock);

        Vec3 center = owner.position().add(0.0D, 1.0D, 0.0D);

        level.playSound(null, owner.getX(), owner.getY(), owner.getZ(), SoundEvents.GLASS_BREAK, SoundSource.PLAYERS, 1.5F, 0.65F);

        level.sendParticles(
                ParticleTypes.EXPLOSION,
                center.x,
                center.y,
                center.z,
                8,
                0.75D,
                0.65D,
                0.75D,
                0.04D
        );

        level.sendParticles(
                ParticleTypes.END_ROD,
                center.x,
                center.y,
                center.z,
                64,
                1.25D,
                0.9D,
                1.25D,
                0.08D
        );

        AABB burst = owner.getBoundingBox().inflate(4.25D);

        for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class, burst, LivingEntity::isAlive)) {
            if (target.getUUID().equals(owner.getUUID())) {
                continue;
            }

            if (!(target instanceof Enemy)) {
                continue;
            }

            target.invulnerableTime = 0;

            DamageSource source = owner.damageSources().magic();

            if (target.hurt(source, 16.0F)) {
                knockAway(target, center, 1.2D);
            }
        }
    }

    private static boolean isProtectedByChrysalis(ServerPlayer sp) {
        String dimension = sp.level().dimension().location().toString();
        long now = sp.level().getGameTime();

        for (Chrysalis chrysalis : CHRYSALISES) {
            if (chrysalis.owner.equals(sp.getUUID())
                    && chrysalis.dimension.equals(dimension)
                    && now < chrysalis.expiresAt) {
                return true;
            }
        }

        return false;
    }

    private static boolean isAlly(LivingEntity entity, ServerPlayer owner) {
        if (entity.getUUID().equals(owner.getUUID())) {
            return true;
        }

        if (entity instanceof ServerPlayer) {
            return true;
        }

        if (entity instanceof TamableAnimal tameable) {
            return tameable.isOwnedBy(owner);
        }

        return false;
    }

    private static void spawnAegisParticles(ServerLevel level, AegisPillar pillar) {
        level.sendParticles(
                ParticleTypes.END_ROD,
                pillar.center.x,
                pillar.center.y,
                pillar.center.z,
                8,
                0.35D,
                1.0D,
                0.35D,
                0.02D
        );

        for (int i = 0; i < 16; i++) {
            double angle = (Math.PI * 2.0D * i) / 16.0D;
            double x = pillar.center.x + Math.cos(angle) * pillar.radius;
            double z = pillar.center.z + Math.sin(angle) * pillar.radius;

            level.sendParticles(
                    ParticleTypes.ENCHANT,
                    x,
                    pillar.center.y - 0.75D,
                    z,
                    1,
                    0.0D,
                    0.1D,
                    0.0D,
                    0.0D
            );
        }
    }

    private static void knockAway(LivingEntity target, Vec3 center, double strength) {
        Vec3 dir = target.position().subtract(center);

        if (dir.lengthSqr() < 0.0001D) {
            dir = new Vec3(0.0D, 1.0D, 0.0D);
        }

        Vec3 push = dir.normalize().scale(strength);

        target.setDeltaMovement(target.getDeltaMovement().add(push.x, 0.45D, push.z));
        target.hurtMarked = true;
    }

    private static void placeTemporaryBlock(
            ServerLevel level,
            List<StoredBlock> blocks,
            Set<BlockPos> used,
            BlockPos pos,
            BlockState placed
    ) {
        BlockPos immutable = pos.immutable();

        if (!used.add(immutable)) {
            return;
        }

        BlockState old = level.getBlockState(immutable);

        if (!old.canBeReplaced()) {
            return;
        }

        blocks.add(new StoredBlock(immutable, old));
        level.setBlockAndUpdate(immutable, placed);
    }

    private static void restoreBlocks(ServerLevel level, List<StoredBlock> blocks, Block placedBlock) {
        for (int i = blocks.size() - 1; i >= 0; i--) {
            StoredBlock stored = blocks.get(i);
            BlockState current = level.getBlockState(stored.pos);

            if (current.is(placedBlock)) {
                level.setBlockAndUpdate(stored.pos, stored.oldState);
            }
        }
    }

    private record StoredBlock(
            BlockPos pos,
            BlockState oldState
    ) {}

    private record TempStructure(
            String dimension,
            List<StoredBlock> blocks,
            Block placedBlock,
            long expiresAt
    ) {}

    private record AegisPillar(
            String dimension,
            Vec3 center,
            UUID owner,
            List<StoredBlock> blocks,
            Block placedBlock,
            long expiresAt,
            double radius
    ) {}

    private record Chrysalis(
            String dimension,
            UUID owner,
            BlockPos base,
            Vec3 center,
            List<StoredBlock> blocks,
            Block placedBlock,
            long expiresAt
    ) {}
}