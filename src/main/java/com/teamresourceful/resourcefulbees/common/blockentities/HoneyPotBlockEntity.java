package com.teamresourceful.resourcefulbees.common.blockentities;

import com.teamresourceful.resourcefulbees.common.blockentities.base.GUISyncedBlockEntity;
import com.teamresourceful.resourcefulbees.common.components.TankData;
import com.teamresourceful.resourcefulbees.common.lib.constants.translations.GuiTranslations;
import com.teamresourceful.resourcefulbees.common.lib.tags.ModFluidTags;
import com.teamresourceful.resourcefulbees.common.menus.HoneyPotMenu;
import com.teamresourceful.resourcefulbees.common.menus.content.PositionContent;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlockEntityTypes;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModDataComponents;
import com.teamresourceful.resourcefullib.common.menu.ContentMenuProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.FluidStacksResourceHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class HoneyPotBlockEntity extends GUISyncedBlockEntity implements ContentMenuProvider<PositionContent> {

    public static final int TANK_CAPACITY = 64000;
    private final FluidHandler tank = new FluidHandler();
    private List<TankData> tankData = Collections.nCopies(1, TankData.EMPTY);

    public HoneyPotBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.HONEY_POT_TILE_ENTITY.get(), pos, state);
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, @NotNull Inventory playerInventory, @NotNull Player playerEntity) {
        return new HoneyPotMenu(id, playerInventory, this);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return GuiTranslations.POT;
    }

    @Override
    public PositionContent createContent(ServerPlayer serverPlayer) {
        return new PositionContent(this.getBlockPos());
    }

    @Override
    public DataComponentPatch getSyncData() {
        return DataComponentPatch.builder()
                .set(ModDataComponents.TANK_DATA.get(), createTankDataPatch())
                .build();
    }

    @Override
    public <Data> void setSyncData(DataComponentType<Data> type, Optional<Data> data) {
        if (type == ModDataComponents.TANK_DATA.get()) {
            data.ifPresent(data1 -> tankData = (List<TankData>) data1);
        }
    }

    private @NonNull List<TankData> createTankDataPatch() {
        return List.of(new TankData(tank.fluidStack(), tank.getCapacity()));
    }

    public FluidHandler tank() {
        return tank;
    }

    public List<TankData> tankData() {
        return tankData;
    }

    public class FluidHandler extends FluidStacksResourceHandler {

        public FluidHandler() {
            super(1, TANK_CAPACITY);
        }

        @Override
        public boolean isValid(int index, FluidResource resource) {
            return resource.is(ModFluidTags.HONEY);
        }

        @Override
        protected void onContentsChanged(int index, @NonNull FluidStack previousContents) {
            super.onContentsChanged(index, previousContents);
            HoneyPotBlockEntity.this.setChanged();
            sendToListeningPlayers();
        }

        public FluidStack fluidStack() {
            return stacks.getFirst();
        }

        public int getCapacity() {
            return capacity;
        }
    }
}
