package com.resourcefulbees.resourcefulbees.data;

import com.resourcefulbees.resourcefulbees.init.BeeSetup;
import net.minecraft.server.packs.FolderPackResources;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.function.Consumer;

public class DataPackLoader implements RepositorySource {

    public static final DataPackLoader INSTANCE = new DataPackLoader();

    @Override
    public void loadPacks(@NotNull Consumer<Pack> packList, @NotNull Pack.PackConstructor factory) {
        File configDataPackFile = BeeSetup.getResourcePath().toFile();
        if(configDataPackFile.exists() && configDataPackFile.isDirectory()) {
            Pack pack = Pack.create(
                    "resourcefulbees:internals",
                    true,
                    () -> new FolderPackResources(BeeSetup.getResourcePath().toFile()),
                    factory,
                    Pack.Position.BOTTOM,
                    PackSource.BUILT_IN
            );
            if (pack != null) packList.accept(pack);
        }
    }
}
