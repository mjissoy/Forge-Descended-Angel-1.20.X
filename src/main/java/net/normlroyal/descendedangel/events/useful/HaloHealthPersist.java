package net.normlroyal.descendedangel.events.useful;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.normlroyal.descendedangel.DescendedAngel;
import net.normlroyal.descendedangel.util.HaloUtils;

@Mod.EventBusSubscriber(modid = DescendedAngel.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class HaloHealthPersist {
    private static final String KEY_RATIO     = "da_halo_health_ratio";
    private static final String KEY_PENDING   = "da_halo_health_pending";
    private static final String KEY_TRIES     = "da_halo_health_tries";
    private static final String KEY_LAST_HALO = "da_halo_had_halo_last_tick";

    private static boolean hasHalo(Player p) {
        return HaloUtils.findEquippedHalo(p).isPresent();
    }

    private static float baseMaxHealth(ServerPlayer sp) {
        var inst = sp.getAttribute(Attributes.MAX_HEALTH);
        return inst == null ? 20.0f : (float) inst.getBaseValue(); // usually 20
    }

    private static float currentMaxHealth(ServerPlayer sp) {
        return sp.getMaxHealth();
    }

    private static boolean modifiersApplied(ServerPlayer sp) {
        float base = baseMaxHealth(sp);
        float max  = currentMaxHealth(sp);
        return Math.abs(max - base) > 0.001f;
    }

    private static void markPending(ServerPlayer sp) {
        var tag = sp.getPersistentData();
        if (!tag.contains(KEY_RATIO)) return;
        tag.putBoolean(KEY_PENDING, true);
        tag.putInt(KEY_TRIES, 0);
    }

    @SubscribeEvent
    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent e) {
        if (!(e.getEntity() instanceof ServerPlayer sp)) return;

        var tag = sp.getPersistentData();

        if (!hasHalo(sp)) {
            tag.remove(KEY_RATIO);
            tag.remove(KEY_PENDING);
            tag.remove(KEY_TRIES);
            tag.remove(KEY_LAST_HALO);
            return;
        }

        float max = currentMaxHealth(sp);
        if (max <= 0) return;

        tag.putFloat(KEY_RATIO, sp.getHealth() / max);
    }

    @SubscribeEvent
    public static void onLogin(PlayerEvent.PlayerLoggedInEvent e) {
        if (e.getEntity() instanceof ServerPlayer sp) {
            markPending(sp);
        }
    }

    @SubscribeEvent
    public static void onRespawn(PlayerEvent.PlayerRespawnEvent e) {
        if (e.getEntity() instanceof ServerPlayer sp) {
            markPending(sp);
        }
    }

    @SubscribeEvent
    public static void onDimensionChange(PlayerEvent.PlayerChangedDimensionEvent e) {
        if (e.getEntity() instanceof ServerPlayer sp) {
            markPending(sp);
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent e) {
        if (e.phase != TickEvent.Phase.END) return;
        if (!(e.player instanceof ServerPlayer sp)) return;

        var tag = sp.getPersistentData();

        boolean hasHaloNow = hasHalo(sp);
        boolean hadHaloLast = tag.getBoolean(KEY_LAST_HALO);

        if (hadHaloLast && !hasHaloNow) {
            sp.serverLevel().getServer().execute(() -> {
                float newMax = sp.getMaxHealth();
                if (sp.getHealth() > newMax) {
                    sp.setHealth(newMax);
                }
            });

            tag.remove(KEY_PENDING);
            tag.remove(KEY_TRIES);
        }
        tag.putBoolean(KEY_LAST_HALO, hasHaloNow);

        if (!tag.getBoolean(KEY_PENDING)) return;

        int tries = tag.getInt(KEY_TRIES);
        if (tries++ > 40) {
            tag.remove(KEY_PENDING);
            tag.remove(KEY_TRIES);
            return;
        }
        tag.putInt(KEY_TRIES, tries);

        if (!hasHaloNow) return;
        if (!modifiersApplied(sp)) return;

        float ratio = tag.getFloat(KEY_RATIO);
        if (ratio <= 0) {
            tag.remove(KEY_PENDING);
            tag.remove(KEY_TRIES);
            return;
        }

        float max = currentMaxHealth(sp);
        float target = Math.min(max, Math.max(1.0f, ratio * max));
        sp.setHealth(target);

        tag.remove(KEY_PENDING);
        tag.remove(KEY_TRIES);
    }
}
