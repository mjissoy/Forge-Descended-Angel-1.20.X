package net.normlroyal.descendedangel.content.item.custom;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.normlroyal.descendedangel.haloabilities.DominionAbilities;
import net.normlroyal.descendedangel.content.item.custom.enums.FruitType;
import net.normlroyal.descendedangel.util.AbilityUtils;

public class FruitUnlockItem extends Item {
    private final FruitType type;

    public FruitUnlockItem(FruitType type, Properties props) {
        super(props);
        this.type = type;
    }

    public FruitType getType() {
        return type;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide && player instanceof ServerPlayer sp) {
            var data = sp.getPersistentData();
            String tag = type.tag();

            if (data.getBoolean(tag)) {
                sp.displayClientMessage(Component.translatable("message.descendedangel.dominion_already_unlocked"), true);
                return InteractionResultHolder.fail(stack);
            }

            if (DominionAbilities.countUnlockedDominions(sp) >= 2) {
                sp.displayClientMessage(Component.translatable("message.descendedangel.dominion_limit_reached"), true);
                return InteractionResultHolder.fail(stack);
            }
        }

        return super.use(level, player, hand);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (!level.isClientSide && entity instanceof ServerPlayer sp) {
            var data = sp.getPersistentData();
            String tag = type.tag();

            if (data.getBoolean(tag)) {
                sp.displayClientMessage(Component.translatable("message.descendedangel.dominion_already_unlocked"), true);
                return stack;
            }

            if (DominionAbilities.countUnlockedDominions(sp) >= 2) {
                sp.displayClientMessage(Component.translatable("message.descendedangel.dominion_limit_reached"), true);
                return stack;
            }
        }

        ItemStack result = super.finishUsingItem(stack, level, entity);

        if (!level.isClientSide && entity instanceof ServerPlayer sp) {
            var data = sp.getPersistentData();
            String tag = type.tag();

            if (!data.getBoolean(tag)) {
                data.putBoolean(tag, true);
                AbilityUtils.syncUnlocks(sp);

                sp.displayClientMessage(unlockMessage(type), true);
            }
        }

        return result;
    }

    private static Component unlockMessage(FruitType type) {
        return switch (type) {
            case SPACE -> Component.translatable("ability.descendedangel.space");
            case TIME -> Component.translatable("ability.descendedangel.time");
            case CELESTIAL -> Component.translatable("ability.descendedangel.celestial");
            case RESONANCE -> Component.translatable("ability.descendedangel.resonance");
        };
    }
}