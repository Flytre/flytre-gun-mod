package net.flytre.fguns;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.flytre.fguns.entity.BulletEntityRenderer;
import net.flytre.fguns.entity.BulletModel;

public class FlytreGunsClient implements ClientModInitializer {

    public HUD hud;


    @Override
    public void onInitializeClient() {

        EntityRendererRegistry.INSTANCE.register(FlytreGuns.BULLET, (dispatcher, context) -> new BulletEntityRenderer(dispatcher, new BulletModel()));

        hud = new HUD();

        Key.init();
        Key.keyBindCode();


    }
}
