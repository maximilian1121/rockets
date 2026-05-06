package ca.maximilian.create_rockets;

import lombok.experimental.UtilityClass;
import net.createmod.catnip.config.ConfigBase;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;

@UtilityClass
public class CreateRocketsConfigService {

    private static final Map<ModConfig.Type, ConfigBase> CONFIGS = new EnumMap<>(ModConfig.Type.class);

    public static Config server;

    private static <T extends ConfigBase> T register(Supplier<T> factory, ModConfig.Type type) {
        Pair<T, ModConfigSpec> pair = new ModConfigSpec.Builder().configure(builder -> {
            T cfg = factory.get();
            cfg.registerAll(builder);
            return cfg;
        });

        T config = pair.getLeft();
        config.specification = pair.getRight();

        CONFIGS.put(type, config);
        return config;
    }

    public static void register(ModContainer container) {
        server = CreateRocketsConfigService.register(
                Config::new,
                ModConfig.Type.SERVER
        );

        for (var entry : CONFIGS.entrySet()) {
            container.registerConfig(entry.getKey(), entry.getValue().specification);
        }
    }
}