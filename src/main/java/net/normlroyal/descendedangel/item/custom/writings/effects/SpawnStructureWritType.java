package net.normlroyal.descendedangel.item.custom.writings.effects;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.util.RandomSource;
import net.normlroyal.descendedangel.config.ModConfigs;
import net.normlroyal.descendedangel.item.custom.writings.IWritType;

public class SpawnStructureWritType implements IWritType {

    @Override
    public void execute(JsonObject data, ServerLevel level, ServerPlayer player, ItemStack stack) {
        if (!ModConfigs.COMMON.ENABLE_SPAWN_STRUCTURE_WRITS.get()) return;

        String s = data.has("structure") ? data.get("structure").getAsString() : "";
        ResourceLocation id = ResourceLocation.tryParse(s);
        if (id == null) return;

        String mode = data.has("mode") ? safeLower(data.get("mode").getAsString()) : "template";
        if (mode.isBlank()) mode = "template";

        BlockPos base = player.blockPosition();
        BlockPos offset = readOffset(data);
        BlockPos pos = base.offset(offset);

        int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, pos.getX(), pos.getZ());
        pos = new BlockPos(pos.getX(), y, pos.getZ());

        if (mode.equals("command")) {
            placeViaCommand(level, player, id, pos);
            return;
        }

        // Template mode
        StructureTemplate template = level.getStructureManager().get(id).orElse(null);
        if (template == null) return;

        Rotation rot = parseRotationRandomDefault(data, level.random);
        Mirror mir = parseMirrorRandomDefault(data, level.random);

        boolean includeEntities = !data.has("include_entities") || data.get("include_entities").getAsBoolean();

        StructurePlaceSettings settings = new StructurePlaceSettings()
                .setRotation(rot)
                .setMirror(mir)
                .setIgnoreEntities(!includeEntities);

        template.placeInWorld(level, pos, pos, settings, level.random, 2);
    }

    // Place mode
    private static void placeViaCommand(ServerLevel level, ServerPlayer player, ResourceLocation structureId, BlockPos pos) {
        MinecraftServer server = level.getServer();
        if (server == null) return;

        var source = player.createCommandSourceStack()
                .withPermission(4)
                .withSuppressedOutput();

        String cmd = "place structure " + structureId + " " + pos.getX() + " " + pos.getY() + " " + pos.getZ();
        server.getCommands().performPrefixedCommand(source, cmd);
    }

    private static BlockPos readOffset(JsonObject data) {
        if (data.has("offset") && data.get("offset").isJsonArray()) {
            JsonArray a = data.getAsJsonArray("offset");
            int ox = a.size() > 0 ? a.get(0).getAsInt() : 0;
            int oy = a.size() > 1 ? a.get(1).getAsInt() : 0;
            int oz = a.size() > 2 ? a.get(2).getAsInt() : 0;
            return new BlockPos(ox, oy, oz);
        }
        return BlockPos.ZERO;
    }

    private static Rotation parseRotationRandomDefault(JsonObject data, RandomSource rand) {
        String r = data.has("rotation") ? safeLower(data.get("rotation").getAsString()) : "";
        if (r.isBlank() || r.equals("random")) return randomRotation(rand);

        return switch (r) {
            case "clockwise_90" -> Rotation.CLOCKWISE_90;
            case "clockwise_180" -> Rotation.CLOCKWISE_180;
            case "counterclockwise_90" -> Rotation.COUNTERCLOCKWISE_90;
            case "none" -> Rotation.NONE;
            default -> randomRotation(rand);
        };
    }

    private static Mirror parseMirrorRandomDefault(JsonObject data, RandomSource rand) {
        String m = data.has("mirror") ? safeLower(data.get("mirror").getAsString()) : "";
        if (m.isBlank() || m.equals("random")) return randomMirror(rand);

        return switch (m) {
            case "left_right" -> Mirror.LEFT_RIGHT;
            case "front_back" -> Mirror.FRONT_BACK;
            case "none" -> Mirror.NONE;
            default -> randomMirror(rand);
        };
    }

    private static Rotation randomRotation(RandomSource rand) {
        return switch (rand.nextInt(4)) {
            case 1 -> Rotation.CLOCKWISE_90;
            case 2 -> Rotation.CLOCKWISE_180;
            case 3 -> Rotation.COUNTERCLOCKWISE_90;
            default -> Rotation.NONE;
        };
    }

    private static Mirror randomMirror(RandomSource rand) {
        return switch (rand.nextInt(3)) {
            case 1 -> Mirror.LEFT_RIGHT;
            case 2 -> Mirror.FRONT_BACK;
            default -> Mirror.NONE;
        };
    }

    private static String safeLower(String s) {
        return s == null ? "" : s.trim().toLowerCase();
    }
}
