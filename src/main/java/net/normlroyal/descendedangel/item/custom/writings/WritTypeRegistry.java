package net.normlroyal.descendedangel.item.custom.writings;

import net.minecraft.resources.ResourceLocation;
import net.normlroyal.descendedangel.DescendedAngel;
import net.normlroyal.descendedangel.item.custom.writings.effects.PotionBoostWritType;
import net.normlroyal.descendedangel.item.custom.writings.effects.SpawnEntityWritType;
import net.normlroyal.descendedangel.item.custom.writings.effects.WeatherWritType;

import java.util.HashMap;
import java.util.Map;

public class WritTypeRegistry {
    private static final Map<ResourceLocation, IWritType> TYPES = new HashMap<>();

    private WritTypeRegistry() {}

    public static void registerDefaults() {
        register(ResourceLocation.fromNamespaceAndPath(DescendedAngel.MOD_ID, "weather"), new WeatherWritType());
        register(ResourceLocation.fromNamespaceAndPath(DescendedAngel.MOD_ID, "spawn_entity"), new SpawnEntityWritType());
        register(ResourceLocation.fromNamespaceAndPath(DescendedAngel.MOD_ID, "potion_boost"), new PotionBoostWritType());
        register(ResourceLocation.fromNamespaceAndPath(DescendedAngel.MOD_ID, "spawn_structure"), new SpawnEntityWritType());
    }

    public static void register(ResourceLocation id, IWritType effect) {
        TYPES.put(id, effect);
    }

    public static IWritType get(ResourceLocation typeId) {
        return TYPES.get(typeId);
    }
}

