package ca.maximilian.create.rockets.integration.thruster;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IServerDataProvider;

@Getter
@RequiredArgsConstructor
public class ThrusterIntegrationServerDataProvider implements IServerDataProvider<BlockAccessor> {

    private final ResourceLocation uid;

    @Override
    public void appendServerData(CompoundTag tag, BlockAccessor accessor) {
        var thruster = ThrusterIntegrationHelper.resolveThruster(accessor);

        if (thruster == null) return;

        ThrusterIntegrationHelper.appendFuelData(tag, thruster);
    }
}
