package com.github.andrew0030.pandora_core.client.utils.gui;

import com.github.andrew0030.pandora_core.client.registry.PaCoPostShaders;
import com.github.andrew0030.pandora_core.client.utils.gui.enums.PaCoBorderSide;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.github.andrew0030.pandora_core.client.registry.PaCoPostShaders.BlurVariables.*;

// TODO write javadoc for some of these methods that still need it.
public class PaCoGuiUtils {
    // Padding
    public static final int PADDING_ONE = 1;
    public static final int PADDING_TWO = 2;
    public static final int PADDING_THREE = 3;
    public static final int PADDING_FOUR = 4;
    // Reusable Lists
    private static final ArrayList<PaCoBorderSide> BORDER_LIST = new ArrayList<>();
    private static final HashMap<String, Object> PARAMETERS = new HashMap<>();

    public static void renderBox(GuiGraphics graphics, int posX, int posY, int width, int height, int boxColor) {
        PaCoGuiUtils.renderBoxWithRim(graphics, posX, posY, width, height, boxColor, null, null);
    }

    public static void renderBoxWithRim(GuiGraphics graphics, int posX, int posY, int width, int height, @Nullable Integer boxColor, @Nullable Integer rimColor, @Nullable Integer rimSize) {
        ArrayList<PaCoBorderSide> rims = null;
        if (rimColor != null && rimSize != null) {
            rims = PaCoGuiUtils.getBorderList();
            rims.add(PaCoBorderSide.TOP.setColor(rimColor).setSize(rimSize));
            rims.add(PaCoBorderSide.RIGHT.setColor(rimColor).setSize(rimSize));
            rims.add(PaCoBorderSide.BOTTOM.setColor(rimColor).setSize(rimSize));
            rims.add(PaCoBorderSide.LEFT.setColor(rimColor).setSize(rimSize));
        }
        PaCoGuiUtils.renderBoxWithRim(graphics, posX, posY, width, height, boxColor, rims);
    }

    public static void renderBoxWithRim(GuiGraphics graphics, int posX, int posY, int width, int height, @Nullable Integer boxColor, @Nullable List<PaCoBorderSide> rims) {
        // Box
        if (boxColor != null)
            graphics.fill(posX, posY, posX + width, posY + height, boxColor);
        // Rims
        if (rims != null) {
            for (PaCoBorderSide side : rims) {
                if (side.equals(PaCoBorderSide.TOP))
                    graphics.fill(posX, posY, posX + width, posY + side.getSize(), side.getColor());
                if (side.equals(PaCoBorderSide.RIGHT))
                    graphics.fill(posX + width - side.getSize(), posY, posX + width, posY + height, side.getColor());
                if (side.equals(PaCoBorderSide.BOTTOM))
                    graphics.fill(posX, posY + height - side.getSize(), posX + width, posY + height, side.getColor());
                if (side.equals(PaCoBorderSide.LEFT))
                    graphics.fill(posX, posY, posX + side.getSize(), posY + height, side.getColor());
            }
        }
    }

    /** @return Reusable list for {@link PaCoBorderSide} that clears itself when obtained. */
    public static ArrayList<PaCoBorderSide> getBorderList() {
        PaCoGuiUtils.BORDER_LIST.clear();
        return PaCoGuiUtils.BORDER_LIST;
    }

    /**
     * Alternative method to enable scissors, this method doesn't take start and end coordinates,
     * instead it only takes start coordinates and then the wanted width and height.<br/>
     * <strong>NOTE</strong>: it is important to call {@link GuiGraphics#disableScissor()} when done to disable the scissors.
     * @param graphics The {@link GuiGraphics}.
     * @param posX The starting X position.
     * @param posY The starting Y position.
     * @param width The width of the scissor rectangle.
     * @param height The height of the scissor rectangle.
     */
    public static void enableScissor(GuiGraphics graphics, int posX, int posY, int width, int height) {
        graphics.enableScissor(posX, posY, posX + width, posY + height);
    }

    /**
     * Draws a centered string at the specified coordinates using the given font, text, color and dropShadow.
     * @param graphics The {@link GuiGraphics}.
     * @param font the {@link Font} to use for rendering.
     * @param text the text to draw.
     * @param x the x-coordinate of the center of the string.
     * @param y the y-coordinate of the string.
     * @param color the color of the string.
     * @param dropShadow – whether to apply a drop shadow to the string.
     */
    public static void drawCenteredString(GuiGraphics graphics, Font font, String text, int x, int y, int color, boolean dropShadow) {
        graphics.drawString(font, text, x - font.width(text) / 2, y, color, dropShadow);
    }

    /**
     * Draws a centered string at the specified coordinates using the given font, text component, color and dropShadow.
     * @param graphics The {@link GuiGraphics}.
     * @param font the {@link Font} to use for rendering.
     * @param text the text {@link Component} to draw.
     * @param x the x-coordinate of the center of the string.
     * @param y the y-coordinate of the string.
     * @param color the color of the string.
     * @param dropShadow – whether to apply a drop shadow to the string.
     */
    public static void drawCenteredString(GuiGraphics graphics, Font font, Component text, int x, int y, int color, boolean dropShadow) {
        FormattedCharSequence charSequence = text.getVisualOrderText();
        graphics.drawString(font, charSequence, x - font.width(charSequence) / 2, y, color, dropShadow);
    }

    /**
     * Draws a centered string at the specified coordinates using the given font, formatted character sequence, color and dropShadow.
     * @param graphics The {@link GuiGraphics}.
     * @param font the {@link Font} to use for rendering.
     * @param text the {@link FormattedCharSequence} to draw.
     * @param x the x-coordinate of the center of the string.
     * @param y the y-coordinate of the string.
     * @param color the color of the string.
     * @param dropShadow whether to apply a drop shadow to the string.
     */
    public static void drawCenteredString(GuiGraphics graphics, Font font, FormattedCharSequence text, int x, int y, int color, boolean dropShadow) {
        graphics.drawString(font, text, x - font.width(text) / 2, y, color, dropShadow);
    }

    /**
     * Checks if the mouse is within the bounds of the given rectangle.
     * @param mouseX The current x-coordinate of the mouse
     * @param mouseY The current y-coordinate of the mouse
     * @param x      The x-coordinate of the top left corner
     * @param y      The y-coordinate of the top left corner
     * @param width  The width of the rectangle
     * @param height The height of the rectangle
     * @return Whether the mouse is within the specified rectangle.
     */
    public static boolean isMouseWithin(double mouseX, double mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
    }

    /**
     * Draws a formatted text with word wrapping at the specified coordinates using the given font, text, line width,
     * color and drop shadow.
     * @param graphics   The {@link GuiGraphics}.
     * @param font       The {@link Font} to use for rendering.
     * @param text       The {@link FormattedCharSequence} to draw.
     * @param x          The x-coordinate of the starting position.
     * @param y          The y-coordinate of the starting position.
     * @param lineWidth  The maximum width of each line before wrapping.
     * @param color      The color of the text.
     * @param dropShadow Whether to apply a drop shadow to the text.
     */
    public static void drawWordWrap(GuiGraphics graphics, Font font, FormattedText text, int x, int y, int lineWidth, int color, boolean dropShadow) {
        for(FormattedCharSequence charSequence : font.split(text, lineWidth)) {
            graphics.drawString(font, charSequence, x, y, color, dropShadow);
            y += 9;
        }
    }

    /**
     * Draws a formatted text with word wrapping at the specified coordinates using the given font, text, line width,
     * color and drop shadow.<br/>
     * This version of the method also returns a new {@link Pair} containing the rendered text's width and height. If the
     * dimensions are not needed, instead call {@link PaCoGuiUtils#drawWordWrap}.
     * @param graphics   The {@link GuiGraphics}.
     * @param font       The {@link Font} to use for rendering.
     * @param text       The {@link FormattedCharSequence} to draw.
     * @param x          The x-coordinate of the starting position.
     * @param y          The y-coordinate of the starting position.
     * @param lineWidth  The maximum width of each line before wrapping.
     * @param color      The color of the text.
     * @param dropShadow Whether to apply a drop shadow to the text.
     * @return A {@link Pair} containing the width and height of the text.
     */
    public static Pair<Integer, Integer> drawWordWrapWithDimensions(GuiGraphics graphics, Font font, FormattedText text, int x, int y, int lineWidth, int color, boolean dropShadow) {
        int startY = y;
        int biggestWidth = 0;
        for(FormattedCharSequence charSequence : font.split(text, lineWidth)) {
            graphics.drawString(font, charSequence, x, y, color, dropShadow);
            biggestWidth = Math.max(font.width(charSequence), biggestWidth);
            y += 9;
        }
        return Pair.of(biggestWidth, y - startY);
    }

    /**
     * Draws a centered formatted text, with word wrapping of the specified coordinates
     * using the given font, text, line width, color and drop shadow.
     * @param graphics   The {@link GuiGraphics}.
     * @param font       The {@link Font} to use for rendering.
     * @param text       The {@link FormattedCharSequence} to draw.
     * @param x          The x-coordinate of the starting position.
     * @param y          The y-coordinate of the starting position.
     * @param lineWidth  The maximum width of each line before wrapping.
     * @param color      The color of the text.
     * @param dropShadow Whether to apply a drop shadow to the text.
     */
    public static void drawCenteredWordWrap(GuiGraphics graphics, Font font, FormattedText text, int x, int y, int lineWidth, int color, boolean dropShadow) {
        for(FormattedCharSequence charSequence : font.split(text, lineWidth / 2)) {
            PaCoGuiUtils.drawCenteredString(graphics, font, charSequence, x + lineWidth / 2, y, color, dropShadow);
            y += 9;
        }
    }

    /**
     * Draws a centered formatted text, with word wrapping of the specified coordinates
     * using the given font, text, line width, color and drop shadow.<br/>
     * This version of the method also returns a new {@link Pair} containing the rendered text's width and height. If the
     * dimensions are not needed, instead call {@link PaCoGuiUtils#drawCenteredWordWrap}.
     * @param graphics   The {@link GuiGraphics}.
     * @param font       The {@link Font} to use for rendering.
     * @param text       The {@link FormattedCharSequence} to draw.
     * @param x          The x-coordinate of the starting position.
     * @param y          The y-coordinate of the starting position.
     * @param lineWidth  The maximum width of each line before wrapping.
     * @param color      The color of the text.
     * @param dropShadow Whether to apply a drop shadow to the text.
     * @return A {@link Pair} containing the width and height of the text.
     */
    public static Pair<Integer, Integer> drawCenteredWordWrapWithDimensions(GuiGraphics graphics, Font font, FormattedText text, int x, int y, int lineWidth, int color, boolean dropShadow) {
        int startY = y;
        int biggestWidth = 0;
        for(FormattedCharSequence charSequence : font.split(text, lineWidth / 2)) {
            PaCoGuiUtils.drawCenteredString(graphics, font, charSequence, x + lineWidth / 2, y, color, dropShadow);
            biggestWidth = Math.max(font.width(charSequence), biggestWidth);
            y += 9;
        }
        return Pair.of(biggestWidth, y - startY);
    }

    /**
     * Renders the given {@link ItemStack} at the specified scale, centered on the given position.
     * @param poseStack The {@link PoseStack} used for rendering
     * @param itemStack The {@link ItemStack} of the {@link Item} or {@link Block} that will be rendered
     * @param pX        The x-axis position the {@link ItemStack} will be centered on
     * @param pY        The y-axis position the {@link ItemStack} will be centered on
     * @param size      The size in pixels the {@link ItemStack} should be rendered as
     */
    public static void renderScaledItemStack(PoseStack poseStack, ItemStack itemStack, int pX, int pY, int size) {
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        BakedModel itemBakedModel = itemRenderer.getModel(itemStack, null, null, 0);
        Minecraft.getInstance().getTextureManager().getTexture(TextureAtlas.LOCATION_BLOCKS).setFilter(false, false);
        RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_BLOCKS);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        poseStack.pushPose();
        poseStack.translate(pX, pY, 100.0F);
        poseStack.scale(1.0F, -1.0F, 1.0F);
        poseStack.scale(size, size, size);
        RenderSystem.applyModelViewMatrix();
        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        Lighting.setupForFlatItems();
        itemRenderer.render(itemStack, ItemDisplayContext.GUI, false, poseStack, bufferSource, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, itemBakedModel);
        bufferSource.endBatch();
        poseStack.popPose();
        RenderSystem.applyModelViewMatrix();
    }

    /**
     * Applies a blur effect to the screen, using a default blur radius of {@code 5.0F}.
     * <p> This is a convenience overload for {@link #blurScreen(float, float)}. </p>
     *
     * @param partialTick The current partial tick value used for frame interpolation
     */
    public static void blurScreen(float partialTick) {
        PaCoGuiUtils.blurScreen(5.0F, partialTick);
    }

    /**
     * Applies a blur effect to the screen, using the given radius to determine the intensity.
     *
     * @param radius      The radius/intensity of the blur effect
     * @param partialTick The current partial tick value used for frame interpolation
     */
    public static void blurScreen(float radius, float partialTick) {
        Minecraft minecraft = Minecraft.getInstance();
        PARAMETERS.clear();
        PARAMETERS.put("radius", radius);
        PASS0_MUL.get().set(1.0F);
        PASS1_MUL.get().set(0.5f);
        PASS2_MUL.get().set(0.25f);
        PaCoPostShaders.PACO_BLUR.processPostChain(partialTick, PARAMETERS);
        minecraft.getMainRenderTarget().bindWrite(false);
    }

    /**
     * Applies a blur effect to the specified rect, using a default blur radius of {@code 5.0F}.
     * <p> This is a convenience overload for {@link #blurRect(GuiGraphics, float, int, int, int, int, float)}. </p>
     *
     * @param graphics    The {@link GuiGraphics}
     * @param x           The x-coordinate of the starting position
     * @param y           The y-coordinate of the starting position
     * @param width       The width of the rectangle
     * @param height      The height of the rectangle
     * @param partialTick The current partial tick value used for frame interpolation
     */
    public static void blurRect(GuiGraphics graphics, int x, int y, int width, int height, float partialTick) {
        PaCoGuiUtils.blurRect(graphics, 5.0F, x, y, width, height, partialTick);
    }

    /**
     * Applies a blur effect to the specified rect, using the given radius to determine the intensity.
     *
     * @param graphics    The {@link GuiGraphics}
     * @param radius      The radius/intensity of the blur effect
     * @param x           The x-coordinate of the starting position
     * @param y           The y-coordinate of the starting position
     * @param width       The width of the rectangle
     * @param height      The height of the rectangle
     * @param partialTick The current partial tick value used for frame interpolation
     */
    public static void blurRect(GuiGraphics graphics, float radius, int x, int y, int width, int height, float partialTick) {
        PaCoGuiUtils.enableScissor(graphics, x, y, width, height);
        RenderSystem.disableDepthTest();
        PaCoGuiUtils.blurScreen(radius, partialTick);
        RenderSystem.enableDepthTest();
        graphics.disableScissor();
    }

    /** @return The total height a tooltip will occupy when rendered */
    public static int getTooltipHeight(Font font, List<Component> tooltipLines, int width, int sliceSize) {
        List<ClientTooltipComponent> components = getTooltipComponents(font, tooltipLines, width, sliceSize);
        int innerHeight = components.size() == 1 ? -2 : 0;
        for (ClientTooltipComponent component : components)
            innerHeight += component.getHeight();
        return innerHeight + (sliceSize * 2);
    }

    /** Helper method to build and wrap the tooltip text components */
    private static List<ClientTooltipComponent> getTooltipComponents(Font font, List<Component> tooltipLines, int width, int sliceSize) {
        int innerWidth = Math.max(0, width - (sliceSize * 2));
        List<ClientTooltipComponent> components = new ArrayList<>();
        for (Component line : tooltipLines) {
            List<FormattedCharSequence> wrappedLines = font.split(line, innerWidth);
            if (wrappedLines.isEmpty()) {
                components.add(ClientTooltipComponent.create(FormattedCharSequence.EMPTY));
            } else {
                for (FormattedCharSequence wrappedLine : wrappedLines)
                    components.add(ClientTooltipComponent.create(wrappedLine));
            }
        }
        return components;
    }

    /**
     * Renders a tooltip with a custom nine-sliced background texture.
     * The tooltip background will always be exactly the specified width, and text will wrap
     * to fit inside the box with standard padding.
     *
     * @param graphics      The {@link GuiGraphics}
     * @param font          The {@link Font} to use for rendering
     * @param tooltipLines  A list of {@link Component Components} used as the lines of the tooltip
     * @param x             The top-left X coordinate of the tooltip box
     * @param y             The top-left Y coordinate of the tooltip box
     * @param width         The total width the background should occupy (including the rim)
     * @param texture       The {@link ResourceLocation} of the texture
     * @param sliceSize     The size of the corner slices for nine-slicing (used for rim size)
     * @param uOffset       The X coordinate of the top-left corner of the source texture
     * @param vOffset       The Y coordinate of the top-left corner of the source texture
     * @param textureWidth  The total width of the source texture
     * @param textureHeight The total height of the source texture
     */
    public static void renderFixedTooltipNineSliced(GuiGraphics graphics, Font font, List<Component> tooltipLines, int x, int y, int width, ResourceLocation texture, int sliceSize, int uOffset, int vOffset, int textureWidth, int textureHeight) {
        List<ClientTooltipComponent> components = PaCoGuiUtils.getTooltipComponents(font, tooltipLines, width, sliceSize);
        if (components.isEmpty()) return;
        // Calculates the total height of the tooltip
        int innerHeight = components.size() == 1 ? -2 : 0;
        for (ClientTooltipComponent component : components)
            innerHeight += component.getHeight();
        int totalHeight = innerHeight + (sliceSize * 2);
        // Renders the tooltip
        graphics.pose().pushPose();
        int z = 400; // The default z-level MC uses
        // Note: we need to translate the tooltip on the z-axis, because MC uses fill which behaves slightly differently.
        graphics.pose().translate(0.0F, 0.0F, z);
        // Renders the tooltip background as a nine sliced texture
        graphics.drawManaged(() -> {
            graphics.blitNineSliced(texture, x, y, width, totalHeight, sliceSize, textureWidth, textureHeight, uOffset, vOffset);
        });
        // Renders the tooltip text
        int currentY = y + sliceSize;
        for (int i = 0; i < components.size(); i++) {
            ClientTooltipComponent component = components.get(i);
            component.renderText(font, x + sliceSize, currentY, graphics.pose().last().pose(), graphics.bufferSource());
            currentY += component.getHeight() + (i == 0 ? 2 : 0);
        }
        // Renders the tooltip images
        currentY = y + sliceSize;
        for (int i = 0; i < components.size(); i++) {
            ClientTooltipComponent component = components.get(i);
            component.renderImage(font, x + sliceSize, currentY, graphics);
            currentY += component.getHeight() + (i == 0 ? 2 : 0);
        }
        graphics.pose().popPose();
    }
}