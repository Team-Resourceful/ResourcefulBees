package com.teamresourceful.resourcefulbees.common.utils;

public record Vec2i(int x, int z) {
    public static Vec2i of(int x, int z) {
        return new Vec2i(x, z);
    }
}
