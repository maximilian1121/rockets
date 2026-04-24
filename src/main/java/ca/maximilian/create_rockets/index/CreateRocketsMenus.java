package ca.maximilian.create_rockets.index;

import ca.maximilian.create_rockets.CreateRockets;
import ca.maximilian.create_rockets.menu.ThrusterFuelMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class CreateRocketsMenus {

    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(Registries.MENU, CreateRockets.MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<ThrusterFuelMenu>> THRUSTER_FUEL =
            MENUS.register("thruster_fuel", () -> IMenuTypeExtension.create(ThrusterFuelMenu::new));

    private CreateRocketsMenus() {
    }
}
