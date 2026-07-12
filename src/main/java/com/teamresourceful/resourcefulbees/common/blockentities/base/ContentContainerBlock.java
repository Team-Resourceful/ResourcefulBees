
package com.teamresourceful.resourcefulbees.common.blockentities.base;

import com.teamresourceful.resourcefullib.common.menu.ContentMenuProvider;
import com.teamresourceful.resourcefullib.common.menu.MenuContent;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;


public interface ContentContainerBlock<T extends MenuContent<T>> extends ContentMenuProvider<T> {
}

