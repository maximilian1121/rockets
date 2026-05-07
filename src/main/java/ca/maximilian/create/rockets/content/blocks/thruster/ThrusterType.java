package ca.maximilian.create.rockets.content.blocks.thruster;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.util.StringRepresentable;

@Getter
@RequiredArgsConstructor
public enum ThrusterType implements StringRepresentable {
    RAPTOR_3("raptor_3", new ThrusterStats(100, 0.001f, 0.001f)),
    SATURN_V_F1("saturn_v_f1", new ThrusterStats(100, 0.001f, 0.001f));

    private final String serializedName;
    private final ThrusterStats stats;

    @Override
    public String toString() {
        return getSerializedName();
    }
}
