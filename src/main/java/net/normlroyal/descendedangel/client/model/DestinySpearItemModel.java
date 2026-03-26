package net.normlroyal.descendedangel.client.model;

import net.minecraft.resources.ResourceLocation;
import net.normlroyal.descendedangel.DescendedAngel;
import net.normlroyal.descendedangel.item.custom.DestinySpearItem;
import software.bernie.geckolib.model.GeoModel;

public class DestinySpearItemModel extends GeoModel<DestinySpearItem> {
    @Override
    public ResourceLocation getModelResource(DestinySpearItem animatable) {
        return new ResourceLocation(DescendedAngel.MOD_ID, "geo/destiny_spear.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(DestinySpearItem animatable) {
        return new ResourceLocation(DescendedAngel.MOD_ID, "textures/item/destiny_spear.png");
    }

    @Override
    public ResourceLocation getAnimationResource(DestinySpearItem animatable) {
        return null;
    }
}