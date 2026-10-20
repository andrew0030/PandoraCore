package com.github.andrew0030.pandora_core.client.gui.screen.paco_config.entry.entries;

import com.github.andrew0030.pandora_core.client.gui.screen.paco_config.PaCoConfigScreen;
import com.github.andrew0030.pandora_core.client.gui.screen.paco_config.tree.ConfigTreeNode;
import com.github.andrew0030.pandora_core.client.utils.gui.PaCoGuiUtils;
import com.github.andrew0030.pandora_core.config.manager.IConfigValueHolder;
import com.github.andrew0030.pandora_core.utils.color.PaCoColor;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.CommonInputs;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class EnumEntry extends BaseConfigEntry<Enum<?>> {

    public EnumEntry(PaCoConfigScreen screen, ConfigTreeNode node, int y, int height, boolean hasScrollbar) {
        super(screen, node, y, height, hasScrollbar);
        // Creates the interactable widget
        // TODO maybe improve what kind of data is passed to the widgets? Something to look into after more of the types are implemented!
        EnumEntry.Button widget = new EnumEntry.Button(this, Component.literal("TODO")); //TODO fix narration
        // Sets the value to the current value from the config
        widget.setValue(this.getValue());
        // Lastly we add the widget to the list
        this.widgets.add(widget);
    }

    private static class Button extends AbstractWidget {
        private final List<Enum<?>> acceptedValues;
        private final BaseConfigEntry<Enum<?>> entry;
        private Enum<?> value;

        public Button(BaseConfigEntry<Enum<?>> entry, Component message) {
            super(entry.getX(), entry.getY(), entry.getWidth(), entry.getHeight(), message);
            this.entry = entry;
            this.value = entry.getValue();
            // NOTE: needs to be called after the value field was initialized
            this.acceptedValues = this.resolveAcceptedValues();
        }

        /**
         * Resolves the accepted enum values.
         * @return If the config restricts valid values, only those are returned. Otherwise, all enum constants are used.
         */
        @SuppressWarnings("unchecked")
        private List<Enum<?>> resolveAcceptedValues() {
            if (this.value == null || !this.entry.getDataHolder().hasValue()) return List.of();
            Enum<?>[] enumConstants = this.value.getDeclaringClass().getEnumConstants();
            List<String> validNames = ((IConfigValueHolder<Enum<?>>) this.entry.getDataHolder()).getValidValues();
            // If no restrictions are defined all constants are returned
            if (validNames == null || validNames.isEmpty()) return Arrays.asList(enumConstants);
            // Filters to only return the restricted values
            return Arrays.stream(enumConstants).filter(e -> validNames.contains(e.name())).toList();
        }

        // NOTE: The code in this block should be implemented by all widgets used for config entries, the methods
        //       ensure that the widgets move along the config entries, and aren't clickable when out of bounds!
        // #########################################################################################################
        @Override
        public int getY() {
            return super.getY() + this.entry.getScrollOffset();
        }
        @Override
        public boolean isHovered() {
            return this.entry.isHovered() && super.isHovered();
        }
        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            return this.entry.screen.isMouseInEntriesBounds(mouseX, mouseY) && super.mouseClicked(mouseX, mouseY, button);
        }
        // #########################################################################################################

        @Override
        protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
            int posX = this.getX() + this.width - 40;
            int posY = this.getY() + 4;

            // TODO replace these placeholder "textures" with an actual texture
            Minecraft minecraft = Minecraft.getInstance();
            graphics.setColor(1.0F, 1.0F, 1.0F, this.alpha);
            RenderSystem.enableBlend();
            RenderSystem.enableDepthTest();
            graphics.blitNineSliced(WIDGETS_LOCATION, this.getX() + getWidth() - 80, this.getY(), 80, this.getHeight(), 20, 4, 200, 20, 0, this.getTextureY());
            graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);

            PaCoGuiUtils.drawCenteredString(graphics, minecraft.font, this.value.name(), posX, posY, PaCoColor.WHITE, true);
        }

        @Override
        protected void updateWidgetNarration(@NotNull NarrationElementOutput output) {
            this.defaultButtonNarrationText(output);
        }

        @Override
        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            if (this.active && this.visible && CommonInputs.selected(keyCode)) {
                this.playDownSound(Minecraft.getInstance().getSoundManager());
                this.onPress();
                return true;
            }
            return false;
        }

        @Override
        public void onClick(double mouseX, double mouseY) {
            this.onPress();
        }

        private void onPress() {
            if (this.acceptedValues.isEmpty()) return;
            boolean forward = true; // TODO maybe add backwards cycling through different buttons or right/shift click?
            int step = forward ? 1 : -1;
            int currentIndex = this.acceptedValues.indexOf(this.value);
            int nextIndex = Math.floorMod(currentIndex + step, this.acceptedValues.size());
            Enum<?> nextValue = this.acceptedValues.get(nextIndex);
            this.setValue(nextValue);       // Cycles the enum and updates the button message
            this.entry.setValue(nextValue); // Lets the backend handle the config logic
        }

        private void setValue(Enum<?> value) {
            this.value = value;
            this.setMessage(Component.literal(value != null ? value.name() : "null"));
        }

        private int getTextureY() {
            int i = 1;
            if (!this.active) {
                i = 0;
            } else if (this.isHoveredOrFocused()) {
                i = 2;
            }

            return 46 + i * 20;
        }
    }
}