package ca.maximilian.create.rockets.client;

import ca.maximilian.create.rockets.Constants;
import ca.maximilian.create.rockets.CreateRockets;
import ca.maximilian.create.rockets.CreateRocketsPartialModels;
import ca.maximilian.create.rockets.client.particle.LargeSmokeParticle;
import ca.maximilian.create.rockets.client.ponder.CreateRocketsPonderPlugin;
import ca.maximilian.create.rockets.content.blocks.thruster.ThrusterBlockRenderer;
import ca.maximilian.create.rockets.index.CreateRocketsBlockEntities;
import ca.maximilian.create.rockets.index.CreateRocketsItems;
import ca.maximilian.create.rockets.index.CreateRocketsMenus;
import ca.maximilian.create.rockets.index.CreateRocketsParticleTypes;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.TooltipModifier;
import dev.simulated_team.simulated.ponder.new_ponder_tooltip.NewPonderTooltipManager;
import net.createmod.catnip.config.ui.BaseConfigScreen;
import net.createmod.catnip.lang.FontHelper;
import net.createmod.ponder.foundation.PonderIndex;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

public final class CreateRocketsClient {

    private CreateRocketsClient() {
    }

    public static void register(final IEventBus modEventBus, ModContainer container) {
        container.registerExtensionPoint(
            IConfigScreenFactory.class,
            (client, parent) -> new BaseConfigScreen(parent, Constants.MOD_ID));

        CreateRocketsPartialModels.init();
        modEventBus.addListener(CreateRocketsClient::onClientSetup);
        modEventBus.addListener(CreateRocketsClient::onRegisterRenderers);
        modEventBus.addListener(CreateRocketsClient::onRegisterScreens);
        modEventBus.addListener(CreateRocketsClient::onRegisterParticleProviders);
    }

    private static void onClientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(
            () -> {
                TooltipModifier.REGISTRY.register(
                    CreateRocketsItems.RAPTOR_3.get(),
                    new ItemDescription.Modifier(
                        CreateRocketsItems.RAPTOR_3.get(),
                        FontHelper.Palette.STANDARD_CREATE));
                NewPonderTooltipManager.forItems(CreateRocketsItems.RAPTOR_3.get())
                    .addScenes(CreateRockets.path("thruster"));
                PonderIndex.addPlugin(new CreateRocketsPonderPlugin());
            });
    }

    private static void onRegisterParticleProviders(
        final net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(
            CreateRocketsParticleTypes.LARGE_SMOKE.get(),
            LargeSmokeParticle.Provider::new);
    }

    private static void onRegisterRenderers(final EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(
            CreateRocketsBlockEntities.THRUSTER.get(), ThrusterBlockRenderer::new);
    }

    private static void onRegisterScreens(final RegisterMenuScreensEvent event) {
        event.register(CreateRocketsMenus.THRUSTER_FUEL.get(), ThrusterFuelScreen::new);
    }
}
