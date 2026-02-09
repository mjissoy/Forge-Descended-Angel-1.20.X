package net.normlroyal.descendedangel.item.custom;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.normlroyal.descendedangel.item.custom.enums.FruitType;

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
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        ItemStack result = super.finishUsingItem(stack, level, entity);

        if (!level.isClientSide && entity instanceof ServerPlayer sp) {
            var data = sp.getPersistentData();
            String tag = type.tag();

            if (!data.getBoolean(tag)) {
                data.putBoolean(tag, true);

                Component msg = switch (type) {
                    case SPACE -> Component.translatable("ability.descendedangel.space");
                    case TIME  -> Component.translatable("ability.descendedangel.time");
                };
                sp.displayClientMessage(msg, true);
            }
        }

        return result;
    }
}
