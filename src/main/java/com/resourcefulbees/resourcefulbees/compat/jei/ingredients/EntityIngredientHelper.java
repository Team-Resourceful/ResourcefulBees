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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Iterator;

public class EntityIngredientHelper<T extends EntityIngredient> implements IIngredientHelper<EntityIngredient> {

    @Nonnull
    @Override
    public IFocus<?> translateFocus(@Nonnull IFocus<EntityIngredient> focus, @Nonnull IFocusFactory focusFactory) {
        return focus;
    }

    @Nullable
    @Override
    public EntityIngredient getMatch(Iterable<EntityIngredient> iterable, @Nonnull EntityIngredient entityIngredient) {
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

    @Nonnull
    @Override
    public String getDisplayName(EntityIngredient entityIngredient) {
        return I18n.format("entity.resourcefulbees." + entityIngredient.getBeeType());
    }

    @Nonnull
    @Override
    public ItemStack getCheatItemStack(EntityIngredient ingredient) {
        return ingredient.getBeeType().equals(BeeConstants.VANILLA_BEE_TYPE) ? new ItemStack(Items.BEE_SPAWN_EGG) : new ItemStack(BeeRegistry.getRegistry().getBeeData(ingredient.getBeeType()).getSpawnEggItemRegistryObject().get());
    }

    @Nonnull
    @Override
    public String getUniqueId(EntityIngredient entityIngredient) {
        return "bee:" + entityIngredient.getBeeType();
    }

    @Nonnull
    @Override
    public String getWildcardId(@Nonnull EntityIngredient entityIngredient) {
        return this.getUniqueId(entityIngredient);
    }

    @Nonnull
    @Override
    public String getModId(@Nonnull EntityIngredient entityIngredient) {
        return ResourcefulBees.MOD_ID;
    }

    @Nonnull
    @Override
    public String getResourceId(EntityIngredient entityIngredient) {
        return entityIngredient.getBeeType();
    }

    @Nonnull
    @Override
    public EntityIngredient copyIngredient(@Nonnull EntityIngredient entityIngredient) {
        return entityIngredient;
    }

    @Nonnull
    @Override
    public String getErrorInfo(@Nullable EntityIngredient entityIngredient) {
        return null;
    }
}
