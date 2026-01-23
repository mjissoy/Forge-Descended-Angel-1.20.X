package net.normlroyal.descendedangel.item.custom.writings.effects;

import com.google.gson.JsonObject;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.normlroyal.descendedangel.item.custom.writings.IWritType;

public class SpawnEntityWritType implements IWritType {

    @Override
    public void execute(JsonObject data, ServerLevel level, ServerPlayer player, ItemStack stack) {
        String entityStr = data.has("entity") ? data.get("entity").getAsString() : "minecraft:villager";
        ResourceLocation entityId = ResourceLocation.tryParse(entityStr);
        if (entityId == null) return;

        EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.get(entityId);
        if (type == null || type == EntityType.PIG && !entityStr.equals("minecraft:pig")) {
            if (!BuiltInRegistries.ENTITY_TYPE.containsKey(entityId)) return;
            type = BuiltInRegistries.ENTITY_TYPE.get(entityId);
        }

        int count = data.has("count") ? data.get("count").getAsInt() : net.normlroyal.descendedangel.config.ModConfigs.COMMON.ENTITY_SPAWN_COUNT.get();
        double radius = data.has("radius") ? data.get("radius").getAsDouble() : 4.0;

        Vec3 base = player.position();

        for (int i = 0; i < count; i++) {
            double dx = (level.random.nextDouble() - 0.5) * 2.0 * radius;
            double dz = (level.random.nextDouble() - 0.5) * 2.0 * radius;

            BlockPos pos = BlockPos.containing(base.x + dx, base.y, base.z + dz);

            Entity ent = type.create(level);
            if (ent == null) continue;

            ent.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, level.random.nextFloat() * 360f, 0f);

            if (ent instanceof net.minecraft.world.entity.Mob mob) {
                mob.finalizeSpawn(level, level.getCurrentDifficultyAt(pos), MobSpawnType.MOB_SUMMONED, null, null);
            }

            level.addFreshEntity(ent);
        }
    }
}
