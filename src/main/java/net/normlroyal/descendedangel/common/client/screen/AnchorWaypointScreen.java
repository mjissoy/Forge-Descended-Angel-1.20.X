package net.normlroyal.descendedangel.common.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.normlroyal.descendedangel.menu.AnchorWaypointMenu;

import java.util.List;

public class AnchorWaypointScreen extends AbstractContainerScreen<AnchorWaypointMenu> {
    private static final int ROWS_PER_PAGE = 6;
    private int page = 0;

    public AnchorWaypointScreen(AnchorWaypointMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 248;
        this.imageHeight = 188;
        this.titleLabelX = 12;
        this.titleLabelY = 10;
        this.inventoryLabelY = 10_000;
    }

    @Override
    protected void init() {
        super.init();
        rebuildButtons();
    }

    private void rebuildButtons() {
        clearWidgets();

        List<AnchorWaypointMenu.Entry> entries = menu.getEntries();
        int start = page * ROWS_PER_PAGE;
        int end = Math.min(entries.size(), start + ROWS_PER_PAGE);

        for (int i = start; i < end; i++) {
            AnchorWaypointMenu.Entry entry = entries.get(i);
            int row = i - start;
            int buttonX = leftPos + 12;
            int buttonY = topPos + 34 + row * 21;
            int buttonW = imageWidth - 24;
            int index = i;

            Component label = Component.literal(entry.name() + "  -  " + entry.cost().shortLabel());
            addRenderableWidget(Button.builder(label, button -> {
                if (minecraft != null && minecraft.gameMode != null) {
                    minecraft.gameMode.handleInventoryButtonClick(menu.containerId, index);
                    onClose();
                }
            }).bounds(buttonX, buttonY, buttonW, 18).build());
        }

        int maxPage = maxPage();
        addRenderableWidget(Button.builder(Component.literal("<"), button -> {
            page = Math.max(0, page - 1);
            rebuildButtons();
        }).bounds(leftPos + 12, topPos + imageHeight - 28, 24, 18).build()).active = page > 0;

        addRenderableWidget(Button.builder(Component.literal(">"), button -> {
            page = Math.min(maxPage(), page + 1);
            rebuildButtons();
        }).bounds(leftPos + imageWidth - 36, topPos + imageHeight - 28, 24, 18).build()).active = page < maxPage;
    }

    private int maxPage() {
        int entries = menu.getEntries().size();
        if (entries <= 0) {
            return 0;
        }
        return (entries - 1) / ROWS_PER_PAGE;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        renderEntryDetails(graphics, mouseX, mouseY);
        renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.enableBlend();
        graphics.fill(leftPos, topPos, leftPos + imageWidth, topPos + imageHeight, 0xEE12071F);
        graphics.fill(leftPos + 4, topPos + 4, leftPos + imageWidth - 4, topPos + imageHeight - 4, 0xAA2D1645);
        graphics.fill(leftPos + 8, topPos + 28, leftPos + imageWidth - 8, topPos + imageHeight - 36, 0x66100018);
        RenderSystem.disableBlend();
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(font, title, titleLabelX, titleLabelY, 0xFFE9D8FF, false);

        int count = menu.getEntries().size();
        String subtitle = count == 1 ? "1 awakened waypoint" : count + " awakened waypoints";
        graphics.drawString(font, subtitle, 12, 20, 0xFFBCA7D8, false);

        if (count <= 0) {
            graphics.drawString(font, "No other Angelic Anchors are awake yet.", 12, 52, 0xFFBCA7D8, false);
            graphics.drawString(font, "Place another anchor, then return here.", 12, 64, 0xFF8F7AAA, false);
        } else {
            graphics.drawString(font, "Page " + (page + 1) + " / " + (maxPage() + 1), imageWidth / 2 - 24, imageHeight - 23, 0xFFBCA7D8, false);
        }
    }

    private void renderEntryDetails(GuiGraphics graphics, int mouseX, int mouseY) {
        List<AnchorWaypointMenu.Entry> entries = menu.getEntries();
        int start = page * ROWS_PER_PAGE;
        int end = Math.min(entries.size(), start + ROWS_PER_PAGE);

        for (int i = start; i < end; i++) {
            int row = i - start;
            int y = topPos + 51 + row * 21;
            AnchorWaypointMenu.Entry entry = entries.get(i);
            String detail = entry.dimensionLabel() + " | " + entry.distanceLabel();
            if (entry.voidPocket()) {
                detail += " | Preserved Pocket";
            }
            graphics.drawString(font, detail, leftPos + 18, y, 0xFFC7B7DD, false);
        }
    }
}
