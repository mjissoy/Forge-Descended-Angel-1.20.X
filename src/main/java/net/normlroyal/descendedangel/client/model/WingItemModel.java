package net.normlroyal.descendedangel.client.model;

import net.minecraft.resources.ResourceLocation;
import net.normlroyal.descendedangel.item.custom.TieredWingItem;
import software.bernie.geckolib.model.GeoModel;

public class WingItemModel extends GeoModel<TieredWingItem> {

    @Override
    public ResourceLocation getModelResource(TieredWingItem object) {
        int tier = object.getTier();
        return ResourceLocation.fromNamespaceAndPath("descendedangel",
                "geo/wing_t" + tier + ".geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(TieredWingItem object) {
        int tier = object.getTier();
        return ResourceLocation.fromNamespaceAndPath("descendedangel",
                "textures/item/wing_t" + tier + ".png");
    }

    @Override
    public ResourceLocation getAnimationResource(TieredWingItem object) {
        int tier = object.getTier();
        return ResourceLocation.fromNamespaceAndPath("descendedangel",
                "animations/wing_t" + tier + ".animation.json");
    }
}
