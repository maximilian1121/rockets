package ca.maximilian.create_rockets.ModBlock;

import ca.maximilian.create_rockets.index.CreateRocketsBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class ThrusterBlockEntity extends AbstractThrusterBlockEntity {
    public ThrusterBlockEntity(BlockPos pos, BlockState state) {
        super(CreateRocketsBlockEntities.THRUSTER.get(), pos, state);
    }

    @Override
    public ThrusterStats getThrusterStats() {
        if (getBlockState().hasProperty(AbstractThrusterBlock.TYPE)) {
            return getBlockState().getValue(AbstractThrusterBlock.TYPE).getStats();
        }
        return ThrusterType.RAPTOR_3.getStats();
    }
}
