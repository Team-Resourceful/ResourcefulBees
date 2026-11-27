package com.teamresourceful.resourcefulbees.common.setup;

import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import net.minecraft.resources.Identifier;
import net.neoforged.fml.ModList;

import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public final class ModDataDiscovery {

    private ModDataDiscovery() {
    }

    @FunctionalInterface
    public interface ResourceConsumer {
        void accept(Identifier id, Reader reader) throws IOException;
    }

    public static void discover(String category, ResourceConsumer consumer) {
        String prefix = ModConstants.MOD_ID + "/" + category + "/";
        Map<Identifier, String> discovered = new HashMap<>();

        ModList.get().getModFiles().forEach(modFileInfo -> {
            var modFile = modFileInfo.getFile();
            var contents = modFile.getContents();

            contents.visitContent("data", (path, resource) -> {
                String normalized = path.replace('\\', '/');

                // Expected:
                // data/<namespace>/resourcefulbees/<category>/<name>.json
                String[] parts = normalized.split("/", 3);

                if (parts.length != 3 || !parts[0].equals("data")) {
                    return;
                }

                String namespace = parts[1];
                String relative = parts[2];

                if (!relative.startsWith(prefix)
                        || !relative.endsWith(".json")) {
                    return;
                }

                String name = relative.substring(
                        prefix.length(),
                        relative.length() - ".json".length()
                );

                if (name.isEmpty()) {
                    return;
                }

                Identifier id = Identifier.fromNamespaceAndPath(
                        namespace,
                        name.toLowerCase(Locale.ROOT).replace(" ", "_")
                );

                String source = modFile.getFileName() + ":" + normalized;

                String previous = discovered.putIfAbsent(id, source);

                if (previous != null) {
                    throw new IllegalStateException(
                            "Duplicate Resourceful Bees " + category
                                    + " definition " + id
                                    + " in " + previous
                                    + " and " + source
                    );
                }

                try (Reader reader = resource.bufferedReader(
                        StandardCharsets.UTF_8
                )) {
                    consumer.accept(id, reader);

                    ModConstants.LOGGER.debug(
                            "Discovered {} from {}",
                            id,
                            source
                    );
                } catch (IOException e) {
                    throw new UncheckedIOException(
                            "Failed to load " + source,
                            e
                    );
                }
            });
        });
    }
}
