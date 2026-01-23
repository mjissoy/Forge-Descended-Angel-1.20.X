package net.normlroyal.descendedangel.item.custom.writings.effects;

import com.google.gson.JsonObject;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.normlroyal.descendedangel.config.ModConfigs;
import net.normlroyal.descendedangel.item.custom.writings.IWritType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpawnEntityWritType implements IWritType {

    private record WeightedEntity(EntityType<?> type, double weight) {}

    @Override
    public void execute(JsonObject data, ServerLevel level, ServerPlayer player, ItemStack stack) {

        List<WeightedEntity> pool = buildEntityPool(data);
        if (pool.isEmpty()) return;

        int count = data.has("count")
                ? data.get("count").getAsInt()
                : ModConfigs.COMMON.ENTITY_SPAWN_COUNT.get();

        double radius = data.has("radius")
                ? data.get("radius").getAsDouble()
                : 4.0;

        Vec3 base = player.position();

        for (int i = 0; i < count; i++) {
            EntityType<?> type = pickWeighted(level.random, pool);
            if (type == null) continue;

            double dx = (level.random.nextDouble() - 0.5) * 2.0 * radius;
            double dz = (level.random.nextDouble() - 0.5) * 2.0 * radius;
            BlockPos pos = BlockPos.containing(base.x + dx, base.y, base.z + dz);

            Entity ent = type.create(level);
            if (ent == null) continue;

            ent.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5,
                    level.random.nextFloat() * 360f, 0f);

            if (ent instanceof Mob mob) {
                mob.finalizeSpawn(level, level.getCurrentDifficultyAt(pos),
                        MobSpawnType.MOB_SUMMONED, null, null);
            }

            level.addFreshEntity(ent);
        }
    }


    private static List<WeightedEntity> buildEntityPool(JsonObject data) {
        List<ResourceLocation> ids = new ArrayList<>();
        Map<ResourceLocation, Double> explicitWeights = new HashMap<>();

        // single entity
        if (data.has("entity")) {
            ResourceLocation id = ResourceLocation.tryParse(data.get("entity").getAsString());
            if (id != null) ids.add(id);
        }

        // multiple entities
        if (data.has("entities")) {
            for (var el : data.getAsJsonArray("entities")) {
                if (el.isJsonPrimitive()) {
                    ResourceLocation id = ResourceLocation.tryParse(el.getAsString());
                    if (id != null) ids.add(id);
                } else if (el.isJsonObject()) {
                    JsonObject obj = el.getAsJsonObject();
                    ResourceLocation id = ResourceLocation.tryParse(obj.get("id").getAsString());
                    if (id != null) {
                        ids.add(id);
                        if (obj.has("weight")) {
                            explicitWeights.put(id, obj.get("weight").getAsDouble());
                        }
                    }
                }
            }
        }

        if (ids.isEmpty()) return List.of();

        double explicitSum = explicitWeights.values().stream().mapToDouble(Double::doubleValue).sum();
        explicitSum = Math.min(1.0, Math.max(0.0, explicitSum));

        int unweightedCount = ids.size() - explicitWeights.size();
        double fallbackWeight = unweightedCount > 0
                ? (1.0 - explicitSum) / unweightedCount
                : 0.0;

        List<WeightedEntity> pool = new ArrayList<>();

        for (ResourceLocation id : ids) {
            EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.get(id);
            if (type == null) continue;

            double weight = explicitWeights.getOrDefault(id,
                    explicitWeights.isEmpty()
                            ? 1.0 / ids.size()
                            : fallbackWeight);

            if (weight > 0) {
                pool.add(new WeightedEntity(type, weight));
            }
        }

        return pool;
    }

    private static EntityType<?> pickWeighted(RandomSource random, List<WeightedEntity> pool) {
        double roll = random.nextDouble();
        double acc = 0.0;

        for (WeightedEntity e : pool) {
            acc += e.weight();
            if (roll <= acc) return e.type();
        }

        return pool.get(pool.size() - 1).type();
    }
}

