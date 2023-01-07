package com.teamresourceful.resourcefulbees.platform.common.recipe.ingredient.forge;

import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.platform.common.recipe.ingredient.CodecIngredient;
import com.teamresourceful.resourcefulbees.platform.common.recipe.ingredient.CodecIngredientSerializer;
import com.teamresourceful.resourcefullib.common.codecs.yabn.YabnOps;
import com.teamresourceful.resourcefullib.common.networking.PacketHelper;
import com.teamresourceful.resourcefullib.common.utils.readers.ByteBufByteReader;
import com.teamresourceful.resourcefullib.common.yabn.YabnParser;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import org.jetbrains.annotations.NotNull;

public class ForgeIngredientSerializer<T extends CodecIngredient<T>> implements IIngredientSerializer<ForgeIngredient<T>> {

    private final CodecIngredientSerializer<T> serializer;

    public ForgeIngredientSerializer(CodecIngredientSerializer<T> serializer) {
        this.serializer = serializer;
    }

    public ResourceLocation id() {
        return serializer.id();
    }

    @Override
    public @NotNull ForgeIngredient<T> parse(@NotNull JsonObject json) {
        CodecIngredient<T> ingredient = serializer.codec().parse(JsonOps.INSTANCE, json).getOrThrow(false, ModConstants.LOGGER::error);
        return new ForgeIngredient<>(ingredient);
    }

    @Override
    public @NotNull ForgeIngredient<T> parse(@NotNull FriendlyByteBuf buf) {
        CodecIngredient<T> ingredient = serializer.network()
                .parse(YabnOps.COMPRESSED, YabnParser.parseCompress(new ByteBufByteReader(buf)))
                .getOrThrow(false, ModConstants.LOGGER::error);
        return new ForgeIngredient<>(ingredient);
    }

    @Override
    public void write(@NotNull FriendlyByteBuf buf, @NotNull ForgeIngredient<T> ingredient) {
        PacketHelper.writeWithYabn(buf, serializer.network(), ingredient.getIngredient(), true)
                .getOrThrow(false, s -> ModConstants.LOGGER.error("Could not parse {}", ingredient.getIngredient()));
    }
}
