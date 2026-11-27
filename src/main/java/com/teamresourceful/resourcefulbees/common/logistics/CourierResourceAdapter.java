package com.teamresourceful.resourcefulbees.common.logistics;

import net.minecraft.server.level.ServerLevel;

public interface CourierResourceAdapter {

    boolean canExtract(ServerLevel level, CourierEndpoint endpoint);

    boolean canInsert(ServerLevel level, CourierEndpoint endpoint);

    int estimateAvailable(ServerLevel level, CourierEndpoint endpoint);

    int estimateSpace(ServerLevel level, CourierEndpoint endpoint);
}