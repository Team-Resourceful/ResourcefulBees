package com.teamresourceful.resourcefulbees.common.item.upgrade.nestupgrade;

import com.teamresourceful.resourcefulbees.common.item.upgrade.IUpgrade;

public interface INestUpgrade extends IUpgrade {
    BeehiveUpgrade getTier();
}
