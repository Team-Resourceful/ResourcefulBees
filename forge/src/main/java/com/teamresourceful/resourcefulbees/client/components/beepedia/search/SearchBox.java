package com.teamresourceful.resourcefulbees.client.components.beepedia.search;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.client.screens.beepedia.BeepediaScreen;
import com.teamresourceful.resourcefulbees.client.utils.ClientUtils;
import com.teamresourceful.resourcefullib.client.utils.RenderUtils;
import com.teamresourceful.resourcefullib.common.utils.types.Vec2i;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.util.Objects;

public class SearchBox extends AbstractWidget {

    private static final ResourceLocation TEXTURE = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/beepedia/search_box.png");

    private final Font font;
    private String value = "";
    private int frame, displayPos, cursorPos, highlightPos;
    private boolean shiftPressed;

    private final BeepediaScreen screen;

    public SearchBox(Font font, int x, int y, BeepediaScreen screen) {
        super(x, y, 90, 12, CommonComponents.EMPTY);
        this.font = font;
        this.screen = screen;
        if (screen.getState().search != null) this.setValue(screen.getState().search);
    }

    public void tick() {
        ++this.frame;
    }

    @Override
    protected @NotNull MutableComponent createNarrationMessage() {
        return Component.translatable("gui.narrate.editBox", this.getMessage(), this.value);
    }

    public void setValue(String text) {
        if (Objects.nonNull(text)) {
            if (text.length() > 32) {
                this.value = text.substring(0, 32);
            } else {
                this.value = text;
            }

            this.moveCursorToEnd();
            this.setHighlightPos(this.cursorPos);
            this.onValueChange(text);
        }
    }

    public String getValue() {
        return this.value;
    }

    public String getHighlighted() {
        int minPos = Math.min(this.cursorPos, this.highlightPos);
        int maxPos = Math.max(this.cursorPos, this.highlightPos);
        return this.value.substring(minPos, maxPos);
    }

    public void insertText(String text) {
        int minPos = Math.min(this.cursorPos, this.highlightPos);
        int maxPos = Math.max(this.cursorPos, this.highlightPos);
        int k = 32 - this.value.length() - (minPos - maxPos);
        String filteredText = SharedConstants.filterText(text);
        int l = filteredText.length();
        if (k < l) {
            filteredText = filteredText.substring(0, k);
            l = k;
        }

        this.value = new StringBuilder(this.value).replace(minPos, maxPos, filteredText).toString();
        this.setCursorPosition(minPos + l);
        this.setHighlightPos(this.cursorPos);
        this.onValueChange(this.value);
    }

    private void onValueChange(String text) {
        this.screen.getState().search = text;
        this.screen.updateSelections();
    }

    private void deleteText(int amount) {
        if (Screen.hasControlDown()) {
            this.deleteWords(amount);
        } else {
            this.deleteChars(amount);
        }
    }

    public void deleteWords(int amount) {
        if (this.value.isEmpty()) return;
        if (this.highlightPos != this.cursorPos) {
            this.insertText("");
        } else {
            this.deleteChars(this.getWordPosition(amount) - this.cursorPos);
        }
    }

    public void deleteChars(int amount) {
        if (this.value.isEmpty()) return;
        if (this.highlightPos != this.cursorPos) {
            this.insertText("");
        } else {
            int amountPos = this.getCursorPos(amount);
            int minPos = Math.min(amountPos, this.cursorPos);
            int maxPos = Math.max(amountPos, this.cursorPos);
            if (minPos != maxPos) {
                this.value = new StringBuilder(this.value).delete(minPos, maxPos).toString();
                this.moveCursorTo(minPos);
            }
        }
    }

    public int getWordPosition(int amount) {
        return this.getWordPosition(amount, this.getCursorPosition());
    }

    private int getWordPosition(int amount, int pos) {
        int newPos = pos;
        boolean backwards = amount < 0;

        for(int i = 0; i < Math.abs(amount); ++i) {
            if (!backwards) {
                int textLength = this.value.length();
                newPos = this.value.indexOf(' ', newPos);
                if (newPos == -1) {
                    newPos = textLength;
                } else {
                    while(newPos < textLength && this.value.charAt(newPos) == ' ') {
                        ++newPos;
                    }
                }
            } else {
                while(newPos > 0 && this.value.charAt(newPos - 1) == ' ') {
                    --newPos;
                }

                while(newPos > 0 && this.value.charAt(newPos - 1) != ' ') {
                    --newPos;
                }
            }
        }

        return newPos;
    }

    private int getCursorPos(int amount) {
        return Util.offsetByCodepoints(this.value, this.cursorPos, amount);
    }

    public void moveCursorTo(int pos) {
        this.setCursorPosition(pos);
        if (!this.shiftPressed) {
            this.setHighlightPos(this.cursorPos);
        }

        this.onValueChange(this.value);
    }

    public void setCursorPosition(int pos) {
        this.cursorPos = Mth.clamp(pos, 0, this.value.length());
    }

    public void moveCursorToStart() {
        this.moveCursorTo(0);
    }

    public void moveCursorToEnd() {
        this.moveCursorTo(this.value.length());
    }

    @Override
    public boolean keyPressed(int key, int scan, int modifiers) {
        if (this.cantConsumeInput()) return false;

        this.shiftPressed = Screen.hasShiftDown();
        if (Screen.isSelectAll(key)) {
            this.moveCursorToEnd();
            this.setHighlightPos(0);
            return true;
        } else if (Screen.isCopy(key)) {
            Minecraft.getInstance().keyboardHandler.setClipboard(this.getHighlighted());
            return true;
        } else if (Screen.isPaste(key)) {
            this.insertText(Minecraft.getInstance().keyboardHandler.getClipboard());
            return true;
        } else if (Screen.isCut(key)) {
            Minecraft.getInstance().keyboardHandler.setClipboard(this.getHighlighted());
            this.insertText("");

            return true;
        } else {
            switch (key) {
                case GLFW.GLFW_KEY_DELETE:
                case GLFW.GLFW_KEY_BACKSPACE:
                    this.shiftPressed = false;
                    this.deleteText(key == GLFW.GLFW_KEY_BACKSPACE ? -1 : 1);
                    this.shiftPressed = Screen.hasShiftDown();
                    return true;
                case GLFW.GLFW_KEY_LEFT:
                case GLFW.GLFW_KEY_RIGHT:
                    int amount = key == GLFW.GLFW_KEY_RIGHT ? 1 : -1;
                    this.moveCursorTo(Screen.hasControlDown() ? this.getWordPosition(amount) : amount);
                    return true;
                case GLFW.GLFW_KEY_HOME:
                    this.moveCursorToStart();
                    return true;
                case GLFW.GLFW_KEY_END:
                    this.moveCursorToEnd();
                    return true;
                case GLFW.GLFW_KEY_INSERT:
                case GLFW.GLFW_KEY_DOWN:
                case GLFW.GLFW_KEY_UP:
                case GLFW.GLFW_KEY_PAGE_UP:
                case GLFW.GLFW_KEY_PAGE_DOWN:
                default:
                    return false;
            }
        }
    }

    public boolean cantConsumeInput() {
        return !this.isVisible() || !this.isFocused();
    }

    @Override
    public boolean charTyped(char character, int modifiers) {
        if (this.cantConsumeInput()) return false;

        if (SharedConstants.isAllowedChatCharacter(character)) {
            this.insertText(Character.toString(character));
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!this.isVisible()) return false;

        this.setFocused(mouseX >= (double) this.x && mouseX < (double) (this.x + this.width) && mouseY >= (double) this.y && mouseY < (double) (this.y + this.height));

        if (this.isFocused() && button == 0) {
            int i = Mth.floor(mouseX) - this.x - 4;

            String s = this.font.plainSubstrByWidth(this.value.substring(this.displayPos), this.getInnerWidth());
            this.moveCursorTo(this.font.plainSubstrByWidth(s, i).length() + this.displayPos);
            return true;
        }
        return false;
    }

    @Override
    public void renderButton(@NotNull PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        if (!this.isVisible()) return;

        ClientUtils.bindTexture(TEXTURE);
        blit(stack, this.x, this.y, 0, 0, this.width, this.height, this.width, this.height);

        int j = this.cursorPos - this.displayPos;
        int k = this.highlightPos - this.displayPos;
        String displayText = this.font.plainSubstrByWidth(this.value.substring(this.displayPos), this.getInnerWidth());
        boolean flag = j >= 0 && j <= displayText.length();
        boolean showFlash = this.isFocused() && this.frame / 6 % 2 == 0 && flag;
        int j1 = this.x + 3;
        if (k > displayText.length()) {
            k = displayText.length();
        }

        if (!displayText.isEmpty()) {
            String s1 = flag ? displayText.substring(0, j) : displayText;
            j1 = this.font.drawShadow(stack, FormattedCharSequence.forward(s1, Style.EMPTY), (float)this.x + 2, (float)this.y + 2, 0xE0E0E0);
        }

        boolean showVerticalBar = this.cursorPos < this.value.length() || this.value.length() >= 32;
        int k1 = j1;
        if (!flag) {
            k1 = j > 0 ? this.x + this.width : this.x;
        } else if (showVerticalBar) {
            k1 = j1 - 1;
            --j1;
        }

        if (!displayText.isEmpty() && flag && j < displayText.length()) {
            this.font.drawShadow(stack, FormattedCharSequence.forward(displayText.substring(j), Style.EMPTY), (float)j1, (float)this.y + 2, 0xE0E0E0);
        }

        if (showFlash) {
            if (showVerticalBar) {
                GuiComponent.fill(stack, k1, this.y - 2, k1 + 1, this.y + 11, -3092272);
            } else {
                this.font.drawShadow(stack, "_", (float)k1, (float)this.y + 1, 0xE0E0E0);
            }
        }

        if (k != j) {
            int l1 = this.x + this.font.width(displayText.substring(0, k));
            this.renderHighlight(stack, k1, this.y + 1, l1, this.y + 11);
        }

    }

    private void renderHighlight(PoseStack stack, int startX, int startY, int endX, int endY) {
        if (startX < endX) {
            int i = startX;
            startX = endX;
            endX = i;
        }

        if (startY < endY) {
            int j = startY;
            startY = endY;
            endY = j;
        }

        if (endX > this.x + this.width) {
            endX = this.x + this.width;
        }

        if (startX > this.x + this.width) {
            startX = this.x + this.width;
        }

        Vec2i coords = RenderUtils.getTranslation(stack);
        startX += coords.x();
        startY += coords.y();
        endX += coords.x();
        endY += coords.y();

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tesselator.getBuilder();
        RenderSystem.setShader(GameRenderer::getPositionShader);
        RenderSystem.setShaderColor(0.0F, 0.0F, 1.0F, 1.0F);
        RenderSystem.disableTexture();
        RenderSystem.enableColorLogicOp();
        RenderSystem.logicOp(GlStateManager.LogicOp.OR_REVERSE);
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);
        bufferbuilder.vertex(startX, endY, 0.0D).endVertex();
        bufferbuilder.vertex(endX, endY, 0.0D).endVertex();
        bufferbuilder.vertex(endX, startY, 0.0D).endVertex();
        bufferbuilder.vertex(startX, startY, 0.0D).endVertex();
        tesselator.end();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableColorLogicOp();
        RenderSystem.enableTexture();
    }

    public int getCursorPosition() {
        return this.cursorPos;
    }

    @Override
    public boolean changeFocus(boolean p_94172_) {
        return this.isVisible() && super.changeFocus(p_94172_);
    }

    @Override
    public boolean isMouseOver(double p_94157_, double p_94158_) {
        return this.isVisible() && p_94157_ >= (double)this.x && p_94157_ < (double)(this.x + this.width) && p_94158_ >= (double)this.y && p_94158_ < (double)(this.y + this.height);
    }

    @Override
    protected void onFocusedChanged(boolean focused) {
        if (focused) {
            this.frame = 0;
        }
    }

    public int getInnerWidth() {
        return this.width - 2;
    }

    public void setHighlightPos(int pos) {
        int textLength = this.value.length();
        this.highlightPos = Mth.clamp(pos, 0, textLength);
        if (this.font != null) {
            this.displayPos = Math.min(this.displayPos, textLength);

            String displayText = this.font.plainSubstrByWidth(this.value.substring(this.displayPos), this.getInnerWidth());
            int k = displayText.length() + this.displayPos;
            if (this.highlightPos == this.displayPos) {
                this.displayPos -= this.font.plainSubstrByWidth(this.value, this.getInnerWidth(), true).length();
            }

            if (this.highlightPos > k) {
                this.displayPos += this.highlightPos - k;
            } else if (this.highlightPos <= this.displayPos) {
                this.displayPos -= this.displayPos - this.highlightPos;
            }

            this.displayPos = Mth.clamp(this.displayPos, 0, textLength);
        }

    }

    public boolean isVisible() {
        return this.screen.getState().search != null && this.visible;
    }

    public void updateNarration(NarrationElementOutput p_169009_) {
        p_169009_.add(NarratedElementType.TITLE, Component.translatable("narration.edit_box", this.getValue()));
    }
}
