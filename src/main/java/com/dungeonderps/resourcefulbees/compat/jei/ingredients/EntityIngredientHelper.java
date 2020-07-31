package com.dungeonderps.resourcefulbees.compat.jei.ingredients;

import com.dungeonderps.resourcefulbees.ResourcefulBees;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.IFocusFactory;
import net.minecraft.client.resources.I18n;

import javax.annotation.Nullable;
import java.util.Iterator;

public class EntityIngredientHelper<T extends EntityIngredient> implements IIngredientHelper<EntityIngredient> {
    @Override
    public IFocus<?> translateFocus(IFocus<EntityIngredient> focus, IFocusFactory focusFactory) {
        return focus;
    }

    @Nullable
    @Override
    public EntityIngredient getMatch(Iterable<EntityIngredient> iterable, EntityIngredient entityIngredient) {
        Iterator var3 = iterable.iterator();

        EntityIngredient entity;
        do {
            if (!var3.hasNext()) {
                return null;
            }

            entity = (EntityIngredient)var3.next();
        } while(entityIngredient.getBeeType().equals(entity.getBeeType()));

        return entity;
    }

    @Override
    public String getDisplayName(EntityIngredient entityIngredient) {
        return I18n.format("entity.resourcefulbees." + entityIngredient.getBeeType());
    }

    @Override
    public String getUniqueId(EntityIngredient entityIngredient) {
        return "bee";
    }

    @Override
    public String getWildcardId(EntityIngredient entityIngredient) {
        return "bee";
    }

    @Override
    public String getModId(EntityIngredient entityIngredient) {
        return ResourcefulBees.MOD_ID;
    }

    @Override
    public String getResourceId(EntityIngredient entityIngredient) {
        return "bee";
    }

    @Override
    public EntityIngredient copyIngredient(EntityIngredient entityIngredient) {
        return entityIngredient;
    }

    @Override
    public String getErrorInfo(@Nullable EntityIngredient entityIngredient) {
        return null;
    }
}
