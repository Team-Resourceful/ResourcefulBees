package com.teamresourceful.resourcefulbees.common.config;

import com.teamresourceful.resourcefulconfig.common.annotations.*;
import com.teamresourceful.resourcefulconfig.common.config.EntryType;

@Category(id = "honey_generator", translation = "Honey Generator")
public final class HoneyGenConfig {

    @ConfigEntry(
        id = "energyTransferAmount",
        type = EntryType.INTEGER,
        translation = "Energy Transfer Amount"
    )
    @Comment("Amount of energy transferred out of the generator in rf/t.")
    @IntRange(min = 50, max = 500)
    public static int energyTransferAmount = 100;

    @ConfigEntry(
        id = "maxEnergyCapacity",
        type = EntryType.INTEGER,
        translation = "Max Energy Capacity"
    )
    @Comment("Maximum internal energy buffer.")
    @IntRange(min = 10000, max = 1000000)
    public static int maxEnergyCapacity = 100000;

    @ConfigEntry(
        id = "maxTankCapacity",
        type = EntryType.INTEGER,
        translation = "Max Tank Capacity"
    )
    @Comment("Maximum internal honey capacity.")
    @IntRange(min = 1000, max = 100000)
    public static int maxTankCapacity = 10000;

    @ConfigEntry(
            id = "upgradeStackLimit",
            type = EntryType.INTEGER,
            translation = "Upgrade Stack Limit"
    )
    @Comment("""
            Stack limit applies to all honey generator upgrades!
            Note: Value cannot be reloaded without restarting game.
            """)
    @IntRange(min = 1, max = 64)
    public static int upgradeStackLimit = 16;

    @ConfigEntry(
            id = "tankCapacityUpgradeBonus",
            type = EntryType.DOUBLE,
            translation = "Tank Capacity Upgrade Bonus"
    )
    @Comment("""
            x=b+(b*p*n). (WIP - may change)
            b = maxTankCapacity
            p = this
            n = total upgrades in slot
                                    
            Recommended value:
            64 stack size: 0.5
            32 stack size: 0.5
            16 stack size: 0.5
            """)
    @DoubleRange(min = .01, max = 2.00)
    public static double tankCapacityUpgradeBonus = 0.5;

    @ConfigEntry(
            id = "energyCapacityUpgradeBonus",
            type = EntryType.DOUBLE,
            translation = "Energy Capacity Upgrade Bonus"
    )
    @Comment("""
            x=b+(b*p*n). (WIP - may change)
            b = maxTankCapacity
            p = this
            n = total upgrades in slot
                        
            Recommended value:
            64 stack size: 2.0
            32 stack size: 2.0
            16 stack size: 2.25
            """)
    @DoubleRange(min = .01, max = 4.00)
    public static double energyCapacityUpgradeBonus = 2.25;

    @ConfigEntry(
            id = "honeyConsumptionUpgradePenalty",
            type = EntryType.DOUBLE,
            translation = "Honey Consumption Upgrade Bonus"
    )
    @Comment("""
            x=b+(b*p*n). (WIP - may change)
            b = honey consumption rate
            p = this
            n = total upgrades in slot
            
            Recommended value:
            64 stack size: 2.0
            32 stack size: 2.5
            16 stack size: 2.75
            """)
    @DoubleRange(min = 0.01, max = 4.00)
    public static double honeyConsumptionUpgradePenalty = 2.75;

    @ConfigEntry(
            id = "energyFillUpgradeBonus",
            type = EntryType.DOUBLE,
            translation = "Energy Fill Upgrade Bonus"
    )
    @Comment("""
            consumption rate increases as fill rate increases
            x=b*p^n. (WIP - may change)
            b = energy fill rate
            p = this
            n = total upgrades in slot
            
            Recommended value:
            64 stack size: 1.1
            32 stack size: 1.15
            16 stack size: 1.25
            """)
    @DoubleRange(min = 1.0, max = 2.0)
    public static double energyFillUpgradeBonus = 1.25;

    @ConfigEntry(
            id = "energyTransferUpgradeBonus",
            type = EntryType.DOUBLE,
            translation = "Energy Transfer Upgrade Bonus"
    )
    @Comment("""
            x=b*p^n. (WIP - may change)
            b = energy transfer rate
            p = this
            n = total upgrades in slot
             
            Recommended value:
            64 stack size: 1.15
            32 stack size: 1.3
            16 stack size: 1.5
            """)
    @DoubleRange(min = 1.0, max = 2.0)
    public static double energyTransferUpgradeBonus = 1.5;
}
