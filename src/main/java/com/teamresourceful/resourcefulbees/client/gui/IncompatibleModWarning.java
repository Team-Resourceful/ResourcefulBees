package com.teamresourceful.resourcefulbees.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.teamresourceful.resourcefulbees.common.config.CommonConfig;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ServerListScreen;
import net.minecraft.client.gui.screen.WorldSelectionScreen;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.loading.FMLLoader;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

public class IncompatibleModWarning {

    private static boolean hasBeenShownOnce;
    private static boolean isPerformantLoaded;

    private IncompatibleModWarning() {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    public static void init() {
        isPerformantLoaded = FMLLoader.getLoadingModList().getModFileById("performant") != null;
        MinecraftForge.EVENT_BUS.addListener(IncompatibleModWarning::guiOpened);
    }

    private static void guiOpened(GuiOpenEvent event) {
        Screen curr = event.getGui();
        if (isPerformantLoaded && !hasBeenShownOnce && (curr instanceof WorldSelectionScreen || curr instanceof ServerListScreen) && Boolean.FALSE.equals(CommonConfig.BYPASS_PERFORMANT_CHECK.get())) {
            event.setGui(new ModWarningScreen(curr));
            hasBeenShownOnce = true;
        }
    }

    private static class ModWarningScreen extends Screen {
        private final Screen parent;
        private int ticksElapsed = 0;

        protected ModWarningScreen(Screen parent) {
            super(new StringTextComponent(""));
            this.parent = parent;
        }

        @Override
        public void init() {
            super.init();
        }

        @Override
        public void tick() {
            super.tick();
            ticksElapsed++;
        }

        @Override
        public void render(@NotNull MatrixStack stack, int mx, int my, float partialTicks) {
            super.render(stack, mx, my, partialTicks);

            renderDirtBackground(0);
            int middle = width / 2;
            drawCenteredString(stack, this.font, "Performant is incompatible with Resourceful Bees", middle, 62, 0xFFFFFF);
            drawCenteredString(stack, this.font, "This is a known issue with performant and it breaking other mods, the author does not care", middle, 74, 0xFFFFFF);
            drawCenteredString(stack, this.font, "GitHub issue on the matter: https://github.com/someaddons/performant_issues/issues/70", middle, 86, 0xFFFFFF);
            drawCenteredString(stack, this.font, "To bypass this check set \"bypassPerformantCheck: true\" in your Resourceful Bees common.toml config", middle, 98, 0xFFFFFF);
            drawCenteredString(stack, this.font, "If you bypass this check we will not provide any support for", middle, 110, 0xFFFFFF);
            drawCenteredString(stack, this.font, "any issues related to Resourceful Bees or the mods that use it", middle, 122, 0xFFFFFF);
            drawCenteredString(stack, this.font, "If you believe your issue is unrelated, disable performant and reproduce it", middle, 134, 0xFFFFFF);
            drawCenteredString(stack, this.font, "By choosing to bypass this check you understand that here there be dragons", middle, 146, 0xFFFFFF);
            drawCenteredString(stack, this.font, "Press ESC if you would like to close this screen.", middle, 158, 0xFFFFFF);
            drawCenteredString(stack, this.font, "This will auto close in: " + ((1800 - ticksElapsed) / 20) + "s", middle, 170, 0xFFFFFF);

            if (ticksElapsed > 1800){
                Minecraft.getInstance().setScreen(parent);
            }
        }

        @Override
        public boolean keyPressed(int keycode, int scanCode, int modifiers) {
            if (keycode == GLFW.GLFW_KEY_ESCAPE) {
                Minecraft.getInstance().setScreen(parent);
            }

            return super.keyPressed(keycode, scanCode, modifiers);
        }

    }

}
