package net.normlroyal.descendedangel.datagen;

import net.minecraft.data.PackOutput;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.normlroyal.descendedangel.DescendedAngel;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, DescendedAngel.MOD_ID, existingFileHelper);
    }

    private ModelFile builtinEntity() {
        return new ModelFile.UncheckedModelFile("minecraft:builtin/entity");
    }

    @Override
    protected void registerModels() {

        // Halo Item Models
        getBuilder("halo_t1").parent(builtinEntity());
        getBuilder("halo_t2").parent(builtinEntity());
        getBuilder("halo_t3").parent(builtinEntity());
        getBuilder("halo_t4").parent(builtinEntity());
        getBuilder("halo_t5").parent(builtinEntity());
        getBuilder("halo_t6").parent(builtinEntity());
        getBuilder("halo_t7").parent(builtinEntity());
        getBuilder("halo_t8").parent(builtinEntity());
        getBuilder("halo_t9").parent(builtinEntity());

        // Material Item Models
        basicItem(modLoc("angel_feather"));
        basicItem(modLoc("angel_feather_real"));
        basicItem(modLoc("demon_heart"));
        basicItem(modLoc("spatial_core"));
        basicItem(modLoc("void_tear"));
        basicItem(modLoc("sacred_ore_ingot"));
        basicItem(modLoc("sacred_ore_raw"));

        // Ring and Necklace Item Models
        basicItem(modLoc("holy_ring"));
        basicItem(modLoc("cloud_ring"));
        basicItem(modLoc("flame_ring"));
        basicItem(modLoc("holy_necklace"));
        basicItem(modLoc("messenger_pendant"));
        basicItem(modLoc("nanos_lantern"));

        // Other Items
        basicItem(modLoc("sacred_writings"));
        withExistingParent("void_anomaly_spawn_egg", mcLoc("item/template_spawn_egg"));
        withExistingParent("imp_spawn_egg", mcLoc("item/template_spawn_egg"));


    }
}
