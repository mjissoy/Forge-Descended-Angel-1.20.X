package net.normlroyal.descendedangel.content.dimension;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.network.NetworkHooks;
import net.normlroyal.descendedangel.content.block.ModBlocks;
import net.normlroyal.descendedangel.content.block.void_decorations.VoidVineBlock;
import net.normlroyal.descendedangel.content.block.void_decorations.VoidVinePlantBlock;
import net.normlroyal.descendedangel.content.entity.ModEntities;
import net.normlroyal.descendedangel.content.entity.voidanomaly.VoidAnomaly;
import net.normlroyal.descendedangel.content.item.ModItems;
import net.normlroyal.descendedangel.menu.AnchorWaypointMenu;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class VoidPocketManager {
    private static final int ANOMALY_CAP = 7;
    private static final int SPAWN_PADDING = 3;

    private VoidPocketManager() {
    }

    public static boolean isVoidPocket(Level level) {
        return level.dimension().equals(ModDimensions.VOID_POCKET_LEVEL);
    }

    public static void enterNewPocket(ServerPlayer player) {
        MinecraftServer server = player.getServer();
        if (server == null) {
            return;
        }

        ServerLevel voidLevel = server.getLevel(ModDimensions.VOID_POCKET_LEVEL);
        if (voidLevel == null) {
            player.displayClientMessage(Component.literal("The void pocket dimension is missing.").withStyle(ChatFormatting.RED), true);
            return;
        }

        VoidPocketData data = VoidPocketData.get(server);
        Optional<VoidPocketData.Anchor> nearestAnchor = data.findNearestAnchor(
                player.level().dimension(),
                player.blockPosition(),
                8,
                player.getUUID()
        );

        VoidPocketData.Pocket pocket = data.createPocket(player, nearestAnchor.orElse(null));
        generateChamber(voidLevel, pocket);
        data.setDirty();

        teleportToPocket(player, voidLevel, pocket);
        player.displayClientMessage(Component.literal("The Void Heart opens a pocket of nothing.").withStyle(ChatFormatting.DARK_PURPLE), true);
    }

    public static void enterExistingPocket(ServerPlayer player, VoidPocketData.Pocket pocket) {
        MinecraftServer server = player.getServer();
        if (server == null) {
            return;
        }

        ServerLevel voidLevel = server.getLevel(ModDimensions.VOID_POCKET_LEVEL);
        if (voidLevel == null) {
            player.displayClientMessage(Component.literal("The void pocket dimension is missing.").withStyle(ChatFormatting.RED), true);
            return;
        }

        ensureChamber(voidLevel, pocket);
        teleportToPocket(player, voidLevel, pocket);
        player.displayClientMessage(Component.literal("The anchor pulls you into its preserved void.").withStyle(ChatFormatting.LIGHT_PURPLE), true);
    }

    public static void useAnchor(ServerPlayer player, BlockPos anchorPos) {
        MinecraftServer server = player.getServer();
        if (server == null) {
            return;
        }

        VoidPocketData data = VoidPocketData.get(server);
        VoidPocketData.Anchor sourceAnchor = data.registerAnchor(player.level().dimension(), anchorPos, player.getUUID());
        if (sourceAnchor.owner != null && !sourceAnchor.owner.equals(player.getUUID())) {
            player.displayClientMessage(Component.literal("This Angelic Anchor does not answer to you.").withStyle(ChatFormatting.GRAY), true);
            return;
        }

        if (isVoidPocket(player.level())) {
            preservePocketAtAnchor(player, data, anchorPos, sourceAnchor);
        }

        openAnchorMenu(player, anchorPos);
    }

    public static void openAnchorMenu(ServerPlayer player, BlockPos sourcePos) {
        MinecraftServer server = player.getServer();
        if (server == null) {
            return;
        }

        List<AnchorWaypointMenu.Entry> entries = buildWaypointEntries(player, sourcePos);
        Component title = Component.translatable("container.descendedangel.angelic_anchor");

        NetworkHooks.openScreen(
                player,
                new SimpleMenuProvider(
                        (id, inventory, menuPlayer) -> new AnchorWaypointMenu(id, inventory, sourcePos, entries),
                        title
                ),
                buf -> {
                    buf.writeBlockPos(sourcePos);
                    AnchorWaypointMenu.writeEntries(buf, entries);
                }
        );
    }

    public static List<AnchorWaypointMenu.Entry> buildWaypointEntries(ServerPlayer player, BlockPos sourcePos) {
        MinecraftServer server = player.getServer();
        if (server == null) {
            return List.of();
        }

        VoidPocketData data = VoidPocketData.get(server);
        ResourceKey<Level> sourceDimension = player.level().dimension();
        List<AnchorWaypointMenu.Entry> entries = new ArrayList<>();

        for (VoidPocketData.Anchor anchor : data.anchorsFor(player.getUUID())) {
            if (data.isSameAnchor(anchor, sourceDimension, sourcePos)) {
                continue;
            }
            if (!isAnchorDestinationValid(server, data, anchor)) {
                continue;
            }

            AnchorTeleportCost cost = calculateTeleportCost(sourceDimension, sourcePos, anchor.dimension, anchor.pos);
            entries.add(new AnchorWaypointMenu.Entry(
                    anchor.dimension,
                    anchor.pos,
                    anchor.displayName(),
                    dimensionLabel(anchor.dimension),
                    distanceLabel(sourceDimension, sourcePos, anchor.dimension, anchor.pos),
                    anchor.dimension.equals(ModDimensions.VOID_POCKET_LEVEL),
                    cost
            ));
        }

        return entries;
    }

    public static void teleportBetweenAnchors(ServerPlayer player, BlockPos sourcePos, ResourceKey<Level> targetDimension, BlockPos targetPos) {
        MinecraftServer server = player.getServer();
        if (server == null) {
            return;
        }

        VoidPocketData data = VoidPocketData.get(server);
        Optional<VoidPocketData.Anchor> sourceAnchorOptional = data.getAnchor(player.level().dimension(), sourcePos);
        Optional<VoidPocketData.Anchor> targetAnchorOptional = data.getAnchor(targetDimension, targetPos);

        if (sourceAnchorOptional.isEmpty()) {
            player.displayClientMessage(Component.literal("The anchor beneath this menu has gone silent.").withStyle(ChatFormatting.GRAY), true);
            return;
        }

        VoidPocketData.Anchor sourceAnchor = sourceAnchorOptional.get();
        if (sourceAnchor.owner != null && !sourceAnchor.owner.equals(player.getUUID())) {
            player.displayClientMessage(Component.literal("This Angelic Anchor does not answer to you.").withStyle(ChatFormatting.GRAY), true);
            return;
        }

        if (targetAnchorOptional.isEmpty()) {
            player.displayClientMessage(Component.literal("That waypoint has faded.").withStyle(ChatFormatting.GRAY), true);
            return;
        }

        VoidPocketData.Anchor targetAnchor = targetAnchorOptional.get();
        if (targetAnchor.owner != null && !targetAnchor.owner.equals(player.getUUID())) {
            player.displayClientMessage(Component.literal("That waypoint is not yours.").withStyle(ChatFormatting.GRAY), true);
            return;
        }

        if (!isAnchorDestinationValid(server, data, targetAnchor)) {
            player.displayClientMessage(Component.literal("That waypoint is unstable.").withStyle(ChatFormatting.GRAY), true);
            return;
        }

        AnchorTeleportCost cost = calculateTeleportCost(player.level().dimension(), sourcePos, targetDimension, targetPos);
        if (!cost.canAfford(player)) {
            player.displayClientMessage(Component.literal("The anchor requires " + cost.label() + ".").withStyle(ChatFormatting.RED), true);
            return;
        }

        ServerLevel targetLevel = server.getLevel(targetDimension);
        if (targetLevel == null) {
            player.displayClientMessage(Component.literal("That destination dimension is missing.").withStyle(ChatFormatting.RED), true);
            return;
        }

        if (targetDimension.equals(ModDimensions.VOID_POCKET_LEVEL)) {
            data.findPocketAt(targetPos).ifPresent(pocket -> ensureChamber(targetLevel, pocket));
        }

        cost.consume(player);
        BlockPos arrival = findArrivalPos(targetLevel, targetPos);
        player.teleportTo(targetLevel, arrival.getX() + 0.5D, arrival.getY(), arrival.getZ() + 0.5D, player.getYRot(), player.getXRot());
        player.fallDistance = 0.0F;
        player.displayClientMessage(Component.literal("The Angelic Anchor carries you through the waypoint lattice.").withStyle(ChatFormatting.AQUA), true);
    }

    public static void registerAnchor(Level level, BlockPos pos, Entity placer) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        MinecraftServer server = serverLevel.getServer();
        VoidPocketData data = VoidPocketData.get(server);
        VoidPocketData.Anchor anchor = data.registerAnchor(
                level.dimension(),
                pos,
                placer instanceof ServerPlayer player ? player.getUUID() : null
        );

        if (isVoidPocket(level)) {
            data.findPocketAt(pos).ifPresent(pocket -> {
                pocket.preserved = true;
                anchor.pocketId = pocket.id;
                data.linkReturnAnchor(pocket);
                data.setDirty();
            });
        }
    }

    public static void removeAnchor(Level level, BlockPos pos) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }
        VoidPocketData.get(serverLevel.getServer()).removeAnchor(level.dimension(), pos);
    }

    public static void preserveAndExit(ServerPlayer player, BlockPos anchorPos) {
        MinecraftServer server = player.getServer();
        if (server == null) {
            return;
        }

        VoidPocketData data = VoidPocketData.get(server);
        Optional<VoidPocketData.Pocket> pocketOptional = data.findPocketAt(anchorPos);
        if (pocketOptional.isEmpty()) {
            pocketOptional = data.findPocketAt(player.blockPosition());
        }

        if (pocketOptional.isEmpty()) {
            emergencyExit(player, Component.literal("The anchor finds no pocket to preserve.").withStyle(ChatFormatting.GRAY));
            return;
        }

        VoidPocketData.Pocket pocket = pocketOptional.get();
        VoidPocketData.Anchor anchor = data.registerAnchor(player.level().dimension(), anchorPos, player.getUUID());
        anchor.pocketId = pocket.id;
        pocket.preserved = true;
        data.linkReturnAnchor(pocket);
        data.setDirty();

        exitPocket(player, pocket, Component.literal("The Angelic Anchor pins the void in place.").withStyle(ChatFormatting.AQUA));
    }

    public static void exitWithVoidHeart(ServerPlayer player) {
        MinecraftServer server = player.getServer();
        if (server == null) {
            return;
        }

        VoidPocketData data = VoidPocketData.get(server);
        Optional<VoidPocketData.Pocket> pocket = data.findPocketAt(player.blockPosition());
        if (pocket.isPresent()) {
            exitPocket(player, pocket.get(), Component.literal("The Void Heart tears open a way home.").withStyle(ChatFormatting.DARK_PURPLE));
        } else {
            emergencyExit(player, Component.literal("The Void Heart rejects this broken pocket.").withStyle(ChatFormatting.DARK_PURPLE));
        }
    }

    public static void ejectPlayer(ServerPlayer player, Component message) {
        MinecraftServer server = player.getServer();
        if (server == null) {
            return;
        }

        VoidPocketData data = VoidPocketData.get(server);
        Optional<VoidPocketData.Pocket> pocket = data.findPocketAt(player.blockPosition());
        if (pocket.isPresent()) {
            exitPocket(player, pocket.get(), message);
        } else {
            emergencyExit(player, message);
        }
    }

    public static void recordAnomalyKill(ServerLevel level, BlockPos pos, ServerPlayer player) {
        recordAnomalyKill(level, pos, player, 1);
    }

    public static void recordAnomalyKill(ServerLevel level, BlockPos pos, ServerPlayer player, int killValue) {
        if (!isVoidPocket(level)) {
            return;
        }

        MinecraftServer server = level.getServer();
        VoidPocketData data = VoidPocketData.get(server);
        Optional<VoidPocketData.Pocket> pocketOptional = data.findPocketAt(pos);
        if (pocketOptional.isEmpty()) {
            return;
        }

        VoidPocketData.Pocket pocket = pocketOptional.get();
        if (pocket.completed) {
            return;
        }

        pocket.kills += Math.max(1, killValue);
        data.setDirty();

        int remaining = Math.max(0, pocket.requiredKills - pocket.kills);
        if (remaining > 0) {
            player.displayClientMessage(Component.literal("Void anomalies remaining: " + remaining).withStyle(ChatFormatting.DARK_PURPLE), true);
            return;
        }

        pocket.completed = true;
        ItemStack link = new ItemStack(ModItems.VOID_HEART_LINK.get());
        if (!player.getInventory().add(link)) {
            player.drop(link, false);
        }
        data.setDirty();
        player.displayClientMessage(Component.literal("The pocket ruptures. A Void Heart Link forms in your grasp.").withStyle(ChatFormatting.LIGHT_PURPLE), true);
    }

    public static void tickPlayer(ServerPlayer player) {
        if (!isVoidPocket(player.level())) {
            return;
        }

        MinecraftServer server = player.getServer();
        if (server == null || !(player.level() instanceof ServerLevel level)) {
            return;
        }

        VoidPocketData data = VoidPocketData.get(server);
        Optional<VoidPocketData.Pocket> pocketOptional = data.findPocketAt(player.blockPosition());
        if (pocketOptional.isEmpty()) {
            emergencyExit(player, Component.literal("The unstable void spits you out.").withStyle(ChatFormatting.DARK_PURPLE));
            return;
        }

        VoidPocketData.Pocket pocket = pocketOptional.get();
        if (player.getY() < pocket.center.getY() - VoidPocketData.HALF_HEIGHT - 4) {
            ejectPlayer(player, Component.literal("You fall through the pocket's seam and wake outside it.").withStyle(ChatFormatting.DARK_PURPLE));
            return;
        }

        if (!pocket.bounds().inflate(2.0D).contains(player.position())) {
            BlockPos entry = pocket.entryPos();
            player.teleportTo(level, entry.getX() + 0.5D, entry.getY(), entry.getZ() + 0.5D, player.getYRot(), player.getXRot());
            player.fallDistance = 0.0F;
            return;
        }

        int spawnInterval = pocket.completed ? 120 : 80;

        if (player.tickCount % spawnInterval == 0) {
            spawnAnomalyIfNeeded(level, pocket);
        }
    }

    private static void preservePocketAtAnchor(ServerPlayer player, VoidPocketData data, BlockPos anchorPos, VoidPocketData.Anchor anchor) {
        Optional<VoidPocketData.Pocket> pocketOptional = data.findPocketAt(anchorPos);
        if (pocketOptional.isEmpty()) {
            pocketOptional = data.findPocketAt(player.blockPosition());
        }

        if (pocketOptional.isEmpty()) {
            player.displayClientMessage(Component.literal("The anchor finds no pocket to preserve.").withStyle(ChatFormatting.GRAY), true);
            return;
        }

        VoidPocketData.Pocket pocket = pocketOptional.get();
        anchor.pocketId = pocket.id;
        pocket.preserved = true;
        data.linkReturnAnchor(pocket);
        data.setDirty();
    }

    private static boolean isAnchorDestinationValid(MinecraftServer server, VoidPocketData data, VoidPocketData.Anchor anchor) {
        if (!anchor.dimension.equals(ModDimensions.VOID_POCKET_LEVEL)) {
            return true;
        }

        Optional<VoidPocketData.Pocket> pocket = data.findPocketAt(anchor.pos);
        return pocket.isPresent() && pocket.get().preserved;
    }

    private static AnchorTeleportCost calculateTeleportCost(
            ResourceKey<Level> sourceDimension,
            BlockPos sourcePos,
            ResourceKey<Level> targetDimension,
            BlockPos targetPos
    ) {
        boolean crossDimension = !sourceDimension.equals(targetDimension);
        boolean sourceIsVoidPocket = sourceDimension.equals(ModDimensions.VOID_POCKET_LEVEL);
        boolean targetIsVoidPocket = targetDimension.equals(ModDimensions.VOID_POCKET_LEVEL);
        boolean touchesVoidPocket = sourceIsVoidPocket || targetIsVoidPocket;

        double distance = crossDimension ? 0.0D : Math.sqrt(sourcePos.distSqr(targetPos));

        int voidTears;
        int compressedVoid;
        int voidMatrix;

        if (crossDimension) {
            voidTears = 6;
            compressedVoid = 1;
            voidMatrix = 0;

            if (touchesVoidPocket) {
                voidTears = 4;
                compressedVoid = 0;
                voidMatrix = 0;
            }
        } else {
            voidTears = 1 + Mth.ceil(distance / 768.0D);
            compressedVoid = distance >= 2048.0D ? Mth.ceil((distance - 2048.0D) / 3072.0D) : 0;
            voidMatrix = distance >= 12000.0D ? Math.max(1, Mth.floor(distance / 12000.0D)) : 0;
        }

        return new AnchorTeleportCost(voidTears, compressedVoid, voidMatrix);
    }

    private static String distanceLabel(ResourceKey<Level> sourceDimension, BlockPos sourcePos, ResourceKey<Level> targetDimension, BlockPos targetPos) {
        if (!sourceDimension.equals(targetDimension)) {
            return "Cross-dimensional";
        }
        int distance = Mth.floor(Math.sqrt(sourcePos.distSqr(targetPos)));
        return distance + " blocks away";
    }

    private static String dimensionLabel(ResourceKey<Level> dimension) {
        String path = dimension.location().getPath();
        return switch (path) {
            case "overworld" -> "Overworld";
            case "the_nether" -> "Nether";
            case "the_end" -> "End";
            case "void_pocket" -> "Void Pocket";
            default -> path.replace('_', ' ');
        };
    }

    private static BlockPos findArrivalPos(ServerLevel level, BlockPos anchorPos) {
        BlockPos candidate = anchorPos.above();
        for (int i = 0; i < 6; i++) {
            if (level.getBlockState(candidate).getCollisionShape(level, candidate).isEmpty()
                    && level.getBlockState(candidate.above()).getCollisionShape(level, candidate.above()).isEmpty()) {
                return candidate;
            }
            candidate = candidate.above();
        }
        return anchorPos.above();
    }

    private static void exitPocket(ServerPlayer player, VoidPocketData.Pocket pocket, Component message) {
        MinecraftServer server = player.getServer();
        if (server == null) {
            return;
        }

        ServerLevel returnLevel = server.getLevel(pocket.returnDimension);
        if (returnLevel == null) {
            returnLevel = server.overworld();
        }

        player.teleportTo(returnLevel, pocket.returnX, pocket.returnY, pocket.returnZ, pocket.returnYaw, pocket.returnPitch);
        player.fallDistance = 0.0F;
        player.setHealth(Math.max(1.0F, player.getHealth()));
        player.displayClientMessage(message, true);

        if (!pocket.preserved) {
            ServerLevel voidLevel = server.getLevel(ModDimensions.VOID_POCKET_LEVEL);
            if (voidLevel != null) {
                clearChamber(voidLevel, pocket);
            }
            VoidPocketData.get(server).removePocket(pocket.id);
        }
    }

    private static void emergencyExit(ServerPlayer player, Component message) {
        MinecraftServer server = player.getServer();
        if (server == null) {
            return;
        }

        BlockPos respawn = player.getRespawnPosition();
        ServerLevel targetLevel = respawn != null && player.getRespawnDimension() != null
                ? server.getLevel(player.getRespawnDimension())
                : server.overworld();

        if (targetLevel == null) {
            targetLevel = server.overworld();
        }

        BlockPos target = respawn != null ? respawn : targetLevel.getSharedSpawnPos();
        player.teleportTo(targetLevel, target.getX() + 0.5D, target.getY() + 1.0D, target.getZ() + 0.5D, player.getYRot(), player.getXRot());
        player.fallDistance = 0.0F;
        player.setHealth(Math.max(1.0F, player.getHealth()));
        player.displayClientMessage(message, true);
    }

    private static void teleportToPocket(ServerPlayer player, ServerLevel voidLevel, VoidPocketData.Pocket pocket) {
        BlockPos entry = pocket.entryPos();
        player.teleportTo(voidLevel, entry.getX() + 0.5D, entry.getY(), entry.getZ() + 0.5D, player.getYRot(), player.getXRot());
        player.fallDistance = 0.0F;
    }

    private static void ensureChamber(ServerLevel level, VoidPocketData.Pocket pocket) {
        if (!pocket.generated) {
            generateChamber(level, pocket);
            return;
        }

        restorePocketAnchors(level, pocket);
    }

    private static void generateChamber(ServerLevel level, VoidPocketData.Pocket pocket) {
        BlockState voidBlock = ModBlocks.VOID_CAVE_BLOCK.get().defaultBlockState();
        BlockState air = Blocks.AIR.defaultBlockState();

        VoidPocketData data = VoidPocketData.get(level.getServer());

        java.util.HashSet<BlockPos> protectedAnchors = new java.util.HashSet<>();
        for (VoidPocketData.Anchor anchor : data.anchorsInPocket(pocket)) {
            protectedAnchors.add(anchor.pos);
        }

        clearPocketVolume(level, pocket, protectedAnchors);

        long seed = pocket.id.getMostSignificantBits() ^ pocket.id.getLeastSignificantBits();
        java.util.Random random = new java.util.Random(seed);

        buildMainVoidIsland(level, pocket, voidBlock, protectedAnchors, random);
        buildSideIslands(level, pocket, voidBlock, protectedAnchors, random);
        buildEntryPlatform(level, pocket, voidBlock, air, protectedAnchors);
        addAshenRuinScars(level, pocket, protectedAnchors, random);
        carveSurfaceDepressions(level, pocket, air, protectedAnchors, random);

        decorateVoidPocketFlora(level, pocket, protectedAnchors, random);

        restorePocketAnchors(level, pocket);

        pocket.generated = true;
        data.setDirty();
    }

    private static void clearPocketVolume(
            ServerLevel level,
            VoidPocketData.Pocket pocket,
            java.util.Set<BlockPos> protectedAnchors
    ) {
        BlockState air = Blocks.AIR.defaultBlockState();
        AABB bounds = pocket.bounds();
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

        for (int x = Mth.floor(bounds.minX); x < Mth.floor(bounds.maxX); x++) {
            for (int y = Mth.floor(bounds.minY); y < Mth.floor(bounds.maxY); y++) {
                for (int z = Mth.floor(bounds.minZ); z < Mth.floor(bounds.maxZ); z++) {
                    mutable.set(x, y, z);
                    if (protectedAnchors.contains(mutable) || level.getBlockState(mutable).is(ModBlocks.ANGELIC_ANCHOR.get())) {
                        continue;
                    }
                    level.setBlock(mutable, air, 3);
                }
            }
        }
    }

    private static void buildMainVoidIsland(
            ServerLevel level,
            VoidPocketData.Pocket pocket,
            BlockState block,
            java.util.Set<BlockPos> protectedAnchors,
            java.util.Random random
    ) {
        BlockPos center = pocket.center;

        placeIslandBlob(level, center.offset(0, 0, 0), 18, 7, 14, block, protectedAnchors, random);
        placeIslandBlob(level, center.offset(7, -1, 3), 10, 5, 8, block, protectedAnchors, random);
        placeIslandBlob(level, center.offset(-8, -1, -4), 9, 5, 7, block, protectedAnchors, random);
        placeIslandBlob(level, center.offset(2, 1, -8), 8, 4, 6, block, protectedAnchors, random);

        for (int i = 0; i < 18; i++) {
            int x = center.getX() + random.nextInt(29) - 14;
            int z = center.getZ() + random.nextInt(25) - 12;
            int length = 2 + random.nextInt(5);

            for (int y = center.getY() - 5; y > center.getY() - 5 - length; y--) {
                BlockPos pos = new BlockPos(x, y, z);
                if (!protectedAnchors.contains(pos)) {
                    level.setBlock(pos, ModBlocks.VOID_CAVE_BLOCK.get().defaultBlockState(), 3);
                }
            }
        }
    }

    private static void buildSideIslands(
            ServerLevel level,
            VoidPocketData.Pocket pocket,
            BlockState block,
            java.util.Set<BlockPos> protectedAnchors,
            java.util.Random random
    ) {
        int count = 1 + random.nextInt(3);

        for (int i = 0; i < count; i++) {
            double angle = random.nextDouble() * Math.PI * 2.0D;
            int distance = 20 + random.nextInt(8);

            int x = pocket.center.getX() + Mth.floor(Math.cos(angle) * distance);
            int z = pocket.center.getZ() + Mth.floor(Math.sin(angle) * distance);
            int y = pocket.center.getY() + random.nextInt(7) - 3;

            BlockPos sideCenter = new BlockPos(x, y, z);

            placeIslandBlob(
                    level,
                    sideCenter,
                    5 + random.nextInt(4),
                    3 + random.nextInt(2),
                    5 + random.nextInt(4),
                    block,
                    protectedAnchors,
                    random
            );
        }
    }

    private static void buildEntryPlatform(
            ServerLevel level,
            VoidPocketData.Pocket pocket,
            BlockState block,
            BlockState air,
            java.util.Set<BlockPos> protectedAnchors
    ) {
        BlockPos entry = pocket.entryPos();
        BlockPos floorCenter = entry.below();

        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

        for (int x = -3; x <= 3; x++) {
            for (int z = -3; z <= 3; z++) {
                if (x * x + z * z <= 10) {
                    BlockPos floor = floorCenter.offset(x, 0, z);
                    mutable.set(floor);

                    if (!protectedAnchors.contains(mutable) && !level.getBlockState(mutable).is(ModBlocks.ANGELIC_ANCHOR.get())) {
                        BlockState platformBlock = Math.abs(x) <= 1 && Math.abs(z) <= 1
                                ? ModBlocks.VOID_WALL_BRICKS.get().defaultBlockState()
                                : ModBlocks.VOID_CAVE_BLOCK.get().defaultBlockState();

                        level.setBlock(mutable, platformBlock, 3);
                        level.setBlock(floor.above(), air, 3);
                        level.setBlock(floor.above(2), air, 3);
                    }
                }
            }
        }
    }

    private static void addAshenRuinScars(
            ServerLevel level,
            VoidPocketData.Pocket pocket,
            java.util.Set<BlockPos> protectedAnchors,
            java.util.Random random
    ) {
        int scarCount = 3 + random.nextInt(4);

        for (int i = 0; i < scarCount; i++) {
            BlockPos start = pocket.center.offset(
                    random.nextInt(25) - 12,
                    7,
                    random.nextInt(21) - 10
            );

            Direction direction = Direction.Plane.HORIZONTAL.getRandomDirection(level.random);
            int length = 4 + random.nextInt(8);

            BlockPos pos = start;
            for (int step = 0; step < length; step++) {
                BlockPos surface = findSurfaceBelow(level, pos, pocket);
                if (surface != null && !protectedAnchors.contains(surface)) {
                    BlockState scarBlock = random.nextBoolean()
                            ? ModBlocks.VOID_WALL_BRICKS.get().defaultBlockState()
                            : ModBlocks.SMOOTH_VOID_WALL.get().defaultBlockState();

                    level.setBlock(surface, scarBlock, 3);
                }

                pos = pos.relative(direction).offset(
                        random.nextInt(3) - 1,
                        0,
                        random.nextInt(3) - 1
                );
            }
        }
    }

    @Nullable
    private static BlockPos findSurfaceBelow(ServerLevel level, BlockPos start, VoidPocketData.Pocket pocket) {
        for (int y = start.getY(); y >= pocket.center.getY() - 8; y--) {
            BlockPos pos = new BlockPos(start.getX(), y, start.getZ());
            BlockPos above = pos.above();

            if (level.getBlockState(pos).is(ModBlocks.VOID_CAVE_BLOCK.get()) && level.isEmptyBlock(above)) {
                return pos;
            }
        }

        return null;
    }

    private static void carveSurfaceDepressions(
            ServerLevel level,
            VoidPocketData.Pocket pocket,
            BlockState air,
            java.util.Set<BlockPos> protectedAnchors,
            java.util.Random random
    ) {
        int count = 2 + random.nextInt(3);

        for (int i = 0; i < count; i++) {
            BlockPos center = pocket.center.offset(
                    random.nextInt(21) - 10,
                    4 + random.nextInt(2),
                    random.nextInt(17) - 8
            );

            int radius = 2 + random.nextInt(3);

            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    if (x * x + z * z > radius * radius) {
                        continue;
                    }

                    BlockPos pos = center.offset(x, 0, z);
                    if (!protectedAnchors.contains(pos) && !level.getBlockState(pos).is(ModBlocks.ANGELIC_ANCHOR.get())) {
                        level.setBlock(pos, air, 3);
                    }
                }
            }
        }
    }

    private static void placeIslandBlob(
            ServerLevel level,
            BlockPos center,
            int radiusX,
            int radiusY,
            int radiusZ,
            BlockState block,
            java.util.Set<BlockPos> protectedAnchors,
            java.util.Random random
    ) {
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

        for (int x = -radiusX; x <= radiusX; x++) {
            for (int y = -radiusY; y <= radiusY; y++) {
                for (int z = -radiusZ; z <= radiusZ; z++) {
                    double nx = x / (double) radiusX;
                    double ny = y / (double) radiusY;
                    double nz = z / (double) radiusZ;

                    double distance = nx * nx + ny * ny + nz * nz;

                    if (y > 2) {
                        distance += y * 0.08D;
                    }
                    if (y < -2) {
                        distance -= 0.10D;
                    }

                    double roughness = ((x * 734287 + y * 912931 + z * 438289) & 15) / 100.0D;

                    if (distance <= 1.0D - roughness) {
                        BlockPos pos = center.offset(x, y, z);
                        mutable.set(pos);
                        if (!protectedAnchors.contains(mutable) && !level.getBlockState(mutable).is(ModBlocks.ANGELIC_ANCHOR.get())) {
                            boolean surfaceLike = y >= -1;
                            BlockState chosenBlock = chooseIslandBlock(random, y, surfaceLike);
                            level.setBlock(mutable, chosenBlock, 3);
                        }
                    }
                }
            }
        }
    }

    private static void decorateVoidPocketFlora(
            ServerLevel level,
            VoidPocketData.Pocket pocket,
            java.util.Set<BlockPos> protectedAnchors,
            java.util.Random random
    ) {
        AABB bounds = pocket.bounds();
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

        for (int x = Mth.floor(bounds.minX) + 2; x < Mth.floor(bounds.maxX) - 2; x++) {
            for (int y = Mth.floor(bounds.minY) + 2; y < Mth.floor(bounds.maxY) - 2; y++) {
                for (int z = Mth.floor(bounds.minZ) + 2; z < Mth.floor(bounds.maxZ) - 2; z++) {
                    mutable.set(x, y, z);

                    if (protectedAnchors.contains(mutable)) {
                        continue;
                    }

                    BlockPos pos = mutable.immutable();

                    if (pos.distSqr(pocket.entryPos()) < 25.0D) {
                        continue;
                    }

                    if (isVoidSurface(level, pos.below()) && level.isEmptyBlock(pos)) {
                        if (random.nextFloat() < 0.045F) {
                            level.setBlock(pos, ModBlocks.VOID_GRASS.get().defaultBlockState(), 3);
                        } else if (random.nextFloat() < 0.008F) {
                            placeVoidVine(level, pos, Direction.UP, 1 + random.nextInt(4));
                        }
                    }

                    if (isVoidSurface(level, pos.above()) && level.isEmptyBlock(pos)) {
                        if (random.nextFloat() < 0.015F) {
                            placeVoidVine(level, pos, Direction.DOWN, 2 + random.nextInt(6));
                        }
                    }
                }
            }
        }
    }

    private static void restorePocketAnchors(ServerLevel level, VoidPocketData.Pocket pocket) {
        VoidPocketData data = VoidPocketData.get(level.getServer());
        for (VoidPocketData.Anchor anchor : data.anchorsInPocket(pocket)) {
            BlockState current = level.getBlockState(anchor.pos);
            if (!current.is(ModBlocks.ANGELIC_ANCHOR.get())) {
                level.setBlock(anchor.pos, ModBlocks.ANGELIC_ANCHOR.get().defaultBlockState(), 3);
            }
        }
    }

    private static void clearChamber(ServerLevel level, VoidPocketData.Pocket pocket) {
        BlockState air = Blocks.AIR.defaultBlockState();
        AABB bounds = pocket.bounds();
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

        for (int x = Mth.floor(bounds.minX); x < Mth.floor(bounds.maxX); x++) {
            for (int y = Mth.floor(bounds.minY); y < Mth.floor(bounds.maxY); y++) {
                for (int z = Mth.floor(bounds.minZ); z < Mth.floor(bounds.maxZ); z++) {
                    mutable.set(x, y, z);
                    level.setBlock(mutable, air, 3);
                }
            }
        }
    }

    private static void spawnAnomalyIfNeeded(ServerLevel level, VoidPocketData.Pocket pocket) {
        int existing = level.getEntitiesOfClass(
                Entity.class,
                pocket.bounds(),
                entity -> entity.isAlive() && entity instanceof VoidAnomaly
        ).size();

        int cap = pocket.completed ? Math.max(3, ANOMALY_CAP - 2) : ANOMALY_CAP;

        if (existing >= cap) {
            return;
        }

        Mob anomaly = chooseVoidPocketAnomaly(level).create(level);
        if (anomaly == null) {
            return;
        }

        Optional<BlockPos> spawnPos = findIslandSpawnPos(level, pocket);
        if (spawnPos.isEmpty()) {
            return;
        }

        BlockPos pos = spawnPos.get();
        anomaly.moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, level.random.nextFloat() * 360.0F, 0.0F);
        anomaly.setPersistenceRequired();
        level.addFreshEntity(anomaly);
    }

    private static Optional<BlockPos> findIslandSpawnPos(ServerLevel level, VoidPocketData.Pocket pocket) {
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

        for (int attempt = 0; attempt < 48; attempt++) {
            int range = VoidPocketData.HORIZONTAL_RADIUS - SPAWN_PADDING;
            int x = pocket.center.getX() + level.random.nextInt(range * 2 + 1) - range;
            int z = pocket.center.getZ() + level.random.nextInt(range * 2 + 1) - range;

            for (int y = pocket.center.getY() + 12; y >= pocket.center.getY() - 14; y--) {
                mutable.set(x, y, z);

                BlockPos floor = mutable.below();
                boolean solidFloor = !level.getBlockState(floor).getCollisionShape(level, floor).isEmpty();
                boolean feetClear = level.getBlockState(mutable).getCollisionShape(level, mutable).isEmpty();
                boolean headClear = level.getBlockState(mutable.above()).getCollisionShape(level, mutable.above()).isEmpty();

                if (solidFloor && feetClear && headClear) {
                    if (mutable.distSqr(pocket.entryPos()) < 64.0D) {
                        continue;
                    }

                    return Optional.of(mutable.immutable());
                }
            }
        }

        return Optional.empty();
    }

    private static BlockState chooseIslandBlock(java.util.Random random, int localY, boolean surfaceLike) {
        if (surfaceLike) {
            int roll = random.nextInt(100);

            if (roll < 82) {
                return ModBlocks.VOID_CAVE_BLOCK.get().defaultBlockState();
            }
            if (roll < 93) {
                return ModBlocks.VOID_WALL_BRICKS.get().defaultBlockState();
            }
            return ModBlocks.SMOOTH_VOID_WALL.get().defaultBlockState();
        }

        int roll = random.nextInt(100);

        if (roll < 92) {
            return ModBlocks.VOID_CAVE_BLOCK.get().defaultBlockState();
        }
        if (roll < 97) {
            return ModBlocks.VOID_WALL_BRICKS.get().defaultBlockState();
        }
        return ModBlocks.SMOOTH_VOID_WALL.get().defaultBlockState();
    }

    private static EntityType<? extends Mob> chooseVoidPocketAnomaly(ServerLevel level) {
        int roll = level.random.nextInt(100);
        if (roll < 60) {
            return ModEntities.VOID_ANOMALY.get();
        }
        if (roll < 82) {
            return ModEntities.VOID_SKELETON_ANOMALY.get();
        }
        return ModEntities.VOID_SLIME_ANOMALY.get();
    }

    private static boolean isVoidSurface(ServerLevel level, BlockPos pos) {
        return level.getBlockState(pos).is(ModBlocks.VOID_CAVE_BLOCK.get());
    }

    private static void placeVoidVine(ServerLevel level, BlockPos startPos, Direction growthDirection, int length) {
        if (growthDirection != Direction.UP && growthDirection != Direction.DOWN) {
            return;
        }

        length = Mth.clamp(length, 1, 12);

        for (int i = 0; i < length; i++) {
            BlockPos pos = startPos.relative(growthDirection, i);

            if (!level.isEmptyBlock(pos)) {
                return;
            }

            boolean isHead = i == length - 1;

            if (isHead) {
                level.setBlock(
                        pos,
                        ModBlocks.VOID_VINE.get()
                                .defaultBlockState()
                                .setValue(VoidVineBlock.GROWTH_DIRECTION, growthDirection),
                        3
                );
            } else {
                level.setBlock(
                        pos,
                        ModBlocks.VOID_VINE_PLANT.get()
                                .defaultBlockState()
                                .setValue(VoidVinePlantBlock.GROWTH_DIRECTION, growthDirection),
                        3
                );
            }
        }
    }
}
