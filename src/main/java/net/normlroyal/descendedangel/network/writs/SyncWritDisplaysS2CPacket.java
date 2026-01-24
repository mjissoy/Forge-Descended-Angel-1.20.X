package net.normlroyal.descendedangel.network.writs;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;
import net.normlroyal.descendedangel.events.useful.ClientWritDisplayCache;
import net.normlroyal.descendedangel.item.custom.writings.WritDisplay;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class SyncWritDisplaysS2CPacket {

        private final Map<ResourceLocation, WritDisplay> displays;

        public SyncWritDisplaysS2CPacket(Map<ResourceLocation, WritDisplay> displays) {
            this.displays = displays;
        }

        public static void encode(SyncWritDisplaysS2CPacket msg, FriendlyByteBuf buf) {
            buf.writeVarInt(msg.displays.size());
            for (var e : msg.displays.entrySet()) {
                buf.writeResourceLocation(e.getKey());
                buf.writeUtf(e.getValue().name(), 32767);

                List<String> lines = e.getValue().tooltip();
                buf.writeVarInt(lines.size());
                for (String line : lines) {
                    buf.writeUtf(line, 32767);
                }
            }
        }

        public static SyncWritDisplaysS2CPacket decode(FriendlyByteBuf buf) {
            int size = buf.readVarInt();
            Map<ResourceLocation, WritDisplay> map = new HashMap<>(size);

            for (int i = 0; i < size; i++) {
                ResourceLocation id = buf.readResourceLocation();
                String name = buf.readUtf(32767);

                int lineCount = buf.readVarInt();
                java.util.ArrayList<String> lines = new java.util.ArrayList<>(lineCount);
                for (int j = 0; j < lineCount; j++) {
                    lines.add(buf.readUtf(32767));
                }

                map.put(id, new WritDisplay(name, lines));
            }

            return new SyncWritDisplaysS2CPacket(map);
        }

        public static void handle(SyncWritDisplaysS2CPacket msg, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> ClientWritDisplayCache.setAll(msg.displays));
            ctx.get().setPacketHandled(true);
        }

    }
