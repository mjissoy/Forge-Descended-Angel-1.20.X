package net.normlroyal.descendedangel.content.block.font;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.normlroyal.descendedangel.content.item.ModItems;

public class BaptismalFontBlock extends Block {
    public static final BooleanProperty HAS_BLOOD = BooleanProperty.create("has_blood");

    private static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 24, 16);

    public BaptismalFontBlock(Properties props) {
        super(props);
        this.registerDefaultState(this.stateDefinition.any().setValue(HAS_BLOOD, false));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        return SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        return SHAPE;
    }

    @Override
    public InteractionResult use(
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            BlockHitResult hit
    ) {
        ItemStack stack = player.getItemInHand(hand);

        if (stack.is(ModItems.SACRED_BLOOD.get())) {
            if (state.getValue(HAS_BLOOD)) {
                if (!level.isClientSide) {
                    player.displayClientMessage(Component.translatable("message.descendedangel.font_already_blooded"), true);
                }
                return InteractionResult.sidedSuccess(level.isClientSide);
            }

            if (!level.isClientSide) {
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }

                level.setBlock(pos, state.setValue(HAS_BLOOD, true), 3);
                level.playSound(null, pos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 0.8F);

                if (level instanceof ServerLevel sl) {
                    sl.sendParticles(
                            ParticleTypes.FALLING_LAVA,
                            pos.getX() + 0.5,
                            pos.getY() + 1.15,
                            pos.getZ() + 0.5,
                            12,
                            0.25,
                            0.15,
                            0.25,
                            0.0
                    );
                }

                player.displayClientMessage(Component.translatable("message.descendedangel.font_blooded"), true);
            }

            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        if (stack.is(ModItems.FIRE_SHARD.get())
                || stack.is(ModItems.AIR_SHARD.get())
                || stack.is(ModItems.EARTH_SHARD.get())
                || stack.is(ModItems.WATER_SHARD.get())) {
            if (!state.getValue(HAS_BLOOD)) {
                if (!level.isClientSide) {
                    player.displayClientMessage(Component.translatable("message.descendedangel.font_requires_blood"), true);
                }
                return InteractionResult.sidedSuccess(level.isClientSide);
            }

            if (!level.isClientSide) {
                boolean fire = stack.is(ModItems.FIRE_SHARD.get());
                boolean air = stack.is(ModItems.AIR_SHARD.get());
                boolean earth = stack.is(ModItems.EARTH_SHARD.get());
                boolean water = stack.is(ModItems.WATER_SHARD.get());

                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }

                level.setBlock(pos, state.setValue(HAS_BLOOD, false), 3);

                ItemStack empowered = new ItemStack(
                        fire
                                ? ModItems.EMPOWERED_FIRE_SHARD.get()
                                : air
                                ? ModItems.EMPOWERED_AIR_SHARD.get()
                                : earth
                                ? ModItems.EMPOWERED_EARTH_SHARD.get()
                                : ModItems.EMPOWERED_WATER_SHARD.get()
                );

                if (!player.getInventory().add(empowered)) {
                    player.drop(empowered, false);
                }

                level.playSound(null, pos, SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.BLOCKS, 1.0F, earth ? 0.85F : air ? 1.65F : 1.35F);

                if (level instanceof ServerLevel sl) {
                    sl.sendParticles(
                            fire ? ParticleTypes.END_ROD : air ? ParticleTypes.CLOUD : earth ? ParticleTypes.ENCHANT : ParticleTypes.BUBBLE_POP,
                            pos.getX() + 0.5,
                            pos.getY() + 1.25,
                            pos.getZ() + 0.5,
                            24,
                            0.35,
                            0.25,
                            0.35,
                            0.02
                    );
                }

                player.displayClientMessage(
                        Component.translatable(
                                fire
                                        ? "message.descendedangel.font_fire_empowered"
                                        : air
                                        ? "message.descendedangel.font_air_empowered"
                                        : earth
                                        ? "message.descendedangel.font_earth_empowered"
                                        : "message.descendedangel.font_water_empowered"
                        ),
                        true
                );
            }

            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        return InteractionResult.PASS;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HAS_BLOOD);
    }
}