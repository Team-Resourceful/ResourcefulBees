package com.dungeonderps.resourcefulbees.config;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;
import com.electronwill.nightconfig.toml.TomlFormat;
import com.electronwill.nightconfig.core.Config;

import java.util.ArrayList;
import java.util.Map;

public class ResourcefulBeesConfig {
    public static final Common COMMON;

    public static final ForgeConfigSpec COMMON_SPEC;

    static {
        final Pair<Common, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Common::new);
        COMMON = specPair.getLeft();
        COMMON_SPEC = specPair.getRight();
    }

    public static class Common {
        public final ForgeConfigSpec.ConfigValue<Config> mobCustomBees;

        Common(final ForgeConfigSpec.Builder builder) {
            builder.push("common");

            final Map<String, ArrayList<String>>;
//            this.mobCustomBees = builder.comment()
        }
    }

    public static class BeeConfig {
        private ForgeConfigSpec.BooleanValue enabled;
        private ForgeConfigSpec.ConfigValue<ArrayList<Integer>> beeColor;
        private ForgeConfigSpec.ConfigValue<ArrayList<Integer>> hiveColor1;
        private ForgeConfigSpec.ConfigValue<ArrayList<Integer>> hiveColor2;
        private ForgeConfigSpec.ConfigValue<String> name;
        private ForgeConfigSpec.ConfigValue<String> resource;


    }
}
