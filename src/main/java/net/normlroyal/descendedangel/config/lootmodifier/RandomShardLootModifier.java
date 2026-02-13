package net.normlroyal.descendedangel.config.lootmodifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class RandomShardLootModifier extends LootModifier {

    public static final Codec<RandomShardLootModifier> CODEC = RecordCodecBuilder.create(inst -> codecStart(inst)
            .and(ForgeRegistries.ITEMS.getCodec().listOf().fieldOf("shards").forGetter(m -> m.shards))
            .and(Codec.INT.fieldOf("minCount").forGetter(m -> m.minCount))
            .and(Codec.INT.fieldOf("maxCount").forGetter(m -> m.maxCount))
            .apply(inst, RandomShardLootModifier::new));

    private final List<Item> shards;
    private final int minCount;
    private final int maxCount;

    public RandomShardLootModifier(LootItemCondition[] conditions, List<Item> shards, int minCount, int maxCount) {
        super(conditions);
        this.shards = shards;
        this.minCount = minCount;
        this.maxCount = maxCount;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(
            ObjectArrayList<ItemStack> generatedLoot,
            LootContext context
    ) {
        if (shards == null || shards.isEmpty()) {
            return generatedLoot;
        }

        var random = context.getRandom();

        Item chosen = shards.get(random.nextInt(shards.size()));

        int count = minCount;
        if (maxCount > minCount) {
            count = minCount + random.nextInt((maxCount - minCount) + 1);
        }

        generatedLoot.add(new ItemStack(chosen, Math.max(1, count)));
        return generatedLoot;
    }

}
