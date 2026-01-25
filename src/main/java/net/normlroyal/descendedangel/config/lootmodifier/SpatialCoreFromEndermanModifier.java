package net.normlroyal.descendedangel.config.lootmodifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import net.normlroyal.descendedangel.config.ModConfigs;
import net.normlroyal.descendedangel.item.ModItems;

public class SpatialCoreFromEndermanModifier extends LootModifier {
    public static final Codec<SpatialCoreFromEndermanModifier> CODEC =
            RecordCodecBuilder.create(inst -> codecStart(inst)
                    .apply(inst, SpatialCoreFromEndermanModifier::new));

    protected SpatialCoreFromEndermanModifier(LootItemCondition[] conditionsIn) {
        super(conditionsIn);
    }

    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        var entity = context.getParamOrNull(LootContextParams.THIS_ENTITY);
        if (entity instanceof EnderMan) {
            double chance = ModConfigs.COMMON.spatialCoreEndermanDropChance.get();
            if (context.getRandom().nextDouble() < chance) {
                generatedLoot.add(new ItemStack(ModItems.SPATIALCORE.get()));
            }
        }
        return generatedLoot;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }
}
