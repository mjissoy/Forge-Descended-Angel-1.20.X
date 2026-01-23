package net.normlroyal.descendedangel.item.custom.writings.effects;

import com.google.gson.JsonObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.normlroyal.descendedangel.item.custom.writings.IWritType;

public class PotionBoostWritType implements IWritType {

    @Override
    public void execute(JsonObject data, ServerLevel level, ServerPlayer player, ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null || !tag.contains("descendedangel", Tag.TAG_COMPOUND)) return;

        CompoundTag root = tag.getCompound("descendedangel");
        if (!root.contains("boost_potion", Tag.TAG_STRING)) return;

        ResourceLocation potionId = ResourceLocation.tryParse(root.getString("boost_potion"));
        if (potionId == null || !BuiltInRegistries.POTION.containsKey(potionId)) return;

        Potion potion = BuiltInRegistries.POTION.get(potionId);
        if (potion == null) return;

        double durMult = data.has("duration_multiplier") ? data.get("duration_multiplier").getAsDouble() : 1.5;
        int ampBonus = data.has("amplifier_bonus") ? data.get("amplifier_bonus").getAsInt() : 1;

        for (MobEffectInstance base : potion.getEffects()) {
            int newDuration = (int) Math.max(20, Math.round(base.getDuration() * durMult));
            int newAmp = Math.max(0, base.getAmplifier() + ampBonus);

            MobEffectInstance boosted = new MobEffectInstance(
                    base.getEffect(),
                    newDuration,
                    newAmp,
                    base.isAmbient(),
                    base.isVisible(),
                    base.showIcon()
            );

            player.addEffect(boosted);
        }
    }
}
