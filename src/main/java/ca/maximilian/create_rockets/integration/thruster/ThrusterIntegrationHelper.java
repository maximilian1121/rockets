package ca.maximilian.create_rockets.integration.thruster;

import ca.maximilian.create_rockets.ModBlock.AbstractThrusterBlockEntity;
import ca.maximilian.create_rockets.ModBlock.ThrusterBlock;
import lombok.experimental.UtilityClass;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import snownee.jade.api.BlockAccessor;

@UtilityClass
class ThrusterIntegrationHelper {

    static final String TOTAL_FUEL_TICKS_IDENTIFIER = "TotalFuelTicks";

    static void appendFuelData(final CompoundTag data, final @NotNull AbstractThrusterBlockEntity thruster) {
        ItemStack fuelStack = thruster.getFuelContainer().getItem(0);

        int currentBurnRemaining = thruster.getFuelTicksRemaining();
        int totalFuelTicks = currentBurnRemaining;

        if (!fuelStack.isEmpty() && currentBurnRemaining < Integer.MAX_VALUE) {
            int extraItems = Math.max(0, fuelStack.getCount() - 1);
            totalFuelTicks += (extraItems * thruster.getFuelTicksTotal());
        }

        data.putInt(TOTAL_FUEL_TICKS_IDENTIFIER, totalFuelTicks);
    }

    static @Nullable AbstractThrusterBlockEntity resolveThruster(final @NotNull BlockAccessor accessor) {
        if (accessor.getBlockEntity() instanceof AbstractThrusterBlockEntity thruster) {
            return thruster;
        }

        final BlockState state = accessor.getBlockState();
        if (state.getBlock() instanceof ThrusterBlock thrusterBlock) {
            return thrusterBlock.getBaseBlockEntity(accessor.getLevel(), state, accessor.getPosition());
        }

        return null;
    }

    static @NotNull String tickToString(int ticks) {
        var seconds = ticks / 20;
        var minutes = seconds / 60;
        var hours = minutes / 60;

        if (hours > 0) {
            return String.format("%dh %dm %ds", hours, minutes % 60, seconds % 60);
        }

        if (minutes > 0) {
            return String.format("%dm %ds", minutes, seconds % 60);
        }

        return String.format("%d sec", seconds);
    }

}
