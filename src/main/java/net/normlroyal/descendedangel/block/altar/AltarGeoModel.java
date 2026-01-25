package net.normlroyal.descendedangel.block.altar;

import net.minecraft.resources.ResourceLocation;
import net.normlroyal.descendedangel.DescendedAngel;
import software.bernie.geckolib.model.GeoModel;

public class AltarGeoModel extends GeoModel<AltarBlockEntity> {

    @Override
    public ResourceLocation getModelResource(AltarBlockEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(
                DescendedAngel.MOD_ID,
                "geo/altar.geo.json"
        );
    }

    @Override
    public ResourceLocation getTextureResource(AltarBlockEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(
                DescendedAngel.MOD_ID,
                "textures/block/altar.png"
        );
    }

    @Override
    public ResourceLocation getAnimationResource(AltarBlockEntity altarBlockEntity) {
        return null;
    }

}