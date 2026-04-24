package ca.maximilian.create_rockets.menu;

import ca.maximilian.create_rockets.ModBlock.AbstractThrusterBlockEntity;
import ca.maximilian.create_rockets.index.CreateRocketsMenus;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class ThrusterFuelMenu extends AbstractContainerMenu {

    private static final int THRUSTER_SLOT_COUNT = 1;
    private static final int PLAYER_INV_START = THRUSTER_SLOT_COUNT;
    private static final int PLAYER_INV_END = PLAYER_INV_START + 27;
    private static final int HOTBAR_START = PLAYER_INV_END;
    private static final int HOTBAR_END = HOTBAR_START + 9;

    private final Container fuelContainer;
    private final ContainerData data;
    private final BlockPos blockPos;

    public ThrusterFuelMenu(final int containerId, final Inventory playerInventory, final RegistryFriendlyByteBuf extraData) {
        this(
                containerId,
                playerInventory,
                new SimpleContainer(THRUSTER_SLOT_COUNT),
                new SimpleContainerData(3),
                extraData.readBlockPos()
        );
    }

    public ThrusterFuelMenu(
            final int containerId,
            final Inventory playerInventory,
            final Container fuelContainer,
            final ContainerData data,
            final BlockPos blockPos
    ) {
        super(CreateRocketsMenus.THRUSTER_FUEL.get(), containerId);
        checkContainerSize(fuelContainer, THRUSTER_SLOT_COUNT);
        checkContainerDataCount(data, 3);
        this.fuelContainer = fuelContainer;
        this.data = data;
        this.blockPos = blockPos;
        this.fuelContainer.startOpen(playerInventory.player);
        this.addDataSlots(data);

        this.addSlot(new Slot(fuelContainer, 0, 80, 35) {
            @Override
            public boolean mayPlace(final ItemStack stack) {
                return fuelContainer.canPlaceItem(0, stack);
            }
        });

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }

        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
        }
    }

    @Override
    public boolean stillValid(final Player player) {
        return this.fuelContainer.stillValid(player);
    }

    @Override
    public ItemStack quickMoveStack(final Player player, final int index) {
        ItemStack moved = ItemStack.EMPTY;
        final Slot slot = this.slots.get(index);
        if (!slot.hasItem()) {
            return moved;
        }

        final ItemStack stack = slot.getItem();
        moved = stack.copy();

        if (index < THRUSTER_SLOT_COUNT) {
            if (!this.moveItemStackTo(stack, PLAYER_INV_START, HOTBAR_END, true)) {
                return ItemStack.EMPTY;
            }
        } else if (!this.moveItemStackTo(stack, 0, THRUSTER_SLOT_COUNT, false)) {
            return ItemStack.EMPTY;
        }

        if (stack.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }

        return moved;
    }

    @Override
    public void removed(final Player player) {
        super.removed(player);
        this.fuelContainer.stopOpen(player);
    }

    public float getThrottle() {
        return this.getRedstoneSignal() / 15.0f;
    }

    public int getRedstoneSignal() {
        return this.data.get(0);
    }

    public int getFuelTicksRemaining() {
        return this.data.get(1);
    }

    public int getFuelTicksTotal() {
        return this.data.get(2);
    }

    public BlockPos getBlockPos() {
        return this.blockPos;
    }

    public static ThrusterFuelMenu forBlockEntity(
            final int containerId,
            final Inventory playerInventory,
            final AbstractThrusterBlockEntity blockEntity
    ) {
        return new ThrusterFuelMenu(
                containerId,
                playerInventory,
                blockEntity.getFuelContainer(),
                blockEntity.getMenuData(),
                blockEntity.getBlockPos()
        );
    }
}
