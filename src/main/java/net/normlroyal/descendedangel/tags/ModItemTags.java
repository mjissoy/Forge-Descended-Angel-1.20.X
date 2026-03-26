package net.normlroyal.descendedangel.tags;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.normlroyal.descendedangel.DescendedAngel;

public class ModItemTags {

    public static final TagKey<Item> HALOS = TagKey.create(
            Registries.ITEM,
            new ResourceLocation(DescendedAngel.MOD_ID, "halos")
    );

    public static final TagKey<Item> WINGS = TagKey.create(
            Registries.ITEM,
            new ResourceLocation(DescendedAngel.MOD_ID, "wings")
    );
}
