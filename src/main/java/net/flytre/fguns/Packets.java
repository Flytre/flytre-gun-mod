package net.flytre.fguns;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.flytre.fguns.gun.AbstractGun;
import net.flytre.fguns.workbench.WorkbenchEntity;
import net.flytre.fguns.workbench.WorkbenchRecipe;
import net.flytre.fguns.workbench.WorkbenchScreenHandler;
import net.flytre.flytre_lib.common.util.InventoryUtils;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.Identifier;

public class Packets {
    //Packet
    public static final Identifier RELOAD = new Identifier("fguns", "reload");
    public static final Identifier FIRING_PATTERN = new Identifier("fguns", "firing_pattern");
    public static final Identifier RECEIVE_RECIPE = new Identifier("fguns", "receive_recipe");
    public static final Identifier REQUEST_RECIPE = new Identifier("fguns", "request_recipe");
    public static final Identifier NEXT_RECIPE = new Identifier("fguns", "next_recipe");
    public static final Identifier CRAFT_ITEM = new Identifier("fguns", "assemble");
    public static final Identifier BULLET_VELOCITY = new Identifier("fguns", "velocity_packet");

    public static void init() {
        ServerPlayNetworking.registerGlobalReceiver(Packets.RELOAD, (server, player, handler, buf, responseSender) -> server.execute(() -> AbstractGun.attemptEarlyReload(player)));

        ServerPlayNetworking.registerGlobalReceiver(Packets.FIRING_PATTERN, (server, player, handler, buf, responseSender) -> server.execute(() -> AbstractGun.switchFiringPattern(player)));

        ServerPlayNetworking.registerGlobalReceiver(Packets.NEXT_RECIPE, (server, player, handler, buf, responseSender) -> {
            int i = buf.readInt();
            server.execute(() -> {
                ScreenHandler screenHandler = player.currentScreenHandler;
                if (screenHandler instanceof WorkbenchScreenHandler) {
                    WorkbenchEntity entity = ((WorkbenchScreenHandler) screenHandler).getWorkbenchEntity();
                    if (entity != null) {
                        if (i == 1)
                            entity.nextRecipe();
                        else
                            entity.previousRecipe();
                    }
                }
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(Packets.REQUEST_RECIPE, (server, player, handler, buf, responseSender) -> server.execute(() -> {
            ScreenHandler screenHandler = player.currentScreenHandler;
            if (screenHandler instanceof WorkbenchScreenHandler) {
                WorkbenchEntity entity = ((WorkbenchScreenHandler) screenHandler).getWorkbenchEntity();
                if (entity != null) {
                    entity.sendRecipe(player);
                }
            }
        }));

        ServerPlayNetworking.registerGlobalReceiver(Packets.CRAFT_ITEM, (server, player, handler, buf, responseSender) -> {
            Identifier recipe = buf.readIdentifier();
            server.execute(() -> {
                ScreenHandler screenHandler = player.currentScreenHandler;
                if (screenHandler instanceof WorkbenchScreenHandler) {
                    WorkbenchEntity entity = ((WorkbenchScreenHandler) screenHandler).getWorkbenchEntity();
                    if (entity != null) {
                        Recipe<?> workbenchRecipe = server.getRecipeManager().get(recipe).orElse(null);
                        if (!(workbenchRecipe instanceof WorkbenchRecipe))
                            return;
                        WorkbenchRecipe actualRecipe = (WorkbenchRecipe) workbenchRecipe;
                        if (actualRecipe.matches(player.inventory, player.world)) {
                            ItemStack stack = actualRecipe.craft(player.inventory);
                            stack = InventoryUtils.putStackInInventory(stack, player.inventory, 0, 36);
                            if (!stack.isEmpty())
                                player.world.spawnEntity(new ItemEntity(player.world, player.getX(), player.getY(), player.getZ(), stack));
                        }
                    }
                }
            });
        });
    }
}
