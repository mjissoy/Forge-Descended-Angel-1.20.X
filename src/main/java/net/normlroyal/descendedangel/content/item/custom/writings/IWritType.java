package net.normlroyal.descendedangel.content.item.custom.writings;

import com.google.gson.JsonObject;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public interface IWritType {
    void execute(JsonObject data, ServerLevel level, ServerPlayer player, ItemStack stack);
}

