package ca.maximilian.create_rockets.ModBlock;

import net.minecraft.util.StringRepresentable;

public enum ThrusterType implements StringRepresentable {
    RAPTOR_3("raptor_3", new ThrusterStats(100, 120, 1.5f, 1)),
    SATURN_V_F1("saturn_v_f1", new ThrusterStats(400, 600, 2.0f, 1.5f));

    private final String name;
    private final ThrusterStats stats;

    ThrusterType(String name, ThrusterStats stats) {
        this.name = name;
        this.stats = stats;
    }

    public ThrusterStats getStats() {
        return stats;
    }

    @Override
    public String getSerializedName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
