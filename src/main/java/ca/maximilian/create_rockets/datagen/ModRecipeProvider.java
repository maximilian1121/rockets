package ca.maximilian.create_rockets.datagen;

import ca.maximilian.create_rockets.index.CreateRocketsItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends RecipeProvider {

    public ModRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider);
    }

    @Override
    protected void buildRecipes(@NotNull RecipeOutput output) {
        // SpaceCraft Raptor 3
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, CreateRocketsItems.RAPTOR_3.get())
                .pattern(" M ")
                .pattern(" C ")
                .pattern(" S ")
                .define('M', Ingredient.of(getItem("create", "precision_mechanism")))
                .define('C', Ingredient.of(getItem("simulated", "engine_assembly")))
                .define('S', Ingredient.of(getItem("create", "sturdy_sheet")))
                .unlockedBy("has_assembly", has(getItem("simulated", "engine_assembly")))
                .save(output);

        // Saturn V F1
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, CreateRocketsItems.SATURN_V_F1.get())
                .pattern(" M ")
                .pattern("CCC")
                .pattern("SSS")
                .define('M', Ingredient.of(getItem("create", "precision_mechanism")))
                .define('C', Ingredient.of(getItem("simulated", "engine_assembly")))
                .define('S', Ingredient.of(getItem("create", "sturdy_sheet")))
                .unlockedBy("has_assembly", has(getItem("simulated", "engine_assembly")))
                .save(output);
    }

    private Item getItem(String namespace, String path) {
        return BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(namespace, path));
    }
}
