package com.teamresourceful.resourcefulbees.client.gui.widget;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;

@OnlyIn(Dist.CLIENT)
public class TooltipTextFieldWidget extends TooltipWidget{

    private final Font font;
    private String value = "";
    private int maxLength = 32;
    private int frame;
    private boolean bordered = true;
    private boolean canLoseFocus = true;
    private boolean isEditable = true;
    private boolean shiftPressed;
    private int displayPos;
    private int cursorPos;
    private int highlightPos;
    private int textColor = 14737632;
    private int textColorUneditable = 7368816;
    private String suggestion;
    private Consumer<String> responder;
    private Predicate<String> filter = Objects::nonNull;
    private BiFunction<String, Integer, FormattedCharSequence> formatter = (text, pos) -> FormattedCharSequence.forward(text, Style.EMPTY);

    public TooltipTextFieldWidget(Font font, int x, int y, int width, int height, Component message) {
        this(font, x, y, width, height, null, message);
    }

    public TooltipTextFieldWidget(Font font, int x, int y, int width, int height, @Nullable EditBox box, Component message) {
        super(x, y, width, height, message);
        this.font = font;
        if (box != null) {
            this.setValue(box.getValue());
        }

    }

    public void setResponder(Consumer<String> responder) {
        this.responder = responder;
    }

    public void setFormatter(BiFunction<String, Integer, FormattedCharSequence> formatter) {
        this.formatter = formatter;
    }

    public void tick() {
        ++this.frame;
    }

    @NotNull
    @Override
    protected MutableComponent createNarrationMessage() {
        return new TranslatableComponent("gui.narrate.editBox", this.getMessage(), this.value);
    }

    public void setValue(String text) {
        if (this.filter.test(text)) {
            this.value = text.length() > this.maxLength ? text.substring(0, this.maxLength) : text;

            this.moveCursorToEnd();
            this.setHighlightPos(this.cursorPos);
            this.onValueChange(text);
        }
    }

    public String getValue() {
        return this.value;
    }

    public String getHighlighted() {
        int i = Math.min(this.cursorPos, this.highlightPos);
        int j = Math.max(this.cursorPos, this.highlightPos);
        return this.value.substring(i, j);
    }

    public void setFilter(Predicate<String> filter) {
        this.filter = filter;
    }

    public void insertText(String text) {
        int i = Math.min(this.cursorPos, this.highlightPos);
        int j = Math.max(this.cursorPos, this.highlightPos);
        int k = this.maxLength - this.value.length() - (i - j);
        String s = SharedConstants.filterText(text);
        int l = s.length();
        if (k < l) {
            s = s.substring(0, k);
            l = k;
        }

        String s1 = (new StringBuilder(this.value)).replace(i, j, s).toString();
        if (this.filter.test(s1)) {
            this.value = s1;
            this.setCursorPosition(i + l);
            this.setHighlightPos(this.cursorPos);
            this.onValueChange(this.value);
        }
    }

    private void onValueChange(String text) {
        if (this.responder != null) {
            this.responder.accept(text);
        }
    }

    private void deleteText(int pos) {
        if (Screen.hasControlDown()) {
            this.deleteWords(pos);
        } else {
            this.deleteChars(pos);
        }

    }

    public void deleteWords(int pos) {
        if (!this.value.isEmpty()) {
            if (this.highlightPos != this.cursorPos) {
                this.insertText("");
            } else {
                this.deleteChars(this.getWordPosition(pos) - this.cursorPos);
            }
        }
    }

    public void deleteChars(int pos) {
        if (!this.value.isEmpty()) {
            if (this.highlightPos != this.cursorPos) {
                this.insertText("");
            } else {
                int i = this.getCursorPos(pos);
                int j = Math.min(i, this.cursorPos);
                int k = Math.max(i, this.cursorPos);
                if (j != k) {
                    String s = (new StringBuilder(this.value)).delete(j, k).toString();
                    if (this.filter.test(s)) {
                        this.value = s;
                        this.moveCursorTo(j);
                    }
                }
            }
        }
    }

    public int getWordPosition(int pos) {
        return this.getWordPosition(pos, this.getCursorPosition());
    }

    private int getWordPosition(int pos, int cursorPos) {
        int i = cursorPos;
        boolean flag = pos < 0;
        int j = Math.abs(pos);

        for(int k = 0; k < j; ++k) {
            if (!flag) {
                int l = this.value.length();
                i = this.value.indexOf(32, i);
                if (i == -1) {
                    i = l;
                } else {
                    while(i < l && this.value.charAt(i) == ' ') {
                        ++i;
                    }
                }
            } else {
                while(i > 0 && this.value.charAt(i - 1) == ' ') {
                    --i;
                }

                while(i > 0 && this.value.charAt(i - 1) != ' ') {
                    --i;
                }
            }
        }

        return i;
    }

    public void moveCursor(int pos) {
        this.moveCursorTo(this.getCursorPos(pos));
    }

    private int getCursorPos(int pos) {
        return Util.offsetByCodepoints(this.value, this.cursorPos, pos);
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
        if (this.canConsumeInput()) {
            this.shiftPressed = Screen.hasShiftDown();
            if (Screen.isSelectAll(key)) {
                this.moveCursorToEnd();
                this.setHighlightPos(0);
                return true;
            } else if (runCutPasteCopyOperations(key)) {
                return true;
            } else {
                switch(key) {
                    case 261:
                    case 259:
                        if (this.isEditable) {
                            this.shiftPressed = false;
                            this.deleteText(key == 261 ? 1 : -1);
                            this.shiftPressed = Screen.hasShiftDown();
                        }

                        return true;
                    case 263:
                    case 262:
                        if (Screen.hasControlDown()) {
                            this.moveCursorTo(this.getWordPosition(key == 262 ? 1 : -1));
                        } else {
                            this.moveCursor(1);
                        }

                        return true;
                    case 268:
                        this.moveCursorToStart();
                        return true;
                    case 269:
                        this.moveCursorToEnd();
                        return true;
                    case 260:
                    case 264:
                    case 265:
                    case 266:
                    case 267:
                    default:
                        return false;
                }
            }
        }
        return false;
    }

    private boolean runCutPasteCopyOperations(int key) {
        if (Screen.isCopy(key) || Screen.isCut(key)) {
            Minecraft.getInstance().keyboardHandler.setClipboard(this.getHighlighted());
            if (this.isEditable && Screen.isCut(key)) this.insertText("");
            return true;
        }
        if (Screen.isPaste(key)) {
            if (this.isEditable)
                this.insertText(Minecraft.getInstance().keyboardHandler.getClipboard());
            return true;
        }
        return false;
    }

    public boolean canConsumeInput() {
        return this.isVisible() && this.isFocused() && this.isEditable();
    }

    @Override
    public boolean charTyped(char point, int modifiers) {
        if (this.canConsumeInput() && SharedConstants.isAllowedChatCharacter(point)) {
            if (this.isEditable) this.insertText(Character.toString(point));
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!this.isVisible()) {
            return false;
        } else {
            boolean flag = mouseX >= this.x && mouseX < (this.x + this.width) && mouseY >= this.y && mouseY < (this.y + this.height);
            if (this.canLoseFocus) {
                this.setFocus(flag);
            }

            if (this.isFocused() && flag && button == 0) {
                int i = Mth.floor(mouseX) - this.x;
                if (this.bordered) {
                    i -= 4;
                }

                String s = this.font.plainSubstrByWidth(this.value.substring(this.displayPos), this.getInnerWidth());
                this.moveCursorTo(this.font.plainSubstrByWidth(s, i).length() + this.displayPos);
                return true;
            } else {
                return false;
            }
        }
    }

    public void setFocus(boolean focus) {
        super.setFocused(focus);
    }

    @Override
    public void renderButton(@NotNull PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        if (this.isVisible()) {
            if (this.isBordered()) {
                int i = this.isFocused() ? -1 : -6250336;
                fill(stack, this.x - 1, this.y - 1, this.x + this.width + 1, this.y + this.height + 1, i);
                fill(stack, this.x, this.y, this.x + this.width, this.y + this.height, -16777216);
            }

            int i2 = this.isEditable ? this.textColor : this.textColorUneditable;
            int j = this.cursorPos - this.displayPos;
            int k = this.highlightPos - this.displayPos;
            String s = this.font.plainSubstrByWidth(this.value.substring(this.displayPos), this.getInnerWidth());
            boolean flag = j >= 0 && j <= s.length();
            boolean flag1 = this.isFocused() && this.frame / 6 % 2 == 0 && flag;
            int l = this.bordered ? this.x + 4 : this.x;
            int i1 = this.bordered ? this.y + (this.height - 8) / 2 : this.y;
            int j1 = l;
            if (k > s.length()) {
                k = s.length();
            }

            if (!s.isEmpty()) {
                String s1 = flag ? s.substring(0, j) : s;
                j1 = this.font.drawShadow(stack, this.formatter.apply(s1, this.displayPos), l, i1, i2);
            }

            boolean flag2 = this.cursorPos < this.value.length() || this.value.length() >= this.getMaxLength();
            int k1 = j1;
            if (!flag) {
                k1 = j > 0 ? l + this.width : l;
            } else if (flag2) {
                k1 = j1 - 1;
                --j1;
            }

            if (!s.isEmpty() && flag && j < s.length()) {
                this.font.drawShadow(stack, this.formatter.apply(s.substring(j), this.cursorPos), j1, i1, i2);
            }

            if (!flag2 && this.suggestion != null) {
                this.font.drawShadow(stack, this.suggestion, (k1 - 1), i1, -8355712);
            }

            if (flag1) {
                if (flag2) {
                    GuiComponent.fill(stack, k1, i1 - 1, k1 + 1, i1 + 1 + 9, -3092272);
                } else {
                    this.font.drawShadow(stack, "_", k1, i1, i2);
                }
            }

            if (k != j) {
                int l1 = l + this.font.width(s.substring(0, k));
                this.renderHighlight(k1, i1 - 1, l1 - 1, i1 + 1 + 9);
            }

        }
    }

    private void renderHighlight(int x1, int y1, int x2, int y2) {
        if (x1 < x2) {
            int i = x1;
            x1 = x2;
            x2 = i;
        }

        if (y1 < y2) {
            int j = y1;
            y1 = y2;
            y2 = j;
        }

        if (x2 > this.x + this.width) {
            x2 = this.x + this.width;
        }

        if (x1 > this.x + this.width) {
            x1 = this.x + this.width;
        }

        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuilder();
        RenderSystem.setShaderColor(0.0F, 0.0F, 255.0F, 255.0F);
        RenderSystem.disableTexture();
        RenderSystem.enableColorLogicOp();
        RenderSystem.logicOp(GlStateManager.LogicOp.OR_REVERSE);
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);
        bufferbuilder.vertex(x1, y2, 0.0D).endVertex();
        bufferbuilder.vertex(x2, y2, 0.0D).endVertex();
        bufferbuilder.vertex(x2, y1, 0.0D).endVertex();
        bufferbuilder.vertex(x1, y1, 0.0D).endVertex();
        tessellator.end();
        RenderSystem.disableColorLogicOp();
        RenderSystem.enableTexture();
    }

    public void setMaxLength(int length) {
        this.maxLength = length;
        if (this.value.length() > length) {
            this.value = this.value.substring(0, length);
            this.onValueChange(this.value);
        }

    }

    private int getMaxLength() {
        return this.maxLength;
    }

    public int getCursorPosition() {
        return this.cursorPos;
    }

    private boolean isBordered() {
        return this.bordered;
    }

    public void setBordered(boolean bordered) {
        this.bordered = bordered;
    }

    public void setTextColor(int color) {
        this.textColor = color;
    }

    public void setTextColorUneditable(int color) {
        this.textColorUneditable = color;
    }

    @Override
    public boolean changeFocus(boolean focus) {
        return this.visible && this.isEditable && super.changeFocus(focus);
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return this.visible && mouseX >= this.x && mouseX < (this.x + this.width) && mouseY >= this.y && mouseY < (this.y + this.height);
    }

    @Override
    protected void onFocusedChanged(boolean focus) {
        if (focus) {
            this.frame = 0;
        }
    }

    private boolean isEditable() {
        return this.isEditable;
    }

    public void setEditable(boolean editable) {
        this.isEditable = editable;
    }

    public int getInnerWidth() {
        return this.isBordered() ? this.width - 8 : this.width;
    }

    public void setHighlightPos(int pos) {
        int i = this.value.length();
        this.highlightPos = Mth.clamp(pos, 0, i);
        if (this.font != null) {
            if (this.displayPos > i) {
                this.displayPos = i;
            }

            int j = this.getInnerWidth();
            String s = this.font.plainSubstrByWidth(this.value.substring(this.displayPos), j);
            int k = s.length() + this.displayPos;
            if (this.highlightPos == this.displayPos) {
                this.displayPos -= this.font.plainSubstrByWidth(this.value, j, true).length();
            }

            if (this.highlightPos > k) {
                this.displayPos += this.highlightPos - k;
            } else if (this.highlightPos <= this.displayPos) {
                this.displayPos -= this.displayPos - this.highlightPos;
            }

            this.displayPos = Mth.clamp(this.displayPos, 0, i);
        }

    }

    public void setCanLoseFocus(boolean canLoseFocus) {
        this.canLoseFocus = canLoseFocus;
    }

    public boolean isVisible() {
        return this.visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public void setSuggestion(@Nullable String suggestion) {
        this.suggestion = suggestion;
    }

    public int getScreenX(int start) {
        return start > this.value.length() ? this.x : this.x + this.font.width(this.value.substring(0, start));
    }

    public void setX(int x) {
        this.x = x;
    }

    @Override
    public void updateNarration(@NotNull NarrationElementOutput pNarrationElementOutput) {
        pNarrationElementOutput.add(NarratedElementType.TITLE, new TranslatableComponent("narration.edit_box", this.getValue()));
    }
}
