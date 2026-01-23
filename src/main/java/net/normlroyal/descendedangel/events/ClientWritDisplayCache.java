package net.normlroyal.descendedangel.events;

import net.minecraft.resources.ResourceLocation;
import net.normlroyal.descendedangel.item.custom.writings.WritDisplay;

import java.util.HashMap;
import java.util.Map;

public final class ClientWritDisplayCache {
    private static final Map<ResourceLocation, WritDisplay> CACHE = new HashMap<>();

    private ClientWritDisplayCache() {}

    public static void setAll(Map<ResourceLocation, WritDisplay> data) {
        CACHE.clear();
        CACHE.putAll(data);
    }

    public static WritDisplay get(ResourceLocation id) {
        return CACHE.get(id);
    }
}
