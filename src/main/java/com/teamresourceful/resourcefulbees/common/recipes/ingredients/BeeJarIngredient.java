package com.teamresourceful.resourcefulbees.common.recipes.ingredients;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.common.components.JarOccupant;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModDataComponents;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModIngredientTypes;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import net.neoforged.neoforge.common.crafting.IngredientType;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.stream.Stream;

public record BeeJarIngredient(Identifier id) implements ICustomIngredient {

    public static final MapCodec<BeeJarIngredient> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Identifier.CODEC.fieldOf("id").forGetter(BeeJarIngredient::id)
    ).apply(instance, BeeJarIngredient::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, BeeJarIngredient> STREAM_CODEC = StreamCodec.composite(
            Identifier.STREAM_CODEC,
            BeeJarIngredient::id,
            BeeJarIngredient::new
    );

    @Override
    public boolean test(@Nullable ItemStack stack) {
        if (stack == null) return false;

        JarOccupant occupant = stack.get(ModDataComponents.JAR_BEE);

        if (occupant == null || occupant.entityData().isEmpty()) {
            return false;
        }

        Identifier entityId = BuiltInRegistries.ENTITY_TYPE.getKey(occupant.entityType());
        return id.equals(entityId);
    }

    @Override
    public @NonNull Stream<Holder<Item>> items() {
        return Stream.empty();
    }

    @Override
    public boolean isSimple() {
        return false;
    }

    @Override
    public @NonNull IngredientType<?> getType() {
        return ModIngredientTypes.BEE_JAR.get();
    }
}
