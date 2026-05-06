package ca.maximilian.create_rockets.client.ponder;

import ca.maximilian.create_rockets.index.CreateRocketsItems;
import dev.simulated_team.simulated.index.SimPonderTags;
import lombok.experimental.UtilityClass;
import net.createmod.catnip.registry.RegisteredObjectsHelper;
import net.createmod.ponder.api.registration.PonderTagRegistrationHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public class CreateRocketsPonderTags {

    public static final ResourceLocation THRUST_PRODUCING_BLOCKS = SimPonderTags.THRUST_PRODUCING_BLOCKS;

    public static void register(final @NotNull PonderTagRegistrationHelper<ResourceLocation> helper) {
        final PonderTagRegistrationHelper<ItemLike> itemHelper = helper.withKeyFunction(RegisteredObjectsHelper::getKeyOrThrow);

        itemHelper.addToTag(THRUST_PRODUCING_BLOCKS)
                .add(CreateRocketsItems.RAPTOR_3.get());
    }
}
