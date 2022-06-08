package com.teamresourceful.resourcefulbees.api.beedata;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;

import java.util.List;
import java.util.function.Function;

public class TagAndListSetCodec<E> implements Codec<HolderSet<E>> {

    private final Registry<E> registry;
    private final Codec<Either<TagKey<E>, List<Holder<E>>>> holderCodec;

    private TagAndListSetCodec(Registry<E> registry, Codec<Holder<E>> codec) {
        this.registry = registry;
        this.holderCodec = Codec.either(TagKey.hashedCodec(registry.key()), holderListCodec(codec));
    }

    public static <E> TagAndListSetCodec<E> of(Registry<E> registry) {
        return new TagAndListSetCodec<>(registry, registry.holderByNameCodec());
    }

    private static <E> Codec<List<Holder<E>>> holderListCodec(Codec<Holder<E>> holderCodec) {
        Function<List<Holder<E>>, DataResult<List<Holder<E>>>> function = ExtraCodecs.ensureHomogenous(Holder::kind);
        Codec<List<Holder<E>>> codec = holderCodec.listOf().flatXmap(function, function);
        return Codec.either(codec, holderCodec)
                .xmap(
                    either -> either.map(p -> p, List::of),
                    set -> set.size() == 1 ? Either.right(set.get(0)) : Either.left(set)
                );
    }

    @Override
    public <T> DataResult<Pair<HolderSet<E>, T>> decode(DynamicOps<T> ops, T input) {
        return this.holderCodec.decode(ops, input)
                .map(pair -> pair.mapFirst(either -> either.map(registry::getOrCreateTag, HolderSet::direct)));
    }

    @Override
    public <T> DataResult<T> encode(HolderSet<E> p_206674_, DynamicOps<T> p_206675_, T p_206676_) {
        if (!p_206674_.isValidInRegistry(registry)) {
            return DataResult.error("HolderSet " + p_206674_ + " is not valid in current registry set");
        }

        return this.holderCodec.encode(p_206674_.unwrap().mapRight(List::copyOf), p_206675_, p_206676_);
    }
}
