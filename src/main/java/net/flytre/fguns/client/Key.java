package net.flytre.fguns.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.flytre.fguns.Packets;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class Key {
    public static KeyBinding reload;
    public static KeyBinding firingPattern;
    public static KeyBinding scope;

    public static boolean SCOPED = false;


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
        scope = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.fguns.scope",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_LEFT_SHIFT,
                "category.fguns.main"
        ));
    }

    public static void keyBindCode() {

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (reload.wasPressed()) {
                ClientPlayNetworking.send(Packets.RELOAD, PacketByteBufs.empty());
            }
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (firingPattern.wasPressed()) {
                ClientPlayNetworking.send(Packets.FIRING_PATTERN, PacketByteBufs.empty());
            }
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            SCOPED = scope.isPressed() || (scope.isUnbound() && MinecraftClient.getInstance().options.keySneak.isPressed());
        });
    }

}
