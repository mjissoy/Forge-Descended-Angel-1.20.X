package net.normlroyal.descendedangel.client.model;

import net.minecraft.resources.ResourceLocation;
import net.normlroyal.descendedangel.DescendedAngel;
import net.normlroyal.descendedangel.entity.ImpEntity;
import software.bernie.geckolib.model.GeoModel;

public class ImpModel extends GeoModel<ImpEntity> {

    @Override
    public ResourceLocation getModelResource(ImpEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(DescendedAngel.MOD_ID, "geo/imp.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(ImpEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(DescendedAngel.MOD_ID, "textures/entity/imp.png");
    }

    @Override
    public ResourceLocation getAnimationResource(ImpEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(DescendedAngel.MOD_ID, "animations/imp.animation.json");
    }
}
