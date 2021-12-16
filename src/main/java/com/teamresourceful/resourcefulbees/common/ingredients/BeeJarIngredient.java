package com.teamresourceful.resourcefulbees.common.ingredients;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.common.item.BeeJar;
import com.teamresourceful.resourcefulbees.common.lib.constants.NBTConstants;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModItems;
import com.teamresourceful.resourcefulbees.common.utils.color.Color;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

public class BeeJarIngredient extends Ingredient {

    private final ResourceLocation id;
    private final int color;

    public BeeJarIngredient(ResourceLocation id, int color) {
        super(Stream.of(new Ingredient.ItemValue(getBeeJar(id, color))));
        this.id = id;
        this.color = color;
    }

    public ResourceLocation getId() {
        return id;
    }

    public int getColor() {
        return color;
    }

    public static ItemStack getBeeJar(ResourceLocation id, int color) {
        ItemStack stack = new ItemStack(ModItems.BEE_JAR.get());
        stack.getOrCreateTag().putString(NBTConstants.NBT_ENTITY, id.toString());
        stack.getOrCreateTag().putString(NBTConstants.NBT_COLOR, new Color(color).toString());
        stack.setHoverName(new TranslatableComponent("item.resourcefulbees.bee_jar_filled").append(" - ").append(new TranslatableComponent("entity."+id.getNamespace()+"."+id.getPath())));
        return stack;
    }

    @Override
    public boolean isSimple() {
        return false;
    }

    @Override
    public boolean test(@Nullable ItemStack input) {
        return input != null && input.getItem() instanceof BeeJar && input.hasTag() && input.getTag().getString(NBTConstants.NBT_ENTITY).equals(id.toString());
    }

    @Override
    public @NotNull IIngredientSerializer<? extends Ingredient> getSerializer() {
        return Serializer.INSTANCE;
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
