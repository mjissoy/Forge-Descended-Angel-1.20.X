package net.normlroyal.descendedangel.content.block.void_decorations;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.normlroyal.descendedangel.content.block.ModBlocks;

public class VoidGrassBlock extends BushBlock implements BonemealableBlock {
    public VoidGrassBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return state.is(ModBlocks.VOID_CAVE_BLOCK.get()) || state.isFaceSturdy(level, pos, net.minecraft.core.Direction.UP);
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state, boolean clientSide) {
        return false;
    }

    @Override
    public boolean isBonemealSuccess(net.minecraft.world.level.Level level, RandomSource random, BlockPos pos, BlockState state) {
        return false;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
    }
}