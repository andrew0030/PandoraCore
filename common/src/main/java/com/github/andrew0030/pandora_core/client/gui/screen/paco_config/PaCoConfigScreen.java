package com.github.andrew0030.pandora_core.client.gui.screen.paco_config;

import com.github.andrew0030.pandora_core.PandoraCore;
import com.github.andrew0030.pandora_core.client.gui.screen.paco_config.entry.entries.BaseConfigEntry;
import com.github.andrew0030.pandora_core.client.gui.screen.paco_config.tree.ConfigTreeBuilder;
import com.github.andrew0030.pandora_core.client.gui.screen.paco_config.tree.ConfigTreeNode;
import com.github.andrew0030.pandora_core.client.registry.PaCoPostShaders;
import com.github.andrew0030.pandora_core.config.manager.ConfigDataHolder;
import com.github.andrew0030.pandora_core.config.manager.PaCoConfigManager;
import com.github.andrew0030.pandora_core.mixin_interfaces.IPaCoModifyTitleScreen;
import com.github.andrew0030.pandora_core.utils.color.PaCoColor;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.github.andrew0030.pandora_core.client.registry.PaCoPostShaders.BlurVariables.*;

public class PaCoConfigScreen extends Screen {
    // Some generic UI stuff
    public static final ResourceLocation TEXTURE = new ResourceLocation(PandoraCore.MOD_ID, "textures/gui/paco_screen.png");
    public static final int PADDING_TWO = 2;
    public static final int PADDING_FOUR = 4;
    // Config management stuff
    private final List<BaseConfigEntry> configElements = new ArrayList<>();
    private final PaCoConfigManager manager;
    private final ConfigTreeNode currentNode;

    public final TitleScreen titleScreen;
    private final Screen previousScreen;

    public int menuHeight;
    public int menuHeightStart;
    public int menuHeightStop;
    public int menuWidthStart;
    public int menuWidth;
    // Post-processing shader parameters
    private final Map<String, Object> parameters;



    public PaCoConfigScreen(PaCoConfigManager manager, ConfigTreeNode currentNode, @Nullable TitleScreen titleScreen, @Nullable Screen previousScreen) {
        super(Component.empty()); // TODO: Add a proper config screen title (maybe the node name?)

        this.manager = manager;
        this.currentNode = currentNode;
        this.titleScreen = titleScreen;
        this.previousScreen = previousScreen;
        this.parameters = new HashMap<>();
        // If there is a title screen it flags it to cancel element rendering (we only want the background)
        if (titleScreen != null)
            ((IPaCoModifyTitleScreen) titleScreen).pandoraCore$hideElements(true);
    }

    public PaCoConfigScreen(PaCoConfigManager manager, @Nullable TitleScreen titleScreen, @Nullable Screen previousScreen) {
        this(manager, ConfigTreeBuilder.buildTree(manager.getAnnotationHandler().getConfigDataHolders()), titleScreen, previousScreen);
    }

    /**
     * Used to initialize the fields in this class.<br/>
     * This is mainly a method because some of the fields need to be refreshed, and calling this method does that.
     */
    private void fieldInit() {
        this.menuHeight = this.height - 20;
        this.menuHeightStart = (this.height - this.menuHeight) / 2;
        this.menuHeightStop = this.menuHeightStart + this.menuHeight - 20;
        this.menuWidth = Math.min(this.width - PADDING_TWO * 2, Math.round(this.menuHeight * 2.4F));
        this.menuWidthStart = (this.width - this.menuWidth) / 2;
    }

    @Override
    protected void init() {
        /* Compatibility */
        if (this.titleScreen != null) {
            // Technically this isn't needed when the title screen renders a panorama, however when it has
            // a static image, due to mods like "PackMenu" we need this to update the dimensions on resize
            this.titleScreen.width = this.width;
            this.titleScreen.height = this.height;
        }

        /* Field Init */
        this.fieldInit();
        this.populateEntries();

        /* Adding Widgets */

    }

    @Override
    public void tick() {
        this.configElements.forEach(BaseConfigEntry::tick);
        // Technically this isn't needed when the title screen renders a panorama, however when it has
        // animated content, due to mods like "PackMenu" we need this to ensure the content stays animated
        if (this.titleScreen != null) {
            // We set the current screen to the title screen just before rendering it
            // in order to keep the game state as close as possible to what is expected
            boolean isMinecraftNotNull = this.minecraft != null;
            if (isMinecraftNotNull) this.minecraft.screen = this.titleScreen;
            this.titleScreen.tick();
            if (isMinecraftNotNull) this.minecraft.screen = this;
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // Renders the title screen panorama/background
        if (this.titleScreen != null) {
            // We set the current screen to the title screen just before rendering it
            // in order to keep the game state as close as possible to what is expected
            boolean isMinecraftNotNull = this.minecraft != null;
            if (isMinecraftNotNull) this.minecraft.screen = this.titleScreen;
            this.titleScreen.render(graphics, mouseX, mouseY, partialTick);
            if (isMinecraftNotNull) this.minecraft.screen = this;
        }

        // Background Blur and Gradient
        RenderSystem.disableDepthTest(); // Needed so it works if chat is rendering.
        this.renderBlurredBackground(partialTick);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        graphics.fillGradient(0, 0, this.width, this.height, PaCoColor.color(83, 16, 16, 16), PaCoColor.color(67, 16, 16, 16));
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        // Panel Background
        RenderSystem.enableBlend();
        graphics.blitRepeating(TEXTURE, this.menuWidthStart + PADDING_TWO, this.menuHeightStart, this.menuWidth - (PADDING_TWO * 2), this.menuHeight - 20, 0, 122, 48, 48);

        // Top Bar
        graphics.blitNineSliced(TEXTURE, this.menuWidthStart, this.menuHeightStart - 4, this.menuWidth, 4, 1, 18, 18, 0, 36);
        // Bottom Bar
        graphics.blitNineSliced(TEXTURE, this.menuWidthStart, this.menuHeightStop, this.menuWidth, 4, 1, 18, 18, 0, 36);

        this.configElements.forEach(entry -> entry.render(graphics, mouseX, mouseY, partialTick));
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void onClose() {
        // Handles returning to previous Screen if needed
        if (this.previousScreen != null) {
            if (this.titleScreen != null)
                ((IPaCoModifyTitleScreen) this.titleScreen).pandoraCore$hideElements(false);
            Minecraft.getInstance().setScreen(this.previousScreen);
        } else if (this.titleScreen != null) {
            ((IPaCoModifyTitleScreen) this.titleScreen).pandoraCore$hideElements(false);
            Minecraft.getInstance().setScreen(this.titleScreen);
        } else {
            super.onClose();
        }
    }

    private void renderBlurredBackground(float partialTick) {
        Minecraft minecraft = Minecraft.getInstance();

        // TODO: add config to adjust blurriness and fade in time
        // Map Approach
        this.parameters.put("radius", 5.0F);
        // Uniform Holder Approach
        PASS0_MUL.get().set(1.0F);
        PASS1_MUL.get().set(0.5f);
        PASS2_MUL.get().set(0.25f);

        PaCoPostShaders.PACO_BLUR.processPostChain(partialTick, this.parameters);
        minecraft.getMainRenderTarget().bindWrite(false);
    }

    /**
     * Loops over all {@link ConfigTreeNode} instances, that should
     * be displayed in the current menu, and adds them to the screen
     */
    private void populateEntries() {
        this.configElements.clear();

        int currentY = this.menuHeightStart + PADDING_TWO;
        int entryX = this.menuWidthStart + PADDING_FOUR;
        int entryWidth = this.menuWidth - (PADDING_FOUR * 2);
        int entryHeight = 16;
        int spacing = 2;

        for (ConfigTreeNode child : this.currentNode.getChildren()) {

            // TODO make sure this cant be null
            ConfigDataHolder holder = child.getDataHolder();
            BaseConfigEntry element = holder.getConfigEntryFactory().create(this, child, entryX, currentY, entryWidth, entryHeight);

            this.configElements.add(element);
            currentY += entryHeight + spacing;
        }

        // TODO maybe move/change this so rendering and clicking are both handled by MC?
        // Registers widgets to the screen, allowing MC's system to deal with the click logic
        this.configElements.forEach(element -> element.getWidgets().forEach(this::addWidget));
    }

    /** @return The {@link PaCoConfigManager} instance associated to this {@link PaCoConfigScreen}. */
    public PaCoConfigManager getManager() {
        return this.manager;
    }
}