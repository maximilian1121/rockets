package ca.maximilian.create_rockets.integration;

import ca.maximilian.create_rockets.ModBlock.AbstractThrusterBlockEntity;
import ca.maximilian.create_rockets.ModBlock.ThrusterBlock;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;

@WailaPlugin
public class CreateRocketsIntegrations implements IWailaPlugin {

    public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath("create_rockets", "thruster");

    public static class Common implements IServerDataProvider<BlockAccessor> {
        public static final Common INSTANCE = new Common();

        @Override
        public ResourceLocation getUid() {
            return UID;
        }

        @Override
        public void appendServerData(CompoundTag data, BlockAccessor accessor) {
            if (resolveThruster(accessor) instanceof AbstractThrusterBlockEntity thruster) {
                appendFuelData(data, thruster);
            }
        }
    }

    public static class ClientProvider implements IBlockComponentProvider {
        public static final ClientProvider INSTANCE = new ClientProvider();

        @Override
        public ResourceLocation getUid() {
            return UID;
        }

        @Override
        public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
            if (!(resolveThruster(accessor) instanceof AbstractThrusterBlockEntity thruster)) return;

            // Get total ticks from the NBT data synced from the server
            int totalTicks = accessor.getServerData().getInt("TotalFuelTicks");


            if (totalTicks > 0) {
                if (totalTicks >= Integer.MAX_VALUE || totalTicks < 0) { // Check for overflow/infinite
                    tooltip.add(Component.literal("Total time: Infinite"));
                } else {
                    int totalSeconds = totalTicks / 20;
                    int minutes = totalSeconds / 60;
                    int hours = minutes / 60;

                    String timeString;
                    if (hours > 0) {
                        timeString = String.format("%dh %dm %ds", hours, minutes % 60, totalSeconds % 60);
                    } else if (minutes > 0) {
                        timeString = String.format("%d:%02d", minutes, totalSeconds % 60);
                    } else {
                        timeString = String.format("%d sec", totalSeconds);
                    }

                    tooltip.add(Component.literal("Total time: " + timeString));
                }
            } else {
                tooltip.add(Component.literal("No fuel"));
            }


            // Show item icon
            net.minecraft.world.item.ItemStack fuelStack = thruster.getFuelContainer().getItem(0);
            if (!fuelStack.isEmpty()) {
                tooltip.add(snownee.jade.api.ui.IElementHelper.get().item(fuelStack));
            }
        }
    }

    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(Common.INSTANCE, ThrusterBlock.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(ClientProvider.INSTANCE, ThrusterBlock.class);
    }

    private static void appendFuelData(final CompoundTag data, final AbstractThrusterBlockEntity thruster) {
        net.minecraft.world.item.ItemStack fuelStack = thruster.getFuelContainer().getItem(0);

        int currentBurnRemaining = thruster.getFuelTicksRemaining();
        int totalFuelTicks = currentBurnRemaining;

        if (!fuelStack.isEmpty() && currentBurnRemaining < Integer.MAX_VALUE) {
            int extraItems = Math.max(0, fuelStack.getCount() - 1);
            totalFuelTicks += (extraItems * thruster.getFuelTicksTotal());
        }

        data.putInt("TotalFuelTicks", totalFuelTicks);
    }

    private static AbstractThrusterBlockEntity resolveThruster(final BlockAccessor accessor) {
        if (accessor.getBlockEntity() instanceof AbstractThrusterBlockEntity thruster) {
            return thruster;
        }

        final BlockState state = accessor.getBlockState();
        if (state.getBlock() instanceof ThrusterBlock thrusterBlock) {
            return thrusterBlock.getBaseBlockEntity(accessor.getLevel(), state, accessor.getPosition());
        }

        return null;
    }
}
