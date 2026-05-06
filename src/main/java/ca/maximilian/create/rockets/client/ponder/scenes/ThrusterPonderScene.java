package ca.maximilian.create.rockets.client.ponder.scenes;

import ca.maximilian.create.rockets.content.blocks.thruster.ThrusterBlockEntity;
import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import dev.simulated_team.simulated.content.blocks.throttle_lever.ThrottleLeverBlockEntity;
import lombok.experimental.UtilityClass;
import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.createmod.ponder.api.scene.Selection;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public class ThrusterPonderScene {

    public static void thruster(final SceneBuilder builder, final @NotNull SceneBuildingUtil util) {
        final CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        final CreateSceneBuilder.WorldInstructions world = scene.world();

        scene.title("thruster", "Using Rocket Thrusters");
        scene.showBasePlate();
        scene.idle(10);
        world.showSection(util.select().layersFrom(1), Direction.DOWN);
        scene.idle(20);

        var thrusterSelection = util.select().position(2, 1, 2);
        var throttleSelection = util.select().position(1, 2, 2);

        addFuelSubScene(scene, thrusterSelection, world);
        increaseIntensitySubScene(scene, throttleSelection, world, thrusterSelection);
        burnOutSubScene(scene, thrusterSelection, world);

        scene.markAsFinished();
    }

    private static void addFuelSubScene(
            @NotNull CreateSceneBuilder scene,
            @NotNull Selection thrusterSelection,
            CreateSceneBuilder.@NotNull WorldInstructions world) {
        scene.addKeyframe();

        scene.overlay()
                .showControls(thrusterSelection.getCenter(), Pointing.DOWN, 60)
                .withItem(new ItemStack(Items.COAL));

        scene.overlay()
                .showText(60)
                .text("Thrusters use fuel and redstone to operate")
                .pointAt(thrusterSelection.getCenter())
                .placeNearTarget();

        world.modifyBlockEntityNBT(
                thrusterSelection,
                ThrusterBlockEntity.class,
                nbt -> {
                    nbt.putInt("FuelTicksRemaining", 6400);
                    nbt.putInt("FuelTicksTotal", 6400);
                });

        scene.idle(80);
    }

    private static void increaseIntensitySubScene(
            @NotNull CreateSceneBuilder scene,
            @NotNull Selection throttleSelection,
            CreateSceneBuilder.WorldInstructions world,
            Selection thrusterSelection) {
        scene.addKeyframe();

        scene.overlay().showControls(throttleSelection.getCenter(), Pointing.LEFT, 20).rightClick();
        scene.idle(7);

        for (int i = 0; i < 15; i++) {
            scene.idle(2);
            int state = i + 1;
            world.modifyBlockEntityNBT(
                    throttleSelection,
                    ThrottleLeverBlockEntity.class,
                    nbt -> nbt.putInt("State", state));

            var intensity = state / 15.f;

            world.modifyBlockEntityNBT(
                    thrusterSelection,
                    ThrusterBlockEntity.class,
                    nbt -> nbt.putFloat("Intensity", intensity));
        }

        // Keep it running for a while
        scene.idle(70);
    }

    private static void burnOutSubScene(
            @NotNull CreateSceneBuilder scene,
            @NotNull Selection thrusterSelection,
            CreateSceneBuilder.@NotNull WorldInstructions world) {
        scene.addKeyframe();
        scene.overlay()
                .showText(60)
                .text(
                        "The thruster will continue to fire as long as it has fuel and power and burns out when fuel is empty")
                .pointAt(thrusterSelection.getCenter())
                .placeNearTarget();

        scene.idle(7);
        world.modifyBlockEntityNBT(
                thrusterSelection,
                ThrusterBlockEntity.class,
                nbt -> {
                    nbt.putInt("FuelTicksRemaining", 0);
                    nbt.putInt("FuelTicksTotal", 0);
                });

        scene.idle(100);
    }
}
