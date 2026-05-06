package ca.maximilian.create_rockets.integration.thruster;

import ca.maximilian.create_rockets.ModBlock.AbstractThrusterBlockEntity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.IElementHelper;

@Getter
@RequiredArgsConstructor
public class ThrusterIntegrationClientProvider implements IBlockComponentProvider {

    private final ResourceLocation uid;

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        var thruster = ThrusterIntegrationHelper.resolveThruster(accessor);
        if (thruster == null) return;

        showFuelIcon(tooltip, thruster);

        var totalTicks = accessor.getServerData().getInt(ThrusterIntegrationHelper.TOTAL_FUEL_TICKS_IDENTIFIER);
        if (totalTicks <= 0) {
            tooltip.add(Component.translatable("create_rockets.jade.no_fuel"));
            return;
        }

        var burnTime = ThrusterIntegrationHelper.tickToString(totalTicks);
        tooltip.add(Component.translatable("create_rockets.jade.burn_time", burnTime));
    }

    private void showFuelIcon(ITooltip iTooltip, @NotNull AbstractThrusterBlockEntity thruster) {
        var fuelStack = thruster.getFuelContainer().getItem(0);
        if (!fuelStack.isEmpty()) {
            iTooltip.add(IElementHelper.get().item(fuelStack));
        }
    }



}
