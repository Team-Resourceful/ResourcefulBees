package com.teamresourceful.resourcefulbees.common.recipes.ingredients;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.api.data.bee.CustomBeeData;
import com.teamresourceful.resourcefulbees.api.registry.BeeRegistry;
import com.teamresourceful.resourcefulbees.common.items.BeeJarItem;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.NBTConstants;
import com.teamresourceful.resourcefullib.common.codecs.CodecExtras;
import com.teamresourceful.resourcefullib.common.recipe.ingredient.CodecIngredient;
import com.teamresourceful.resourcefullib.common.recipe.ingredient.CodecIngredientSerializer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public record BeeJarIngredient(Set<String> ids) implements CodecIngredient<BeeJarIngredient> {

    public static final Codec<BeeJarIngredient> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            CodecExtras.set(Codec.STRING).fieldOf("ids").orElse(Set.of()).forGetter(BeeJarIngredient::ids)
    ).apply(instance, BeeJarIngredient::new));

    public static final CodecIngredientSerializer<BeeJarIngredient> SERIALIZER = new CodecIngredientSerializer<>(
            new ResourceLocation(ModConstants.MOD_ID, "bee_jar"),
            CODEC
    );


    @Override
    public boolean test(@Nullable ItemStack stack) {
        if (stack == null) return false;
        CompoundTag entityTag = stack.getTagElement(NBTConstants.BeeJar.ENTITY);
        return entityTag != null && (ids.isEmpty() || ids.contains(entityTag.getString(NBTConstants.NBT_ID)));
    }

    @Override
    public List<ItemStack> getStacks() {
        Stream<CustomBeeData> data = ids.isEmpty() ? BeeRegistry.get().getStreamOfBees() : ids.stream().map(BeeRegistry.get()::getBeeData);
        return data.map(bee -> BeeJarItem.createFilledJar(bee.id(), bee.getRenderData().colorData().jarColor())).toList();
    }

    @Override
    public CodecIngredientSerializer<BeeJarIngredient> serializer() {
        return SERIALIZER;
    }
}
