package com.resourcefulbees.resourcefulbees.compat.jei.ingredients;

import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import com.resourcefulbees.resourcefulbees.lib.BeeConstants;
import com.resourcefulbees.resourcefulbees.registry.BeeRegistry;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.IFocusFactory;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Iterator;

public class EntityIngredientHelper implements IIngredientHelper<EntityIngredient> {

    @NotNull
    @Override
    public IFocus<?> translateFocus(@NotNull IFocus<EntityIngredient> focus, @NotNull IFocusFactory focusFactory) {
        return focus;
    }

    @Nullable
    @Override
    public EntityIngredient getMatch(Iterable<EntityIngredient> iterable, @NotNull EntityIngredient entityIngredient) {
        Iterator<EntityIngredient> var3 = iterable.iterator();

        EntityIngredient entity;
        do {
            if (!var3.hasNext()) {
                return null;
            }

            entity = var3.next();
        } while(entityIngredient.getBeeType().equals(entity.getBeeType()));

        return entity;
    }

    @NotNull
    @Override
    public String getDisplayName(EntityIngredient entityIngredient) {
        return I18n.get("entity.resourcefulbees." + entityIngredient.getBeeType());
    }

    @NotNull
    @Override
    public ItemStack getCheatItemStack(EntityIngredient ingredient) {
        return ingredient.getBeeType().equals(BeeConstants.VANILLA_BEE_TYPE) ? new ItemStack(Items.BEE_SPAWN_EGG) : new ItemStack(BeeRegistry.getRegistry().getBeeData(ingredient.getBeeType()).getSpawnEggItemRegistryObject().get());
    }

    @NotNull
    @Override
    public String getUniqueId(EntityIngredient entityIngredient) {
        return "bee:" + entityIngredient.getBeeType();
    }

    @NotNull
    @Override
    public String getWildcardId(@NotNull EntityIngredient entityIngredient) {
        return this.getUniqueId(entityIngredient);
    }

    @NotNull
    @Override
    public String getModId(@NotNull EntityIngredient entityIngredient) {
        return ResourcefulBees.MOD_ID;
    }

    @NotNull
    @Override
    public String getResourceId(EntityIngredient entityIngredient) {
        return entityIngredient.getBeeType();
    }

    @NotNull
    @Override
    public EntityIngredient copyIngredient(@NotNull EntityIngredient entityIngredient) {
        return entityIngredient;
    }

    @NotNull
    @Override
    public String getErrorInfo(@Nullable EntityIngredient entityIngredient) {
        return "BEE INGREDIENT ERROR IN JEI";
    }
}
