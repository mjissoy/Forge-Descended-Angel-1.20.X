package net.normlroyal.descendedangel.client.render;


import net.minecraft.client.Minecraft;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import net.normlroyal.descendedangel.haloabilities.ClientAbilityState;
import net.normlroyal.descendedangel.haloabilities.HaloAbility;
import net.normlroyal.descendedangel.haloabilities.helpers.ClientCooldownState;

public class HaloAbilityHudOverlay {
    public static final IGuiOverlay OVERLAY = (gui, gfx, partialTick, sw, sh) -> {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        HaloAbility selected = ClientAbilityState.get(mc.player);
        if (selected == null) return;

        int hotbarX = sw / 2 - 91;
        int hotbarY = sh - 22;

        int x = hotbarX + 182 + 6;
        int y = hotbarY - 16;

        gfx.blit(selected.icon(), x, y, 0, 0, 16, 16, 16, 16);

        long now = mc.level != null ? mc.level.getGameTime() : 0;
        int rem = ClientCooldownState.remainingTicks(selected, now);
        int total = ClientCooldownState.total(selected);


        if (rem > 0 && total > 0) {
            float frac = Math.min(1f, rem / (float)total);
            int h = Math.max(1, (int)Math.ceil(16 * frac));

            gfx.fill(x, y + (16 - h), x + 16, y + 16, 0x88000000);
        }
    };
}
