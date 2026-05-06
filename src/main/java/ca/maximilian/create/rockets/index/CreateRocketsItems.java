package ca.maximilian.create.rockets.index;

import ca.maximilian.create.rockets.Constants;
import lombok.experimental.UtilityClass;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

@UtilityClass
public final class CreateRocketsItems {

    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(Constants.MOD_ID);

    public static final DeferredItem<BlockItem> RAPTOR_3 =
            ITEMS.register(
                    "raptor_3",
                    () -> new BlockItem(CreateRocketsBlocks.RAPTOR_3.get(), new Item.Properties()));

    public static final DeferredItem<BlockItem> SATURN_V_F1 =
            ITEMS.register(
                    "saturn_v_f1",
                    () ->
                            new BlockItem(
                                    CreateRocketsBlocks.SATURN_V_F1.get(), new Item.Properties()));
}
