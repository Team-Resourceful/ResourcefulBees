package com.teamresourceful.resourcefulbees.common.item.upgrade.nestupgrade;

import com.teamresourceful.resourcefulbees.common.item.upgrade.Upgrade;

public interface NestUpgrade extends Upgrade {
    BeehiveUpgrade getTier();
}
