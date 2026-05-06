package ca.maximilian.create.rockets.content.blocks.thruster;

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
