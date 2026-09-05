package com.teamresourceful.resourcefulbees.common.blockentities;

import com.teamresourceful.resourcefulbees.common.components.BeeBoxOccupant;
import com.teamresourceful.resourcefulbees.common.components.BeeBoxOccupants;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlockEntityTypes;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModDataComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

public class BeeBoxBlockEntity extends BlockEntity {

    private static final String OCCUPANTS_KEY = "occupants";

    private BeeBoxOccupants occupants = BeeBoxOccupants.EMPTY;

    public BeeBoxBlockEntity(BlockPos pos, BlockState state) {
        super(
                ModBlockEntityTypes.BEE_BOX_ENTITY.get(),
                pos,
                state
        );
    }

    public BeeBoxOccupants getOccupants() {
        return this.occupants;
    }

    public void setOccupants(BeeBoxOccupants occupants) {
        this.occupants = occupants;
        this.setChanged();
    }

    public boolean hasBees() {
        return !this.occupants.isEmpty();
    }

    public int beeCount() {
        return this.occupants.size();
    }

    public boolean isFull() {
        return this.occupants.isFull();
    }

    public boolean addBee(Entity entity) {
        if (this.isFull()) {
            return false;
        }

        this.occupants = this.occupants.add(
                BeeBoxOccupant.from(entity)
        );

        this.setChanged();

        return true;
    }

    public void summonBees(Level level, BlockPos pos) {
        if (level.isClientSide() || this.occupants.isEmpty()) {
            return;
        }

        List<BeeBoxOccupant> remaining = new ArrayList<>();

        for (BeeBoxOccupant occupant : this.occupants.occupants()) {
            Entity entity = occupant.createEntity(level, pos);

            if (entity == null || !level.addFreshEntity(entity)) {
                remaining.add(occupant);
            }
        }

        this.occupants = remaining.isEmpty()
                ? BeeBoxOccupants.EMPTY
                : new BeeBoxOccupants(remaining);

        this.setChanged();
    }

    @Override
    protected void loadAdditional(
            @NotNull ValueInput input
    ) {
        super.loadAdditional(input);

        this.occupants = input.read(
                        OCCUPANTS_KEY,
                        BeeBoxOccupants.CODEC
                )
                .orElse(BeeBoxOccupants.EMPTY);
    }

    @Override
    protected void collectImplicitComponents(
            DataComponentMap.@NonNull Builder components
    ) {
        super.collectImplicitComponents(components);

        if (!this.occupants.isEmpty()) {
            components.set(
                    ModDataComponents.BEE_BOX_OCCUPANTS.get(),
                    this.occupants
            );
        }
    }

    @Override
    protected void applyImplicitComponents(
            @NonNull DataComponentGetter components
    ) {
        super.applyImplicitComponents(components);

        this.occupants = components.getOrDefault(
                ModDataComponents.BEE_BOX_OCCUPANTS.get(),
                BeeBoxOccupants.EMPTY
        );
    }

    @Override
    public void removeComponentsFromTag(
            @NotNull ValueOutput output
    ) {
        super.removeComponentsFromTag(output);

        output.discard(OCCUPANTS_KEY);
    }
}