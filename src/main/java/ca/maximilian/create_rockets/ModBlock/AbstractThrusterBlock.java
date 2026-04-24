package ca.maximilian.create_rockets.ModBlock;

import com.simibubi.create.content.equipment.wrench.WrenchItem;
import com.simibubi.create.foundation.block.IBE;
import net.createmod.catnip.math.VoxelShaper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractThrusterBlock<T extends AbstractThrusterBlockEntity> extends Block implements IBE<T> {

    public static final EnumProperty<Direction> FACING = BlockStateProperties.FACING;
    public static final BooleanProperty REVERSED = BooleanProperty.create("reversed");
    public static final EnumProperty<ThrusterPart> PART = EnumProperty.create("part", ThrusterPart.class);
    public static final EnumProperty<ThrusterType> TYPE = EnumProperty.create("type", ThrusterType.class);
    private final VoxelShaper baseShape;
    private final VoxelShaper extensionShape;

    protected AbstractThrusterBlock(final Properties properties, final VoxelShaper baseShape, final VoxelShaper extensionShape) {
        super(properties);
        this.baseShape = baseShape;
        this.extensionShape = extensionShape;
        this.registerDefaultState(this.defaultBlockState()
                .setValue(FACING, Direction.UP)
                .setValue(REVERSED, false)
                .setValue(PART, ThrusterPart.BASE)
                .setValue(TYPE, ThrusterType.RAPTOR_3));
    }

    public Direction getAttachedDirection(final BlockState state) {
        return state.getValue(PART) == ThrusterPart.BASE
                ? state.getValue(FACING)
                : state.getValue(FACING).getOpposite();
    }

    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, REVERSED, PART, TYPE);
    }

    @Override
    public int getLightEmission(@NotNull BlockState state, BlockGetter level, @NotNull BlockPos pos) {
        if (this.getBaseBlockEntity(level, state, pos) instanceof AbstractThrusterBlockEntity be) {
            return be.isActive() ? 15 : 0;
        }
        return 0;
    }

    @Override
    public BlockState getStateForPlacement(final BlockPlaceContext context) {
        // Default to pointing away from the player
        Direction facing = context.getNearestLookingDirection().getOpposite();

        // If the player is shifting, they might want to force a specific attachment
        if (context.getPlayer() != null && context.getPlayer().isShiftKeyDown()) {
            facing = context.getClickedFace();
        }

        final BlockPos basePos = context.getClickedPos();

        // Check if the extension part would be blocked
        if (!context.getLevel().getBlockState(basePos.relative(facing)).canBeReplaced()) {
            // Try the opposite direction automatically if not forcing with shift
            if (context.getPlayer() == null || !context.getPlayer().isShiftKeyDown()) {
                Direction flipped = facing.getOpposite();
                if (context.getLevel().getBlockState(basePos.relative(flipped)).canBeReplaced()) {
                    facing = flipped;
                } else {
                    return null; // Both directions are blocked
                }
            } else {
                return null; // Forced direction is blocked
            }
        }

        return this.defaultBlockState()
                .setValue(FACING, facing)
                .setValue(PART, ThrusterPart.BASE);
    }

    @Override
    public @NotNull VoxelShape getShape(final BlockState state, final @NotNull BlockGetter level, final @NotNull BlockPos pos, final @NotNull CollisionContext context) {
        return (state.getValue(PART) == ThrusterPart.BASE ? this.baseShape : this.extensionShape).get(state.getValue(FACING));
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(
            final @NotNull BlockState state, final Level level, final @NotNull BlockPos pos,
            final net.minecraft.world.entity.player.@NotNull Player player, final @NotNull BlockHitResult hitResult) {
        if (level.isClientSide) return InteractionResult.SUCCESS;

        if (player.getMainHandItem().getItem() instanceof WrenchItem) {
            return InteractionResult.PASS;
        }

        final T blockEntity = this.getBaseBlockEntity(level, state, pos);
        if (blockEntity != null && player instanceof ServerPlayer serverPlayer) {
            serverPlayer.openMenu(blockEntity, buffer -> buffer.writeBlockPos(blockEntity.getBlockPos()));
            return InteractionResult.CONSUME;
        }

        return InteractionResult.PASS;
    }

    @Override
    public boolean canSurvive(final @NotNull BlockState state, final @NotNull LevelReader level, final @NotNull BlockPos pos) {
        if (state.getValue(PART) == ThrusterPart.BASE) {
            return true;
        }
        final BlockPos counterpartPos = this.getCounterpartPos(state, pos);
        final BlockState counterpartState = level.getBlockState(counterpartPos);
        return counterpartState.is(this)
                && counterpartState.getValue(FACING) == state.getValue(FACING)
                && counterpartState.getValue(PART) != state.getValue(PART);
    }

    @Override
    public void setPlacedBy(
            final @NotNull Level level,
            final @NotNull BlockPos pos,
            final @NotNull BlockState state,
            @Nullable final LivingEntity placer,
            final @NotNull ItemStack stack
    ) {
        super.setPlacedBy(level, pos, state, placer, stack);

        final BlockPos extensionPos = pos.relative(state.getValue(FACING));
        level.setBlock(
                extensionPos,
                state.setValue(PART, ThrusterPart.EXTENSION),
                Block.UPDATE_ALL
        );
    }

    @Override
    public void onRemove(final @NotNull BlockState state, final @NotNull Level level, final @NotNull BlockPos pos, final @NotNull BlockState newState, final boolean isMoving) {
        if (!isMoving && state.hasBlockEntity() && (state.getBlock() != newState.getBlock() || !newState.hasBlockEntity())) {
            final T blockEntity = this.getBlockEntity(level, pos);
            if (blockEntity != null) {
                blockEntity.dropFuelInventory();
            }
        }

        IBE.onRemove(state, level, pos, newState);
    }

    @Override
    public @NotNull BlockState playerWillDestroy(final @NotNull Level level, final @NotNull BlockPos pos, final @NotNull BlockState state, final net.minecraft.world.entity.player.@NotNull Player player) {
        final BlockPos counterpartPos = this.getCounterpartPos(state, pos);
        final BlockState counterpartState = level.getBlockState(counterpartPos);
        if (counterpartState.is(this)
                && counterpartState.getValue(PART) != state.getValue(PART)
                && counterpartState.getValue(FACING) == state.getValue(FACING)) {
            level.setBlock(counterpartPos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL | Block.UPDATE_SUPPRESS_DROPS);
        }

        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    public @Nullable net.minecraft.world.level.block.entity.BlockEntity newBlockEntity(final @NotNull BlockPos pos, final @NotNull BlockState state) {
        if (state.getValue(PART) != ThrusterPart.BASE) {
            return null;
        }
        return IBE.super.newBlockEntity(pos, state);
    }

    @Override
    protected @NotNull BlockState updateShape(final @NotNull BlockState state, final @NotNull Direction direction, final @NotNull BlockState neighborState, final @NotNull LevelAccessor level, final @NotNull BlockPos pos, final @NotNull BlockPos neighborPos) {
        final Direction counterpartDirection = this.getAttachedDirection(state);
        if (direction == counterpartDirection && !state.canSurvive(level, pos)) {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    public @NotNull BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    @Override
    public @NotNull BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    public BlockPos getBasePos(final BlockState state, final BlockPos pos) {
        return state.getValue(PART) == ThrusterPart.BASE ? pos : pos.relative(state.getValue(FACING).getOpposite());
    }

    public @Nullable T getBaseBlockEntity(final BlockGetter level, final BlockState state, final BlockPos pos) {
        return this.getBlockEntity(level, this.getBasePos(state, pos));
    }

    private BlockPos getCounterpartPos(final BlockState state, final BlockPos pos) {
        return pos.relative(this.getAttachedDirection(state));
    }

    @Override
    public abstract Class<T> getBlockEntityClass();

    @Override
    public abstract BlockEntityType<? extends T> getBlockEntityType();
}
