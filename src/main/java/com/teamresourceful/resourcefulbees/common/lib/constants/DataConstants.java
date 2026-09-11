
package com.teamresourceful.resourcefulbees.common.lib.constants;

import com.teamresourceful.resourcefullib.common.exceptions.UtilityClassException;

public final class DataConstants {

    private DataConstants() throws UtilityClassException {
        throw new UtilityClassException();
    }

    public static final String FEED_COUNT = "feedCount";
    public static final String MUTATION_COUNT = "mutationCount";

    public static class Beehive {
        public static final String SMOKED = "smoked";
        public static final String HONEYCOMBS = "honeycombs";
    }

    public static class Beepedia {
        public static final String COMPLETE = "complete";
        public static final String CREATIVE = "creative";
    }
}
