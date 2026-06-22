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
import net.minecraftforge.network.PacketDistributor;
import net.normlroyal.descendedangel.haloabilities.HaloAbility;
import net.normlroyal.descendedangel.haloabilities.PowerAbilities;
import net.normlroyal.descendedangel.item.custom.enums.ShardType;
import net.normlroyal.descendedangel.network.ModNetwork;
import net.normlroyal.descendedangel.network.packets.ShardPopS2CPacket;
import net.normlroyal.descendedangel.util.AbilityUtils;
import net.normlroyal.descendedangel.util.HaloUtils;

public class EmpoweredShardUnlockItem extends Item {
    private final ShardType type;

    public EmpoweredShardUnlockItem(ShardType type, Item.Properties props) {
        super(props);
        this.type = type;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide && player instanceof ServerPlayer sp) {
            if (HaloUtils.getEquippedHaloTier(sp) < 7) {
                sp.displayClientMessage(Component.translatable("message.descendedangel.empowered_shard_requires_cherubim"), true);
                return InteractionResultHolder.fail(stack);
            }

            if (type != ShardType.FIRE) {
                sp.displayClientMessage(Component.translatable("message.descendedangel.empowered_shard_future"), true);
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
        if (!level.isClientSide && entity instanceof ServerPlayer sp) {
            if (HaloUtils.getEquippedHaloTier(sp) < 7) {
                sp.displayClientMessage(Component.translatable("message.descendedangel.empowered_shard_requires_cherubim"), true);
                return stack;
            }

            if (type == ShardType.FIRE) {
                HaloAbility current = PowerAbilities.currentFireEvolution(sp);
                HaloAbility selected = rollFireEvolution(level, current);

                PowerAbilities.setFireEvolution(sp, selected);
                AbilityUtils.syncUnlocks(sp);

                ModNetwork.CHANNEL.send(
                        PacketDistributor.PLAYER.with(() -> sp),
                        new ShardPopS2CPacket(stack.copyWithCount(1))
                );

                sp.displayClientMessage(
                        Component.translatable(
                                "ability.descendedangel.fire_evolved",
                                fireEvolutionName(selected)
                        ),
                        true
                );

                if (!sp.getAbilities().instabuild) {
                    stack.shrink(1);
                }
            }
        }

        return stack;
    }

    private static HaloAbility rollFireEvolution(Level level, HaloAbility current) {
        HaloAbility selected = HaloAbility.SACRED_FLARE;

        for (int i = 0; i < 8; i++) {
            selected = switch (level.random.nextInt(3)) {
                case 0 -> HaloAbility.SACRED_FLARE;
                case 1 -> HaloAbility.SOL_CORONA;
                default -> HaloAbility.PILLARS_OF_RADIANCE;
            };

            if (selected != current) {
                break;
            }
        }

        return selected;
    }

    private static Component fireEvolutionName(HaloAbility ability) {
        return switch (ability) {
            case SACRED_FLARE -> Component.translatable("ability.descendedangel.sacred_flare");
            case SOL_CORONA -> Component.translatable("ability.descendedangel.sol_corona");
            case PILLARS_OF_RADIANCE -> Component.translatable("ability.descendedangel.pillars_of_radiance");
            default -> Component.translatable("ability.descendedangel.unknown_fire_evolution");
        };
    }
}