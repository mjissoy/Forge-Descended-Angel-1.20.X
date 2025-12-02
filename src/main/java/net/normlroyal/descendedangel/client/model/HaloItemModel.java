package net.normlroyal.descendedangel.client.model;

import net.minecraft.resources.ResourceLocation;
import net.normlroyal.descendedangel.DescendedAngel;
import net.normlroyal.descendedangel.item.custom.TieredHaloItem;
import software.bernie.geckolib.model.GeoModel;

public class HaloItemModel extends GeoModel<TieredHaloItem> {
    @Override
    public ResourceLocation getModelResource(TieredHaloItem object) {
        int tier = object.getTier(); // 1..9

        return new ResourceLocation(
                "descendedangel",
                "geo/halo_t" + tier + ".geo.json"
        );
    }

    @Override
    public ResourceLocation getTextureResource(TieredHaloItem object) {
        int tier = object.getTier();

        return new ResourceLocation(
                "descendedangel",
                "textures/item/halo_t" + tier + ".png"
        );
    }

    @Override
    public ResourceLocation getAnimationResource(TieredHaloItem object) {
        return null;
    }
}