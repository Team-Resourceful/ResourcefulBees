package com.teamresourceful.resourcefulbees.platform.common.events;

import com.teamresourceful.resourcefulbees.platform.common.events.base.EventHelper;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Mob;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public record SpawnBabyEvent(Mob parent1, Mob parent2, AtomicReference<AgeableMob> child, AtomicBoolean canceled) {

    public static final EventHelper<SpawnBabyEvent> EVENT = new EventHelper<>();

    public SpawnBabyEvent(Mob parent1, Mob parent2, AgeableMob child) {
        this(parent1, parent2, new AtomicReference<>(child), new AtomicBoolean(false));
    }

    public AgeableMob getChild() {
        return child.get();
    }

    public void setChild(AgeableMob child) {
        this.child.set(child);
    }

    public boolean isCanceled() {
        return canceled.get();
    }

    public void setCanceled(boolean canceled) {
        this.canceled.set(canceled);
    }
}
