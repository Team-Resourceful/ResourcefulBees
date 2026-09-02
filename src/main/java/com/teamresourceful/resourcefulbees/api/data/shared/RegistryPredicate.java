package com.teamresourceful.resourcefulbees.api.data.shared;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.TypedInstance;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import org.jspecify.annotations.NullMarked;

import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

@NullMarked
public interface RegistryPredicate<T> extends Predicate<Holder<T>> {

    Either<TagKey<T>, Set<Holder<T>>> unwrap();

    default boolean populated() {
        return unwrap().map(_ -> true, set -> !set.isEmpty());
    }

    default boolean test(Holder<T> holder) {
        return unwrap().map(holder::is, set -> set.contains(holder));
    }

    default boolean test(TypedInstance<T> instance) {
        return test(instance.typeHolder());
    }

    static <T> Codec<RegistryPredicate<T>> codec(Registry<T> registry) {
        var tagCodec = TagKey.hashedCodec(registry.key());
        var setCodec = ExtraCodecs.compactListCodec(registry.holderByNameCodec())
                .xmap(Set::copyOf, ImmutableList::copyOf);
        return Codec.either(tagCodec, setCodec).xmap(RegistryPredicate::create, RegistryPredicate::unwrap);
    }

    static <T> RegistryPredicate<T> empty() {
        return create(Either.right(ImmutableSet.of()));
    }

    static <T> RegistryPredicate<T> create(TagKey<T> tag) {
        return create(Either.left(tag));
    }

    static <T> RegistryPredicate<T> create(Set<Holder<T>> holders) {
        return create(Either.right(ImmutableSet.copyOf(holders)));
    }

    static <T> RegistryPredicate<T> create(Holder<T> holder) {
        return create(Either.right(Set.of(holder)));
    }

    @SafeVarargs
    static <E, T> RegistryPredicate<T> create(Function<E, Holder<T>> factory, E... elements) {
        return create(Either.right(Stream.of(elements).map(factory).collect(ImmutableSet.toImmutableSet())));
    }

    static <T> RegistryPredicate<T> create(Either<TagKey<T>, Set<Holder<T>>> either) {
        return () -> either;
    }
}
