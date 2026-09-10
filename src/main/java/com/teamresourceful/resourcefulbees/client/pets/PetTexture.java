package com.teamresourceful.resourcefulbees.client.pets;

import com.google.common.hash.Hashing;
import com.mojang.blaze3d.platform.NativeImage;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.Identifier;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public final class PetTexture {

    public static final Set<String> ALLOWED_DOMAINS = Set.of(
            "resourcefulbees.com",
            "teamresourceful.com",
            "raw.githubusercontent.com",
            "localhost"
    );

    public static final PetTexture DEFAULT = new PetTexture("missing", null);

    private final String id;
    private final String texture;
    private final Identifier location;

    @Nullable
    private DynamicTexture image;

    public PetTexture(String id, @Nullable String texture) {
        this.id = getIdHash(id, texture);
        this.texture = texture;

        this.location = Identifier.fromNamespaceAndPath(
                "resourcefulbees_pet",
                "textures/entity/" + this.id
        );
    }

    @SuppressWarnings("deprecation")
    private static String getIdHash(String id, @Nullable String texture) {
        String hashedUrl = FilenameUtils.getBaseName(texture);

        return Hashing.sha1()
                .hashUnencodedChars(id + (hashedUrl == null ? "" : hashedUrl))
                .toString();
    }

    public Identifier getResourceLocation() {
        checkOrDownload();
        return location;
    }

    @Nullable
    public String getTexture() {
        return texture;
    }

    public void checkOrDownload() {
        if (image != null || texture == null) {
            return;
        }

        File cacheFile = new File(
                Minecraft.getInstance().gameDirectory,
                "teamresourceful/pet_bees/cache/" + id
        );

        loadTexture(cacheFile, texture);
    }

    private void loadTexture(File file, String url) {
        Optional<NativeImage> cached =
                file.isFile() ? loadImage(file) : Optional.empty();

        if (cached.isPresent()) {
            register(cached.get());
            return;
        }

        CompletableFuture.runAsync(() ->
                createUrl(url).ifPresent(downloadUrl -> {
                    HttpURLConnection connection = null;

                    try {
                        connection = (HttpURLConnection) downloadUrl.openConnection(
                                Minecraft.getInstance().getProxy()
                        );

                        connection.setDoInput(true);
                        connection.setDoOutput(false);
                        connection.connect();

                        if (connection.getResponseCode() / 100 != 2) {
                            return;
                        }

                        FileUtils.copyInputStreamToFile(
                                connection.getInputStream(),
                                file
                        );

                        loadImage(file).ifPresent(nativeImage ->
                                Minecraft.getInstance().execute(
                                        () -> register(nativeImage)
                                )
                        );

                    } catch (IOException e) {
                        ModConstants.LOGGER.warn(
                                "Failed to download pet texture {}",
                                downloadUrl,
                                e
                        );
                    } finally {
                        if (connection != null) {
                            connection.disconnect();
                        }
                    }
                })
        );
    }

    private void register(NativeImage nativeImage) {
        Minecraft minecraft = Minecraft.getInstance();

        DynamicTexture dynamicTexture =
                new DynamicTexture(
                        location::toString,
                        nativeImage
                );

        this.image = dynamicTexture;

        minecraft.getTextureManager().register(
                location,
                dynamicTexture
        );
    }

    private static Optional<NativeImage> loadImage(File file) {
        try (FileInputStream stream = new FileInputStream(file)) {
            return Optional.of(NativeImage.read(stream));
        } catch (Exception _) {
            return Optional.empty();
        }
    }

    private static Optional<URL> createUrl(@Nullable String string) {
        if (string == null) {
            return Optional.empty();
        }

        try {
            URL url = new URI(string).toURL();

            if (!ALLOWED_DOMAINS.contains(url.getHost())) {
                ModConstants.LOGGER.warn(
                        "Tried to load texture from disallowed domain: {}",
                        url.getHost()
                );

                return Optional.empty();
            }

            boolean secure = url.getProtocol().equals("https");
            boolean local = url.getHost().equals("localhost");

            if (!secure && !local) {
                return Optional.empty();
            }

            return Optional.of(url);
        } catch (Exception _) {
            return Optional.empty();
        }
    }
}