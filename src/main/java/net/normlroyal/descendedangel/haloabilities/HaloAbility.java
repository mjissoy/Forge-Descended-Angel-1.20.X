package net.normlroyal.descendedangel.haloabilities;

import net.minecraft.resources.ResourceLocation;
import net.normlroyal.descendedangel.DescendedAngel;

public enum HaloAbility {
    FIREBALL("fireball"),
    GUST("gust"),
    EARTH_WALL("earth_wall"),
    MIST_VEIL("mist_veil"),
    TELEPORT("teleport"),
    FIELD("slow_field"),
    SPACE_CHEST("space_chest"),
    ACCELERATE("accelerate");

    private final ResourceLocation icon;

    HaloAbility(String id) {
        this.icon = ResourceLocation.fromNamespaceAndPath(DescendedAngel.MOD_ID, "textures/gui/ability_icons/" + id + ".png");
    }

    public ResourceLocation icon() {
        return icon;
    }
}
