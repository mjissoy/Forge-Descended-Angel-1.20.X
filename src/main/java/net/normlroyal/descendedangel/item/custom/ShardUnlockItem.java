package net.normlroyal.descendedangel.item.custom;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.normlroyal.descendedangel.item.custom.enums.ShardType;
import net.normlroyal.descendedangel.network.ModNetwork;
import net.normlroyal.descendedangel.network.packets.ShardPopS2CPacket;

public class ShardUnlockItem extends Item {
    private final ShardType type;

    public ShardUnlockItem(ShardType type, Item.Properties props) {
        super(props);
        this.type = type;
    }

    public ShardType getType() {
        return type;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide) {
            var data = player.getPersistentData();
            if (data.getBoolean(type.tag())) {
                if (player instanceof ServerPlayer sp) {
                    sp.displayClientMessage(Component.translatable("message.descendedangel.shard_already_unlocked"), true);
                }
                return InteractionResultHolder.fail(stack);
            }
        }

        player.startUsingItem(hand);
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.NONE;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 20;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        ItemStack result = super.finishUsingItem(stack, level, entity);

        if (!level.isClientSide && entity instanceof ServerPlayer sp) {
            var data = sp.getPersistentData();
            String tag = type.tag();

            if (!data.getBoolean(tag)) {
                data.putBoolean(tag, true);

                ModNetwork.CHANNEL.send(
                        net.minecraftforge.network.PacketDistributor.PLAYER.with(() -> sp),
                        new ShardPopS2CPacket(stack.copyWithCount(1))
                );
                Component msg = switch (type) {
                    case FIRE -> Component.translatable("ability.descendedangel.fire");
                    case WATER  -> Component.translatable("ability.descendedangel.water");
                    case EARTH  -> Component.translatable("ability.descendedangel.earth");
                    case AIR  -> Component.translatable("ability.descendedangel.air");
                };
                sp.displayClientMessage(msg, true);
                if (!sp.getAbilities().instabuild) {
                    stack.shrink(1);
                }
            }
        }

        return result;
    }
}
