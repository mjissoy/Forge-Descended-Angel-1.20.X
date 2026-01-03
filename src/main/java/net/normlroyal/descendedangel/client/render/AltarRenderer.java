package net.normlroyal.descendedangel.client.render;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.normlroyal.descendedangel.block.altar.AltarBlockEntity;
import net.normlroyal.descendedangel.block.altar.AltarGeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class AltarRenderer extends GeoBlockRenderer<AltarBlockEntity> {

    public AltarRenderer(BlockEntityRendererProvider.Context ctx) {
        super(new AltarGeoModel());
    }
}
