package com.teamresourceful.resourcefulbees.common.items;

import com.teamresourceful.resourcefulbees.common.components.PollenRecipe;
import com.teamresourceful.resourcefulbees.common.entities.entity.ThrownPollenItem;
import com.teamresourceful.resourcefulbees.common.recipes.MutationRecipe;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModDataComponents;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModRecipes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeMap;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.List;

public class PollenItem extends Item {

    private static RecipeMap cachedRecipeMap;
    private static List<RecipeHolder<MutationRecipe>> cachedRecipes = List.of();

    public PollenItem(Item.Properties properties) {
        super(properties);
    }

    private static List<RecipeHolder<MutationRecipe>> getRecipes(ServerLevel level) {
        var recipeMap = level.recipeAccess().recipeMap();

        if (cachedRecipeMap != recipeMap) {
            cachedRecipes = List.copyOf(recipeMap.byType(ModRecipes.MUTATION_RECIPE_TYPE.get()));
            cachedRecipeMap = recipeMap;
        }

        return cachedRecipes;
    }

    public static ItemStack createRandom(ServerLevel level) {
        var stack = new ItemStack(ModItems.POLLEN.get());
        var recipes = getRecipes(level);

        if (recipes.isEmpty()) {
            return stack;
        }

        var recipe = recipes.get(level.getRandom().nextInt(recipes.size()));
        stack.set(ModDataComponents.POLLEN_RECIPE.get(), PollenRecipe.of(recipe));

        return stack;
    }

    @Override
    public @NonNull InteractionResult use(Level level, Player player, @NotNull InteractionHand usedHand) {
        ItemStack itemStack = player.getItemInHand(usedHand);
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.SNOWBALL_THROW, SoundSource.NEUTRAL, 0.5F, 0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F));
        player.getCooldowns().addCooldown(itemStack, 5);
        if (!level.isClientSide()) {
            ThrownPollenItem thrownPollen = new ThrownPollenItem(level);
            thrownPollen.setItem(itemStack);
            thrownPollen.setPos(player.getX(), player.getEyeY(), player.getZ());
            thrownPollen.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.5F, 1.0F);
            level.addFreshEntity(thrownPollen);
        }

        player.awardStat(Stats.ITEM_USED.get(this));
        if (!player.isCreative()) {
            itemStack.shrink(1);
        }

        return InteractionResult.SUCCESS_SERVER;
    }
}
