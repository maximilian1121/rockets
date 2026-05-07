package ca.maximilian.create.rockets;

import net.createmod.catnip.config.ConfigBase;
import org.jetbrains.annotations.NotNull;

public class Config extends ConfigBase {

    public final EviscerationSettings evisceration = nested(0, EviscerationSettings::new, Comments.EVISCERATION_GROUP);

    @Override
    public @NotNull String getName() {
        return "server";
    }

    public static class EviscerationSettings extends ConfigBase {

        public final ConfigBool enabled = b(true, "enabled", Comments.ENABLED);

        public final ConfigFloat rate =
                f(8F, 0F, 100F, "rate", Comments.RATE);

        public final ConfigBool spawnFire =
                b(true, "spawnFire", Comments.SPAWN_FIRE);

        public final ConfigBool smeltItemsAndBlocks =
                b(true, "smeltBlocks", Comments.SMELT_BLOCKS);

        @Override
        public @NotNull String getName() {
            return "evisceration";
        }
    }

    private static class Comments {
        static final String EVISCERATION_GROUP = "Settings for thruster-based destruction and environmental heat effects.";

        static final String ENABLED = "Whether thrusters should affect the world.";

        static final String RATE = "The frequency of evisceration.";

        static final String SPAWN_FIRE = "If enabled, thrusters will ignite blocks in their exhaust path.";

        static final String SMELT_BLOCKS = "If enabled, thrusters will smelt blocks and items in their exhaust path.";
    }
}
