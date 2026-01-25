package net.normlroyal.descendedangel.client.model;

import net.minecraft.resources.ResourceLocation;
import net.normlroyal.descendedangel.DescendedAngel;
import net.normlroyal.descendedangel.item.custom.AltarItem;
import software.bernie.geckolib.model.GeoModel;

public class AltarItemModel extends GeoModel<AltarItem> {

    @Override
    public ResourceLocation getModelResource(AltarItem animatable) {
        return ResourceLocation.fromNamespaceAndPath(DescendedAngel.MOD_ID, "geo/altar.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(AltarItem animatable) {
        return ResourceLocation.fromNamespaceAndPath(DescendedAngel.MOD_ID, "textures/block/altar.png");
    }

    @Override
    public ResourceLocation getAnimationResource(AltarItem animatable) {
        return null;
    }
}
