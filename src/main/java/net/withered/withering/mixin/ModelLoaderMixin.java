package net.withered.withering.mixin;

import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.withered.withering.api.SplitModelItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ModelLoader.class)
public abstract class ModelLoaderMixin {
    @Shadow protected abstract void loadItemModel(ModelIdentifier id);

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/model/ModelLoader;loadItemModel(Lnet/minecraft/client/util/ModelIdentifier;)V", ordinal = 1))
    private void loadWitheringSplitModels(CallbackInfo ci) {
        for (Item item : Registries.ITEM) {
            if (item instanceof SplitModelItem splitItem) {
                String namespace = Registries.ITEM.getId(item).getNamespace();
                String base = splitItem.getModelBaseName();

                loadItemModel(ModelIdentifier.ofInventoryVariant(Identifier.of(namespace, base)));
                loadItemModel(ModelIdentifier.ofInventoryVariant(Identifier.of(namespace, base + "_hand")));
            }
        }
    }
}