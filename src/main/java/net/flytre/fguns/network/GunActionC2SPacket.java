package net.flytre.fguns.network;

import net.flytre.fguns.gun.AbstractGun;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

public class GunActionC2SPacket implements Packet<ServerPlayPacketListener> {


    private final Action action;

    public GunActionC2SPacket(Action action) {
        this.action = action;
    }

    public GunActionC2SPacket(PacketByteBuf buf) {
        action = buf.readEnumConstant(Action.class);
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeEnumConstant(action);
    }

    @Override
    public void apply(ServerPlayPacketListener listener) {
        ServerPlayerEntity player = ((ServerPlayNetworkHandler) listener).getPlayer();
        switch (action) {
            case RELOAD -> AbstractGun.attemptEarlyReload(player);
            case CYCLE_FIRING_PATTERN -> AbstractGun.switchFiringPattern(player);
            default -> throw new AssertionError("Unknown action");
        }
    }

    public enum Action {
        RELOAD,
        CYCLE_FIRING_PATTERN;
    }
}
