package net.normlroyal.descendedangel.network;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.normlroyal.descendedangel.config.DANBTS.WingNBTs;
import net.normlroyal.descendedangel.config.ModConfigs;
import net.normlroyal.descendedangel.util.WingUtils;

import java.util.function.Supplier;

public class AdjustT3SpeedC2SPacket {

    private static final int MSG_COOLDOWN_TICKS = 5; // 5 ticks = 0.25s

    private final int direction; // +1 speed up, -1 speed down

    public AdjustT3SpeedC2SPacket(int direction) {
        this.direction = Integer.compare(direction, 0); // normalize to -1/0/1
    }

    public static void encode(AdjustT3SpeedC2SPacket msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.direction);
    }

    public static AdjustT3SpeedC2SPacket decode(FriendlyByteBuf buf) {
        return new AdjustT3SpeedC2SPacket(buf.readInt());
    }

    public static void handle(AdjustT3SpeedC2SPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;

            if (player.isCreative() || player.isSpectator()) return;
            if (WingUtils.getWingTier(player) < 3) return;

            double min = ModConfigs.COMMON.T3_MIN_SPEED.get();
            double max = ModConfigs.COMMON.T3_MAX_SPEED.get();
            double step = ModConfigs.COMMON.T3_SPEED_STEP.get();

            double current = WingUtils.getOrInitT3Speed(player);
            double unclamped = current + (msg.direction * step);
            double next = clamp(unclamped, min, max);

            WingUtils.setT3Speed(player, next);

            float nextSpeed = (float) next;
            if (player.getAbilities().getFlyingSpeed() != nextSpeed) {
                player.getAbilities().setFlyingSpeed(nextSpeed);
                player.onUpdateAbilities();
            }


            long nowTick = player.level().getGameTime();
            CompoundTag data = player.getPersistentData();
            long lastTick = data.getLong(WingNBTs.LAST_SPEED_MSG_TICK);

            if (nowTick - lastTick >= MSG_COOLDOWN_TICKS) {
                data.putLong(WingNBTs.LAST_SPEED_MSG_TICK, nowTick);

                boolean hitMin = unclamped < min && next == min;
                boolean hitMax = unclamped > max && next == max;

                if (hitMin) {
                    player.displayClientMessage(Component.literal("Wing speed: MIN"), true);
                } else if (hitMax) {
                    player.displayClientMessage(Component.literal("Wing speed: MAX"), true);
                } else {
                    double pct = (next - min) / (max - min) * 100.0;
                    player.displayClientMessage(
                            Component.literal(String.format("Wing speed: %.0f%%", pct)),
                            true
                    );
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }

    private static double clamp(double v, double min, double max) {
        return Math.max(min, Math.min(max, v));
    }

}
