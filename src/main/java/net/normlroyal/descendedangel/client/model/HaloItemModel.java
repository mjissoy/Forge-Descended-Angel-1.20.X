package net.normlroyal.descendedangel.client.model;

import net.minecraft.resources.ResourceLocation;
import net.normlroyal.descendedangel.item.custom.TieredHaloItem;
import software.bernie.geckolib.model.GeoModel;

public class HaloItemModel extends GeoModel<TieredHaloItem> {
    @Override
    public ResourceLocation getModelResource(TieredHaloItem object) {
        return new ResourceLocation("descendedangel", "geo/halo_t1.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(TieredHaloItem object) {
        return new ResourceLocation("descendedangel", "textures/item/halo.png");
        // return new ResourceLocation("minecraft", "textures/item/apple.png");
    }

    @Override
    public ResourceLocation getAnimationResource(TieredHaloItem object) {
        // if you don't have animations yet, you can temporarily return null
        return null;
    }
}
