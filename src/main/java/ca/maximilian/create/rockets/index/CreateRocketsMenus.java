package ca.maximilian.create.rockets.index;

import ca.maximilian.create.rockets.Constants;
import ca.maximilian.create.rockets.menu.ThrusterFuelMenu;
import lombok.experimental.UtilityClass;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@UtilityClass
public final class CreateRocketsMenus {

    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(Registries.MENU, Constants.MOD_ID);

    public static final DeferredHolder<MenuType<?>, MenuType<ThrusterFuelMenu>> THRUSTER_FUEL =
            MENUS.register("thruster_fuel", () -> IMenuTypeExtension.create(ThrusterFuelMenu::new));
}
