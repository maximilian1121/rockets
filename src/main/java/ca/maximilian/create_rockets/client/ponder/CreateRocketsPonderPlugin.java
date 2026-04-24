package ca.maximilian.create_rockets.client.ponder;

import ca.maximilian.create_rockets.CreateRockets;
import com.simibubi.create.foundation.ponder.CreatePonderPlugin;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.createmod.ponder.api.registration.PonderTagRegistrationHelper;
import net.minecraft.resources.ResourceLocation;

public class CreateRocketsPonderPlugin extends CreatePonderPlugin {

    @Override
    public String getModId() {
        return CreateRockets.MODID;
    }

    @Override
    public void registerScenes(final PonderSceneRegistrationHelper<ResourceLocation> helper) {
        CreateRocketsPonderScenes.register(helper);
    }

    @Override
    public void registerTags(final PonderTagRegistrationHelper<ResourceLocation> helper) {
        CreateRocketsPonderTags.register(helper);
    }
}
