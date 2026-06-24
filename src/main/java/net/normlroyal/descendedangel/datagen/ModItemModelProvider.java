package net.normlroyal.descendedangel.datagen;

import net.minecraft.data.PackOutput;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.normlroyal.descendedangel.DescendedAngel;
import net.normlroyal.descendedangel.item.ModItems;

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

        // Wing Item Models
        getBuilder("wing_t1").parent(builtinEntity());
        getBuilder("wing_t2").parent(builtinEntity());
        getBuilder("wing_t3").parent(builtinEntity());


        // Material Item Models
        basicItem(ModItems.ANGELFEATHER.getId());
        basicItem(ModItems.REALANGELFEATHER.getId());
        basicItem(ModItems.DEMONHEART.getId());
        basicItem(ModItems.REALDEMONHEART.getId());
        basicItem(ModItems.SPATIALCORE.getId());
        basicItem(ModItems.VOIDTEAR.getId());
        basicItem(ModItems.COMPRESSEDVOID.getId());
        basicItem(ModItems.VOIDMATRIX.getId());
        basicItem(ModItems.SACREDOREINGOT.getId());
        basicItem(ModItems.SACREDORERAW.getId());
        basicItem(ModItems.SACRED_BLOOD.getId());
        basicItem(ModItems.ANGELS_TEARS.getId());

        // Ring and Necklace Item Models
        basicItem(ModItems.HOLY_RING.getId());
        basicItem(ModItems.CLOUD_RING.getId());
        basicItem(ModItems.FLAME_RING.getId());
        basicItem(ModItems.CURE_RING.getId());

        basicItem(ModItems.HOLY_NECKLACE.getId());
        basicItem(ModItems.MESSENGER_PENDANT.getId());
        basicItem(ModItems.LIGHTNESS_NECKLACE.getId());
        basicItem(ModItems.BOOSTER_NECKLACE.getId());


        // Equipment
        basicItem(ModItems.SPEARSHAFT.getId());
        basicItem(ModItems.SPEARHEAD.getId());
        getBuilder("destiny_spear").parent(builtinEntity());
        basicItem(ModItems.MARK_PIECE1.getId());
        basicItem(ModItems.MARK_PIECE2.getId());
        basicItem(ModItems.MARK_PIECE3.getId());
        basicItem(ModItems.MARK_OF_CAIN.getId());
        basicItem(ModItems.PURIFIED_MARK_OF_CAIN.getId());


        // Other Items
        basicItem(ModItems.SACRED_WRITINGS.getId());
        withExistingParent("void_anomaly_spawn_egg", mcLoc("item/template_spawn_egg"));
        withExistingParent("imp_spawn_egg", mcLoc("item/template_spawn_egg"));
        withExistingParent("angel_weeping", mcLoc("item/generated"))
                .texture("layer0", modLoc("block/angel_weeping"));


        // Unlock Items
        basicItem(ModItems.SPACE_FRUIT.getId());
        basicItem(ModItems.TIME_FRUIT.getId());
        basicItem(ModItems.CELESTIAL_FRUIT.getId());
        basicItem(ModItems.RESONANCE_FRUIT.getId());
        basicItem(ModItems.FIRE_SHARD.getId());
        basicItem(ModItems.EMPOWERED_FIRE_SHARD.getId());
        basicItem(ModItems.WATER_SHARD.getId());
        basicItem(ModItems.EMPOWERED_WATER_SHARD.getId());
        basicItem(ModItems.EARTH_SHARD.getId());
        basicItem(ModItems.EMPOWERED_EARTH_SHARD.getId());
        basicItem(ModItems.AIR_SHARD.getId());
        basicItem(ModItems.EMPOWERED_AIR_SHARD.getId());

    }
}
