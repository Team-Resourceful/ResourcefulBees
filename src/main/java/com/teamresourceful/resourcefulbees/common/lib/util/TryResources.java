package com.teamresourceful.resourcefulbees.common.lib.util;

/**
 * A simple interface that extends AutoCloseable, allowing for the use of try-with-resources statements.
 * This removes the Exception from the close method, making it easier to use in contexts where you don't want to handle checked exceptions.
 */
public interface TryResources extends AutoCloseable {

    @Override
    void close();
}
