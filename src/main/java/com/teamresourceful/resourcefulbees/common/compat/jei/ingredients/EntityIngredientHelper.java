package com.teamresourceful.resourcefulbees.common.compat.jei.ingredients;

//public class EntityIngredientHelper implements IIngredientHelper<EntityIngredient> {
//
//    @Override
//    public @NotNull IIngredientType<EntityIngredient> getIngredientType() {
//        return JEICompat.ENTITY_INGREDIENT;
//    }
//
//
//    @NotNull
//    @Override
//    public String getDisplayName(EntityIngredient entityIngredient) {
//        return I18n.get(entityIngredient.getEntityType().getDescriptionId());
//    }
//
//    @Override
//    public @NotNull String getUniqueId(@NotNull EntityIngredient entityIngredient, @NotNull UidContext context) {
//        Entity entity = entityIngredient.getEntity();
//        if (entity == null) return "entity:error";
//        String id = entity.getEncodeId();
//        return id == null ? "entity:error" : "entity:" + id;
//    }
//
//    @NotNull
//    @Override
//    public ItemStack getCheatItemStack(EntityIngredient ingredient) {
//        return Objects.requireNonNull(ForgeSpawnEggItem.fromEntityType(ingredient.getEntityType())).getDefaultInstance();
//    }
//
//    @NotNull
//    @Override
//    public String getWildcardId(@NotNull EntityIngredient entityIngredient) {
//        return this.getUniqueId(entityIngredient, UidContext.Ingredient);
//    }
//
//    @Override
//    public String getModId(@NotNull EntityIngredient ingredient) {
//        return getResourceLocation(ingredient).getNamespace();
//    }
//
//    @Override
//    public String getResourceId(@NotNull EntityIngredient ingredient) {
//        return getResourceLocation(ingredient).getPath();
//    }
//
//    @Override
//    public @NotNull ResourceLocation getResourceLocation(EntityIngredient ingredient) {
//        ResourceLocation id = ingredient.getEntityType().getRegistryName();
//        if (id == null) return new ResourceLocation("error");
//        return id;
//    }
//
//    @NotNull
//    @Override
//    public EntityIngredient copyIngredient(@NotNull EntityIngredient entityIngredient) {
//        return entityIngredient;
//    }
//
//    @NotNull
//    @Override
//    public String getErrorInfo(@Nullable EntityIngredient entityIngredient) {
//        return entityIngredient == null ? "null" : entityIngredient.toString();
//    }
//}
//