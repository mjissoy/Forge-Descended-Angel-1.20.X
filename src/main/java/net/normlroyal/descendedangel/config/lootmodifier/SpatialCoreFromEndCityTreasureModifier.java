package net.normlroyal.descendedangel.config.lootmodifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import net.normlroyal.descendedangel.config.ModConfigs;
import net.normlroyal.descendedangel.item.ModItems;

public class SpatialCoreFromEndCityTreasureModifier extends LootModifier {

    public static final Codec<SpatialCoreFromEndCityTreasureModifier> CODEC =
            RecordCodecBuilder.create(inst -> codecStart(inst)
                    .apply(inst, SpatialCoreFromEndCityTreasureModifier::new));

    public SpatialCoreFromEndCityTreasureModifier(LootItemCondition[] conditionsIn) {
        super(conditionsIn);
    }

    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        var level = context.getLevel();
        if (level == null || level.isClientSide()) return generatedLoot;

        double chance;
        try {
            chance = ModConfigs.COMMON.spatialCoreEndCityChestChance.get();
        } catch (IllegalStateException e) {
            return generatedLoot;
        }

        if (context.getRandom().nextDouble() < chance) {
            generatedLoot.add(new ItemStack(ModItems.SPATIALCORE.get()));
        }

        return generatedLoot;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }
}