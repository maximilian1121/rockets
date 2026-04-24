package ca.maximilian.create_rockets.ModBlock;

import ca.maximilian.create_rockets.index.CreateRocketsBlockEntities;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class ThrusterBlock extends AbstractThrusterBlock<ThrusterBlockEntity> {

    public ThrusterBlock(Properties properties, ThrusterType type) {
        super(properties,
                type == ThrusterType.SATURN_V_F1 ? ThrusterShapes.SATURN_V_F1_BASE : ThrusterShapes.RAPTOR_3_BASE,
                type == ThrusterType.SATURN_V_F1 ? ThrusterShapes.SATURN_V_F1_EXTENSION : ThrusterShapes.RAPTOR_3_EXTENSION);
        this.registerDefaultState(this.defaultBlockState().setValue(TYPE, type));
    }

    @Override
    public Class<ThrusterBlockEntity> getBlockEntityClass() {
        return ThrusterBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends ThrusterBlockEntity> getBlockEntityType() {
        return CreateRocketsBlockEntities.THRUSTER.get();
    }
}
