package net.flytre.fguns;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.flytre.fguns.entity.BulletEntityRenderer;
import net.flytre.fguns.entity.BulletModel;
import net.flytre.fguns.entity.BulletPacket;
import net.flytre.fguns.workbench.WorkbenchRecipe;
import net.flytre.fguns.workbench.WorkbenchScreen;

public class FlytreGunsClient implements ClientModInitializer {

    public HUD hud;


    @Override
    public void onInitializeClient() {

        EntityRendererRegistry.INSTANCE.register(FlytreGuns.BULLET, (dispatcher, context) -> new BulletEntityRenderer(dispatcher, new BulletModel()));

        hud = new HUD();

        Key.init();
        Key.keyBindCode();

        ScreenRegistry.register(FlytreGuns.WORKBENCH_SCREEN_HANDLER, WorkbenchScreen::new);

        ClientPlayNetworking.registerGlobalReceiver(FlytreGuns.RECEIVE_RECIPE_PACKET_ID, (client, handler, buf, responseSender) -> {
            WorkbenchRecipe recipe = FlytreGuns.WORKBENCH_SERIALIZER.read(buf.readIdentifier(), buf);
            client.execute(() -> {
                if (client.currentScreen instanceof WorkbenchScreen)
                    ((WorkbenchScreen) client.currentScreen).setCurrentRecipe(recipe);
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(FlytreGuns.BULLET_VELOCITY_PACKET_ID, (client, handler, buf, responseSender) -> {
            BulletPacket packet = new BulletPacket();
            packet.read(buf);
            assert client.world != null;
            packet.apply(client.world);
            client.execute(() -> {
                packet.apply(client.world);
            });
        });

    }
}
