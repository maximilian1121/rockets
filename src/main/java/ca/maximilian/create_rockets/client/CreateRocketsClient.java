package ca.maximilian.create_rockets.client;

import ca.maximilian.create_rockets.ModBlock.ThrusterBlockRenderer;
import ca.maximilian.create_rockets.client.ponder.CreateRocketsPonderPlugin;
import ca.maximilian.create_rockets.index.CreateRocketsItems;
import ca.maximilian.create_rockets.CreateRocketsPartialModels;
import ca.maximilian.create_rockets.index.CreateRocketsBlockEntities;
import ca.maximilian.create_rockets.index.CreateRocketsMenus;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.TooltipModifier;
import dev.simulated_team.simulated.ponder.new_ponder_tooltip.NewPonderTooltipManager;
import net.createmod.catnip.lang.FontHelper;
import net.createmod.ponder.foundation.PonderIndex;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.createmod.catnip.config.ui.BaseConfigScreen;
import ca.maximilian.create_rockets.CreateRockets;

public final class CreateRocketsClient {

    private CreateRocketsClient() {
    }

    public static void register(final IEventBus modEventBus, ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, (client, parent) -> new BaseConfigScreen(parent, CreateRockets.MODID));

        CreateRocketsPartialModels.init();
        modEventBus.addListener(CreateRocketsClient::onClientSetup);
        modEventBus.addListener(CreateRocketsClient::onRegisterRenderers);
        modEventBus.addListener(CreateRocketsClient::onRegisterScreens);
        modEventBus.addListener(CreateRocketsClient::onRegisterParticleProviders);
    }

    private static void onClientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            TooltipModifier.REGISTRY.register(
                    CreateRocketsItems.RAPTOR_3.get(),
                    new ItemDescription.Modifier(CreateRocketsItems.RAPTOR_3.get(), FontHelper.Palette.STANDARD_CREATE)
            );
            NewPonderTooltipManager.forItems(CreateRocketsItems.RAPTOR_3.get())
                    .addScenes(ca.maximilian.create_rockets.CreateRockets.path("thruster"));
            PonderIndex.addPlugin(new CreateRocketsPonderPlugin());
        });
    }

    private static void onRegisterParticleProviders(final net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(
                ca.maximilian.create_rockets.index.CreateRocketsParticleTypes.LARGE_SMOKE.get(),
                ca.maximilian.create_rockets.client.particle.LargeSmokeParticle.Provider::new
        );
    }

    private static void onRegisterRenderers(final EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(CreateRocketsBlockEntities.THRUSTER.get(), ThrusterBlockRenderer::new);
    }

    private static void onRegisterScreens(final RegisterMenuScreensEvent event) {
        event.register(CreateRocketsMenus.THRUSTER_FUEL.get(), ThrusterFuelScreen::new);
    }
}
