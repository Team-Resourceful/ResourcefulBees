package com.teamresourceful.resourcefulbees.common.menus.content;

import com.teamresourceful.resourcefulbees.common.util.WorldUtils;
import com.teamresourceful.resourcefulbees.platform.common.menu.MenuContent;
import com.teamresourceful.resourcefulbees.platform.common.menu.MenuContentSerializer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record PositionContent(BlockPos pos) implements MenuContent<PositionContent> {

    public static final MenuContentSerializer<PositionContent> SERIALIZER = new Serializer();

    public <T extends BlockEntity> T getBlockEntity(Level level, Class<T> type) {
        if (!level.isClientSide) return null;
        return WorldUtils.getTileEntity(type, level, pos);
    }

    public static <T extends BlockEntity> T getOrNull(Optional<PositionContent> content, Level level, Class<T> type) {
        return content.map(positionContent -> positionContent.getBlockEntity(level, type)).orElse(null);
    }

    @Override
    public MenuContentSerializer<PositionContent> serializer() {
        return SERIALIZER;
    }

    private static class Serializer implements MenuContentSerializer<PositionContent> {

        @Override
        public @Nullable PositionContent from(FriendlyByteBuf buffer) {
            return new PositionContent(buffer.readBlockPos());
        }

        @Override
        public void to(FriendlyByteBuf buffer, PositionContent content) {
            buffer.writeBlockPos(content.pos);
        }
    }
}
