package com.teamresourceful.resourcefulbees.common.compat.jei.ingredients;

import com.teamresourceful.resourcefulbees.common.item.BeeSpawnEggItem;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.IFocusFactory;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
                .filter(ingredient -> {
                    if (ingredient.getEntityType().getRegistryName() == null) return false;
                    return ingredient.getEntityType().getRegistryName().equals(entityIngredient.getEntityType().getRegistryName());
                })
                .findFirst().orElse(null);
    }

    @NotNull
    @Override
    public String getDisplayName(EntityIngredient entityIngredient) {
        return I18n.get(entityIngredient.getEntityType().getDescriptionId());
    }

    @NotNull
    @Override
    public ItemStack getCheatItemStack(EntityIngredient ingredient) {
        return Objects.requireNonNull(BeeSpawnEggItem.byId(ingredient.getEntityType())).getDefaultInstance();
    }

    @NotNull
    @Override
    public String getUniqueId(EntityIngredient entityIngredient) {
        String id = entityIngredient.getEntity().getEncodeId();
        return id == null ? "entity:error" : "entity:" + id;
    }

    @NotNull
    @Override
    public String getWildcardId(@NotNull EntityIngredient entityIngredient) {
        return this.getUniqueId(entityIngredient);
    }

    @NotNull
    @Override
    public String getModId(@NotNull EntityIngredient entityIngredient) {
        ResourceLocation id = entityIngredient.getEntityType().getRegistryName();
        if (id == null) return "minecraft";
        return id.getNamespace();
    }

    @NotNull
    @Override
    public String getResourceId(EntityIngredient entityIngredient) {
        ResourceLocation id = entityIngredient.getEntityType().getRegistryName();
        if (id == null) return "error";
        return id.getPath();
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
