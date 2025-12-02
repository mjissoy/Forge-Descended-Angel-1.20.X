package net.normlroyal.descendedangel.client.render;

import net.normlroyal.descendedangel.client.model.HaloItemModel;
import net.normlroyal.descendedangel.item.custom.TieredHaloItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class HaloItemRenderer extends GeoItemRenderer<TieredHaloItem> {
    public HaloItemRenderer() {
        super(new HaloItemModel());
    }
}
