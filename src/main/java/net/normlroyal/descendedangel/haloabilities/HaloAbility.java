package net.normlroyal.descendedangel.haloabilities;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.normlroyal.descendedangel.DescendedAngel;
import net.normlroyal.descendedangel.haloabilities.helpers.ClientUnlockState;
import net.normlroyal.descendedangel.util.HaloUtils;

public enum HaloAbility {
    FIREBALL("fireball", PowerAbilities.TAG_FIRE, AbilityGroup.POWER),
    GUST("gust", PowerAbilities.TAG_AIR, AbilityGroup.POWER),
    EARTH_WALL("earth_wall", PowerAbilities.TAG_EARTH, AbilityGroup.POWER),
    MIST_VEIL("mist_veil", PowerAbilities.TAG_WATER, AbilityGroup.POWER),

    TELEPORT("teleport", DominionAbilities.TAG_SPACE, AbilityGroup.DOMINION),
    SPACE_CHEST("space_chest", DominionAbilities.TAG_SPACE, AbilityGroup.DOMINION),
    FIELD("slow_field", DominionAbilities.TAG_TIME, AbilityGroup.DOMINION),
    ACCELERATE("accelerate", DominionAbilities.TAG_TIME, AbilityGroup.DOMINION);

    private final ResourceLocation icon;
    private final String unlockTag;
    private final AbilityGroup group;

    HaloAbility(String id, String unlockTag, AbilityGroup group) {
        this.icon = new ResourceLocation(
                DescendedAngel.MOD_ID,
                "textures/gui/ability_icons/" + id + ".png"
        );
        this.unlockTag = unlockTag;
        this.group = group;
    }

    public ResourceLocation icon() {
        return icon;
    }

    public String unlockTag() {
        return unlockTag;
    }

    public AbilityGroup group() {
        return group;
    }

    public boolean allowedForTier(int tier) {
        return switch (group) {
            case POWER -> tier == 4 || tier >= 7;
            case DOMINION -> tier == 6 || tier >= 7;
        };
    }

    public boolean isUnlocked(Player player) {
        if (player == null) return false;

        if (player.level().isClientSide) {
            return ClientUnlockState.has(unlockTag);
        }

        return player.getPersistentData().getBoolean(unlockTag);
    }

    public boolean isVisibleFor(Player player) {
        if (player == null) return false;
        int tier = HaloUtils.getEquippedHaloTier(player);
        return allowedForTier(tier) && isUnlocked(player);
    }

    public enum AbilityGroup {
        POWER,
        DOMINION
    }

    public boolean canUse(ServerPlayer player) {
        return isVisibleFor(player);
    }
}