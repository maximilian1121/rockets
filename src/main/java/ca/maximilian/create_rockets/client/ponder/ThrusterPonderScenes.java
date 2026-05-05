package ca.maximilian.create_rockets.client.ponder;

import ca.maximilian.create_rockets.ModBlock.AbstractThrusterBlockEntity;
import ca.maximilian.create_rockets.ModBlock.ThrusterBlockEntity;
import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import dev.simulated_team.simulated.content.blocks.throttle_lever.ThrottleLeverBlock;
import dev.simulated_team.simulated.content.blocks.throttle_lever.ThrottleLeverBlockEntity;
import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.lang.reflect.Field;

public final class ThrusterPonderScenes {

    private ThrusterPonderScenes() {
    }

    public static void thruster(final SceneBuilder builder, final SceneBuildingUtil util) {
        final CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        final CreateSceneBuilder.WorldInstructions world = scene.world();

        scene.title("thruster", "Using Rocket Thrusters");
        scene.showBasePlate();
        scene.idle(10);
        world.showSection(util.select().layersFrom(1), Direction.DOWN);
        scene.idle(20);

        scene.addKeyframe();

        scene.overlay().showControls(
                util.vector().topOf(2, 1, 2),
                Pointing.DOWN,
                60
        ).withItem(new ItemStack(Items.COAL));

        scene.overlay().showText(60)
                .text("Thrusters use fuel and redstone to operate")
                .pointAt(util.vector().topOf(2, 1, 2))
                .placeNearTarget();

        scene.idle(80);
        scene.addKeyframe();

        scene.overlay().showControls(
                util.vector().centerOf(1, 2, 2),
                Pointing.LEFT,
                20
        ).rightClick();
        scene.idle(30);

        world.modifyBlockEntity(util.grid().at(1, 2, 2), ThrottleLeverBlockEntity.class, be -> {
            be.setSignal(15);

            try {
                Field field = ThrottleLeverBlockEntity.class.getDeclaredField("clientAngle");
                field.setAccessible(true);
                LerpedFloat clientAngle = (LerpedFloat) field.get(be);

                float target = be.getBlockState().getValue(ThrottleLeverBlock.INVERTED) ? 0 : 15;
                clientAngle.chase(target, 0.5f, LerpedFloat.Chaser.EXP);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        world.modifyBlockEntity(
                util.grid().at(2, 1, 2),
                ThrusterBlockEntity.class,
                be -> {
                    be.getFuelContainer().setItem(0, new ItemStack(Items.COAL, 64));
                    try {
                        Field field = AbstractThrusterBlockEntity.class.getDeclaredField("intensity");
                        field.setAccessible(true);
                        field.set(be, 1.0f);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
        );

        // Keep it running for a while
        scene.idle(100);

        scene.overlay().showText(60)
                .text("The thruster will continue to fire as long as it has fuel and power")
                .pointAt(util.vector().centerOf(2, 1, 2))
                .placeNearTarget();

        scene.idle(100);

        scene.markAsFinished();
    }
}
