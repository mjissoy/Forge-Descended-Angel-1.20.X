package net.normlroyal.descendedangel.common.client.render;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.normlroyal.descendedangel.content.block.altar.AltarBlockEntity;
import net.normlroyal.descendedangel.content.block.altar.AltarGeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class AltarRenderer extends GeoBlockRenderer<AltarBlockEntity> {

    public AltarRenderer(BlockEntityRendererProvider.Context ctx) {
        super(new AltarGeoModel());
    }
}
