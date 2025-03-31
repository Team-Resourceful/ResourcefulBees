package com.teamresourceful.resourcefulbees.common.items.upgrade.nestupgrade;

import com.teamresourceful.resourcefulbees.common.items.upgrade.Upgrade;

public interface NestUpgrade extends Upgrade {
    BeehiveUpgrade getTier();
}
