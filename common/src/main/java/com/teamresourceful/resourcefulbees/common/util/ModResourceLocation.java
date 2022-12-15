package com.teamresourceful.resourcefulbees.common.util;

import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.StringUtils;

public class ModResourceLocation extends ResourceLocation {

    public ModResourceLocation(String[] strings) {
        super(ifNoNamespace(strings));
    }

    public ModResourceLocation(String string) {
        this(deconstruct(string, ':'));
    }

    public ModResourceLocation(String string, String string2) {
        this(new String[]{string, string2});
    }

    private static String[] ifNoNamespace(String[] strings) {
        if (StringUtils.isEmpty(strings[0])) {
            strings[0] = ModConstants.MOD_ID;
        }
        return strings;
    }

    private static String[] deconstruct(String location, char separator) {
        String[] strings = new String[]{ModConstants.MOD_ID, location};
        int i = location.indexOf(separator);
        if (i >= 0) {
            strings[1] = location.substring(i + 1);
            if (i >= 1) {
                strings[0] = location.substring(0, i);
            }
        }

        return strings;
    }
}
