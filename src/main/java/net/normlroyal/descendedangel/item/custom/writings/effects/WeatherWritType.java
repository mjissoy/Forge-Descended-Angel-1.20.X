package net.normlroyal.descendedangel.item.custom.writings.effects;

import com.google.gson.JsonObject;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.normlroyal.descendedangel.item.custom.writings.IWritType;

public class WeatherWritType implements IWritType {

    @Override
    public void execute(JsonObject data, ServerLevel level, ServerPlayer player, ItemStack stack) {
        String weather = data.has("weather") ? data.get("weather").getAsString() : "rain";
        int duration = data.has("duration") ? data.get("duration").getAsInt() : net.normlroyal.descendedangel.config.ModConfigs.COMMON.WEATHER_DURATION_TICKS.get();

        boolean rain = weather.equalsIgnoreCase("rain") || weather.equalsIgnoreCase("thunder");
        boolean thunder = weather.equalsIgnoreCase("thunder");

        if (weather.equalsIgnoreCase("clear")) {
            level.setWeatherParameters(duration, 0, false, false);
        } else {
            level.setWeatherParameters(0, duration, rain, thunder);
        }
    }
}

