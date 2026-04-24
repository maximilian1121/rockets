package ca.maximilian.create_rockets;

import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.minecraft.resources.ResourceLocation;

public class CreateRocketsPartialModels {
    public static final PartialModel
            RAPTOR_3 = block("raptor_3"),
            SATURN_V_F1 = block("saturn_v_f1"),
            THRUSTER_FLAME = block("thruster_flame"),
            THRUSTER_FLAME_BLUE = block("thruster_flame_blue");

    private static PartialModel block(final String path) {
        return PartialModel.of(ResourceLocation.tryBuild(CreateRockets.MODID, "block/" + path));
    }

    public static void init() {
    }
}
