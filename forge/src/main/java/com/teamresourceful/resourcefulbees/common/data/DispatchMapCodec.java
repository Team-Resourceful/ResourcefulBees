package com.teamresourceful.resourcefulbees.common.data;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.*;

import java.util.Map;
import java.util.function.Function;

public class DispatchMapCodec<K, V> implements Codec<Map<K, V>> {

    private final Codec<K> keyCodec;
    private final Function<K, Codec<V>> valueCodec;

    public DispatchMapCodec(Codec<K> keyCodec, Function<K, Codec<V>> valueCodec) {
        this.keyCodec = keyCodec;
        this.valueCodec = valueCodec;
    }

    public static <A, B> DispatchMapCodec<A, B> of(Codec<A> keyCodec, Function<A, Codec<B>> valueCodec) {
        return new DispatchMapCodec<>(keyCodec, valueCodec);
    }

    private <T> DataResult<Map<K, V>> decode(final DynamicOps<T> ops, final MapLike<T> input) {
        final ImmutableMap.Builder<K, V> read = ImmutableMap.builder();
        final ImmutableList.Builder<Pair<T, T>> failed = ImmutableList.builder();

        final DataResult<Unit> result = input.entries().reduce(
                DataResult.success(Unit.INSTANCE, Lifecycle.stable()),
                (r, pair) -> {
                    final DataResult<K> key = keyCodec.parse(ops, pair.getFirst());
                    final DataResult<V> value = key.map(valueCodec).flatMap(codec -> codec.parse(ops, pair.getSecond()));

                    final DataResult<Pair<K, V>> entry = key.apply2stable(Pair::of, value);
                    entry.error().ifPresent(e -> failed.add(pair));

                    return r.apply2stable((u, p) -> {
                        read.put(p.getFirst(), p.getSecond());
                        return u;
                    }, entry);
                },
                (r1, r2) -> r1.apply2stable((u1, u2) -> u1, r2)
        );

        final Map<K, V> elements = read.build();
        final T errors = ops.createMap(failed.build().stream());

        return result.map(unit -> elements).setPartial(elements).mapError(e -> e + " missed input: " + errors);
    }

    private <T> RecordBuilder<T> encode(final Map<K, V> input, final DynamicOps<T> ops, final RecordBuilder<T> prefix) {
        for (final Map.Entry<K, V> entry : input.entrySet()) {
            prefix.add(keyCodec.encodeStart(ops, entry.getKey()), valueCodec.apply(entry.getKey()).encodeStart(ops, entry.getValue()));
        }
        return prefix;
    }

    @Override
    public <T> DataResult<Pair<Map<K, V>, T>> decode(DynamicOps<T> ops, T input) {
        return ops.getMap(input).setLifecycle(Lifecycle.stable()).flatMap(map -> decode(ops, map)).map(r -> Pair.of(r, input));
    }

    @Override
    public <T> DataResult<T> encode(Map<K, V> input, DynamicOps<T> ops, T prefix) {
        return encode(input, ops, ops.mapBuilder()).build(prefix);
    }
}
