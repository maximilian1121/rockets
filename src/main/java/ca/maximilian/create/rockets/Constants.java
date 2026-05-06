package ca.maximilian.create.rockets;

import lombok.experimental.UtilityClass;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public class Constants {

    public static final String MOD_ID = "create_rockets";

    public static final ResourceLocation TAB_SECTION = path(MOD_ID);

    @Contract("_ -> new")
    public static @NotNull ResourceLocation path(final String path) {
        return ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, path);
    }
}
