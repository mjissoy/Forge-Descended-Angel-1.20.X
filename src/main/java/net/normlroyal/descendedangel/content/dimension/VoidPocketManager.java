package net.normlroyal.descendedangel.content.dimension;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.normlroyal.descendedangel.content.block.ModBlocks;
import net.normlroyal.descendedangel.content.entity.ModEntities;
import net.normlroyal.descendedangel.content.entity.VoidAnomalyEntity;
import net.normlroyal.descendedangel.content.item.ModItems;

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

        generateChamber(voidLevel, pocket);
        teleportToPocket(player, voidLevel, pocket);
        player.displayClientMessage(Component.literal("The anchor pulls you into its preserved void.").withStyle(ChatFormatting.LIGHT_PURPLE), true);
    }

    public static void useAnchor(ServerPlayer player, BlockPos anchorPos) {
        MinecraftServer server = player.getServer();
        if (server == null) {
            return;
        }

        VoidPocketData data = VoidPocketData.get(server);

        if (isVoidPocket(player.level())) {
            preserveAndExit(player, anchorPos);
            return;
        }

        data.getAnchor(player.level().dimension(), anchorPos).ifPresentOrElse(anchor -> {
            if (anchor.pocketId == null) {
                player.displayClientMessage(Component.literal("This Angelic Anchor is not linked to a void pocket yet.").withStyle(ChatFormatting.GRAY), true);
                return;
            }

            data.getPocket(anchor.pocketId).ifPresentOrElse(pocket -> {
                if (!pocket.preserved) {
                    player.displayClientMessage(Component.literal("The linked pocket has already collapsed.").withStyle(ChatFormatting.GRAY), true);
                    return;
                }
                enterExistingPocket(player, pocket);
            }, () -> player.displayClientMessage(Component.literal("The linked pocket has faded away.").withStyle(ChatFormatting.GRAY), true));
        }, () -> player.displayClientMessage(Component.literal("This anchor has not awakened yet.").withStyle(ChatFormatting.GRAY), true));
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

        pocket.kills++;
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

    private static void generateChamber(ServerLevel level, VoidPocketData.Pocket pocket) {
        BlockState shell = ModBlocks.VOID_CAVE_BLOCK.get().defaultBlockState();
        BlockState air = Blocks.AIR.defaultBlockState();

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
                    level.setBlock(mutable, boundary ? shell : air, 3);
                }
            }
        }

        BlockPos entry = pocket.entryPos();
        level.setBlock(entry, air, 3);
        level.setBlock(entry.above(), air, 3);
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
        int existing = level.getEntities(ModEntities.VOID_ANOMALY.get(), pocket.bounds(), Entity::isAlive).size();
        if (existing >= ANOMALY_CAP) {
            return;
        }

        VoidAnomalyEntity anomaly = ModEntities.VOID_ANOMALY.get().create(level);
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
}
