package com.teamresourceful.resourcefulbees.mixin.common;

import com.google.gson.JsonElement;
import com.teamresourceful.resourcefulbees.api.ResourcefulBeesAPI;
import com.teamresourceful.resourcefulbees.api.data.bee.CustomBeeData;
import com.teamresourceful.resourcefulbees.common.data.RecipeBuilder;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModIdentifier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.crafting.*;
import net.neoforged.neoforge.common.conditions.ConditionalOps;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.*;
import java.util.stream.Stream;

@Mixin(value = RecipeManager.class)
public abstract class RecipeManagerMixin {

    //This mixin is courtesy of the EnderIO dev team.
    // Injects right before the recipemap is created so that the additional recipes are included.
    // We can't inject before RETURN because that's before the return instruction, not before the map is created.
    @Inject(method = "prepare", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/crafting/RecipeMap;create(Ljava/lang/Iterable;)Lnet/minecraft/world/item/crafting/RecipeMap;"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private static void resourcefulbees$prepare(ResourceManager resourceManager, ProfilerFiller profilerFiller, CallbackInfoReturnable<RecipeMap> ci,
                                        SortedMap<Identifier, Recipe<?>> sortedmap, ConditionalOps<JsonElement> conditionalOps, List<RecipeHolder<?>> recipeHolders) {

        Stream<CustomBeeData> bees = ResourcefulBeesAPI.getRegistry().getBeeRegistry().getStreamOfBees();
        bees.forEach(customBeeData -> {
            Recipe<?> recipe = RecipeBuilder.makeHiveRecipe(customBeeData);
            if (recipe != null) {
                String path = customBeeData.name().toLowerCase(Locale.ROOT) + "_hive_output";
                ResourceKey<Recipe<?>> key = ResourceKey.create(Registries.RECIPE, ModIdentifier.of(path));
                recipeHolders.add(new RecipeHolder<>(key, recipe));
            }
        });
    }

}
