package com.resourcefulbees.resourcefulbees.data;

import com.resourcefulbees.resourcefulbees.init.BeeSetup;
import net.minecraft.resources.FolderPack;
import net.minecraft.resources.IPackFinder;
import net.minecraft.resources.IPackNameDecorator;
import net.minecraft.resources.ResourcePackInfo;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.function.Consumer;

public class DataPackLoader implements IPackFinder {

    public static final DataPackLoader INSTANCE = new DataPackLoader();

    @Override
    public void loadPacks(@NotNull Consumer<ResourcePackInfo> packList, ResourcePackInfo.@NotNull IFactory factory) {
        File configDataPackFile = BeeSetup.getResourcePath().toFile();
        if(configDataPackFile.exists() && configDataPackFile.isDirectory()) {
            ResourcePackInfo pack = ResourcePackInfo.create(
                    "resourcefulbees:internals",
                    true,
                    () -> new FolderPack(BeeSetup.getResourcePath().toFile()),
                    factory,
                    ResourcePackInfo.Priority.BOTTOM,
                    IPackNameDecorator.BUILT_IN
            );
            if (pack != null) packList.accept(pack);
        }
    }
}
