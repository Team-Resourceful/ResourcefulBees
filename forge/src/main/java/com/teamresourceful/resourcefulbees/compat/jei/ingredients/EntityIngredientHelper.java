package com.teamresourceful.resourcefulbees.compat.jei.ingredients;

import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.item.BeeSpawnEggItem;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.IFocusFactory;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.stream.StreamSupport;

public class EntityIngredientHelper implements IIngredientHelper<EntityIngredient> {

    @NotNull
    @Override
    public IFocus<?> translateFocus(@NotNull IFocus<EntityIngredient> focus, @NotNull IFocusFactory focusFactory) {
        return focus;
    }

    @Nullable
    @Override
    public EntityIngredient getMatch(Iterable<EntityIngredient> iterable, @NotNull EntityIngredient entityIngredient) {
        return StreamSupport.stream(iterable.spliterator(), false)
                .filter(ingredient -> ingredient.getBeeData().getRegistryID().equals(entityIngredient.getBeeData().getRegistryID()))
                .findFirst().orElse(null);
    }

    @NotNull
    @Override
    public String getDisplayName(EntityIngredient entityIngredient) {
        return I18n.get("entity.resourcefulbees." + entityIngredient.getBeeData().getCoreData().getName() + "_bee");
    }

    @NotNull
    @Override
    public ItemStack getCheatItemStack(EntityIngredient ingredient) {
        return Objects.requireNonNull(BeeSpawnEggItem.byId(ingredient.getBeeData().getEntityType())).getDefaultInstance();
    }

    @NotNull
    @Override
    public String getUniqueId(EntityIngredient entityIngredient) {
        return "bee:" + entityIngredient.getBeeData().getRegistryID().toString();
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
        return entityIngredient.getBeeData().getRegistryID().getPath();
    }

    @NotNull
    @Override
    public EntityIngredient copyIngredient(@NotNull EntityIngredient entityIngredient) {
        return entityIngredient;
    }

    @NotNull
    @Override
    public String getErrorInfo(@Nullable EntityIngredient entityIngredient) {
        return entityIngredient == null ? "null" : entityIngredient.toString();
    }
}
