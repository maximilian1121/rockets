package ca.maximilian.create_rockets.client;

import ca.maximilian.create_rockets.menu.ThrusterFuelMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class ThrusterFuelScreen extends AbstractContainerScreen<ThrusterFuelMenu> {

    private static final ResourceLocation THRUSTER_TEXTURE = ResourceLocation.fromNamespaceAndPath("create_rockets", "textures/gui/thruster.png");

    public ThrusterFuelScreen(final ThrusterFuelMenu menu, final Inventory playerInventory, final Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
        this.inventoryLabelY = 72;
    }

    @Override
    protected void renderBg(final GuiGraphics guiGraphics, final float partialTick, final int mouseX, final int mouseY) {
        final int x = this.leftPos;
        final int y = this.topPos;

        guiGraphics.blit(THRUSTER_TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight);

//        // Throttle bar
//        final int signal = this.menu.getRedstoneSignal();
//        final int barWidth = Mth.floor(14 * this.menu.getThrottle());
//        guiGraphics.fill(x + 81, y + 53 - 14, x + 95, y + 53, 0xFF2A1B16);
//        if (barWidth > 0) {
//            guiGraphics.fill(x + 81, y + 53 - barWidth, x + 95, y + 53, 0xFFE4572E);
//        }
//
//        if (signal > 0) {
//            guiGraphics.drawString(this.font, signal + "/15", x + 102, y + 44, 0x7A5C4F, false);
//        }
//
//        // Fuel burn time bar
//        final int fuelRemaining = this.menu.getFuelTicksRemaining();
//        final int fuelTotal = this.menu.getFuelTicksTotal();
//
//        if (fuelTotal > 0) {
//            final int fuelBarWidth = Mth.floor(14 * ((float) fuelRemaining / fuelTotal));
//            guiGraphics.fill(x + 81, y + 79, x + 95, y + 93, 0xFF1A2A1B);
//            if (fuelBarWidth > 0) {
//                guiGraphics.fill(x + 81, y + 93 - fuelBarWidth, x + 95, y + 93, 0xFF4AE42E);
//            }
//
//            // Show burn time in seconds
//            final int remainingSeconds = fuelRemaining / 20;
//            final int totalSeconds = fuelTotal / 20;
//            final int remainingMinutes = remainingSeconds / 60;
//            final int totalMinutes = totalSeconds / 60;
//
//            String burnTimeText;
//            if (totalMinutes >= 1) {
//                burnTimeText = String.format("%d:%02d", remainingMinutes, remainingSeconds % 60);
//            } else {
//                burnTimeText = remainingSeconds + "s";
//            }
//
//            guiGraphics.drawString(this.font, burnTimeText, x + 102, y + 76, 0x7A5C4F, false);
//        }
    }

    @Override
    protected void renderLabels(final GuiGraphics guiGraphics, final int mouseX, final int mouseY) {
        super.renderLabels(guiGraphics, mouseX, mouseY);
        final Component fuelLabel = Component.literal("Fuel");
        guiGraphics.drawString(this.font, fuelLabel, (this.imageWidth - this.font.width(fuelLabel)) / 2, 22, 0x404040, false);
//        guiGraphics.drawString(this.font, Component.literal("Throttle"), 101, 19, 0x404040, false);
//        guiGraphics.drawString(this.font, Component.literal((int) (this.menu.getThrottle() * 100) + "%"), 101, 31, 0x404040, false);
//        guiGraphics.drawString(this.font, Component.literal("Time"), 101, 65, 0x404040, false);
        
//        final int fuelTotal = this.menu.getFuelTicksTotal();
//        if (fuelTotal > 0) {
//            final int totalMinutes = fuelTotal / 20 / 60;
//            if (totalMinutes >= 1) {
//                guiGraphics.drawString(this.font, Component.literal(String.format("%dm total", totalMinutes)), 101, 113, 0x7A5C4F, false);
//            } else {
//                guiGraphics.drawString(this.font, Component.literal(fuelTotal / 20 + "s total"), 101, 113, 0x7A5C4F, false);
//            }
//        }
    }
}
