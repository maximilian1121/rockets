package ca.maximilian.create_rockets.datagen;

import ca.maximilian.create_rockets.index.CreateRocketsBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class ModBlockLootProvider extends BlockLootSubProvider {

    public ModBlockLootProvider(HolderLookup.Provider lookupProvider) {
        super(Set.of(), FeatureFlags.DEFAULT_FLAGS, lookupProvider);
    }

    @Override
    protected void generate() {
        dropSelf(CreateRocketsBlocks.RAPTOR_3.get());
        dropSelf(CreateRocketsBlocks.SATURN_V_F1.get());
    }

    @Override
    protected @NotNull Iterable<Block> getKnownBlocks() {
        return CreateRocketsBlocks.BLOCKS.getEntries().stream()
                .map(e -> (Block) e.get())
                .toList();
    }
}
