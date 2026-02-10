package net.normlroyal.descendedangel.block.tempwall;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.normlroyal.descendedangel.block.ModBlockEntities;
import net.normlroyal.descendedangel.block.ModBlocks;

public class TempEarthWallBlockEntity extends BlockEntity {

    private static final String KEY_EXPIRES_AT = "expires_at";
    private long expiresAt = -1;

    public TempEarthWallBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.TEMP_EARTH_WALL_BE.get(), pos, state);
    }

    public void setExpiresAt(long expiresAt) {
        this.expiresAt = expiresAt;
        setChanged();
    }

    public static void tick(ServerLevel level, BlockPos pos, BlockState state, TempEarthWallBlockEntity be) {
        if (be.expiresAt < 0) return;

        long now = level.getGameTime();
        if (now >= be.expiresAt) {
            if (state.getBlock() == ModBlocks.TEMP_EARTH_WALL.get()) {
                level.removeBlock(pos, false);
            }
        } else {
            be.setChanged();
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putLong(KEY_EXPIRES_AT, expiresAt);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        expiresAt = tag.getLong(KEY_EXPIRES_AT);
    }
}
