package ca.maximilian.create_rockets;

import ca.maximilian.create_rockets.index.CreateRocketsBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagsProvider extends BlockTagsProvider {

    public ModBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, CreateRockets.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(CreateRocketsBlocks.RAPTOR_3.get())
                .add(CreateRocketsBlocks.SATURN_V_F1.get());

        tag(BlockTags.NEEDS_IRON_TOOL)
                .add(CreateRocketsBlocks.RAPTOR_3.get())
                .add(CreateRocketsBlocks.SATURN_V_F1.get());
    }
}
