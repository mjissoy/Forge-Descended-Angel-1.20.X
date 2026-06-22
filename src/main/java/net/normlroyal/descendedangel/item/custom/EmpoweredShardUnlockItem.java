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

            HaloAbility selected = switch (type) {
                case FIRE -> rollFireEvolution(level, PowerAbilities.currentFireEvolution(sp));
                case AIR -> rollAirEvolution(level, PowerAbilities.currentAirEvolution(sp));
                case EARTH -> rollEarthEvolution(level, PowerAbilities.currentEarthEvolution(sp));
                case WATER -> rollWaterEvolution(level, PowerAbilities.currentWaterEvolution(sp));
                default -> null;
            };

            if (selected == null) {
                sp.displayClientMessage(Component.translatable("message.descendedangel.empowered_shard_future"), true);
                return stack;
            }

            switch (type) {
                case FIRE -> PowerAbilities.setFireEvolution(sp, selected);
                case AIR -> PowerAbilities.setAirEvolution(sp, selected);
                case EARTH -> PowerAbilities.setEarthEvolution(sp, selected);
                case WATER -> PowerAbilities.setWaterEvolution(sp, selected);
                default -> {}
            }

            AbilityUtils.syncUnlocks(sp);
            popShard(sp, stack);

            sp.displayClientMessage(
                    Component.translatable(evolutionMessageKey(type), evolutionName(selected)),
                    true
            );

            if (!sp.getAbilities().instabuild) {
                stack.shrink(1);
            }
        }

        return stack;
    }

    private static void popShard(ServerPlayer sp, ItemStack stack) {
        ModNetwork.CHANNEL.send(
                PacketDistributor.PLAYER.with(() -> sp),
                new ShardPopS2CPacket(stack.copyWithCount(1))
        );
    }

    private static HaloAbility rollFireEvolution(Level level, HaloAbility current) {
        HaloAbility selected = HaloAbility.SACRED_FLARE;

        for (int i = 0; i < 8; i++) {
            selected = switch (level.random.nextInt(3)) {
                case 0 -> HaloAbility.SACRED_FLARE;
                case 1 -> HaloAbility.SOL_CORONA;
                default -> HaloAbility.PILLARS_OF_RADIANCE;
            };

            if (selected != current) break;
        }

        return selected;
    }

    private static HaloAbility rollAirEvolution(Level level, HaloAbility current) {
        HaloAbility selected = HaloAbility.VACUUM_VORTEX;

        for (int i = 0; i < 8; i++) {
            selected = switch (level.random.nextInt(3)) {
                case 0 -> HaloAbility.VACUUM_VORTEX;
                case 1 -> HaloAbility.ZEPHYR_SCYTHES;
                default -> HaloAbility.HEAVENLY_DOWNDRAFT;
            };

            if (selected != current) break;
        }

        return selected;
    }

    private static HaloAbility rollEarthEvolution(Level level, HaloAbility current) {
        HaloAbility selected = HaloAbility.HOLY_BASTION;

        for (int i = 0; i < 8; i++) {
            selected = switch (level.random.nextInt(3)) {
                case 0 -> HaloAbility.HOLY_BASTION;
                case 1 -> HaloAbility.AEGIS_PILLAR;
                default -> HaloAbility.CRYSTAL_CHRYSALIS;
            };

            if (selected != current) break;
        }

        return selected;
    }

    private static HaloAbility rollWaterEvolution(Level level, HaloAbility current) {
        HaloAbility selected = HaloAbility.MOVING_FIELD_OF_MIST;

        for (int i = 0; i < 8; i++) {
            selected = switch (level.random.nextInt(3)) {
                case 0 -> HaloAbility.MOVING_FIELD_OF_MIST;
                case 1 -> HaloAbility.SERAPHIC_MIRAGE;
                default -> HaloAbility.DIVINE_SERENITY;
            };

            if (selected != current) break;
        }

        return selected;
    }

    private static String evolutionMessageKey(ShardType type) {
        return switch (type) {
            case FIRE -> "ability.descendedangel.fire_evolved";
            case AIR -> "ability.descendedangel.air_evolved";
            case EARTH -> "ability.descendedangel.earth_evolved";
            case WATER -> "ability.descendedangel.water_evolved";
            default -> "ability.descendedangel.unknown_evolution";
        };
    }

    private static Component evolutionName(HaloAbility ability) {
        return switch (ability) {
            case SACRED_FLARE -> Component.translatable("ability.descendedangel.sacred_flare");
            case SOL_CORONA -> Component.translatable("ability.descendedangel.sol_corona");
            case PILLARS_OF_RADIANCE -> Component.translatable("ability.descendedangel.pillars_of_radiance");

            case VACUUM_VORTEX -> Component.translatable("ability.descendedangel.vacuum_vortex");
            case ZEPHYR_SCYTHES -> Component.translatable("ability.descendedangel.zephyr_scythes");
            case HEAVENLY_DOWNDRAFT -> Component.translatable("ability.descendedangel.heavenly_downdraft");

            case HOLY_BASTION -> Component.translatable("ability.descendedangel.holy_bastion");
            case AEGIS_PILLAR -> Component.translatable("ability.descendedangel.aegis_pillar");
            case CRYSTAL_CHRYSALIS -> Component.translatable("ability.descendedangel.crystal_chrysalis");

            case MOVING_FIELD_OF_MIST -> Component.translatable("ability.descendedangel.moving_field_of_mist");
            case SERAPHIC_MIRAGE -> Component.translatable("ability.descendedangel.seraphic_mirage");
            case DIVINE_SERENITY -> Component.translatable("ability.descendedangel.divine_serenity");

            default -> Component.translatable("ability.descendedangel.unknown_evolution");
        };
    }
}