package com.github.andrew0030.pandora_core.client.gui.edit_boxes;

import com.github.andrew0030.pandora_core.mixin_interfaces.IPaCoEditBox;
import net.minecraft.Util;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

public class PaCoEditBox extends EditBox implements IPaCoEditBox {
    protected final Font font;
    private boolean isBackgroundHidden;
    private boolean isRimHidden;
    private boolean isLineIndicatorForced;
    private boolean useCharMidpoints;
    private Component rawHint;
    private long lastClickTime;
    private int lastIdx = -1;
    private int clickCount = 0;

    public PaCoEditBox(Font font, int x, int y, int width, int height, Component message) {
        super(font, x, y, width, height, message);
        this.font = font;
    }

    /**
     * Sets whether the backgground (black box behind text) should be hidden.
     *
     * @param isHidden Whether the background is hidden
     */
    public void setBackgroundHidden(boolean isHidden) {
        this.isBackgroundHidden = isHidden;
    }

    /**
     * Sets whether the rim (white outline when the text box is selected) is hidden.
     *
     * @param isHidden Whether the rim is hidden
     */
    public void setRimHidden(boolean isHidden) {
        this.isRimHidden = isHidden;
    }

    /**
     * Sets whether the indicator should always be rendered as a line.
     * <p>By default, if the indicator is at the end of the text (and the max char limit isn't filled),
     * it is rendered as {@code _}, this method allows to always have it render as {@code |}.</p>
     *
     * @param forceLineIndicator Whether to always render the line indicator
     */
    public void setForceLineIndicator(boolean forceLineIndicator) {
        this.isLineIndicatorForced = forceLineIndicator;
    }

    /**
     * Sets whether to use character midpoints for selection.
     * <p>By default, vanilla uses right edge selection, which can feel a bit clunky.</p>
     *
     * @param useCharMidpoints Whether to use the center of a character to determine the cursor position
     */
    public void setMidpointCharSelection(boolean useCharMidpoints) {
        this.useCharMidpoints = useCharMidpoints;
    }

    /**
     * Gets called when the value of this text box changes.
     *
     * @param newText The text inside this text box after it was modified
     */
    public void onTextChanged(String newText) {}

    /**
     * Sets the text that will be displayed as a hint in this text box.
     *
     * @param hint The hint that will be displayed
     */
    @Override
    public void setHint(@NotNull Component hint) {
        this.rawHint = hint;
        this.updateHintString();
    }

    @Override
    public void setBordered(boolean bordered) {
        super.setBordered(bordered);
        this.updateHintString();
    }

    @Override
    public void renderWidget(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        int highlightPos = this.pandoraCore$getHighlightPos();
        int displayPos = this.pandoraCore$getDisplayPos();
        // If the anchor is scrolled off-screen to the left, minecraft's renderer will crash.
        // We temporarily clamp the anchor to the displayPos so minecraft safely draws
        // the highlight starting from the left edge of the visible text box.
        if (highlightPos < displayPos)
            this.pandoraCore$setHighlightPos(displayPos);
        // Delegates rendering to minecraft after we adjusted the highlight position
        super.renderWidget(graphics, mouseX, mouseY, partialTick);
        // Restores the true anchor for the next logic tick
        this.pandoraCore$setHighlightPos(highlightPos);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.canConsumeInput()) {
            boolean hasSelection = this.getCursorPosition() != this.pandoraCore$getHighlightPos();
            // If text is selected and shift isn't held, the left/right arrows collapse the selection to the respective edge
            if (!Screen.hasShiftDown() && hasSelection) {
                if (keyCode == GLFW.GLFW_KEY_RIGHT) {
                    int rightEdge = Math.max(this.getCursorPosition(), this.pandoraCore$getHighlightPos());
                    this.moveCursorTo(rightEdge);
                    // The field "shiftPressed" isn't properly tracked, so we manually set the highlight
                    this.setHighlightPos(rightEdge);
                    return true;
                } else if (keyCode == GLFW.GLFW_KEY_LEFT) {
                    int leftEdge = Math.min(this.getCursorPosition(), this.pandoraCore$getHighlightPos());
                    this.moveCursorTo(leftEdge);
                    // The field "shiftPressed" isn't properly tracked, so we manually set the highlight
                    this.setHighlightPos(leftEdge);
                    return true;
                }
            }

            // Custom word jump logic (ctrl + arrow keys)
            // NOTE: Text collapse is handled above with a higher priority, so if text is selected
            // and word jumping is used, the selection will get collapsed first, this is intentional
            if (Screen.hasControlDown() && keyCode == GLFW.GLFW_KEY_RIGHT) {
                this.moveCursorTo(this.getCustomWordPosition(1));
                return true;
            } else if (Screen.hasControlDown() && keyCode == GLFW.GLFW_KEY_LEFT) {
                this.moveCursorTo(this.getCustomWordPosition(-1));
                return true;
            }
        }
        // For all other key inputs we simply delegate to vanilla
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    // Minecraft edit box seems to have a small bug, where the "shiftPressed" boolean doesn't
    // get updated without pressing keyboard keys. To fix this we use "Screen.hasShiftDown()"
    // instead, the rest of the logic remains unchanged.
    @Override
    public void onClick(double mouseX, double mouseY) {
        // Normal horizontal calculation
        int newPos = this.calculateAbsoluteStringIndex(mouseX);
        // Updates the cursor position and highlight position
        this.setCursorPosition(newPos);
        if (!Screen.hasShiftDown())
            this.setHighlightPos(newPos);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Used to check for multiple consecutive clicks to highlight words
        if (this.isMouseOver(mouseX, mouseY) && button == GLFW.GLFW_MOUSE_BUTTON_1) {
            int displayPos = this.pandoraCore$getDisplayPos();
            int clickedPos = this.calculateAbsoluteStringIndex(mouseX);
            String visibleText = this.font.plainSubstrByWidth(this.getValue().substring(displayPos), this.getInnerWidth());
            int rightEdgeIdx = displayPos + visibleText.length();
            long currentTime = Util.getMillis();

            // Left edge click
            if (clickedPos <= displayPos) {
                // If the text isn't fully displayed yet, we move the displayPos by one to the left
                if (displayPos > 0) this.pandoraCore$setDisplayPos(displayPos - 1);
                // Prevents word selection on edges
                this.clickCount = 0;
            }
            // Right edge click
            else if (clickedPos >= rightEdgeIdx) {
                // If the text isn't fully displayed yet, we move the displayPos by one to the right
                if (rightEdgeIdx < this.getValue().length()) this.pandoraCore$setDisplayPos(displayPos + 1);
                // Prevents word selection on edges
                this.clickCount = 0;
            }

            // Sets the newly determined cursorPos, and handles navigation if needed
            this.setCursorPosition(clickedPos);

            // Checks for clicks within 250ms and on the same character index
            if (clickedPos == this.lastIdx && (currentTime - this.lastClickTime) < 250L) {
                this.clickCount++;
            } else {
                this.clickCount = 1;
            }
            // Updates the cached click values
            this.lastClickTime = currentTime;
            this.lastIdx = clickedPos;
            // Double click logic (selects the word)
            if (this.clickCount == 2) {
                this.selectWord(clickedPos);
                return true;
            }
            // Tripple click logic (selects the entire text)
            else if (this.clickCount >= 3) {
                this.selectAll();
                this.clickCount = 0;
                return true;
            }
        }
        // If there are no "multi-clicks" we simply delegate to vanilla
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (this.isFocused() && button == GLFW.GLFW_MOUSE_BUTTON_1) {
            // Calculates the mouse position relative to the text box
            int relativeX = Mth.floor(mouseX) - this.getX();
            if (this.pandoraCore$isBordered())
                relativeX -= 4;
            // All the needed values to detect the edit box edges
            int displayPos = this.pandoraCore$getDisplayPos();
            int innerWidth = this.getInnerWidth();
            int length = this.getValue().length();
            String visibleText = this.font.plainSubstrByWidth(this.getValue().substring(displayPos), innerWidth);
            int rightEdgeIdx = displayPos + visibleText.length();
            int newPos;

            // Dragging outside the left edge, triggers auto scrolling
            if (relativeX < 0 && displayPos > 0) {
                this.pandoraCore$setDisplayPos(displayPos - 1);
                newPos = displayPos - 1; // Snaps cursor to the new left edge
            }
            // Dragging outside the right edge, triggers auto scrolling
            else if (relativeX > innerWidth && rightEdgeIdx < length) {
                this.pandoraCore$setDisplayPos(displayPos + 1);
                newPos = rightEdgeIdx + 1; // Snaps cursor to the new right edge
            }
            // Dragging above the box, triggers snapping to the start
            else if (mouseY < this.getY()) { newPos = 0; }
            // Dragging below the box, triggers snapping to the end
            else if (mouseY > this.getY() + this.height) { newPos = length; }
            // Normal drag doesn't require any extra logic
            else { newPos = this.calculateAbsoluteStringIndex(mouseX); }

            // Resets the click count if the cursor moved to a new character
            if (newPos != this.getCursorPosition())
                this.clickCount = 0;

            // Sets the newly determined cursorPos, and handles navigation if needed
            this.setCursorPosition(newPos);

            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public void setHighlightPos(int position) {
        int length = this.getValue().length();
        int newHighlightPos = Mth.clamp(position, 0, length);
        // The method no longer updates displayPos, now it only updates the selection anchor
        this.pandoraCore$setHighlightPos(newHighlightPos);
    }

    @Override
    public void setCursorPosition(int pos) {
        super.setCursorPosition(pos);
        this.ensureCursorVisible();
    }

    /**
     * Calculates the target cursor position for {@code Ctrl}+{@code Arrow} word jumping.
     * <p>This is an improved implementation of vanilla's word jumping. It dynamically
     * determines the target cursor position, ensuring it lands at the  {@code start} of the
     * previous word when moving left, or the {@code end} of the next word when moving right.</p>
     *
     * @param direction The direction to jump. A positive value moves right, while a negative value moves left
     * @return The absolute string index of the target cursor position
     */
    private int getCustomWordPosition(int direction) {
        String text = this.getValue();
        int pos = this.getCursorPosition();
        // Moving Right
        if (direction > 0) {
            int i = pos;
            while (i < text.length() && text.charAt(i) != ' ' && !Character.isLetterOrDigit(text.charAt(i))) i++; // Skips punctuation
            while (i < text.length() && text.charAt(i) == ' ') i++;                     // Skips spaces
            while (i < text.length() && Character.isLetterOrDigit(text.charAt(i))) i++; // Skips word characters
            return i;
        }
        // Moving Left
        else if (direction < 0) {
            int i = pos;
            while (i > 0 && text.charAt(i - 1) != ' ' && !Character.isLetterOrDigit(text.charAt(i - 1))) i--; // Skips punctuation
            while (i > 0 && text.charAt(i - 1) == ' ') i--;                     // Skips spaces
            while (i > 0 && Character.isLetterOrDigit(text.charAt(i - 1))) i--; // Skips word characters
            return i;
        }
        // If no direction was specified we return the same position
        return pos;
    }

    /**
     * Ensures the text box is scrolled in a way that keeps the current cursor position visible.
     * <p>This handles all navigation, completely removing the need for {@link #setHighlightPos(int)} to manage {@code displayPos}.</p>
     */
    private void ensureCursorVisible() {
        int length = this.getValue().length();
        int cursorPos = this.getCursorPosition();
        int displayPos = Mth.clamp(this.pandoraCore$getDisplayPos(), 0, length);
        // font shouldn't be null but just in case
        if (this.font != null) {
            int innerWidth = this.getInnerWidth();
            // The cursor is to the right of the visible text
            if (cursorPos > displayPos) {
                String textBetween = this.getValue().substring(displayPos, cursorPos);
                int widthBetween = this.font.width(textBetween);
                if (widthBetween > innerWidth) {
                    String fittingSuffix = this.font.plainSubstrByWidth(this.getValue().substring(0, cursorPos), innerWidth, true);
                    displayPos = Math.min(cursorPos - fittingSuffix.length(), cursorPos);
                }
            }
            // The cursor is to the left of the visible text
            else if (cursorPos < displayPos) {
                displayPos = cursorPos;
            }
            // After we determined the new displayPos we apply it directly
            this.pandoraCore$setDisplayPos(Mth.clamp(displayPos, 0, length));
        }
    }

    /**
     * Calculates the absolute string index based on the mouse X coordinate.
     *
     * @param mouseX The X coordinate of the mouse
     * @return The absolute string index
     */
    private int calculateAbsoluteStringIndex(double mouseX) {
        // Calculates the mouse position relative to the text box
        int relativeX = Mth.floor(mouseX) - this.getX();
        if (this.pandoraCore$isBordered())
            relativeX -= 4;
        int displayPos = this.pandoraCore$getDisplayPos();
        // Gets the text that is currently visible on screen
        String visibleText = this.getValue().substring(displayPos);
        int cursorOffset = 0;
        int cumulativeWidth = 0;
        // Iterates through visible text using code points to properly handle emoji/surrogate pairs
        for (int idx = 0; idx < visibleText.length(); ) {
            int codePoint = visibleText.codePointAt(idx);
            String charStr = String.valueOf(Character.toChars(codePoint));
            int charWidth = this.font.width(charStr);
            // Determines the trigger boundary, center snap is the character midpoint and vanilla is right edge
            float triggerOffset = this.useCharMidpoints ? (charWidth * 0.5F) : (float) charWidth;
            float triggerPoint = cumulativeWidth + triggerOffset;
            // If mouseX is before the trigger point, we place the cursor before this character
            if (relativeX < triggerPoint)
                return displayPos + cursorOffset;
            // Otherwise, we add the character's full width and move on
            cumulativeWidth += charWidth;
            cursorOffset += Character.charCount(codePoint);
            idx += Character.charCount(codePoint);
        }
        // If the mouse hits the edge of the visible characters we place the cursor at the end
        return displayPos + visibleText.length();
    }

    /**
     * Selects the word at the given index.
     * <ul>
     *     <li>If the index is on a word's character, it selects the word's characters</li>
     *     <li>If the index is on a space or punctuation, it selects the non-word characters</li>
     * </ul>
     *
     * @param idx The absolute string index to select the word at
     */
    private void selectWord(int idx) {
        String text = this.getValue();
        // If there is no text to select we return early
        if (text.isEmpty()) {
            this.setCursorPosition(0);
            this.setHighlightPos(0);
            return;
        }
        // Ensures the index stays within bounds
        idx = Mth.clamp(idx, 0, text.length());
        // Checks the characters to the left/right of the cursor
        boolean leftIsWord = idx > 0 && Character.isLetterOrDigit(text.charAt(idx - 1));
        boolean rightIsWord = idx < text.length() && Character.isLetterOrDigit(text.charAt(idx));
        boolean isWordChar = leftIsWord || rightIsWord;
        // Keeps track of the found word indexes
        int start = idx;
        int end = idx;
        // If valid word character(s) have been found on either side we select them
        if (isWordChar) {
            // Expands to the left while there are valid word characters
            while (start > 0 && Character.isLetterOrDigit(text.charAt(start - 1))) start--;
            // Expand to the right while there are valid word characters
            while (end < text.length() && Character.isLetterOrDigit(text.charAt(end))) end++;
        } else {
            // Expands to the left while we see invalid word characters
            while (start > 0 && !Character.isLetterOrDigit(text.charAt(start - 1))) start--;
            // Expands to the right while we see invalid word characters
            while (end < text.length() && !Character.isLetterOrDigit(text.charAt(end))) end++;
        }
        // Sets the cursor and highlight positions based on the found indexes
        this.setCursorPosition(end);
        this.setHighlightPos(start);
    }

    /** Selects all text in the edit box. */
    private void selectAll() {
        this.moveCursorToEnd();
        this.setHighlightPos(0);
    }

    /** Recalculates and applies the hint based on the current border state. */
    private void updateHintString() {
        // If no hint was set yet we return early
        if (this.rawHint == null) return;
        // Truncates the string based on the width of the edit box and if it has a border
        boolean hasBorder = this.pandoraCore$isBordered();
        int maxWidth = this.getWidth() - (hasBorder ? 8 : 0);
        String truncatedText = this.font.plainSubstrByWidth(this.rawHint.getString(), maxWidth);
        // Applies the truncated text using the original setHint method
        super.setHint(Component.literal(truncatedText));
    }

    // #########################################################################################
    // #################### PaCo Internal Stuff (Use methods above instead) ####################
    // #########################################################################################

    /** <strong>NOTE:</strong> Use {@link #setBackgroundHidden(boolean)} instead. */
    @Deprecated(forRemoval = false)
    @ApiStatus.Internal
    @Override
    public boolean pandoraCore$hideBackground() { return this.isBackgroundHidden; }

    /** <strong>NOTE:</strong> Use {@link #setRimHidden(boolean)} instead. */
    @Deprecated(forRemoval = false)
    @ApiStatus.Internal
    @Override
    public boolean pandoraCore$hideRim() { return this.isRimHidden; }

    /** <strong>NOTE:</strong> Use {@link #setForceLineIndicator(boolean)} instead. */
    @Deprecated(forRemoval = false)
    @ApiStatus.Internal
    @Override
    public boolean pandoraCore$forceLineIndicator() { return this.isLineIndicatorForced; }

    /** <strong>NOTE:</strong> Use {@link PaCoEditBox#onTextChanged(String)} instead. */
    @Deprecated(forRemoval = false)
    @ApiStatus.Internal
    @Override
    public void pandoraCore$onValueChange(String newText) {
        // Calls the non-internal method users are supposed to use,
        // if they want to perform actions when the text changes.
        this.onTextChanged(newText);

        // Some QoL code that makes text always "snap" to the right side,
        // when there is more text inside the text box than it can fit.
        int displayPos = this.pandoraCore$getDisplayPos();
        int safeDisplayPos = Math.min(displayPos, newText.length());
        // We only try to shift, if there is actually hidden text on the left
        if (safeDisplayPos > 0) {
            int innerWidth = this.getInnerWidth();
            String visibleText = newText.substring(safeDisplayPos);
            int visibleTextWidth = this.font.width(visibleText);
            // If the visible text doesn't fill the box, we have empty space on the right
            if (visibleTextWidth < innerWidth) {
                String newSuffixText = this.font.plainSubstrByWidth(newText, innerWidth, true);
                int newDisplayPos = newText.length() - newSuffixText.length();
                // Small safety check to avoid shifting the display pos past the cursor
                newDisplayPos = Math.min(newDisplayPos, this.getCursorPosition());
                // Applies the new scroll position if it's smaller (shifting the text to the right)
                if (newDisplayPos < safeDisplayPos)
                    this.pandoraCore$setDisplayPos(newDisplayPos);
            }
        }
    }
}