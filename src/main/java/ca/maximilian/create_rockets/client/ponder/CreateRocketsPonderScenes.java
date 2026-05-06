package ca.maximilian.create_rockets.client.ponder;

import ca.maximilian.create_rockets.client.ponder.scenes.ThrusterPonderScene;
import ca.maximilian.create_rockets.index.CreateRocketsItems;
import lombok.experimental.UtilityClass;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public class CreateRocketsPonderScenes {

    public static void register(final @NotNull PonderSceneRegistrationHelper<ResourceLocation> registry) {
        final PonderSceneRegistrationHelper<Item> helper = registry.withKeyFunction(BuiltInRegistries.ITEM::getKey);

        helper.forComponents(CreateRocketsItems.RAPTOR_3.get())
                .addStoryBoard("thruster", ThrusterPonderScene::thruster, CreateRocketsPonderTags.THRUST_PRODUCING_BLOCKS);
        helper.forComponents(CreateRocketsItems.SATURN_V_F1.get())
                .addStoryBoard("thruster", ThrusterPonderScene::thruster, CreateRocketsPonderTags.THRUST_PRODUCING_BLOCKS);
    }
}
