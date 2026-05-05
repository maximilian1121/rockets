package ca.maximilian.create_rockets.ModBlock;

import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public enum ThrusterPart implements StringRepresentable {
    BASE("base"),
    EXTENSION("extension");

    private final String name;

    ThrusterPart(final String name) {
        this.name = name;
    }

    @Override
    public @NotNull String getSerializedName() {
        return this.name;
    }
}
