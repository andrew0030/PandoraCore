package com.github.andrew0030.pandora_core.client.render.dynamic;

import com.github.andrew0030.pandora_core.client.registry.PaCoRendererRegistry;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.BuiltInModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

/** The dynamic item renderer should be registered using {@link PaCoRendererRegistry}. */
public abstract class PaCoDynamicItemRenderer {
    protected final BlockEntityRenderDispatcher renderDispatcher;
    protected final EntityModelSet modelSet;
    protected final Minecraft minecraft;

    public PaCoDynamicItemRenderer(BlockEntityRenderDispatcher renderDispatcher, EntityModelSet modelSet) {
        this.renderDispatcher = renderDispatcher;
        this.modelSet = modelSet;
        this.minecraft = Minecraft.getInstance();
    }

    /**
     * <b>IMPORTANT:</b> All rendering should be done in {@link #render}, this method is purely for determining if the
     * transforms of the item, the renderer is attached to, should be applied.
     * 
     * @param stack         The {@link ItemStack} being rendered
     * @param context       The {@link ItemDisplayContext}
     * @param packedLight   The light-map coordinates
     * @param packedOverlay The overlay UV passed to {@link VertexConsumer#overlayCoords(int)}
     * @param model         The {@link BakedModel} of the {@code item} the {@code renderer} is attached to, or {@link BuiltInModel} if the {@code json} is missing/invalid
     * @return Whether the {@code poseStack} in {@link #render} will get the {@link ItemTransforms} of the {@code item} applied to it.
     */
    public boolean ignoreAttachedTransforms(ItemStack stack, ItemDisplayContext context, int packedLight, int packedOverlay, BakedModel model) {
        return false;
    }

    /**
     * Used to render the dynamic model, this can be anything from another {@code item} to a {@code BER}/{@code BEWLR}.
     *
     * @param stack         The {@link ItemStack} being rendered
     * @param partialTick   The current partial tick
     * @param context       The {@link ItemDisplayContext}
     * @param poseStack     The {@link PoseStack} of this renderer
     * @param buffer        The {@link MultiBufferSource}
     * @param packedLight   The light-map coordinates
     * @param packedOverlay The overlay UV passed to {@link VertexConsumer#overlayCoords(int)}
     * @param model         The {@link BakedModel} of the {@code item} the {@code renderer} is attached to, or {@link BuiltInModel} if the {@code json} is missing/invalid
     */
    public abstract void render(ItemStack stack, float partialTick, ItemDisplayContext context, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay, BakedModel model);

    /**
     * Called after {@link #render} to apply additional transformations to the {@link PoseStack},
     * that will be used for the {@code item model}/{@code BEWLR}, this {@code renderer} is attached to.
     * <p>
     * <b>IMPORTANT:</b> All rendering should be done in {@link #render}, this method is purely for modifying the {@code poseStack}.</p>
     *
     * @param stack       The {@link ItemStack} being rendered
     * @param partialTick The current partial tick
     * @param context     The {@link ItemDisplayContext}
     * @param poseStack   The {@link PoseStack} used for the attached {@code item model}/{@code BEWLR}
     * @implNote Transformations to the {@code poseStack} should <b>NOT</b> be wrapped in a {@code pushPose()} and {@code popPose()}.
     */
    public void applyPostRenderTransforms(ItemStack stack, float partialTick, ItemDisplayContext context, PoseStack poseStack) {}
}