package ca.maximilian.create_rockets.index;

import ca.maximilian.create_rockets.CreateRockets;
import ca.maximilian.create_rockets.ModBlock.ThrusterBlock;
import ca.maximilian.create_rockets.ModBlock.ThrusterType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class CreateRocketsBlocks {

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(CreateRockets.MODID);

    public static final DeferredBlock<ThrusterBlock> RAPTOR_3 = BLOCKS.register(
            "raptor_3",
            () -> new ThrusterBlock(defaultMetalProperties(), ThrusterType.RAPTOR_3)
    );

    public static final DeferredBlock<ThrusterBlock> SATURN_V_F1 = BLOCKS.register(
            "saturn_v_f1",
            () -> new ThrusterBlock(defaultMetalProperties(), ThrusterType.SATURN_V_F1)
    );

    private CreateRocketsBlocks() {
    }

    private static BlockBehaviour.Properties defaultMetalProperties() {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.METAL)
                .strength(3.0f);
    }
}
