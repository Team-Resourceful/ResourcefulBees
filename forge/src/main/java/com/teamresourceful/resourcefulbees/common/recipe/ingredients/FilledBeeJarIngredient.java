package com.teamresourceful.resourcefulbees.common.recipe.ingredients;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.teamresourceful.resourcefulbees.api.data.bee.CustomBeeData;
import com.teamresourceful.resourcefulbees.api.registry.BeeRegistry;
import com.teamresourceful.resourcefulbees.common.item.BeeJarItem;
import com.teamresourceful.resourcefulbees.common.lib.constants.BeeConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefullib.common.color.Color;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

//REQUIRED determine if this should just be merged with BeeJarIngredient or stay as a separate class
public class FilledBeeJarIngredient extends Ingredient {

    public FilledBeeJarIngredient() {
        super(Stream.concat(
                BeeRegistry.get().getStreamOfBees().map(FilledBeeJarIngredient::getBeeJar),
                Stream.of(getBeeJar(EntityType.getKey(EntityType.BEE), BeeConstants.VANILLA_BEE_INT_COLOR))
        ));
    }

    @NotNull
    private static ItemValue getBeeJar(ResourceLocation id, Color color) {
        return new ItemValue(BeeJarItem.createFilledJar(id, color));
    }

    @SuppressWarnings("SameParameterValue")
    @NotNull
    private static ItemValue getBeeJar(ResourceLocation id, int color) {
        return getBeeJar(id, new Color(color));
    }

    @NotNull
    private static ItemValue getBeeJar(CustomBeeData customBeeData) {
        return getBeeJar(customBeeData.id(), customBeeData.getRenderData().colorData().jarColor());
    }

    @Override
    public boolean isSimple() {
        return false;
    }

    @Override
    public boolean test(@Nullable ItemStack input) {
        return input != null && BeeJarItem.isFilled(input);
    }

    @Override
    public @NotNull IIngredientSerializer<? extends Ingredient> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public @NotNull JsonElement toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("type", CraftingHelper.getID(getSerializer()).toString());
        return json;
    }

    public static class Serializer implements IIngredientSerializer<FilledBeeJarIngredient> {

        public static final Serializer INSTANCE = new Serializer();

        public static final Codec<FilledBeeJarIngredient> CODEC = Codec.unit(new FilledBeeJarIngredient());

        @Override
        public @NotNull FilledBeeJarIngredient parse(FriendlyByteBuf buffer) {
            return buffer.readWithCodec(CODEC);
        }

        @Override
        public @NotNull FilledBeeJarIngredient parse(@NotNull JsonObject json) {
            return CODEC.parse(JsonOps.INSTANCE, json).getOrThrow(false, s -> ModConstants.LOGGER.error("Could not parse FilledBeeJarIngredient"));
        }

        @Override
        public void write(@NotNull FriendlyByteBuf buffer, @NotNull FilledBeeJarIngredient ingredient) {
            buffer.writeWithCodec(CODEC, ingredient);
        }
    }
}
