package net.withered.withering.mixin;

import net.minecraft.client.render.item.ItemModels;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.withered.withering.api.SplitModelItem;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {
    @Shadow @Final private ItemModels models;

    @ModifyVariable(
            method = "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/render/model/BakedModel;)V",
            at = @At("HEAD"), argsOnly = true
    )
    private BakedModel withering$changeSplitModel(BakedModel model, ItemStack stack, ModelTransformationMode renderMode) {
        if (stack.getItem() instanceof SplitModelItem splitItem) {
            String namespace = Registries.ITEM.getId(stack.getItem()).getNamespace();

            String path = (renderMode == ModelTransformationMode.GUI
                    || renderMode == ModelTransformationMode.FIXED
                    || renderMode == ModelTransformationMode.GROUND)
                    ? splitItem.getModelBaseName()
                    : splitItem.getModelBaseName() + "_hand";

            return models.getModelManager().getModel(
                    ModelIdentifier.ofInventoryVariant(Identifier.of(namespace, path))
            );
        }
        return model;
    }
}