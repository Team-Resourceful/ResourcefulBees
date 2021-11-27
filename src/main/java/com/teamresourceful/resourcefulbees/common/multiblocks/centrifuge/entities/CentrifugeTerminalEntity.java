package com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.entities;

import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.containers.CentrifugeTerminalContainer;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.entities.base.AbstractGUICentrifugeEntity;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.helpers.CentrifugeTier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.RegistryObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CentrifugeTerminalEntity extends AbstractGUICentrifugeEntity {

    public CentrifugeTerminalEntity(RegistryObject<TileEntityType<CentrifugeTerminalEntity>> entityType, CentrifugeTier tier) {
        super(entityType.get(), tier);
    }

    @Override
    protected ITextComponent getDefaultName() {
        return new TranslationTextComponent("gui.centrifuge.terminal." + tier.getName());
    }

    //TODO see if this can be handled generically via the superclass AbstractGUICentrifugeEntity
    @Nullable
    @Override
    public Container createMenu(int id, @NotNull PlayerInventory playerInventory, @NotNull PlayerEntity playerEntity) {
        controller.updateCentrifugeState(centrifugeState);
        return new CentrifugeTerminalContainer(id, playerInventory, this);
    }
}
