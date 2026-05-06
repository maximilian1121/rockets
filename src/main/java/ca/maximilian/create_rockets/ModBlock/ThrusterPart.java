package ca.maximilian.create_rockets.ModBlock;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.util.StringRepresentable;

@Getter
@RequiredArgsConstructor
public enum ThrusterPart implements StringRepresentable {
    BASE("base"),
    EXTENSION("extension");

    private final String serializedName;

}
