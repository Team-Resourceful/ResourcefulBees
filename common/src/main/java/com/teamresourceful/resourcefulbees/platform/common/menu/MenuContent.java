package com.teamresourceful.resourcefulbees.platform.common.menu;

public interface MenuContent<T extends MenuContent<T>> {

    MenuContentSerializer<T> serializer();
}
