package net.normlroyal.descendedangel.item.custom.writings;

import com.google.gson.JsonObject;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.normlroyal.descendedangel.config.ModConfigs;
import net.normlroyal.descendedangel.events.ClientWritDisplayCache;

import javax.annotation.Nullable;
import java.util.List;

public class SacredWritingsItem extends Item {
    public static final String ROOT = "descendedangel";
    public static final String KEY_WRIT_ID = "writ_id";
    public static final String KEY_USES = "uses";

    public SacredWritingsItem(Properties props) {
        super(props);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, net.minecraft.world.entity.player.Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (level.isClientSide) {
            return InteractionResultHolder.success(stack);
        }

        if (!ModConfigs.COMMON.ENABLE_SACRED_WRITINGS.get()) {
            player.displayClientMessage(Component.translatable("message.descendedangel.writings_disabled"), true);
            return InteractionResultHolder.fail(stack);
        }

        ResourceLocation writId = getWritId(stack);
        if (writId == null) {
            player.displayClientMessage(Component.translatable("message.descendedangel.writings_no_id").withStyle(ChatFormatting.RED), true);
            return InteractionResultHolder.fail(stack);
        }

        if (ModConfigs.COMMON.BLOCKED_WRITINGS.get().contains(writId.toString())) {
            player.displayClientMessage(Component.translatable("message.descendedangel.writings_blocked").withStyle(ChatFormatting.RED), true);
            return InteractionResultHolder.fail(stack);
        }

        int uses = getUses(stack);
        if (uses <= 0) {
            player.displayClientMessage(Component.translatable("message.descendedangel.writings_spent").withStyle(ChatFormatting.GRAY), true);
            return InteractionResultHolder.fail(stack);
        }

        if (!(player instanceof ServerPlayer sp)) {
            return InteractionResultHolder.fail(stack);
        }
        if (!(level instanceof ServerLevel sl)) {
            return InteractionResultHolder.fail(stack);
        }

        JsonObject writ = SacredWritReloadListener.get(writId);
        if (writ == null) {
            player.displayClientMessage(Component.translatable("message.descendedangel.writings_unknown", writId.toString()).withStyle(ChatFormatting.RED), true);
            return InteractionResultHolder.fail(stack);
        }

        ResourceLocation typeId = ResourceLocation.tryParse(writ.get("type").getAsString());
        if (typeId == null) {
            player.displayClientMessage(Component.literal("Invalid writ type for " + writId).withStyle(ChatFormatting.RED), true);
            return InteractionResultHolder.fail(stack);
        }

        IWritType type = WritTypeRegistry.get(typeId);
        if (type == null) {
            player.displayClientMessage(Component.literal("Unregistered writ type: " + typeId).withStyle(ChatFormatting.RED), true);
            return InteractionResultHolder.fail(stack);
        }

        type.execute(writ, sl, sp, stack);

        uses--;
        setUses(stack, uses);

        if (uses <= 0) {
            stack.shrink(1);
        }

        return InteractionResultHolder.consume(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        ResourceLocation id = getWritId(stack);

        if (id != null) {
            var display = ClientWritDisplayCache.get(id);
            if (display != null) {
                if (!display.name().isEmpty()) {
                    tooltip.add(Component.literal(display.name()).withStyle(ChatFormatting.GOLD));
                }
                for (String line : display.tooltip()) {
                    tooltip.add(Component.literal(line).withStyle(ChatFormatting.GRAY));
                }
            }
        }
    }


        @Nullable
    public static ResourceLocation getWritId(ItemStack stack) {
        CompoundTag root = getOrNullRoot(stack);
        if (root == null || !root.contains(KEY_WRIT_ID, Tag.TAG_STRING)) return null;

        String s = root.getString(KEY_WRIT_ID);
        if (s.isEmpty()) return null;

        return ResourceLocation.tryParse(s);
    }

    public static int getUses(ItemStack stack) {
        CompoundTag root = getOrCreateRoot(stack);
        if (!root.contains(KEY_USES, Tag.TAG_INT)) return 1;
        return root.getInt(KEY_USES);
    }

    public static void setUses(ItemStack stack, int uses) {
        CompoundTag root = getOrCreateRoot(stack);
        root.putInt(KEY_USES, uses);
    }

    public static CompoundTag getOrCreateRoot(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        if (!tag.contains(ROOT, Tag.TAG_COMPOUND)) {
            tag.put(ROOT, new CompoundTag());
        }
        return tag.getCompound(ROOT);
    }

    @Nullable
    public static CompoundTag getOrNullRoot(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null) return null;
        if (!tag.contains(ROOT, Tag.TAG_COMPOUND)) return null;
        return tag.getCompound(ROOT);
    }

}
