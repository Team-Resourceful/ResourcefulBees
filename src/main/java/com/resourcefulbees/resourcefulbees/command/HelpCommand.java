package com.resourcefulbees.resourcefulbees.command;

import com.mojang.blaze3d.platform.PlatformDescriptors;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import com.resourcefulbees.resourcefulbees.client.render.patreon.PetInfo;
import com.resourcefulbees.resourcefulbees.client.render.patreon.PetLoader;
import com.resourcefulbees.resourcefulbees.lib.ModConstants;
import com.resourcefulbees.resourcefulbees.registry.BeeRegistry;
import com.resourcefulbees.resourcefulbees.registry.TraitRegistry;
import com.resourcefulbees.resourcefulbees.utils.DiscordMarkdownBuilder;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.packs.ResourcePackLoader;
import net.minecraftforge.versions.forge.ForgeVersion;
import org.apache.commons.lang3.time.DurationFormatUtils;

import java.lang.management.ManagementFactory;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

public final class HelpCommand {

    private HelpCommand() {
        throw new IllegalAccessError(ModConstants.UTILITY_CLASS);
    }

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("resourcefulbees")
                .then(registerDumpCommand())
                .then(registerPetReloadCommand()));
    }

    private static ArgumentBuilder<CommandSource, ?> registerDumpCommand() {
        return Commands.literal("dump").executes(context -> {
            if (FMLLoader.getDist().isClient()) {
                runDumpCommand();
            }
            return 1;
        });
    }

    private static ArgumentBuilder<CommandSource, ?> registerPetReloadCommand() {
        return Commands.literal("petreload").executes(context -> {
            if (FMLLoader.getDist().isClient()) {
                runPatreonLoadCommand();
            }
            return 1;
        });
    }

    @OnlyIn(Dist.CLIENT)
    private static void runPatreonLoadCommand() {
        PetInfo.clearInfo();
        PetLoader.loadAPI();
    }

    @OnlyIn(Dist.CLIENT)
    private static void runDumpCommand() {
        DiscordMarkdownBuilder builder = new DiscordMarkdownBuilder();
        long maxMemory = Runtime.getRuntime().maxMemory();
        long totalMemory = Runtime.getRuntime().totalMemory();
        long freeMemory = Runtime.getRuntime().freeMemory();
        long currentMemory = totalMemory - freeMemory;
        int modCount = ModList.get().size();

        MainWindow window = Minecraft.getInstance().getWindow();

        builder.category("System Stats");
            builder.append("OS", System.getProperty("os.name"));
            builder.append("CPU", PlatformDescriptors.getCpuInfo());
            builder.append("Display", String.format("%dx%d", window.getWidth(), window.getHeight()));
        long memorySize = getMemorySize();
        if (memorySize > 0) builder.append("Maximum Memory", (memorySize / 1024L / 1024L) + "MB");
        builder.category("Java Stats");
            builder.append(
                    "Java",
                    String.format("%s %dbit", System.getProperty("java.version"), Minecraft.getInstance().is64Bit() ? 64 : 32)
            );
            builder.append(
                    "Memory",
                    String.format(
                            "% 2d%% %03d/%03dMB",
                            currentMemory * 100L / maxMemory,
                            currentMemory / 1024L / 1024L,
                            maxMemory / 1024L / 1024L
                    )
            );
            builder.append(
                    "Memory Allocated",
                    String.format("% 2d%% %03dMB", totalMemory * 100L / maxMemory, totalMemory / 1024L / 1024L)
            );
        builder.category("Game Stats");
            builder.append("FPS", String.valueOf(Minecraft.getInstance().fpsString));
            builder.append("Loaded Mods", String.valueOf(modCount));
            builder.append("Forge", ForgeVersion.getVersion());
        builder.category("Mod Stats");
            ResourcePackLoader.getResourcePackFor(ResourcefulBees.MOD_ID)
                .map(mod -> mod.getModFile().getModInfos().get(0))
                .ifPresent(version -> builder.append("Mod Version", version.getVersion().getQualifier()));
            builder.append("Loaded Bees", BeeRegistry.getRegistry().getBees().size());
            builder.append("Loaded Honey", BeeRegistry.getRegistry().getHoneyBottles().size());
            builder.append("Loaded Traits", TraitRegistry.getRegistry().getTraits().size());
            builder.append("Game Uptime", DurationFormatUtils.formatDurationHMS(Instant.now().minus(ResourcefulBees.STARTED_TIME, ChronoUnit.MILLIS).toEpochMilli()));
        Minecraft.getInstance().keyboardHandler.setClipboard(builder.toString());
        PlayerEntity player = Minecraft.getInstance().player;
        if (player != null) {
            Minecraft.getInstance().player.displayClientMessage(new StringTextComponent("Copied game stats to clipboard."), false);
        }
    }

    private static long getMemorySize() {
        try {
            return ((com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean()).getTotalPhysicalMemorySize();
        } catch (Exception e) {
            try {
                return ((com.sun.management.UnixOperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean()).getTotalPhysicalMemorySize();
            } catch (Exception ignored) {/*IGNORE*/}
        }
        return -1;
    }
}
