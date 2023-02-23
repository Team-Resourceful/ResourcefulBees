package com.teamresourceful.resourcefulbees.platform.common.menu;

import com.teamresourceful.resourcefulbees.platform.NotImplementedError;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public final class MenuContentHelper {

    @ExpectPlatform
    public static <T extends AbstractContainerMenu, C extends MenuContent<C>> MenuType<T> create(MenuFactory<T, C> factory, MenuContentSerializer<C> serializer) {
        throw new NotImplementedError();
    }

    @ExpectPlatform
    public static <C extends MenuContent<C>> void open(ServerPlayer player, ContentMenuProvider<C> provider) {
        throw new NotImplementedError();
    }

    public interface MenuFactory<T extends AbstractContainerMenu, C extends MenuContent<C>> extends MenuType.MenuSupplier<T> {
        @NotNull T create(int id, Inventory inventory, Optional<C> content);

        @Override
        default @NotNull T create(int id, @NotNull Inventory inventory) {
            return create(id, inventory, Optional.empty());
        }
    }
}
