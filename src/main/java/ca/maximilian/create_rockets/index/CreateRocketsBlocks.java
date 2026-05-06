package ca.maximilian.create_rockets.index;

import ca.maximilian.create_rockets.Constants;
import ca.maximilian.create_rockets.ModBlock.ThrusterBlock;
import ca.maximilian.create_rockets.ModBlock.ThrusterType;
import lombok.experimental.UtilityClass;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

@UtilityClass
public final class CreateRocketsBlocks {

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Constants.MOD_ID);

    public static final DeferredBlock<ThrusterBlock> RAPTOR_3 = BLOCKS.register(
            "raptor_3",
            () -> new ThrusterBlock(defaultMetalProperties(), ThrusterType.RAPTOR_3)
    );

    public static final DeferredBlock<ThrusterBlock> SATURN_V_F1 = BLOCKS.register(
            "saturn_v_f1",
            () -> new ThrusterBlock(defaultMetalProperties(), ThrusterType.SATURN_V_F1)
    );

    private static BlockBehaviour.Properties defaultMetalProperties() {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.METAL)
                .strength(3.0f);
    }
}
