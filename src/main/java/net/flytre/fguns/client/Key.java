package net.flytre.fguns.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.flytre.fguns.network.GunActionC2SPacket;
import net.flytre.flytre_lib.api.base.util.KeyBindUtils;
import net.flytre.flytre_lib.api.event.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class Key {
    public static KeyBinding RELOAD;
    public static KeyBinding FIRING_PATTERN;
    public static KeyBinding SCOPE;

    public static boolean SCOPED = false;


    public static void init() {
        RELOAD = KeyBindUtils.register(new KeyBinding(
                "key.fguns.reload",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_R,
                "category.fguns.main"
        ));
        FIRING_PATTERN = KeyBindUtils.register(new KeyBinding(
                "key.fguns.mode",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_M,
                "category.fguns.main"
        ));
         SCOPE = KeyBindUtils.register(new KeyBinding(
                "key.fguns.scope",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_LEFT_SHIFT,
                "category.fguns.main"
        ));
    }

    public static void keyBindCode() {

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (RELOAD.wasPressed()) {
                ClientPlayNetworkHandler handler = client.getNetworkHandler();
                if (handler != null)
                    handler.sendPacket(new GunActionC2SPacket(GunActionC2SPacket.Action.RELOAD));
            }
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (FIRING_PATTERN.wasPressed()) {
                ClientPlayNetworkHandler handler = client.getNetworkHandler();
                if (handler != null)
                    handler.sendPacket(new GunActionC2SPacket(GunActionC2SPacket.Action.CYCLE_FIRING_PATTERN));
            }
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> SCOPED = SCOPE.isPressed() || (SCOPE.isUnbound() && MinecraftClient.getInstance().options.keySneak.isPressed()));
    }

}
