package net.normlroyal.descendedangel.halohierarchy;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameRules;
import net.normlroyal.descendedangel.config.ModGameRules;
import net.normlroyal.descendedangel.network.ModNetwork;
import net.normlroyal.descendedangel.network.packets.HaloHierarchyGlowS2CPacket;

public class HaloHierarchyGlowSync {

    private static boolean getEnabled(GameRules rules) {
        var rule = rules.getRule(ModGameRules.HALO_HIERARCHY_GLOW);
        return rule == null || rule.get();
    }

    public static void syncToPlayer(ServerPlayer player) {
        boolean enabled = getEnabled(player.serverLevel().getGameRules());
        ModNetwork.sendToPlayer(new HaloHierarchyGlowS2CPacket(enabled), player);
    }

    public static void syncToAll(MinecraftServer server) {
        boolean enabled = getEnabled(server.overworld().getGameRules());

        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            ModNetwork.sendToPlayer(new HaloHierarchyGlowS2CPacket(enabled), player);
        }
    }

    private HaloHierarchyGlowSync() {}
}