package net.normlroyal.descendedangel.item.custom.writings;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.normlroyal.descendedangel.events.useful.ClientWritDisplayCache;
import org.slf4j.Logger;

import java.util.*;


public class SacredWritReloadListener extends SimpleJsonResourceReloadListener {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final String FOLDER = "sacred_writs";


    private static volatile Map<ResourceLocation, JsonObject> WRITS = Collections.emptyMap();

    public SacredWritReloadListener() {
        super(new Gson(), FOLDER);
    }

    private static volatile Map<ResourceLocation, WritDisplay> DISPLAYS = Collections.emptyMap();

    public static Map<ResourceLocation, WritDisplay> displays() {
        return DISPLAYS;
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> elements,
                         ResourceManager resourceManager,
                         ProfilerFiller profiler) {

        Map<ResourceLocation, JsonObject> next = new HashMap<>();

        int ok = 0;
        int bad = 0;

        for (Map.Entry<ResourceLocation, JsonElement> e : elements.entrySet()) {
            ResourceLocation id = e.getKey();

            try {
                if (!e.getValue().isJsonObject()) {
                    bad++;
                    LOGGER.warn("[SacredWrits] Skipping {} because it is not a JSON object.", id);
                    continue;
                }

                JsonObject obj = e.getValue().getAsJsonObject();

                if (!obj.has("type") || !obj.get("type").isJsonPrimitive()) {
                    bad++;
                    LOGGER.warn("[SacredWrits] Skipping {} because it has no valid 'type' field.", id);
                    continue;
                }

                next.put(id, obj);
                ok++;
            } catch (Exception ex) {
                bad++;
                LOGGER.warn("[SacredWrits] Failed to load {}: {}", id, ex.toString());
            }
        }

        WRITS = Collections.unmodifiableMap(next);

        LOGGER.info("[SacredWrits] Loaded {} writ(s), {} invalid.", ok, bad);

        Map<ResourceLocation, WritDisplay> dispNext = new HashMap<>();

        for (var entry : next.entrySet()) {
            ResourceLocation id = entry.getKey();
            JsonObject obj = entry.getValue();

            if (obj.has("display") && obj.get("display").isJsonObject()) {
                JsonObject d = obj.getAsJsonObject("display");

                String name = (d.has("name") && d.get("name").isJsonPrimitive())
                        ? d.get("name").getAsString()
                        : "";

                java.util.ArrayList<String> lines = new java.util.ArrayList<>();
                if (d.has("tooltip") && d.get("tooltip").isJsonArray()) {
                    for (var el : d.getAsJsonArray("tooltip")) {
                        if (el.isJsonPrimitive()) lines.add(el.getAsString());
                    }
                }

                dispNext.put(id, new WritDisplay(name, lines));
            }
        }

        DISPLAYS = Collections.unmodifiableMap(dispNext);
        ClientWritDisplayCache.setAll(DISPLAYS);

    }

    public static JsonObject get(ResourceLocation id) {
        return WRITS.get(id);
    }

    public static Map<ResourceLocation, JsonObject> all() {
        return WRITS;
    }
}
