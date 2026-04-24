package ca.maximilian.create_rockets.ModBlock;

import ca.maximilian.create_rockets.CreateRocketsPartialModels;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;

public class ThrusterBlockRenderer extends SafeBlockEntityRenderer<AbstractThrusterBlockEntity> {

    public ThrusterBlockRenderer(final BlockEntityRendererProvider.Context context) {
    }

    @Override
    protected void renderSafe(AbstractThrusterBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer,
                               int light, int overlay) {

        if (be.getLevel() == null) return;

        final Direction dir = be.getBlockState().getValue(BlockStateProperties.FACING);
        final ThrusterType type = be.getBlockState().getValue(AbstractThrusterBlock.TYPE);

        final PartialModel model = type == ThrusterType.SATURN_V_F1 
                ? CreateRocketsPartialModels.SATURN_V_F1 
                : CreateRocketsPartialModels.RAPTOR_3;
        final PartialModel flameModel = type == ThrusterType.SATURN_V_F1
                ? CreateRocketsPartialModels.THRUSTER_FLAME
                : CreateRocketsPartialModels.THRUSTER_FLAME_BLUE;

        final SuperByteBuffer rocket = CachedBuffers.partial(model, be.getBlockState());
        final SuperByteBuffer flame = CachedBuffers.partial(flameModel, be.getBlockState());
        
        rocket.light(light)
                .overlay(overlay);
        
        flame.light(LightTexture.FULL_BRIGHT)
                .overlay(OverlayTexture.NO_OVERLAY);

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

        EntityRenderDispatcher dispatcher =  Minecraft.getInstance().getEntityRenderDispatcher();

        if (dispatcher.shouldRenderHitBoxes()) {
            AABB box = be.getDmgBox();
            if (box != null) {
                VertexConsumer lineBuffer = buffer.getBuffer(RenderType.lines());

                AABB localBox = box.move(
                        -be.getBlockPos().getX(),
                        -be.getBlockPos().getY(),
                        -be.getBlockPos().getZ()
                );

                LevelRenderer.renderLineBox(
                        ms,
                        lineBuffer,
                        localBox,
                        1.0F, 0.2F, 0.2F, 1.0F
                );
            }
        }
    }

    @Override
    public boolean shouldRenderOffScreen(AbstractThrusterBlockEntity be) {
        return true;
    }

    @Override
    public int getViewDistance() {
        return 128;
    }

    private static @NotNull Quaternionf getRot(Direction dir) {
        Direction exhaustDir = dir.getOpposite();
        Quaternionf rot = new Quaternionf();

        switch (exhaustDir) {
            case DOWN -> rot.identity();
            case UP -> rot.rotationX((float) Math.PI);
            case NORTH -> rot.rotationX((float) (Math.PI / 2f));
            case SOUTH -> rot.rotationX((float) (-Math.PI / 2f));
            case EAST -> rot.rotationZ((float) (Math.PI / 2f));
            case WEST -> rot.rotationZ((float) (-Math.PI / 2f));
        }

        return rot;
    }
}
