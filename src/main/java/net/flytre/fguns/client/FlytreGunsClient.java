package net.flytre.fguns.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.flytre.fguns.FlytreGuns;
import net.flytre.fguns.entity.BulletEntityRenderer;
import net.flytre.fguns.entity.BulletModel;
import net.flytre.fguns.gun.AbstractGun;
import net.flytre.fguns.workbench.WorkbenchScreen;
import net.flytre.flytre_lib.api.base.registry.EntityRendererRegistry;
import net.flytre.flytre_lib.api.event.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;

public class FlytreGunsClient implements ClientModInitializer {

    public HUD hud;

    @Override
    public void onInitializeClient() {

        EntityRendererRegistry.register(FlytreGuns.BULLET, (ctx) -> new BulletEntityRenderer(ctx, new BulletModel()));

        hud = new HUD();

        Key.init();
        Key.keyBindCode();

        ClientTickEvents.START_WORLD_TICK.register(world -> {
            MinecraftClient client = MinecraftClient.getInstance();

            Item item = client.player.getMainHandStack().getItem();
            if (item instanceof AbstractGun && Key.SCOPED) {
                if (((AbstractGun) item).hasScope())
                    TempClientData.shiftTime++;
                TempClientData.gun = (AbstractGun) item;
            } else
                TempClientData.shiftTime = 0;
        });

        ScreenRegistry.register(FlytreGuns.WORKBENCH_SCREEN_HANDLER, WorkbenchScreen::new);

    }
}
