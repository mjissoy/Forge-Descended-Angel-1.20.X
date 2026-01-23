package net.normlroyal.descendedangel.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.normlroyal.descendedangel.DescendedAngel;
import net.normlroyal.descendedangel.block.altar.AltarStartButton;
import net.normlroyal.descendedangel.menu.AltarMenu;

public class AltarScreen extends AbstractContainerScreen<AltarMenu> {

    private static final ResourceLocation TEXTURE =
            new ResourceLocation(DescendedAngel.MOD_ID, "textures/gui/altar_workbench.png");

    public AltarScreen(AltarMenu menu, Inventory playerInv, Component title) {
        super(menu, playerInv, title);
        this.imageWidth = 177;
        this.imageHeight = 240;
    }

    @Override
    protected void init() {
        super.init();

        int x1 = leftPos + 47;
        int y1 = topPos + 126;
        int w  = 85;
        int h  = 17;

        this.addRenderableWidget(new AltarStartButton(
                x1, y1, w, h,
                menu.blockEntity::getStartButtonText,
                menu::getRiteState,
                () -> {
                    if (minecraft != null && minecraft.gameMode != null) {
                        minecraft.gameMode.handleInventoryButtonClick(menu.containerId, AltarMenu.BTN_START_RITE);
                    }
                }
        ));
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);

        graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);

    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);

        this.renderCoreOverlay(graphics);

        this.renderTooltip(graphics, mouseX, mouseY);
    }

    private void renderCoreOverlay(GuiGraphics graphics) {
        if (!menu.isCrafting()) return;

        int x = leftPos + 82;
        int y = topPos + 61;
        int w = 18;
        int h = 18;

        int max = Math.max(1, menu.getMaxProgress());
        float pct = (float) menu.getProgress() / (float) max;
        pct = Math.min(1f, Math.max(0f, pct));

        int filled = Math.max(1, (int) (h * pct));
        int yStart = y + (h - filled);

        graphics.fill(x, yStart, x + w, y + h, 0x88D4AF37);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 0xFF6E5A2B, false);
        graphics.drawString(this.font, this.playerInventoryTitle, 8, 147, 0xFF6E5A2B, false);
    }

}
