package ca.maximilian.create.rockets.content.blocks.thruster;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.util.StringRepresentable;

@Getter
@RequiredArgsConstructor
public enum ThrusterType implements StringRepresentable {
    RAPTOR_3("raptor_3", new ThrusterStats(400, 500, 4, 7)),
    SATURN_V_F1("saturn_v_f1", new ThrusterStats(600, 800, 7, 11));

    private final String serializedName;
    private final ThrusterStats stats;

    @Override
    public String toString() {
        return getSerializedName();
    }
}
