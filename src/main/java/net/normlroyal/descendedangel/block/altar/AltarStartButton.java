package net.normlroyal.descendedangel.block.altar;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

import java.util.function.IntSupplier;
import java.util.function.Supplier;

public class AltarStartButton extends AbstractWidget {
    private final Supplier<Component> textSupplier;
    private final IntSupplier stateSupplier;
    private final Runnable onClick;

    public AltarStartButton(int x, int y, int w, int h,
                            Supplier<Component> textSupplier,
                            IntSupplier stateSupplier,
                            Runnable onClick) {
        super(x, y, w, h, Component.empty());
        this.textSupplier = textSupplier;
        this.stateSupplier = stateSupplier;
        this.onClick = onClick;
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        int state = stateSupplier.getAsInt();
        if (state == AltarBlockEntity.STATE_CAN_START) {
            onClick.run();
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput) {

    }

    @Override
    protected void renderWidget(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        int state = stateSupplier.getAsInt();
        Component msg = textSupplier.get();

        // Enabled only if it can start
        this.active = (state == AltarBlockEntity.STATE_CAN_START);
        this.visible = true; // always draw it


        boolean hover = this.isHovered;

        // Overlays (ARGB)
        if (!this.active) {
            // disabled overlay
            g.fill(getX(), getY(), getX() + width, getY() + height, 0x33000000);
        } else if (this.isHovered) {
            // hover overlay
            g.fill(getX(), getY(), getX() + width, getY() + height, 0x22FFFFFF);
        }

        // pressed overlay (when mouse down on it)
        if (this.active && this.isHovered && net.minecraft.client.Minecraft.getInstance().mouseHandler.isLeftPressed()) {
            g.fill(getX(), getY(), getX() + width, getY() + height, 0x33000000);
        }

        // Text
        String s = (msg == null) ? "" : msg.getString();
        // color: bright gold when active, muted when not
        int color = this.active ? 0xFFE6C35A : 0xFFB7B0A0;

        // Fit text to width by trimming
        int padding = 4;
        int maxW = width - padding * 2;
        var font = net.minecraft.client.Minecraft.getInstance().font;

        String trimmed = font.plainSubstrByWidth(s, maxW);

        int textX = getX() + (width - font.width(trimmed)) / 2;
        int textY = getY() + (height - 8) / 2;

        g.drawString(font, trimmed, textX, textY, color, false);
    }
}