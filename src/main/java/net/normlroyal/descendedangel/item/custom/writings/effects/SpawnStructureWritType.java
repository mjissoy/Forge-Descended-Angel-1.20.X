package net.normlroyal.descendedangel.item.custom.writings.effects;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.normlroyal.descendedangel.DescendedAngel;
import net.normlroyal.descendedangel.config.ModConfigs;
import net.normlroyal.descendedangel.item.custom.writings.IWritType;

public class SpawnStructureWritType implements IWritType {

    private static final int AUTO_SURFACE_THRESHOLD = 6;

    @Override
    public void execute(JsonObject data, ServerLevel level, ServerPlayer player, ItemStack stack) {
        if (!ModConfigs.COMMON.ENABLE_SPAWN_STRUCTURE_WRITS.get()) return;

        String s = data.has("structure") ? data.get("structure").getAsString() : "";
        ResourceLocation id = ResourceLocation.tryParse(s);
        if (id == null) {
            player.displayClientMessage(Component.literal("Invalid structure id.").withStyle(ChatFormatting.RED), true);
            return;
        }

        String mode = data.has("mode") ? safeLower(data.get("mode").getAsString()) : "template";
        if (mode.isBlank()) mode = "template";

        BlockPos pos = resolvePlacement(data, level, player);

        if (mode.equals("command")) {
            placeViaCommand(level, player, id, pos);
        }

        // Template mode
        StructureTemplate template = level.getStructureManager().get(id).orElse(null);
        if (template == null) {
            DescendedAngel.LOGGER.info("[Writ] Template not found for id {}", id);
            return;
        }

        Rotation rot = parseRotationRandomDefault(data, level.random);
        Mirror mir = parseMirrorRandomDefault(data, level.random);

        boolean includeEntities = !data.has("include_entities") || data.get("include_entities").getAsBoolean();

        StructurePlaceSettings settings = new StructurePlaceSettings()
                .setRotation(rot)
                .setMirror(mir)
                .setIgnoreEntities(!includeEntities);

        boolean placed = template.placeInWorld(level, pos, pos, settings, level.random, 2);
        if (!placed) {
            player.displayClientMessage(Component.literal("Template failed to place: " + id).withStyle(ChatFormatting.RED), true);
        }
    }

    // Place mode
    private static boolean placeViaCommand(ServerLevel level, ServerPlayer player, ResourceLocation structureId, BlockPos pos) {
        MinecraftServer server = level.getServer();
        if (server == null) {
            player.displayClientMessage(Component.literal("Server was null.").withStyle(ChatFormatting.RED), true);
            return false;
        }

        CommandSourceStack source = player.createCommandSourceStack()
                .withPermission(4);

        String cmd = "place structure " + structureId + " " + pos.getX() + " " + pos.getY() + " " + pos.getZ();
        int result = server.getCommands().performPrefixedCommand(source, cmd);

        if (result <= 0) {
            player.displayClientMessage(Component.literal("Failed to place structure: " + structureId).withStyle(ChatFormatting.RED), true);
            return false;
        }

        return true;
    }

    // Placement Type
    private static BlockPos resolvePlacement(JsonObject data, ServerLevel level, ServerPlayer player) {
        String placement = data.has("placement") ? safeLower(data.get("placement").getAsString()) : "";
        if (placement.isBlank()) {
            String mode = data.has("mode") ? safeLower(data.get("mode").getAsString()) : "template";
            placement = mode.equals("command") ? "surface" : "relative";
        }

        BlockPos offset = readOffset(data);

        return switch (placement) {
            case "relative" -> resolveRelative(player, offset);
            case "surface" -> resolveSurface(level, player, offset);
            case "clicked_surface" -> resolveClickedSurface(data, level, player, offset);
            case "auto" -> resolveAuto(data, level, player, offset);
            default -> resolveRelative(player, offset);
        };
    }

    private static BlockPos resolveRelative(ServerPlayer player, BlockPos offset) {
        return player.blockPosition().offset(offset);
    }

    private static BlockPos resolveSurface(ServerLevel level, ServerPlayer player, BlockPos offset) {
        BlockPos base = player.blockPosition().offset(offset);
        int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, base.getX(), base.getZ());
        return new BlockPos(base.getX(), y, base.getZ());
    }



    private static BlockPos resolveAuto(JsonObject data, ServerLevel level, ServerPlayer player, BlockPos offset) {
        BlockPos playerPos = player.blockPosition();
        int surfaceY = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, playerPos.getX(), playerPos.getZ());

        int dy = Math.abs(playerPos.getY() - surfaceY);

        if (dy <= AUTO_SURFACE_THRESHOLD) {
            return resolveSurface(level, player, offset);
        }

        return resolveRelative(player, offset);
    }

    private static BlockPos resolveClickedSurface(JsonObject data, ServerLevel level, ServerPlayer player, BlockPos offset) {
        BlockHitResult hit = getPlayerBlockHit(level, player);

        if (hit.getType() != HitResult.Type.BLOCK) {
            return resolveFallbackPlacement(data, level, player, offset);
        }

        BlockPos clicked = hit.getBlockPos();
        Direction face = hit.getDirection();

        String faceMode = data.has("clicked_face_mode")
                ? safeLower(data.get("clicked_face_mode").getAsString())
                : "adjacent";

        BlockPos anchor;

        if (faceMode.equals("same_block")) {
            anchor = clicked;
        } else {
            anchor = clicked.relative(face);
        }

        return anchor.offset(offset);
    }

    private static BlockPos resolveFallbackPlacement(JsonObject data, ServerLevel level, ServerPlayer player, BlockPos offset) {
        String fallback = data.has("fallback_placement")
                ? safeLower(data.get("fallback_placement").getAsString())
                : "surface";

        return switch (fallback) {
            case "relative" -> resolveRelative(player, offset);
            case "surface" -> resolveSurface(level, player, offset);
            case "auto" -> resolveAuto(data, level, player, offset);
            default -> resolveSurface(level, player, offset);
        };
    }

    private static BlockHitResult getPlayerBlockHit(ServerLevel level, ServerPlayer player) {
        return level.clip(new ClipContext(
                player.getEyePosition(),
                player.getEyePosition().add(player.getLookAngle().scale(64.0)),
                ClipContext.Block.OUTLINE,
                ClipContext.Fluid.NONE,
                player
        ));
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

