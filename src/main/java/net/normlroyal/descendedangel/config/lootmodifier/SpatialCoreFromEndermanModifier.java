package net.normlroyal.descendedangel.config.lootmodifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
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
        var level = context.getLevel();
        if (level == null || level.isClientSide()) return generatedLoot;

        if (!level.dimension().equals(Level.END)) return generatedLoot;

        var entity = context.getParamOrNull(LootContextParams.THIS_ENTITY);
        if (!(entity instanceof EnderMan)) return generatedLoot;

        double baseChance;
        try {
            baseChance = ModConfigs.COMMON.spatialCoreEndermanDropChance.get();
        } catch (IllegalStateException e) {
            return generatedLoot;
        }

        int lootingLevel = 0;
        Entity killer = context.getParamOrNull(LootContextParams.KILLER_ENTITY);
        if (killer instanceof LivingEntity livingKiller) {
            ItemStack weapon = livingKiller.getMainHandItem();
            lootingLevel = weapon.getEnchantmentLevel(Enchantments.MOB_LOOTING);
        }

        double finalChance = baseChance + (lootingLevel * 0.04);
        finalChance = Math.min(finalChance, 1.0);

        if (context.getRandom().nextDouble() < finalChance) {
            generatedLoot.add(new ItemStack(ModItems.SPATIALCORE.get()));
        }

        return generatedLoot;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }
}
