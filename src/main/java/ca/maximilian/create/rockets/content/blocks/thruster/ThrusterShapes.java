package ca.maximilian.create.rockets.content.blocks.thruster;

import java.util.function.BiFunction;
import lombok.experimental.UtilityClass;
import net.createmod.catnip.math.VoxelShaper;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public final class ThrusterShapes {

    public static final VoxelShaper RAPTOR_3_BASE =
            shape(0, 0, 0, 16, 5, 16) // Plate (Element 5)
                    .add(4, 5, 11, 11, 14, 12) // (4)
                    .add(11, 5, 5, 12, 14, 12) // (6)
                    .add(5, 5, 4, 12, 14, 5) // (7)
                    .add(4, 5, 4, 5, 14, 11) // (8)
                    .add(6, 14, 5, 11, 16, 6) // (9 part)
                    .add(5, 14, 10, 10, 16, 11) // (14 part)
                    .add(10, 14, 6, 11, 16, 11) // (19 part)
                    .add(5, 14, 5, 6, 16, 10) // (24 part)
                    .forDirectional(Direction.UP);

    public static final VoxelShaper RAPTOR_3_EXTENSION =
            shape(15, 14, 0, 16, 16, 15) // (0 part: 30-32 -> 14-16)
                    .add(0, 14, 1, 1, 16, 16) // (1)
                    .add(1, 14, 15, 16, 16, 16) // (2)
                    .add(0, 14, 0, 15, 16, 1) // (3)
                    .add(6, 0, 5, 11, 1, 6) // (9 part: 14-17 -> 0-1)
                    .add(2, 11, 1, 15, 14, 2) // (10: 27-30 -> 11-14)
                    .add(3, 6, 2, 14, 11, 3) // (11: 22-27 -> 6-11)
                    .add(5, 1, 4, 12, 4, 5) // (12: 17-20 -> 1-4)
                    .add(4, 4, 3, 13, 6, 4) // (13: 20-22 -> 4-6)
                    .add(5, 0, 10, 10, 1, 11) // (14 part)
                    .add(1, 11, 14, 14, 14, 15) // (15)
                    .add(2, 6, 13, 13, 11, 14) // (16)
                    .add(4, 1, 11, 11, 4, 12) // (17)
                    .add(3, 4, 12, 12, 6, 13) // (18)
                    .add(10, 0, 6, 11, 1, 11) // (19 part)
                    .add(14, 11, 2, 15, 14, 15) // (20)
                    .add(13, 6, 3, 14, 11, 14) // (21)
                    .add(11, 1, 5, 12, 4, 12) // (22)
                    .add(12, 4, 4, 13, 6, 13) // (23)
                    .add(5, 0, 5, 6, 1, 10) // (24 part)
                    .add(1, 11, 1, 2, 14, 14) // (25)
                    .add(2, 6, 2, 3, 11, 13) // (26)
                    .add(4, 1, 4, 5, 4, 11) // (27)
                    .add(3, 4, 3, 4, 6, 12) // (28)
                    .forDirectional(Direction.UP);

    public static final VoxelShaper SATURN_V_F1_BASE =
            shape(0, 0, 0, 16, 5, 16) // (0)
                    .add(4, 13, 4, 12, 16, 5) // (8)
                    .add(5, 12, 5, 11, 13, 6) // (9)
                    .add(5, 5, 4, 11, 12, 5) // (10)
                    .add(4, 13, 11, 12, 16, 12) // (14)
                    .add(5, 12, 10, 11, 13, 11) // (15)
                    .add(5, 5, 11, 11, 12, 12) // (16)
                    .add(4, 13, 5, 5, 16, 11) // (20)
                    .add(5, 12, 6, 6, 13, 10) // (21)
                    .add(4, 5, 5, 5, 12, 11) // (22)
                    .add(11, 13, 5, 12, 16, 11) // (26)
                    .add(10, 12, 6, 11, 13, 10) // (27)
                    .add(11, 5, 5, 12, 12, 11) // (28)
                    .add(13, 13, 6, 16, 16, 10) // (32)
                    .add(13, 5, 5, 16, 13, 11) // (33)
                    .forDirectional(Direction.UP);

    public static final VoxelShaper SATURN_V_F1_EXTENSION =
            shape(1, 14, 0, 16, 16, 1) // (1)
                    .add(0, 14, 15, 15, 16, 16) // (2)
                    .add(0, 14, 0, 1, 16, 15) // (3)
                    .add(15, 14, 1, 16, 16, 16) // (4)
                    .add(1, 9, 1, 15, 14, 2) // (5)
                    .add(2, 3, 2, 14, 9, 3) // (6)
                    .add(3, 0, 3, 13, 3, 4) // (7)
                    .add(1, 9, 14, 15, 14, 15) // (11)
                    .add(2, 3, 13, 14, 9, 14) // (12)
                    .add(3, 0, 12, 13, 3, 13) // (13)
                    .add(1, 9, 2, 2, 14, 14) // (17)
                    .add(2, 3, 3, 3, 9, 13) // (18)
                    .add(3, 0, 4, 4, 3, 12) // (19)
                    .add(14, 9, 2, 15, 14, 14) // (23)
                    .add(13, 3, 3, 14, 9, 13) // (24)
                    .add(12, 0, 4, 13, 3, 12) // (25)
                    .add(0, 5, 0, 16, 7, 16) // (29)
                    .add(14, 2, 7, 16, 5, 9) // (30)
                    .add(14, 0, 6, 16, 2, 10) // (31)
                    .add(2, 12, 2, 14, 13, 14) // (34)
                    .forDirectional(Direction.UP);

    @Contract("_, _, _, _, _, _ -> new")
    public static @NotNull Builder shape(
            final double x1,
            final double y1,
            final double z1,
            final double x2,
            final double y2,
            final double z2) {
        return new Builder(Block.box(x1, y1, z1, x2, y2, z2));
    }

    public static final class Builder {
        private VoxelShape shape;

        private Builder(final VoxelShape shape) {
            this.shape = shape;
        }

        public Builder add(
                final double x1,
                final double y1,
                final double z1,
                final double x2,
                final double y2,
                final double z2) {
            this.shape = Shapes.or(this.shape, Block.box(x1, y1, z1, x2, y2, z2));
            return this;
        }

        public Builder erase(
                final double x1,
                final double y1,
                final double z1,
                final double x2,
                final double y2,
                final double z2) {
            this.shape =
                    Shapes.join(
                            this.shape, Block.box(x1, y1, z1, x2, y2, z2), BooleanOp.ONLY_FIRST);
            return this;
        }

        public VoxelShaper forDirectional(final Direction direction) {
            return this.build(VoxelShaper::forDirectional, direction);
        }

        private VoxelShaper build(
                final BiFunction<VoxelShape, Direction, VoxelShaper> factory,
                final Direction direction) {
            return factory.apply(this.shape, direction);
        }
    }
}
