package net.normlroyal.descendedangel.block.tempwall;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class TempEarthWallBlock extends Block implements EntityBlock {

    public TempEarthWallBlock(Properties props) {
        super(props);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TempEarthWallBlockEntity(pos, state);
    }

    public static void arm(ServerLevel level, BlockPos pos, int durationTicks) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof TempEarthWallBlockEntity wall) {
            wall.setExpiresAt(level.getGameTime() + Math.max(1, durationTicks));
            level.sendBlockUpdated(pos, level.getBlockState(pos), level.getBlockState(pos), 3);
        }
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide) return null;

        return (lvl, pos, st, be) -> {
            if (be instanceof TempEarthWallBlockEntity wall && st.getBlock() == this) {
                TempEarthWallBlockEntity.tick((ServerLevel) lvl, pos, st, wall);
            }
        };
    }
}
