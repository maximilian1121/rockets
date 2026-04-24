package ca.maximilian.create_rockets.index;

import ca.maximilian.create_rockets.CreateRockets;
import ca.maximilian.create_rockets.ModBlock.AbstractThrusterBlockEntity;
import ca.maximilian.create_rockets.ModBlock.ThrusterBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class CreateRocketsBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, CreateRockets.MODID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ThrusterBlockEntity>> THRUSTER =
            BLOCK_ENTITIES.register("thruster", () ->
                    BlockEntityType.Builder.of(
                            ThrusterBlockEntity::new,
                            CreateRocketsBlocks.RAPTOR_3.get(),
                            CreateRocketsBlocks.SATURN_V_F1.get()
                    ).build(null)
            );

    public static void registerCapabilities(final RegisterCapabilitiesEvent event) {
        AbstractThrusterBlockEntity.registerCapabilities(event, THRUSTER.get());
    }

    private CreateRocketsBlockEntities() {
    }
}
