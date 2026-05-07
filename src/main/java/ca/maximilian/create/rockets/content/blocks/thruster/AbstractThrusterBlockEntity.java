package ca.maximilian.create.rockets.content.blocks.thruster;

import ca.maximilian.create.rockets.CreateRocketsConfigService;
import ca.maximilian.create.rockets.client.sound.ThrusterSoundInstance;
import ca.maximilian.create.rockets.index.CreateRocketsParticleTypes;
import ca.maximilian.create.rockets.menu.ThrusterFuelMenu;
import com.simibubi.create.AllItems;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.item.ItemHelper;
import dev.eriksonn.aeronautics.content.blocks.propeller.behaviour.PropellerActorBehaviour;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.block.propeller.BlockEntityPropeller;
import dev.ryanhcode.sable.api.block.propeller.BlockEntitySubLevelPropellerActor;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.ryanhcode.sable.sublevel.SubLevel;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class AbstractThrusterBlockEntity extends SmartBlockEntity
    implements BlockEntitySubLevelPropellerActor, BlockEntityPropeller, MenuProvider {

    private static final int FUEL_SLOT = 0;
    private static final float FUEL_CONSUMPTION_MULTIPLIER = 2.0f;

    protected final ItemStackHandler fuelInventory;
    protected final IItemHandler backFuelHandler;
    @Getter
    protected final Container fuelContainer;
    @Getter
    protected final ContainerData menuData;
    @Getter
    protected float intensity = 0;
    protected PropellerActorBehaviour thrusterBehaviour;
    protected AABB dmgBox;

    private final Quaternionf rotation = new Quaternionf();
    private boolean wasActiveLastTick;
    private Object soundInstance;
    @Getter
    private int fuelTicksRemaining;
    @Getter
    private int fuelTicksTotal;
    @Getter
    @Setter
    private long lastTickTime = System.nanoTime();

    protected AbstractThrusterBlockEntity(
        final BlockEntityType<?> type, final BlockPos pos, final BlockState state) {
        super(type, pos, state);
        this.fuelInventory =
            new ItemStackHandler(1) {
                @Override
                protected void onContentsChanged(final int slot) {
                    if (fuelTicksRemaining == Integer.MAX_VALUE) {
                        ItemStack current = getStackInSlot(slot);

                        boolean isCreative =
                            !current.isEmpty()
                                && BuiltInRegistries.ITEM
                                .getKey(current.getItem())
                                .equals(AllItems.CREATIVE_BLAZE_CAKE.getId());

                        if (!isCreative) {
                            fuelTicksRemaining = 0;
                            fuelTicksTotal = 0;
                        }
                    }
                    setChanged();
                    if (level != null && !level.isClientSide) {
                        level.sendBlockUpdated(
                            worldPosition, getBlockState(), getBlockState(), 3);
                    }
                }

                @Override
                public boolean isItemValid(final int slot, final @NotNull ItemStack stack) {
                    return slot == FUEL_SLOT && getCustomBurnTime(stack) > 0;
                }
            };
        this.backFuelHandler = new BackFuelItemHandler();
        this.fuelContainer = new FuelContainer();
        this.menuData =
            new ContainerData() {
                @Override
                public int get(final int index) {
                    return switch (index) {
                        case 0 -> getRedstoneSignal();
                        case 1 -> getFuelTicksRemaining();
                        case 2 -> getFuelTicksTotal();
                        default -> 0;
                    };
                }

                @Override
                public void set(final int index, final int value) {
                    switch (index) {
                        case 1 -> fuelTicksRemaining = value;
                        case 2 -> fuelTicksTotal = value;
                        default -> {
                        }
                    }
                }

                @Override
                public int getCount() {
                    return 3;
                }
            };
    }

    public static void registerCapabilities(
        final RegisterCapabilitiesEvent event,
        final BlockEntityType<? extends AbstractThrusterBlockEntity> type) {
        event.registerBlockEntity(
            Capabilities.ItemHandler.BLOCK,
            type,
            (be, context) -> context == be.getBackFace() ? be.backFuelHandler : null);
    }

    @Override
    public void addBehaviours(final List<BlockEntityBehaviour> behaviours) {
        this.thrusterBehaviour = this.createThrusterBehaviour();
        behaviours.add(this.thrusterBehaviour);
    }

    protected PropellerActorBehaviour createThrusterBehaviour() {
        final PropellerActorBehaviour behaviour = new PropellerActorBehaviour(this, this);
        behaviour.setThrustDirection(
            JOMLConversion.toJOML(Vec3.atLowerCornerOf(this.getBlockDirection().getNormal())));
        behaviour.setParticleAmountUpdater(() -> 0.12 * Math.abs(this.intensity));
        behaviour.setParticleCountProperties(5, 2);

        behaviour.addSimpleLayer(1f, 1f);
        behaviour.setParticlePositionUpdater(
            (vector, random) -> {
                final PropellerActorBehaviour.PropellerLayer layer =
                    behaviour.getLayers().get(random.nextInt(behaviour.getLayers().size()));
                final double radius =
                    Math.sqrt(
                        Mth.lerp(
                            random.nextFloat(),
                            layer.innerRadiusSquared(),
                            layer.outerRadiusSquared()));
                final double angle = Math.PI * 2.0 * random.nextFloat();
                vector.set(Math.cos(angle) * radius, layer.offset(), Math.sin(angle) * radius);
                this.rotation.transform(vector);
            });

        return behaviour;
    }

    public abstract ThrusterStats getThrusterStats();

    @Override
    public BlockEntityPropeller getPropeller() {
        return this;
    }

    @Override
    public void tick() {
        super.tick();
        this.tickFuel();

        long now = System.nanoTime();
        float deltaTime =
                (now - getLastTickTime()) / 1_000_000_000.0f;

        this.updateIntensity(deltaTime);

        setLastTickTime(now);

        this.rotation.set(this.getBlockDirection().getRotation());

        boolean active = this.isActive();

        if (active && level != null) {
            if (!level.isClientSide) {
                this.onActiveTick();
            } else {
                this.spawnExhaustParticles();
                this.tickThrusterSoundClient();
            }
        }

        if (level != null && !level.isClientSide) {
            this.tickThrusterSound();
        }
    }

    protected void tickThrusterSoundClient() {
        if (this.level == null || !this.level.isClientSide) return;

        float throttle = this.getIntensity();
        if (throttle > 0.01f
            && this.isActive()
            && (this.soundInstance == null
            || ((ThrusterSoundInstance) this.soundInstance).isStopped())) {
            this.soundInstance = new ThrusterSoundInstance(this);
            Minecraft.getInstance()
                .getSoundManager()
                .play((ThrusterSoundInstance) this.soundInstance);
        }
    }

    @Override
    public void invalidate() {
        super.invalidate();
        if (level != null && level.isClientSide && soundInstance != null) {
            ((ThrusterSoundInstance) soundInstance).stopSound();
        }
    }

    @Override
    public double getScaledThrust() {
        double pressure = this.getCurrentAirPressure();

        double stretchedPressure = Math.min(1.0, pressure * 4.0);

        return -this.getThrust() * stretchedPressure;
    }

    protected void onActiveTick() {
        this.pushEntitiesWithFire();
        if (this.thrusterBehaviour != null) {
            this.thrusterBehaviour.spawnParticles();
        }
        this.spawnExhaustParticles();
    }

    protected void pushEntitiesWithFire() {
        if (this.thrusterBehaviour != null) {
            this.thrusterBehaviour.pushEntities();
        }
        this.applyFireDamageToPushedEntities();
    }

    protected void applyFireDamageToPushedEntities() {
        if (this.level == null || this.level.isClientSide) {
            return;
        }

        final Direction direction = this.getBlockDirection();
        final Vec3 directionVec = Vec3.atLowerCornerOf(direction.getNormal());
        final Vec3 thrustOrigin = this.getBlockPos().getCenter();
        final double forwardOffset = 3;
        final Vec3 start = thrustOrigin.add(directionVec.scale(forwardOffset));

        final double reach = 2.5d;
        final double length =
            Math.max(
                0.0,
                reach
                    * 5
                    * ((float) this.getThrust()
                    / (float) this.getThrusterStats().thrust()));
        final double startRad = 1d;
        final double endRad = startRad + (length * 0.2);

        final AABB damageBox = computeDamageBox();
        dmgBox = damageBox;

        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);

        final SubLevel subLevel = Sable.HELPER.getContaining(this);
        final Level outerLevel = subLevel != null ? subLevel.getLevel() : this.level;
        final Set<Entity> entitiesToDamage = new HashSet<>();

        if (subLevel == null) {
            List<Entity> candidateEntities = this.level.getEntities(null, damageBox);
            for (Entity entity : candidateEntities) {
                if (isPointInCone(
                    entity.position(), start, directionVec, length, endRad)) {
                    entitiesToDamage.add(entity);
                }
            }
        } else {
            for (int lx = Mth.floor(damageBox.minX); lx <= Mth.floor(damageBox.maxX); lx++) {
                for (int ly = Mth.floor(damageBox.minY); ly <= Mth.floor(damageBox.maxY); ly++) {
                    for (int lz = Mth.floor(damageBox.minZ);
                         lz <= Mth.floor(damageBox.maxZ);
                         lz++) {
                        Vec3 localPos = new Vec3(lx + 0.5, ly + 0.5, lz + 0.5);
                        if (!isPointInCone(
                            localPos, start, directionVec, length, endRad)) {
                            continue;
                        }

                        final Vec3 worldVec =
                            Sable.HELPER.projectOutOfSubLevel(this.level, localPos);
                        entitiesToDamage.addAll(
                            outerLevel.getEntities(
                                null,
                                new AABB(
                                    worldVec.x - 0.75,
                                    worldVec.y - 0.75,
                                    worldVec.z - 0.75,
                                    worldVec.x + 0.75,
                                    worldVec.y + 0.75,
                                    worldVec.z + 0.75)));
                    }
                }
            }
        }

        for (final Entity entity : entitiesToDamage) {
            if (entity.isAlive() && !entity.isOnFire()) {
                entity.igniteForSeconds(2);
                entity.hurt(outerLevel.damageSources().inFire(), 2);
            }
        }

        if (CreateRocketsConfigService.server.evisceration.get()) {

            final RandomSource random = outerLevel.random;

            for (int lx = Mth.floor(damageBox.minX); lx <= Mth.floor(damageBox.maxX); lx++) {
                for (int ly = Mth.floor(damageBox.minY); ly <= Mth.floor(damageBox.maxY); ly++) {
                    for (int lz = Mth.floor(damageBox.minZ);
                         lz <= Mth.floor(damageBox.maxZ);
                         lz++) {
                        Vec3 localPos = new Vec3(lx + 0.5, ly + 0.5, lz + 0.5);
                        if (!isPointInCone(
                            localPos, start, directionVec, length, endRad)) {
                            continue;
                        }

                        if (random.nextFloat()
                            > CreateRocketsConfigService.server.eviscerationRate.get() / 100f)
                            continue;

                        final Vec3 worldVec =
                            Sable.HELPER.projectOutOfSubLevel(this.level, localPos);
                        BlockPos pos = BlockPos.containing(worldVec);
                        BlockPos below = pos.below();

                        if (outerLevel.isEmptyBlock(pos)
                            && outerLevel.getBlockState(below).isSolidRender(outerLevel, below)
                            && net.minecraft.world.level.block.BaseFireBlock.canBePlacedAt(
                            outerLevel, pos, direction)) {

                            outerLevel.setBlock(
                                pos,
                                net.minecraft.world.level.block.Blocks.FIRE.defaultBlockState(),
                                11);
                        } else if (outerLevel
                            .getBlockState(pos)
                            .isFlammable(outerLevel, pos, direction)) {
                            outerLevel.destroyBlock(pos, true);
                        } else if (outerLevel.getBlockState(pos).is(BlockTags.IMPERMEABLE)) {
                            outerLevel.destroyBlock(pos, true);
                        }
                    }
                }
            }
        }
    }

    public float getThrustMagnitude() {
        return Math.abs(this.intensity);
    }

    protected void spawnExhaustParticles() {
        if (this.level == null || !this.level.isClientSide) {
            return;
        }

        final Direction thrustDirection = this.getBlockDirection();
        final Vec3 localExhaustDirection = Vec3.atLowerCornerOf(thrustDirection.getNormal());
        final Vec3 center = this.getBlockPos().getCenter();
        final Vec3 localNozzleCenter = center.add(localExhaustDirection.scale(0.42));
        final Vec3 worldNozzleCenter =
            Sable.HELPER.projectOutOfSubLevel(this.level, localNozzleCenter);
        final Vec3 worldExhaustSample =
            Sable.HELPER.projectOutOfSubLevel(
                this.level, localNozzleCenter.add(localExhaustDirection));
        final Vec3 exhaustDirection = worldExhaustSample.subtract(worldNozzleCenter).normalize();
        final RandomSource random = this.level.random;
        final double radius = 0.35d;

        final SubLevel subLevel = Sable.HELPER.getContaining(this);
        final Level outerLevel = subLevel != null ? subLevel.getLevel() : this.level;

        for (int i = 0; i < 3; i++) {
            final double spreadX = (random.nextDouble() - 0.5) * radius;
            final double spreadY = (random.nextDouble() - 0.5) * radius;
            final double spreadZ = (random.nextDouble() - 0.5) * radius;

            final double x = worldNozzleCenter.x + spreadX;
            final double y = worldNozzleCenter.y + spreadY;
            final double z = worldNozzleCenter.z + spreadZ;

            final double speed = 2 + random.nextDouble() * 0.06;
            final double vx = exhaustDirection.x * speed + spreadX * 0.15;
            final double vy = exhaustDirection.y * speed + spreadY * 0.15;
            final double vz = exhaustDirection.z * speed + spreadZ * 0.15;

            final double offset =
                Math.max(
                    6
                        * ((float) this.getThrust()
                        / (float) this.getThrusterStats().thrust()),
                    2);
            final double cx = x + exhaustDirection.x * offset;
            final double cy = y + exhaustDirection.y * offset;
            final double cz = z + exhaustDirection.z * offset;

            outerLevel.addParticle(
                CreateRocketsParticleTypes.LARGE_SMOKE.get(),
                cx,
                cy,
                cz,
                vx,
                vy,
                vz);
        }
    }

    protected void tickThrusterSound() {
        if (this.level == null || this.level.isClientSide) {
            return;
        }

        float throttle = (float) this.getThrust() / (float) this.getThrusterStats().thrust();

        if (throttle <= 0.01f) {
            this.wasActiveLastTick = false;
            return;
        }

        if (!this.wasActiveLastTick) {
            this.playThrusterSound(
                this.getStartupSound(),
                this.getStartupSoundVolume(),
                this.getStartupSoundPitch());
        }

        this.wasActiveLastTick = true;
    }

    protected void playThrusterSound(
        final SoundEvent sound, final float volume, final float pitch) {
        final Vec3 worldPos =
            Sable.HELPER.projectOutOfSubLevel(this.level, this.getBlockPos().getCenter());
        assert this.level != null;
        this.level.playSound(
            null, worldPos.x, worldPos.y, worldPos.z, sound, SoundSource.BLOCKS, volume, pitch);
    }

    protected SoundEvent getStartupSound() {
        return SoundEvents.FIRECHARGE_USE;
    }

    protected float getStartupSoundVolume() {
        return 0.35f;
    }

    protected float getStartupSoundPitch() {
        return 0.8f;
    }

    protected Direction getBackFace() {
        return this.getBlockDirection().getOpposite();
    }

    protected int getCustomBurnTime(ItemStack stack) {
        int standard = stack.getBurnTime(null);
        if (standard > 0) return standard;

        String id = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
        if (id.equals("create:blaze_cake")) {
            return 6400;
        }
        if (id.equals("create:creative_blaze_cake")) {
            return Integer.MAX_VALUE;
        }
        return 0;
    }

    protected boolean hasFuel() {
        return this.fuelTicksRemaining > 0;
    }

    protected int getRedstoneSignal() {
        return this.level == null ? 0 : this.level.getBestNeighborSignal(this.worldPosition);
    }

    protected float getThrottle() {
        return this.getRedstoneSignal() / 15.0f;
    }

    private void tickFuel() {
        if (this.level == null) {
            return;
        }

        final boolean wasActive = this.hasFuel();
        final float throttle = this.getThrottle();

        if (this.fuelTicksRemaining > 0
            && throttle > 0
            && this.fuelTicksRemaining != Integer.MAX_VALUE) {
            int fuelToConsume = Math.max(1, (int) (throttle * FUEL_CONSUMPTION_MULTIPLIER));
            this.fuelTicksRemaining = Math.max(0, this.fuelTicksRemaining - fuelToConsume);
        }

        if (!this.level.isClientSide) {
            if (this.fuelTicksRemaining <= 0 && throttle > 0) {
                this.tryConsumeFuel();
            }

            if (wasActive != this.hasFuel()) {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            }
        }
    }

    private void tryConsumeFuel() {
        final ItemStack stack = this.fuelInventory.getStackInSlot(FUEL_SLOT);
        if (stack.isEmpty()) {
            this.fuelTicksRemaining = 0;
            this.fuelTicksTotal = 0;
            return;
        }

        final int burnTime = this.getCustomBurnTime(stack);
        if (burnTime <= 0) {
            return;
        }

        boolean isCreative =
            BuiltInRegistries.ITEM
                .getKey(stack.getItem())
                .toString()
                .equals("create:creative_blaze_cake");

        if (!isCreative) {
            this.fuelInventory.extractItem(FUEL_SLOT, 1, false);

            if (stack.hasCraftingRemainingItem()) {
                if (this.fuelInventory.getStackInSlot(FUEL_SLOT).isEmpty()) {
                    this.fuelInventory.setStackInSlot(
                        FUEL_SLOT, new ItemStack(stack.getCraftingRemainingItem().getItem()));
                }
            }
        }

        this.fuelTicksTotal = burnTime;
        final float throttle = this.getThrottle();
        if (throttle > 0 && !isCreative) {
            this.fuelTicksRemaining = (int) (burnTime / (throttle * FUEL_CONSUMPTION_MULTIPLIER));
        } else if (isCreative) {
            this.fuelTicksRemaining = Integer.MAX_VALUE;
        } else {
            this.fuelTicksRemaining = burnTime;
        }
        setChanged();
        assert level != null;
        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
    }

    private void updateIntensity(float deltaTime) {
        final float targetIntensity = this.hasFuel() ? this.getThrottle() : 0.0f;

        float riseTime = 0.4f;
        float fallTime = 5.0f;

        float riseSpeed = 1.0f / riseTime;
        float fallSpeed = 1.0f / fallTime;

        if (this.intensity < targetIntensity) {
            this.intensity += riseSpeed * deltaTime;

            if (this.intensity > targetIntensity)
                this.intensity = targetIntensity;

        } else if (this.intensity > targetIntensity) {
            this.intensity -= fallSpeed * deltaTime;

            if (this.intensity < targetIntensity)
                this.intensity = targetIntensity;
        }
    }

    @Override
    protected void write(
        final CompoundTag compound,
        final HolderLookup.Provider registries,
        final boolean clientPacket) {
        super.write(compound, registries, clientPacket);
        compound.put("FuelInventory", this.fuelInventory.serializeNBT(registries));
        compound.putInt("FuelTicksRemaining", this.fuelTicksRemaining);
        compound.putInt("FuelTicksTotal", this.fuelTicksTotal);
        compound.putFloat("Intensity", this.intensity);

        if (this.dmgBox != null) {
            CompoundTag boxTag = new CompoundTag();
            boxTag.putDouble("minX", this.dmgBox.minX);
            boxTag.putDouble("minY", this.dmgBox.minY);
            boxTag.putDouble("minZ", this.dmgBox.minZ);
            boxTag.putDouble("maxX", this.dmgBox.maxX);
            boxTag.putDouble("maxY", this.dmgBox.maxY);
            boxTag.putDouble("maxZ", this.dmgBox.maxZ);
            compound.put("DmgBox", boxTag);
        }
    }

    @Override
    protected void read(
        final CompoundTag compound,
        final HolderLookup.Provider registries,
        final boolean clientPacket) {
        super.read(compound, registries, clientPacket);
        this.fuelInventory.deserializeNBT(registries, compound.getCompound("FuelInventory"));
        this.fuelTicksRemaining = compound.getInt("FuelTicksRemaining");
        this.fuelTicksTotal = compound.getInt("FuelTicksTotal");
        this.intensity = compound.getFloat("Intensity");

        if (compound.contains("DmgBox")) {
            CompoundTag boxTag = compound.getCompound("DmgBox");
            this.dmgBox =
                new AABB(
                    boxTag.getDouble("minX"),
                    boxTag.getDouble("minY"),
                    boxTag.getDouble("minZ"),
                    boxTag.getDouble("maxX"),
                    boxTag.getDouble("maxY"),
                    boxTag.getDouble("maxZ"));
        } else {
            this.dmgBox = null;
        }
    }

    private AABB computeDamageBox() {
        final double reach = 2.5d;
        final Direction direction = this.getBlockDirection();
        final Vec3 directionVec = Vec3.atLowerCornerOf(direction.getNormal());

        final Vec3 thrustOrigin = this.getBlockPos().getCenter();

        final double forwardOffset = 3d;
        final double startRad = 1d;
        final double length =
            Math.max(
                0.0,
                reach
                    * 8
                    * ((float) this.getThrust()
                    / (float) this.getThrusterStats().thrust()));
        final double endRad = startRad + (length * 0.2);

        final Vec3 start = thrustOrigin.add(directionVec.scale(forwardOffset));
        final Vec3 end = start.add(directionVec.scale(length));

        double maxRad = Math.max(startRad, endRad);

        return new AABB(
            Math.min(start.x, end.x) - maxRad,
            Math.min(start.y, end.y) - maxRad,
            Math.min(start.z, end.z) - maxRad,
            Math.max(start.x, end.x) + maxRad,
            Math.max(start.y, end.y) + maxRad,
            Math.max(start.z, end.z) + maxRad);
    }

    private boolean isPointInCone(
        Vec3 point,
        Vec3 start,
        Vec3 directionVec,
        double length,
        double endRad) {
        Vec3 toPoint = point.subtract(start);
        double distForward = toPoint.dot(directionVec);

        if (distForward < 0 || distForward > length) {
            return false;
        }

        double progress = distForward / length;
        double currentRad = Mth.lerp(progress, 1.0, endRad);

        Vec3 project = directionVec.scale(distForward);
        Vec3 perp = toPoint.subtract(project);
        return perp.x * perp.x + perp.y * perp.y + perp.z * perp.z <= currentRad * currentRad;
    }

    public void dropFuelInventory() {
        ItemHelper.dropContents(level, worldPosition, this.fuelInventory);
    }

    @Override
    public Direction getBlockDirection() {
        return this.getBlockState().getValue(BlockStateProperties.FACING);
    }

    @Override
    public double getThrust() {
        return this.getThrusterStats().thrust() * this.getThrustMagnitude();
    }

    @Override
    public double getAirflow() {
        return 1;
    }

    @Override
    public boolean isActive() {
        return (this.hasFuel() && this.getRedstoneSignal() > 0) || Math.abs(this.intensity) > 0.01f;
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable(this.getBlockState().getBlock().getDescriptionId());
    }

    @Override
    public ThrusterFuelMenu createMenu(
        final int containerId,
        final @NotNull Inventory playerInventory,
        final @NotNull Player player) {
        return ThrusterFuelMenu.forBlockEntity(containerId, playerInventory, this);
    }

    private class FuelContainer implements Container {

        @Override
        public int getContainerSize() {
            return fuelInventory.getSlots();
        }

        @Override
        public boolean isEmpty() {
            return fuelInventory.getStackInSlot(FUEL_SLOT).isEmpty();
        }

        @Override
        public @NotNull ItemStack getItem(final int slot) {
            return fuelInventory.getStackInSlot(slot);
        }

        @Override
        public @NotNull ItemStack removeItem(final int slot, final int amount) {
            return fuelInventory.extractItem(slot, amount, false);
        }

        @Override
        public @NotNull ItemStack removeItemNoUpdate(final int slot) {
            final ItemStack existing = fuelInventory.getStackInSlot(FUEL_SLOT);
            fuelInventory.setStackInSlot(FUEL_SLOT, ItemStack.EMPTY);
            return existing;
        }

        @Override
        public void setItem(final int slot, final @NotNull ItemStack stack) {
            fuelInventory.setStackInSlot(slot, stack);
        }

        @Override
        public void setChanged() {
            AbstractThrusterBlockEntity.this.setChanged();
        }

        @Override
        public boolean stillValid(final @NotNull Player player) {
            return level != null
                && level.getBlockEntity(worldPosition) == AbstractThrusterBlockEntity.this
                && player.canInteractWithBlock(worldPosition, 4.0);
        }

        @Override
        public boolean canPlaceItem(final int slot, final @NotNull ItemStack stack) {
            return fuelInventory.isItemValid(slot, stack);
        }

        @Override
        public void clearContent() {
            fuelInventory.setStackInSlot(FUEL_SLOT, ItemStack.EMPTY);
        }
    }

    private class BackFuelItemHandler implements IItemHandler {

        @Override
        public int getSlots() {
            return fuelInventory.getSlots();
        }

        @Override
        public @NotNull ItemStack getStackInSlot(final int slot) {
            return fuelInventory.getStackInSlot(slot);
        }

        @Override
        public @NotNull ItemStack insertItem(
            final int slot, final @NotNull ItemStack stack, final boolean simulate) {
            if (!fuelInventory.isItemValid(slot, stack)) {
                return stack;
            }

            return fuelInventory.insertItem(slot, stack, simulate);
        }

        @Override
        public @NotNull ItemStack extractItem(
            final int slot, final int amount, final boolean simulate) {
            return fuelInventory.extractItem(slot, amount, simulate);
        }

        @Override
        public int getSlotLimit(final int slot) {
            return fuelInventory.getSlotLimit(slot);
        }

        @Override
        public boolean isItemValid(final int slot, final @NotNull ItemStack stack) {
            return fuelInventory.isItemValid(slot, stack);
        }
    }
}
