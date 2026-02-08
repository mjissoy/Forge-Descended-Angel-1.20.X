package net.normlroyal.descendedangel.worldgens.subs;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.normlroyal.descendedangel.DescendedAngel;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

public class ModBiomeModifiersJsonProvider implements DataProvider {

    private final PackOutput output;
    private final PackOutput.PathProvider pathProvider;

    public ModBiomeModifiersJsonProvider(PackOutput output) {
        this.output = output;
        this.pathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, "forge/biome_modifier");
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        CompletableFuture<?> a = save(cache, "sacred_ore", sacredOre());
        CompletableFuture<?> b = save(cache, "imp_spawn", impSpawn());
        CompletableFuture<?> c = save(cache, "void_anomaly_spawn", voidAnomalySpawn());
        CompletableFuture<?> d = save(cache, "blessed_rock_patch", blessedRockPatch());
        CompletableFuture<?> e = save(cache, "ashen_rock_patch", ashenRockPatch());
        return CompletableFuture.allOf(a, b, c, d, e);
    }

    private CompletableFuture<?> save(CachedOutput cache, String name, JsonObject json) {
        Path path = pathProvider.json(ResourceLocation.fromNamespaceAndPath(DescendedAngel.MOD_ID, name));
        return DataProvider.saveStable(cache, json, path);
    }

    private static JsonObject sacredOre() {
        JsonObject root = new JsonObject();
        root.addProperty("type", "forge:add_features");
        root.addProperty("biomes", "#minecraft:is_overworld");
        root.addProperty("features", "descendedangel:sacred_ore");
        root.addProperty("step", "underground_ores");
        return root;
    }

    private static JsonObject impSpawn() {
        JsonObject root = new JsonObject();
        root.addProperty("type", "forge:add_spawns");
        root.addProperty("biomes", "#minecraft:is_nether");

        JsonArray spawners = new JsonArray();
        JsonObject s = new JsonObject();
        s.addProperty("type", "descendedangel:imp");
        s.addProperty("weight", 60);
        s.addProperty("minCount", 2);
        s.addProperty("maxCount", 3);
        spawners.add(s);

        root.add("spawners", spawners);
        return root;
    }

    private static JsonObject voidAnomalySpawn() {
        JsonObject root = new JsonObject();
        root.addProperty("type", "forge:add_spawns");
        root.addProperty("biomes", "minecraft:deep_dark");

        JsonArray spawners = new JsonArray();
        JsonObject s = new JsonObject();
        s.addProperty("type", "descendedangel:void_anomaly");
        s.addProperty("weight", 50);
        s.addProperty("minCount", 1);
        s.addProperty("maxCount", 3);
        spawners.add(s);

        root.add("spawners", spawners);
        return root;
    }

    private static JsonObject blessedRockPatch() {
        JsonObject root = new JsonObject();
        root.addProperty("type", "forge:add_features");
        root.addProperty("biomes", "#minecraft:is_overworld");
        root.addProperty("step", "underground_ores");

        JsonArray features = new JsonArray();
        features.add("descendedangel:blessed_rock_patch_placed");
        root.add("features", features);

        return root;
    }

    private static JsonObject ashenRockPatch() {
        JsonObject root = new JsonObject();
        root.addProperty("type", "forge:add_features");
        root.addProperty("biomes", "#minecraft:is_overworld");
        root.addProperty("step", "underground_ores");

        JsonArray features = new JsonArray();
        features.add("descendedangel:ashen_rock_patch_placed");
        root.add("features", features);

        return root;
    }

    @Override
    public String getName() {
        return "DescendedAngel Forge Biome Modifiers (JSON)";
    }
}
