package com.teamresourceful.resourcefulbees.common.util;

import net.minecraft.nbt.*;

import java.util.function.Function;
import java.util.function.Supplier;

public class SerializedDataEntry<T, N extends Tag> implements Supplier<T> {

    private final String key;
    private final byte tagKey;
    private final Function<T, N> writer;
    private final Function<N, T> reader;
    private T data;

    protected SerializedDataEntry(T data, String key, byte tagKey, Function<T, N> writer, Function<N, T> reader) {
        this.data = data;
        this.key = key;
        this.tagKey = tagKey;
        this.writer = writer;
        this.reader = reader;
    }

    public T get() {
        return data;
    }

    public void set(T data) {
        this.data = data;
    }

    public void clear() {
        this.data = null;
    }

    public boolean hasData() {
        return data != null;
    }

    public void read(CompoundTag tag) {
        if (tag.contains(key, tagKey) && reader != null) {
            //noinspection unchecked
            this.data = reader.apply((N) tag.get(key));
        }
    }

    public void save(CompoundTag tag) {
        if (writer != null && data != null) {
            tag.put(key, writer.apply(data));
        }
    }

    public static class Builder<T, N extends Tag> {
        private final String key;
        private final byte tagKey;
        private final T data;
        private Function<T, N> writer;
        private Function<N, T> reader;

        public Builder(String key, byte tagKey, T data) {
            this.key = key;
            this.tagKey = tagKey;
            this.data = data;
        }

        public static <T, N extends Tag> Builder<T, N> of(String key, TagType<N> tagKey, T data) {
            return new Builder<>(key, idFromTagType(tagKey), data);
        }

        public Builder<T, N> withWriter(Function<T, N> writer) {
            this.writer = writer;
            return this;
        }

        public Builder<T, N> withReader(Function<N, T> reader) {
            this.reader = reader;
            return this;
        }

        public SerializedDataEntry<T, N> build() {
            return new SerializedDataEntry<>(data, key, tagKey, writer, reader);
        }

        private static byte idFromTagType(TagType<?> type) {
            byte id = -1;
            if (type == StringTag.TYPE) {
                id = Tag.TAG_STRING;
            } else if (type == IntTag.TYPE) {
                id = Tag.TAG_INT;
            } else if (type == ByteTag.TYPE) {
                id = Tag.TAG_BYTE;
            } else if (type == ShortTag.TYPE) {
                id = Tag.TAG_SHORT;
            } else if (type == LongTag.TYPE) {
                id = Tag.TAG_LONG;
            } else if (type == FloatTag.TYPE) {
                id = Tag.TAG_FLOAT;
            } else if (type == DoubleTag.TYPE) {
                id = Tag.TAG_DOUBLE;
            } else if (type == ByteArrayTag.TYPE) {
                id = Tag.TAG_BYTE_ARRAY;
            } else if (type == ListTag.TYPE) {
                id = Tag.TAG_LIST;
            } else if (type == CompoundTag.TYPE) {
                id = Tag.TAG_COMPOUND;
            } else if (type == IntArrayTag.TYPE) {
                id = Tag.TAG_INT_ARRAY;
            } else if (type == LongArrayTag.TYPE) {
                id = Tag.TAG_LONG_ARRAY;
            }
            if (TagTypes.getType(id) != type) {
                throw new IllegalArgumentException("Unknown tag type " + type);
            }
            return id;
        }
    }
}
