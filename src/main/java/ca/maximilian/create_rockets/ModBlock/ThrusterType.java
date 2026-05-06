package ca.maximilian.create_rockets.ModBlock;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.util.StringRepresentable;

@Getter
@RequiredArgsConstructor
public enum ThrusterType implements StringRepresentable {
    RAPTOR_3("raptor_3", new ThrusterStats(100, 120, 1.5f, 1)),
    SATURN_V_F1("saturn_v_f1", new ThrusterStats(400, 600, 2.0f, 1.5f));

    private final String serializedName;
    private final ThrusterStats stats;

    @Override
    public String toString() {
        return getSerializedName();
    }
}
