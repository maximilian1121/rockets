package ca.maximilian.create_rockets;

import net.createmod.catnip.config.ConfigBase;
import org.jetbrains.annotations.NotNull;

public class Config extends ConfigBase {
    public final ConfigBool evisceration = b(
            true,
            "evisceration",
            Comments.EVISCERATION
    );

    public final ConfigFloat eviscerationRate = f(
            8,
            1,
            100,
            "evisceration_rate",
            Comments.EVISCERATION_RATE
    );

    @Override
    public @NotNull String getName() {
        return "server";
    }

    private static class Comments {
        static final String EVISCERATION =
                "If true, thrusters spawn fire behind them (Evisceration mode)";

        static final String EVISCERATION_RATE =
                "How fast you want evisceration to happen! (Evisceration mode must be enabled)";
    }
}