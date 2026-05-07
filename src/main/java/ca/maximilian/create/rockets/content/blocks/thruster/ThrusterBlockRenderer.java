package ca.maximilian.create.rockets.content.blocks.thruster;

import ca.maximilian.create.rockets.CreateRocketsPartialModels;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import lombok.RequiredArgsConstructor;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;

@RequiredArgsConstructor
public class ThrusterBlockRenderer extends SafeBlockEntityRenderer<AbstractThrusterBlockEntity> {

    private final BlockEntityRendererProvider.Context renderContext;

    @Override
    protected void renderSafe(
        @NotNull AbstractThrusterBlockEntity be,
        float partialTicks,
        PoseStack ms,
        MultiBufferSource buffer,
        int light,
        int overlay) {

        if (be.getLevel() == null) return;

        final Direction dir = be.getBlockState().getValue(BlockStateProperties.FACING);
        final ThrusterType type = be.getBlockState().getValue(AbstractThrusterBlock.TYPE);

        final PartialModel model =
            type == ThrusterType.SATURN_V_F1
                ? CreateRocketsPartialModels.SATURN_V_F1
                : CreateRocketsPartialModels.RAPTOR_3;
        final PartialModel flameModel =
            type == ThrusterType.SATURN_V_F1
                ? CreateRocketsPartialModels.THRUSTER_FLAME
                : CreateRocketsPartialModels.THRUSTER_FLAME_BLUE;

        final SuperByteBuffer rocket = CachedBuffers.partial(model, be.getBlockState());
        final SuperByteBuffer flame = CachedBuffers.partial(flameModel, be.getBlockState());

        rocket.light(light).overlay(overlay);

        flame.light(LightTexture.FULL_BRIGHT).overlay(OverlayTexture.NO_OVERLAY);

        Quaternionf rot = getRot(dir);

        rocket.translate(0.5, 0.5, 0.5)
            .rotate(rot)
            .translate(-0.5, -0.5, -0.5)
            .renderInto(ms, buffer.getBuffer(RenderType.cutoutMipped()));

        float throttle = be.getIntensity();
        if (throttle > 0.01f) {
            flame.translate(0.5, 0.5, 0.5)
                .rotate(rot)
                .scale(1.5f, 1.5f, 1.5f)
                .translate(-0.5, 1d, -0.5)
                .scale(1F, throttle * 4f, 1)
                .renderInto(ms, buffer.getBuffer(RenderType.cutoutMipped()));
        }

        EntityRenderDispatcher dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();

        if (dispatcher.shouldRenderHitBoxes()) {
            final Direction direction = be.getBlockDirection();
            final Vec3 directionVec = Vec3.atLowerCornerOf(direction.getNormal());
            final Vec3 thrustOrigin = be.getBlockPos().getCenter();
            final double forwardOffset = 3;
            final Vec3 start = thrustOrigin.add(directionVec.scale(forwardOffset));

            final double reach = be.getThrusterStats().radius() * 2.5;
            final double length =
                Math.max(
                    0.0,
                    reach
                        * 5
                        * ((float) be.getThrust()
                        / (float) be.getThrusterStats().thrust()));
            final double startRad = 1d;
            final double endRad = startRad + (length * 0.2);

            VertexConsumer lineBuffer = buffer.getBuffer(RenderType.lines());

            renderCone(
                ms,
                lineBuffer,
                start.subtract(Vec3.atLowerCornerOf(be.getBlockPos())),
                directionVec,
                length,
                startRad,
                endRad,
                1.0F,
                0.2F,
                0.2F,
                1.0F);
        }
    }

    private void renderCone(
        PoseStack ms,
        VertexConsumer buffer,
        @NotNull Vec3 start,
        @NotNull Vec3 dir,
        double length,
        double startRad,
        double endRad,
        float r,
        float g,
        float b,
        float a) {
        Vec3 end = start.add(dir.scale(length));

        Vec3 axis1;
        if (Math.abs(dir.x) < 0.9) {
            axis1 = dir.cross(new Vec3(1, 0, 0)).normalize();
        } else {
            axis1 = dir.cross(new Vec3(0, 1, 0)).normalize();
        }
        Vec3 axis2 = dir.cross(axis1).normalize();

        int segments = 16;
        for (int i = 0; i < segments; i++) {
            double angle1 = (i * 2 * Math.PI) / segments;
            double angle2 = ((i + 1) * 2 * Math.PI) / segments;

            Vec3 s1 =
                start.add(axis1.scale(Math.cos(angle1) * startRad))
                    .add(axis2.scale(Math.sin(angle1) * startRad));
            Vec3 s2 =
                start.add(axis1.scale(Math.cos(angle2) * startRad))
                    .add(axis2.scale(Math.sin(angle2) * startRad));
            Vec3 e1 =
                end.add(axis1.scale(Math.cos(angle1) * endRad))
                    .add(axis2.scale(Math.sin(angle1) * endRad));
            Vec3 e2 =
                end.add(axis1.scale(Math.cos(angle2) * endRad))
                    .add(axis2.scale(Math.sin(angle2) * endRad));

            line(ms, buffer, s1, s2, r, g, b, a);
            line(ms, buffer, e1, e2, r, g, b, a);
            line(ms, buffer, s1, e1, r, g, b, a);
        }
    }

    private void line(
        @NotNull PoseStack ms,
        @NotNull VertexConsumer buffer,
        @NotNull Vec3 p1,
        @NotNull Vec3 p2,
        float r,
        float g,
        float b,
        float a) {
        PoseStack.Pose pose = ms.last();
        buffer.addVertex(pose.pose(), (float) p1.x, (float) p1.y, (float) p1.z)
            .setColor(r, g, b, a)
            .setNormal(pose, 0, 1, 0);
        buffer.addVertex(pose.pose(), (float) p2.x, (float) p2.y, (float) p2.z)
            .setColor(r, g, b, a)
            .setNormal(pose, 0, 1, 0);
    }

    @Override
    public boolean shouldRenderOffScreen(@NotNull AbstractThrusterBlockEntity be) {
        return true;
    }

    @Override
    public int getViewDistance() {
        return 128;
    }

    private static @NotNull Quaternionf getRot(@NotNull Direction dir) {
        Direction exhaustDir = dir.getOpposite();
        Quaternionf rot = new Quaternionf();

        switch (exhaustDir) {
            case UP -> rot.rotationX((float) Math.PI);
            case NORTH -> rot.rotationX((float) (Math.PI / 2f));
            case SOUTH -> rot.rotationX((float) (-Math.PI / 2f));
            case EAST -> rot.rotationZ((float) (Math.PI / 2f));
            case WEST -> rot.rotationZ((float) (-Math.PI / 2f));
            default ->  rot.identity();
        }

        return rot;
    }
}
