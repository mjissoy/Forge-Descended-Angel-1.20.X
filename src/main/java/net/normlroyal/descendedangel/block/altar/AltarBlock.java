package net.normlroyal.descendedangel.block.altar;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import net.normlroyal.descendedangel.block.ModBlockEntities;

import java.util.List;

public class AltarBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public enum Part implements StringRepresentable {
        LEFT("left"), CENTER("center"), RIGHT("right");
        private final String name;
        Part(String name) { this.name = name; }
        @Override public String getSerializedName() { return name; }
    }

    public static final EnumProperty<Part> PART = EnumProperty.create("part", Part.class);

    public AltarBlock(Properties props) {
        super(props);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(PART, Part.CENTER));
    }


    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        Direction facing = ctx.getHorizontalDirection().getOpposite();
        BlockPos centerPos = ctx.getClickedPos();

        BlockPos leftPos  = centerPos.relative(facing.getCounterClockWise());
        BlockPos rightPos = centerPos.relative(facing.getClockWise());

        Level level = ctx.getLevel();

        if (!level.getBlockState(leftPos).canBeReplaced(ctx)) return null;
        if (!level.getBlockState(rightPos).canBeReplaced(ctx)) return null;

        return this.defaultBlockState()
                .setValue(FACING, facing)
                .setValue(PART, Part.CENTER);
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);

        if (level.isClientSide) return;
        if (oldState.is(this)) return;
        if (state.getValue(PART) != Part.CENTER) return;

        Direction facing = state.getValue(FACING);

        BlockPos leftPos  = pos.relative(facing.getCounterClockWise());
        BlockPos rightPos = pos.relative(facing.getClockWise());

        if (!level.getBlockState(leftPos).canBeReplaced() || !level.getBlockState(rightPos).canBeReplaced()) {
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
            return;
        }

        level.setBlock(leftPos,  state.setValue(PART, Part.LEFT),  Block.UPDATE_ALL);
        level.setBlock(rightPos, state.setValue(PART, Part.RIGHT), Block.UPDATE_ALL);
    }


    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        Direction facing = state.getValue(FACING);

        BlockPos leftPos  = pos.relative(facing.getCounterClockWise());
        BlockPos rightPos = pos.relative(facing.getClockWise());

        return switch (state.getValue(PART)) {
            case CENTER -> {
                BlockState left = level.getBlockState(leftPos);
                BlockState right = level.getBlockState(rightPos);

                boolean leftOk =
                        left.is(this) && left.getValue(PART) == Part.LEFT && left.getValue(FACING) == facing
                                || left.canBeReplaced();

                boolean rightOk =
                        right.is(this) && right.getValue(PART) == Part.RIGHT && right.getValue(FACING) == facing
                                || right.canBeReplaced();

                yield leftOk && rightOk;
            }

            case LEFT -> {
                BlockPos centerPos = pos.relative(facing.getClockWise());
                BlockState center = level.getBlockState(centerPos);
                yield center.is(this)
                        && center.getValue(PART) == Part.CENTER
                        && center.getValue(FACING) == facing;
            }

            case RIGHT -> {
                BlockPos centerPos = pos.relative(facing.getCounterClockWise());
                BlockState center = level.getBlockState(centerPos);
                yield center.is(this)
                        && center.getValue(PART) == Part.CENTER
                        && center.getValue(FACING) == facing;
            }
        };
    }

    @Override
    public BlockState updateShape(BlockState state, Direction dir, BlockState neighbor,
                                  LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (!state.canSurvive(level, pos)) {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(state, dir, neighbor, level, pos, neighborPos);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return state.getValue(PART) == Part.CENTER
                ? RenderShape.ENTITYBLOCK_ANIMATED
                : RenderShape.INVISIBLE;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (level.isClientSide) {
            super.onRemove(state, level, pos, newState, isMoving);
            return;
        }

        if (state.is(newState.getBlock())) {
            super.onRemove(state, level, pos, newState, isMoving);
            return;
        }

        BlockPos centerPos = getCenterPos(state, pos);
        BlockState centerState = level.getBlockState(centerPos);

        if (!pos.equals(centerPos)) {
            super.onRemove(state, level, pos, newState, isMoving);
            return;
        }

        if (!centerState.is(this)) {
            super.onRemove(state, level, pos, newState, isMoving);
            return;
        }

        Direction facing = centerState.getValue(FACING);
        BlockPos leftPos  = centerPos.relative(facing.getCounterClockWise());
        BlockPos rightPos = centerPos.relative(facing.getClockWise());

        level.setBlock(leftPos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
        level.setBlock(rightPos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
        level.setBlock(centerPos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);

        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return state.getValue(PART) == Part.CENTER ? new AltarBlockEntity(pos, state) : null;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
                                                                  BlockEntityType<T> type) {
        if (state.getValue(PART) != Part.CENTER) return null;
        return level.isClientSide ? null : createTickerHelper(type, ModBlockEntities.ALTAR.get(), AltarBlockEntity::tick);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos,
                                 Player player, InteractionHand hand, BlockHitResult hit) {

        if (level.isClientSide) return InteractionResult.SUCCESS;

        BlockPos centerPos = getCenterPos(state, pos);

        BlockEntity be = level.getBlockEntity(centerPos);
        if (!(be instanceof MenuProvider provider)) return InteractionResult.PASS;

        NetworkHooks.openScreen((ServerPlayer) player, provider, centerPos);
        return InteractionResult.CONSUME;
    }

    private static BlockPos getCenterPos(BlockState state, BlockPos pos) {
        Direction facing = state.getValue(FACING);
        return switch (state.getValue(PART)) {
            case CENTER -> pos;
            case LEFT   -> pos.relative(facing.getClockWise());
            case RIGHT  -> pos.relative(facing.getCounterClockWise());
        };
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, PART);
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        return List.of();
    }
}