package ca.maximilian.create.rockets.client.ponder;

import ca.maximilian.create.rockets.Constants;
import com.simibubi.create.foundation.ponder.CreatePonderPlugin;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.createmod.ponder.api.registration.PonderTagRegistrationHelper;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class CreateRocketsPonderPlugin extends CreatePonderPlugin {

    @Override
    public @NotNull String getModId() {
        return Constants.MOD_ID;
    }

    @Override
    public void registerScenes(
            final @NotNull PonderSceneRegistrationHelper<ResourceLocation> helper) {
        CreateRocketsPonderScenes.register(helper);
    }

    @Override
    public void registerTags(final @NotNull PonderTagRegistrationHelper<ResourceLocation> helper) {
        CreateRocketsPonderTags.register(helper);
    }
}
