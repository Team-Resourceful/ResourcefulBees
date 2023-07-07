package com.teamresourceful.resourcefulbees.common.blockentities.base;

import com.teamresourceful.resourcefullib.common.menu.ContentMenuProvider;
import com.teamresourceful.resourcefullib.common.menu.MenuContent;
import earth.terrarium.botarium.common.item.ItemContainerBlock;

public interface ContentContainerBlock<T extends MenuContent<T>> extends ItemContainerBlock, ContentMenuProvider<T> {
}
