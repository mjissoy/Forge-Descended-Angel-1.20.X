package net.normlroyal.descendedangel.config.lootmodifier;

import com.mojang.serialization.Codec;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.normlroyal.descendedangel.DescendedAngel;

public class ModLootModifiers {
    public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> LOOT_MODIFIERS =
            DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, DescendedAngel.MOD_ID);

    public static final RegistryObject<Codec<? extends IGlobalLootModifier>> SPATIAL_CORE_ENDERMAN =
            LOOT_MODIFIERS.register("spatial_core_from_enderman", () -> SpatialCoreFromEndermanModifier.CODEC);
}
