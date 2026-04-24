package ca.maximilian.create_rockets.client.ponder;

import ca.maximilian.create_rockets.index.CreateRocketsItems;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public final class CreateRocketsPonderScenes {

    private CreateRocketsPonderScenes() {
    }

    public static void register(final PonderSceneRegistrationHelper<ResourceLocation> registry) {
        final PonderSceneRegistrationHelper<Item> helper = registry.withKeyFunction(BuiltInRegistries.ITEM::getKey);

        helper.forComponents(CreateRocketsItems.RAPTOR_3.get())
                .addStoryBoard("thruster", ThrusterPonderScenes::thruster, CreateRocketsPonderTags.THRUST_PRODUCING_BLOCKS);
    }
}
