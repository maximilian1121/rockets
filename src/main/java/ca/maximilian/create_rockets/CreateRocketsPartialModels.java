package ca.maximilian.create_rockets;

import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import lombok.experimental.UtilityClass;
import net.minecraft.resources.ResourceLocation;

@UtilityClass
public class CreateRocketsPartialModels {

    public static final PartialModel RAPTOR_3 = block("raptor_3");
    public static final PartialModel SATURN_V_F1 = block("saturn_v_f1");
    public static final PartialModel  THRUSTER_FLAME = block("thruster_flame");
    public static final PartialModel  THRUSTER_FLAME_BLUE = block("thruster_flame_blue");

    public static void init() {
    }

    private static PartialModel block(final String path) {
        return PartialModel.of(ResourceLocation.tryBuild(Constants.MOD_ID, "block/" + path));
    }
}
