package net.flytre.fguns.mixin;

import net.flytre.fguns.ConfigHandler;
import net.flytre.fguns.guns.GunItem;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.Set;

@Mixin(ModelLoader.class)
public abstract class ModelLoaderMixin {

    @Shadow
    @Final
    public static ModelIdentifier MISSING;
    @Shadow
    @Final
    private static Logger LOGGER;
    @Shadow
    @Final
    private Map<Identifier, UnbakedModel> unbakedModels;
    @Shadow
    @Final
    private Set<Identifier> modelsToLoad;

    @Shadow
    protected abstract void loadModel(Identifier id) throws Exception;

    @Inject(method = "getOrLoadModel", at = @At("HEAD"), cancellable = true)
    public void overrideModelForCustomGuns(Identifier id, CallbackInfoReturnable<UnbakedModel> cir) {
        if (id.getNamespace().equals("fguns") && ConfigHandler.LOADED_GUNS.containsKey(id.getPath())) {
            for (GunItem item : GunItem.GUNS) {
                if (item.getType() == ConfigHandler.LOADED_GUNS.get(id.getPath()).getType() && !ConfigHandler.CONFIG_ADDED_GUNS.contains(item)) {
                    Identifier id2 = Registry.ITEM.getId(item);
                    if (id instanceof ModelIdentifier) {
                        ModelIdentifier id3 = new ModelIdentifier(id2, ((ModelIdentifier) id).getVariant());
                        UnbakedModel model = helper(id3);
                        cir.setReturnValue(model);
                    } else {
                        UnbakedModel model = helper(id2);
                        cir.setReturnValue(model);
                    }
                    return;
                }
            }
        }
    }

    private UnbakedModel helper(Identifier id) {
        if (unbakedModels.containsKey(id)) {
            return this.unbakedModels.get(id);
        } else if (modelsToLoad.contains(id)) {
            throw new IllegalStateException("Circular reference while loading " + id);
        } else {
            this.modelsToLoad.add(id);
            UnbakedModel unbakedModel = this.unbakedModels.get(MISSING);

            while (!this.modelsToLoad.isEmpty()) {
                Identifier identifier = this.modelsToLoad.iterator().next();

                try {
                    if (!this.unbakedModels.containsKey(identifier)) {
                        this.loadModel(identifier);
                    }
                } catch (Exception var9) {
                    LOGGER.warn(var9.getMessage());
                    LOGGER.warn("Unable to load model: '{}' referenced from: {}: {}", identifier, id, var9);
                    this.unbakedModels.put(identifier, unbakedModel);
                } finally {
                    this.modelsToLoad.remove(identifier);
                }
            }

            return this.unbakedModels.getOrDefault(id, unbakedModel);
        }
    }

}