package net.flytre.fguns;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class Key {
    public static KeyBinding reload;
    public static KeyBinding firingPattern;


    public static void init() {
        reload = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.fguns.reload",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_R,
                "category.fguns.main"
        ));
        firingPattern = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.fguns.mode",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_M,
                "category.fguns.main"
        ));
    }

    public static void keyBindCode() {

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (reload.wasPressed()) {
                ClientPlayNetworking.send(FlytreGuns.RELOAD_PACKET_ID, PacketByteBufs.empty());
            }
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (firingPattern.wasPressed()) {
                ClientPlayNetworking.send(FlytreGuns.FIRING_PATTERN_PACKET_ID, PacketByteBufs.empty());
            }
        });
    }

}
