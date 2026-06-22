package net.normlroyal.descendedangel.haloabilities;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.normlroyal.descendedangel.DescendedAngel;
import net.normlroyal.descendedangel.haloabilities.helpers.ClientUnlockState;
import net.normlroyal.descendedangel.util.HaloUtils;

public enum HaloAbility {
    FIREBALL("fireball", PowerAbilities.TAG_FIRE, AbilityGroup.POWER, 4),

    SACRED_FLARE("fireball", PowerAbilities.TAG_FIRE_SACRED_FLARE, AbilityGroup.POWER, 7),
    SOL_CORONA("fireball", PowerAbilities.TAG_FIRE_SOL_CORONA, AbilityGroup.POWER, 7),
    PILLARS_OF_RADIANCE("fireball", PowerAbilities.TAG_FIRE_PILLARS_OF_RADIANCE, AbilityGroup.POWER, 7),

    GUST("gust", PowerAbilities.TAG_AIR, AbilityGroup.POWER, 4),

    VACUUM_VORTEX("gust", PowerAbilities.TAG_AIR_VACUUM_VORTEX, AbilityGroup.POWER, 7),
    ZEPHYR_SCYTHES("gust", PowerAbilities.TAG_AIR_ZEPHYR_SCYTHES, AbilityGroup.POWER, 7),
    HEAVENLY_DOWNDRAFT("gust", PowerAbilities.TAG_AIR_HEAVENLY_DOWNDRAFT, AbilityGroup.POWER, 7),

    EARTH_WALL("earth_wall", PowerAbilities.TAG_EARTH, AbilityGroup.POWER, 4),
    MIST_VEIL("mist_veil", PowerAbilities.TAG_WATER, AbilityGroup.POWER, 4),

    TELEPORT("teleport", DominionAbilities.TAG_SPACE, AbilityGroup.DOMINION, 6),
    SPACE_CHEST("space_chest", DominionAbilities.TAG_SPACE, AbilityGroup.DOMINION, 6),
    FIELD("slow_field", DominionAbilities.TAG_TIME, AbilityGroup.DOMINION, 6),
    ACCELERATE("accelerate", DominionAbilities.TAG_TIME, AbilityGroup.DOMINION, 6);

    private final ResourceLocation icon;
    private final String unlockTag;
    private final AbilityGroup group;
    private final int minTier;

    HaloAbility(String id, String unlockTag, AbilityGroup group, int minTier) {
        this.icon = new ResourceLocation(
                DescendedAngel.MOD_ID,
                "textures/gui/ability_icons/" + id + ".png"
        );
        this.unlockTag = unlockTag;
        this.group = group;
        this.minTier = minTier;
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
        if (tier < minTier) return false;

        return switch (group) {
            case POWER -> tier == 4 || tier >= 7;
            case DOMINION -> tier == 6 || tier >= 7;
        };
    }

    public boolean isUnlocked(Player player) {
        if (player == null) return false;

        if (this == FIREBALL && PowerAbilities.hasFireEvolution(player)) {
            return false;
        }

        if (this == GUST && PowerAbilities.hasAirEvolution(player)) {
            return false;
        }

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