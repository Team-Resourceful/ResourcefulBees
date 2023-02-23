package com.teamresourceful.resourcefulbees.platform.common.menu;

import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.Nullable;

public interface MenuContentSerializer<T extends MenuContent<T>> {

    @Nullable
    T from(FriendlyByteBuf buffer);

    void to(FriendlyByteBuf buffer, T content);
}
