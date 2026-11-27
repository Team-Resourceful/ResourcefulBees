
package com.teamresourceful.resourcefulbees.common.logistics;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.common.entities.entity.ResourcefulBee;
import net.minecraft.core.UUIDUtil;
import net.minecraft.util.StringRepresentable;
import org.jspecify.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public record ColonyCourierSnapshot(
        UUID id,
        String name,
        Location location,
        @Nullable UUID routeId,
        String deliveryState,
        String cargoName,
        int cargoAmount,
        boolean manualMode,
        @Nullable UUID preferredRouteId
) {

    public static final Codec<ColonyCourierSnapshot> CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    UUIDUtil.CODEC.fieldOf("id")
                            .forGetter(ColonyCourierSnapshot::id),
                    Codec.STRING.fieldOf("name")
                            .forGetter(ColonyCourierSnapshot::name),
                    Location.CODEC.fieldOf("location")
                            .forGetter(ColonyCourierSnapshot::location),
                    UUIDUtil.CODEC.optionalFieldOf("route_id")
                            .forGetter(snapshot -> Optional.ofNullable(snapshot.routeId())),
                    Codec.STRING.fieldOf("delivery_state")
                            .forGetter(ColonyCourierSnapshot::deliveryState),
                    Codec.STRING.fieldOf("cargo_name")
                            .forGetter(ColonyCourierSnapshot::cargoName),
                    Codec.INT.fieldOf("cargo_amount")
                            .forGetter(ColonyCourierSnapshot::cargoAmount),
                    Codec.BOOL.fieldOf("manual_mode")
                            .forGetter(ColonyCourierSnapshot::manualMode),
                    UUIDUtil.CODEC.optionalFieldOf("preferred_route_id")
                            .forGetter(snapshot -> Optional.ofNullable(snapshot.preferredRouteId()))
            ).apply(instance, (id, name, location, routeId, deliveryState, cargoName, cargoAmount, manualMode ,preferredRoute) ->
                    new ColonyCourierSnapshot(
                            id,
                            name,
                            location,
                            routeId.orElse(null),
                            deliveryState,
                            cargoName,
                            cargoAmount,
                            manualMode,
                            preferredRoute.orElse(null)
                    )
            ));

    public enum Location implements StringRepresentable {
        ACTIVE("active"),
        STORED("stored"),
        UNLOADED("unloaded");

        public static final Codec<Location> CODEC =
                StringRepresentable.fromEnum(Location::values);

        private final String serializedName;

        Location(String serializedName) {
            this.serializedName = serializedName;
        }

        @Override
        public String getSerializedName() {
            return serializedName;
        }
    }

    public static CargoSummary cargoFrom(ResourcefulBee bee) {
        var items = bee.getCourierCargo();

        for (int slot = 0; slot < items.size(); slot++) {
            int amount = items.getAmountAsInt(slot);

            if (amount > 0) {
                return new CargoSummary(
                        "ITEM",
                        items.getResource(slot)
                                .toStack(amount)
                                .getHoverName()
                                .getString(),
                        amount
                );
            }
        }

        var fluids = bee.getCourierFluidCargo();

        for (int slot = 0; slot < fluids.size(); slot++) {
            int amount = fluids.getAmountAsInt(slot);

            if (amount > 0) {
                return new CargoSummary(
                        "FLUID",
                        fluids.getResource(slot)
                                .getFluid()
                                .toString(),
                        amount
                );
            }
        }

        int energy = bee.getCourierEnergyCargo().getAmountAsInt();

        if (energy > 0) {
            return new CargoSummary("ENERGY", "Energy", energy);
        }

        return new CargoSummary("EMPTY", "Empty", 0);
    }

    public record CargoSummary(
            String type,
            String name,
            int amount
    ) {
    }
}
