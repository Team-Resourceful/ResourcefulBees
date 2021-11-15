package com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.entities;

import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.containers.CentrifugeTerminalContainer;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.entities.base.AbstractTieredCentrifugeEntity;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.helpers.CentrifugeTier;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.states.CentrifugeState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.RegistryObject;
import net.roguelogix.phosphophyllite.gui.client.api.IHasUpdatableState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

public class CentrifugeTerminalEntity extends AbstractTieredCentrifugeEntity implements IHasUpdatableState<CentrifugeState> {

    public final CentrifugeState centrifugeState = new CentrifugeState(this);

    public CentrifugeTerminalEntity(RegistryObject<TileEntityType<CentrifugeTerminalEntity>> entityType, CentrifugeTier tier) {
        super(entityType.get(), tier);
    }

    @Override
    public @NotNull ITextComponent getDisplayName() {
        return new TranslationTextComponent("gui.resourcefulbees.centrifuge_terminal");
    }

    @Nullable
    @Override
    public Container createMenu(int id, @NotNull PlayerInventory playerInventory, @NotNull PlayerEntity playerEntity) {
        return new CentrifugeTerminalContainer(id, playerInventory, this);
    }

    @Override
    @Nonnull
    public CentrifugeState getState() {
        this.updateState();
        return this.centrifugeState;
    }

    @Override
    public void updateState() {
        if (controller != null) {
            controller.updateCentrifugeState(centrifugeState);
        }
    }
}
