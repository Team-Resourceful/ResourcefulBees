package com.teamresourceful.resourcefulbees.common.blockentities.base;

import com.teamresourceful.bytecodecs.base.ByteCodec;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;

import java.io.IOException;
import java.util.Optional;

public final class ListTagByteCodec implements ByteCodec<Optional<ListTag>> {

    public static final ListTagByteCodec INSTANCE = new ListTagByteCodec();

    @Override
    public void encode(Optional<ListTag> value, ByteBuf buffer) {
        if (value.isEmpty()) {
            buffer.writeByte(0);
        } else {
            try {
                NbtIo.write(value.get(), new ByteBufOutputStream(buffer));
            } catch (IOException exception) {
                throw new RuntimeException(exception);
            }
        }

    }

    @Override
    public Optional<ListTag> decode(ByteBuf buffer) {
        int i = buffer.readerIndex();
        byte b = buffer.readByte();
        if (b == 0) {
            return Optional.empty();
        } else {
            buffer.readerIndex(i);

            try {
                return Optional.of(NbtIo.read(new ByteBufInputStream(buffer), NbtAccounter.create(2097152L)));
            } catch (IOException exception) {
                throw new RuntimeException(exception);
            }
        }
    }
}
