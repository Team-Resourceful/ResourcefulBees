package com.teamresourceful.resourcefulbees.client.components.centrifuge.infopanels;

import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.entities.base.AbstractGUICentrifugeEntity;
import com.teamresourceful.resourcefullib.client.components.ParentWidget;
import org.jetbrains.annotations.Nullable;

//TODO ParentWidget might not be the best class to extend from
public abstract class AbstractInfoPanel<T extends AbstractGUICentrifugeEntity> extends ParentWidget {

    protected @Nullable T selectedEntity;

    protected AbstractInfoPanel(int x, int y) {
        super(x, y);
    }

    @Override
    protected void init() {

    }

    public abstract void updateSelectedEntity(AbstractGUICentrifugeEntity selectedEntity);

}
