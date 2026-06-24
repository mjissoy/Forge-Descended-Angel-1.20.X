package net.normlroyal.descendedangel.content.dimension;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
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
import net.normlroyal.descendedangel.content.entity.ModEntities;
import net.normlroyal.descendedangel.content.entity.voidanomaly.VoidAnomaly;
import net.normlroyal.descendedangel.content.item.ModItems;
import net.normlroyal.descendedangel.menu.AnchorWaypointMenu;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class VoidPocketManager {
    private static final int ANOMALY_CAP = 5;
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

        if (!pocket.completed && player.tickCount % 80 == 0) {
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
        BlockState shell = ModBlocks.VOID_CAVE_BLOCK.get().defaultBlockState();
        BlockState air = Blocks.AIR.defaultBlockState();
        VoidPocketData data = VoidPocketData.get(level.getServer());
        java.util.HashSet<BlockPos> protectedAnchors = new java.util.HashSet<>();
        for (VoidPocketData.Anchor anchor : data.anchorsInPocket(pocket)) {
            protectedAnchors.add(anchor.pos);
        }

        int minX = pocket.center.getX() - VoidPocketData.HORIZONTAL_RADIUS;
        int maxX = pocket.center.getX() + VoidPocketData.HORIZONTAL_RADIUS;
        int minY = pocket.center.getY() - VoidPocketData.HALF_HEIGHT;
        int maxY = pocket.center.getY() + VoidPocketData.HALF_HEIGHT;
        int minZ = pocket.center.getZ() - VoidPocketData.HORIZONTAL_RADIUS;
        int maxZ = pocket.center.getZ() + VoidPocketData.HORIZONTAL_RADIUS;

        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    boolean boundary = x == minX || x == maxX || y == minY || y == maxY || z == minZ || z == maxZ;
                    mutable.set(x, y, z);
                    if (protectedAnchors.contains(mutable) || level.getBlockState(mutable).is(ModBlocks.ANGELIC_ANCHOR.get())) {
                        continue;
                    }
                    level.setBlock(mutable, boundary ? shell : air, 3);
                }
            }
        }

        BlockPos entry = pocket.entryPos();
        if (!protectedAnchors.contains(entry) && !level.getBlockState(entry).is(ModBlocks.ANGELIC_ANCHOR.get())) {
            level.setBlock(entry, air, 3);
        }
        if (!protectedAnchors.contains(entry.above()) && !level.getBlockState(entry.above()).is(ModBlocks.ANGELIC_ANCHOR.get())) {
            level.setBlock(entry.above(), air, 3);
        }

        restorePocketAnchors(level, pocket);
        pocket.generated = true;
        data.setDirty();
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
        if (existing >= ANOMALY_CAP) {
            return;
        }

        Mob anomaly = chooseVoidPocketAnomaly(level).create(level);
        if (anomaly == null) {
            return;
        }

        int range = VoidPocketData.HORIZONTAL_RADIUS - SPAWN_PADDING;
        int x = pocket.center.getX() + level.random.nextInt(range * 2 + 1) - range;
        int z = pocket.center.getZ() + level.random.nextInt(range * 2 + 1) - range;
        int y = pocket.center.getY() - VoidPocketData.HALF_HEIGHT + 1;

        anomaly.moveTo(x + 0.5D, y, z + 0.5D, level.random.nextFloat() * 360.0F, 0.0F);
        anomaly.setPersistenceRequired();
        level.addFreshEntity(anomaly);
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
}
