package net.normlroyal.descendedangel.config;

import net.minecraft.world.level.GameRules;

public class ModGameRules {

    public static final GameRules.Key<GameRules.BooleanValue> HALO_HIERARCHY_GLOW =
            GameRules.register(
                    "haloHierarchyGlow",
                    GameRules.Category.PLAYER,
                    GameRules.BooleanValue.create(true)
            );

    public static void init() {
    }

    private ModGameRules() {}
}