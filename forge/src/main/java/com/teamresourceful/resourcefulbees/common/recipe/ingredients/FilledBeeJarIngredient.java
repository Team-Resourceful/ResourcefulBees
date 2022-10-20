package com.teamresourceful.resourcefulbees.common.recipe.ingredients;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.common.item.BeeJar;
import com.teamresourceful.resourcefulbees.common.lib.constants.BeeConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.NBTConstants;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
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
        super(Stream.of(new ItemValue(getBeeJar())));
    }

    public static ItemStack getBeeJar() {
        ItemStack stack = new ItemStack(ModItems.BEE_JAR.get());

        CompoundTag stackTag = new CompoundTag();
        CompoundTag entityTag = new CompoundTag();

        entityTag.putString(NBTConstants.NBT_ID, "minecraft:bee");
        entityTag.putString(NBTConstants.BeeJar.COLOR, BeeConstants.VANILLA_BEE_COLOR);
        stackTag.putString(NBTConstants.BeeJar.DISPLAY_NAME, Component.Serializer.toJson(Component.literal("Any Filled Bee Jar")));

        stackTag.put(NBTConstants.BeeJar.ENTITY, entityTag);
        stack.setTag(stackTag);
        return stack;
    }

    @Override
    public boolean isSimple() {
        return false;
    }

    @Override
    public boolean test(@Nullable ItemStack input) {
        return input != null && BeeJar.isFilled(input);
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
            return CODEC.parse(JsonOps.INSTANCE, json).getOrThrow(false, s -> ResourcefulBees.LOGGER.error("Could not parse FilledBeeJarIngredient"));
        }

        @Override
        public void write(@NotNull FriendlyByteBuf buffer, @NotNull FilledBeeJarIngredient ingredient) {
            buffer.writeWithCodec(CODEC, ingredient);
        }
    }
}
