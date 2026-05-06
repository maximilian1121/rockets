package ca.maximilian.create.rockets.integration;

import ca.maximilian.create.rockets.Constants;
import ca.maximilian.create.rockets.content.blocks.thruster.ThrusterBlock;
import ca.maximilian.create.rockets.integration.thruster.ThrusterIntegrationClientProvider;
import ca.maximilian.create.rockets.integration.thruster.ThrusterIntegrationServerDataProvider;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class CreateRocketsIntegrations implements IWailaPlugin {

    public static final ResourceLocation THRUSTER_UID = Constants.path("thruster");

    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(
                new ThrusterIntegrationServerDataProvider(THRUSTER_UID), ThrusterBlock.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(
                new ThrusterIntegrationClientProvider(THRUSTER_UID), ThrusterBlock.class);
    }
}
