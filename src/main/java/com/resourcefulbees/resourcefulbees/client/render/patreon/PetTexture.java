package com.resourcefulbees.resourcefulbees.client.render.patreon;

import com.google.common.collect.Sets;
import com.google.common.hash.Hashing;
import com.mojang.blaze3d.systems.RenderSystem;
import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class PetTexture {

    public static final Set<String> ALLOWED_DOMAINS = Sets.newHashSet(
            "resourcefulbees.com",
            "teamresourceful.com",
            "raw.githubusercontent.com",
            "localhost"
    );

    private static final ResourceLocation LOADING = new ResourceLocation("textures/entity/bee/bee.png");
    public static final PetTexture DEFAULT = new PetTexture("missing", null, LOADING);


    private final String id;
    private final String texture;
    private SimpleTexture img;
    private ResourceLocation location;

    public PetTexture(String id, @Nullable String texture, ResourceLocation defaultLocation) {
        this.id = getIdHash(id, texture);
        this.texture = texture;
        this.location = defaultLocation;
    }

    @SuppressWarnings("UnstableApiUsage")
    private static String getIdHash(String id, @Nullable String texture) {
        String hashedUrl = FilenameUtils.getBaseName(texture);
        return Hashing.sha1().hashUnencodedChars(id + (hashedUrl == null ? "" : hashedUrl)).toString();
    }

    public ResourceLocation getResourceLocation() {
        checkOrDownload();
        return location;
    }

    public String getTexture() {
        return texture;
    }

    public void checkOrDownload() {
        if (img != null || this.getTexture() == null) return;
        location = new ResourceLocation("resourcefulbees_pet",  "textures/entity/" + id);
        img = new PetTexture.DownloadableTexture(new File(Minecraft.getInstance().gameDirectory,"teamresourceful/pet_bees/cache/" + this.id), this.getTexture(), location);
        Minecraft.getInstance().textureManager.register(this.location, img);
    }


    public static class DownloadableTexture extends SimpleTexture {

        @Nullable
        private final File file;
        @Nullable
        private final String url;
        @Nullable
        private CompletableFuture<Void> future;
        private boolean uploaded;

        public DownloadableTexture(@Nullable File file, @Nullable String url, ResourceLocation location) {
            super(location);
            this.file = file;
            this.url = url;
        }

        private void loadCallback(NativeImage pNativeImage) {
            Minecraft.getInstance().execute(() -> {
                this.uploaded = true;
                if (!RenderSystem.isOnRenderThread()) {
                    RenderSystem.recordRenderCall(() -> this.upload(pNativeImage));
                } else {
                    this.upload(pNativeImage);
                }
            });
        }

        private void upload(NativeImage pImage) {
            TextureUtil.prepareImage(this.getId(), pImage.getWidth(), pImage.getHeight());
            pImage.upload(0, 0, 0, true);
        }

        @Override
        public void load(@NotNull IResourceManager manager) throws IOException {
            Minecraft.getInstance().execute(() -> {
                if (!this.uploaded) {
                    try {
                        super.load(manager);
                    } catch (Exception ignored) {
                        /*Do Nothing*/
                    }
                    this.uploaded = true;
                }
            });
            if (this.future == null) {
                Optional<NativeImage> nativeimage = this.file != null && this.file.isFile() ? this.load(new FileInputStream(this.file)) : Optional.empty();

                if (nativeimage.isPresent()) {
                    this.loadCallback(nativeimage.get());
                } else {
                    this.future = runDownload();
                }
            }
        }

        private CompletableFuture<Void> runDownload() {
            return CompletableFuture.runAsync(() ->
                createUrl(this.url).ifPresent(url -> {
                    HttpURLConnection httpurlconnection = null;
                    try {
                        httpurlconnection = (HttpURLConnection) url.openConnection(Minecraft.getInstance().getProxy());
                        httpurlconnection.setDoInput(true);
                        httpurlconnection.setDoOutput(false);
                        httpurlconnection.connect();

                        if (httpurlconnection.getResponseCode() / 100 == 2) {
                            InputStream inputstream = httpurlconnection.getInputStream();

                            if (this.file != null) {
                                FileUtils.copyInputStreamToFile(httpurlconnection.getInputStream(), this.file);
                            }

                            inputstream.reset();

                            Minecraft.getInstance().execute(() -> this.load(inputstream).ifPresent(this::loadCallback));
                        }
                    } catch (IOException ignored) {
                        //Do Nothing!
                    } finally {
                        if (httpurlconnection != null) httpurlconnection.disconnect();
                    }
                }), Util.backgroundExecutor());
        }


        private Optional<NativeImage> load(InputStream pInputStream) {
            try {
                return Optional.of(NativeImage.read(pInputStream));
            }
            catch (Exception ignored) {
                return Optional.empty();
            }
        }

        private static Optional<URL> createUrl(@Nullable String string) {
            if (string == null) return Optional.empty();
            try {
                URL url = new URL(string);
                if (!PetTexture.ALLOWED_DOMAINS.contains(url.getHost())) {
                    ResourcefulBees.LOGGER.warn("Tried to load texture from disallowed domain: " + url.getHost());
                    return Optional.empty();
                }
                if (!url.getProtocol().equals("https")) return Optional.empty();
                return Optional.of(url);
            } catch (Exception ignored) {
                return Optional.empty();
            }
        }

    }
}
