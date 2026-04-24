package ca.maximilian.create_rockets;

import net.createmod.catnip.config.ConfigBase;
import org.jetbrains.annotations.NotNull;

public class Config extends ConfigBase {
    public final ConfigBool EVISCERATION = b(
            true,
            "evisceration",
            Comments.evisceration
    );

    public final ConfigFloat EVISCERATION_RATE = f(
            8,
            1,
            100,
            "evisceration_rate",
            Comments.evisceration_rate
    );

    @Override
    public @NotNull String getName() {
        return "server";
    }

    private static class Comments {
        static String evisceration =
                "If true, thrusters spawn fire behind them (Evisceration mode)";

        static String evisceration_rate =
                "How fast you want evisceration to happen! (Evisceration mode must be enabled)";
    }
}