package com.teamresourceful.resourcefulbees.common.recipe.ingredients;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.common.item.BeeJarItem;
import com.teamresourceful.resourcefulbees.common.lib.constants.NBTConstants;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

public class BeeJarIngredient extends Ingredient {

    private final ResourceLocation id;
    private final int color;

    public BeeJarIngredient(ResourceLocation id, int color) {
        super(Stream.of(new ItemValue(BeeJarItem.createFilledJar(id, color))));
        this.id = id;
        this.color = color;
    }

    public ResourceLocation getId() {
        return id;
    }

    public int getColor() {
        return color;
    }

    @Override
    public boolean isSimple() {
        return false;
    }

    @Override
    public boolean test(@Nullable ItemStack input) {
        if (input == null) return false;
        return BeeJarItem.isFilled(input) && input.getOrCreateTag().getCompound(NBTConstants.BeeJar.ENTITY).getString(NBTConstants.NBT_ID).equals(id.toString());
    }

    @Override
    public @NotNull IIngredientSerializer<? extends Ingredient> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public @NotNull JsonElement toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("type", CraftingHelper.getID(getSerializer()).toString());
        json.addProperty("id", id.toString());
        json.addProperty("color", color);
        return json;
    }

    public static class Serializer implements IIngredientSerializer<BeeJarIngredient> {

        public static final Serializer INSTANCE = new Serializer();

        public static final Codec<BeeJarIngredient> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("id").forGetter(BeeJarIngredient::getId),
                Codec.INT.fieldOf("color").forGetter(BeeJarIngredient::getColor)
        ).apply(instance, BeeJarIngredient::new));

        @Override
        public @NotNull BeeJarIngredient parse(FriendlyByteBuf buffer) {
            return buffer.readWithCodec(CODEC);
        }

        @Override
        public @NotNull BeeJarIngredient parse(@NotNull JsonObject json) {
            return CODEC.parse(JsonOps.INSTANCE, json).getOrThrow(false, s -> ResourcefulBees.LOGGER.error("Could not parse BeeJarIngredient"));
        }

        @Override
        public void write(@NotNull FriendlyByteBuf buffer, @NotNull BeeJarIngredient ingredient) {
            buffer.writeWithCodec(CODEC, ingredient);
        }
    }
}
