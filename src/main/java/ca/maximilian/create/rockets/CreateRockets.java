package ca.maximilian.create.rockets;

import ca.maximilian.create.rockets.client.CreateRocketsClient;
import ca.maximilian.create.rockets.content.blocks.thruster.AbstractThrusterBlock;
import ca.maximilian.create.rockets.datagen.ModBlockLootProvider;
import ca.maximilian.create.rockets.datagen.ModBlockTagsProvider;
import ca.maximilian.create.rockets.datagen.ModRecipeProvider;
import ca.maximilian.create.rockets.index.CreateRocketsBlockEntities;
import ca.maximilian.create.rockets.index.CreateRocketsBlocks;
import ca.maximilian.create.rockets.index.CreateRocketsItems;
import ca.maximilian.create.rockets.index.CreateRocketsMenus;
import ca.maximilian.create.rockets.index.CreateRocketsParticleTypes;
import ca.maximilian.create.rockets.index.CreateRocketsSounds;
import com.simibubi.create.api.contraption.BlockMovementChecks;
import dev.simulated_team.simulated.registrate.SimulatedRegistrate;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@Mod(Constants.MOD_ID)
public class CreateRockets {

    public CreateRockets(IEventBus eventBus, ModContainer container) {
        CreateRocketsBlocks.BLOCKS.register(eventBus);
        CreateRocketsBlockEntities.BLOCK_ENTITIES.register(eventBus);
        CreateRocketsItems.ITEMS.register(eventBus);
        CreateRocketsSounds.SOUND_EVENTS.register(eventBus);
        CreateRocketsParticleTypes.PARTICLE_TYPES.register(
            eventBus);
        CreateRocketsMenus.MENUS.register(eventBus);
        eventBus.addListener(CreateRocketsBlockEntities::registerCapabilities);
        eventBus.addListener(CreateRockets::onGatherData);
        eventBus.addListener(CreateRockets::onCommonSetup);

        CreateRocketsConfigService.register(container);

        if (FMLEnvironment.dist.isClient()) {
            CreateRocketsClient.register(eventBus, container);
        }
    }

    private static void onCommonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(
            () ->
                BlockMovementChecks.registerAttachedCheck(
                    (state, world, pos, direction) -> {
                        if (!(state.getBlock()
                            instanceof AbstractThrusterBlock<?> thrusterBlock)) {
                            return BlockMovementChecks.CheckResult.PASS;
                        }

                        return BlockMovementChecks.CheckResult.of(
                            direction == thrusterBlock.getAttachedDirection(state));
                    }));
        event.enqueueWork(
            () -> {
                SimulatedRegistrate.TAB_ITEMS.add(CreateRocketsItems.RAPTOR_3::get);
                SimulatedRegistrate.TAB_ITEMS.add(CreateRocketsItems.SATURN_V_F1::get);
                SimulatedRegistrate.ITEM_TO_SECTION.put(
                    path("raptor_3"), Constants.TAB_SECTION);
                SimulatedRegistrate.ITEM_TO_SECTION.put(
                    path("saturn_v_f1"), Constants.TAB_SECTION);
            });
    }

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();
        PackOutput output = gen.getPackOutput();
        ExistingFileHelper helper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookup = event.getLookupProvider();

        gen.addProvider(event.includeServer(), new ModBlockTagsProvider(output, lookup, helper));
        gen.addProvider(event.includeServer(), new ModRecipeProvider(output, lookup));
        gen.addProvider(
            event.includeServer(),
            new LootTableProvider(
                output,
                Set.of(),
                List.of(
                    new LootTableProvider.SubProviderEntry(
                        ModBlockLootProvider::new, LootContextParamSets.BLOCK)),
                lookup));
    }

    public static ResourceLocation path(final String path) {
        return ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, path);
    }
}
