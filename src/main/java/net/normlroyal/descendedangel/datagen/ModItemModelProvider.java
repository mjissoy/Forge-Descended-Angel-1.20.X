package net.normlroyal.descendedangel.datagen;

import net.minecraft.data.PackOutput;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.normlroyal.descendedangel.DescendedAngel;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, DescendedAngel.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {

    }
}
