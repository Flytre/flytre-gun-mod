package net.flytre.fguns;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.flytre.fguns.config.Config;
import net.flytre.fguns.config.CustomGunConfigHandler;
import net.flytre.fguns.entity.Bullet;
import net.flytre.fguns.guns.*;
import net.flytre.fguns.workbench.*;
import net.flytre.flytre_lib.common.util.InventoryUtils;
import net.flytre.flytre_lib.config.ConfigHandler;
import net.flytre.flytre_lib.config.ConfigRegistry;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class FlytreGuns implements ModInitializer {

    //Packet
    public static final Identifier RELOAD_PACKET_ID = new Identifier("fguns", "reload");
    public static final Identifier FIRING_PATTERN_PACKET_ID = new Identifier("fguns", "firing_pattern");
    public static final Identifier RECEIVE_RECIPE_PACKET_ID = new Identifier("fguns", "receive_recipe");
    public static final Identifier REQUEST_RECIPE_PACKET_ID = new Identifier("fguns", "request_recipe");
    public static final Identifier NEXT_RECIPE_PACKET_ID = new Identifier("fguns", "next_recipe");
    public static final Identifier CRAFT_ITEM_PACKET_ITEM = new Identifier("fguns", "assemble");

    //Item
    public static final ItemGroup TAB = FabricItemGroupBuilder.build(
            new Identifier("fguns", "all"),
            () -> new ItemStack(FlytreGuns.LETHAL_MARK));
    public static final GunItem LETHAL_MARK = new GunItem(5, .40, 2, 0.02, 3, 30, 10, 1.4, GunType.PISTOL);
    public static final GunItem BEAMER = new GunItem(12, .50, 1, 0.02, 3, 30, 6, 3.0, GunType.PISTOL);
    public static final GunItem LASER_SPEED = new GunItem(4, .20, 4, 0.03, 6, 25, 20, 1.4, GunType.PISTOL);
    public static final GunItem HUNTER = new GunItem(5, .40, 6, 0.04, 9, 25, 25, 3.2, GunType.RIFLE);
    public static final GunItem BLASTER = new GunItem(5, .50, 4, 0.03, 7, 25, 30, 3.0, GunType.RIFLE);
    public static final GunItem RAPIDSTRIKE = new GunItem(4, .30, 10, 0.06, 13, 20, 40, 2.5, GunType.SMG);
    public static final Sniper SEEKER = new Sniper(13, .75, 0.2, 0.0, 17, 100, 5, 12.0, GunType.SNIPER);
    public static final Sniper NIGHTMARE = new Sniper(22, .90, 0.11, 0.0, 17, 100, 1, 9.0, GunType.SNIPER);
    public static final Shotgun SHOTGUN = new Shotgun(4, .40, 3, 0.06, 16, 12, 2, 2.2);
    public static final GunItem TRIFORCE = new GunItem(6, .40, 20, 0.01, 3, 35, 3, 1.25, GunType.RIFLE);
    public static final SlimeGun SLIMER = new SlimeGun(4, .20, 4, 0.01, 0, 40, 10, 2.0);
    public static final RocketLauncher ROCKET_LAUNCHER = new RocketLauncher(1, 100, 0.33, 0.00, 0, 40, 2, 2.5);
    public static final Shocker VOLT = new Shocker(10, 0.4, 1.0, 0, 3, 30, 3, 1.25);
    public static final Minigun MINIGUN = new Minigun(5, 0.40, 20.0, 0.04, 20, 30, 80, 6.5);

    public static final Item BASIC_AMMO = new Item(new Item.Settings().group(FlytreGuns.TAB));
    public static final Item SHOTGUN_SHELL = new Item(new Item.Settings().group(FlytreGuns.TAB));
    public static final Item SNIPER_AMMO = new Item(new Item.Settings().group(FlytreGuns.TAB));
    public static final Item ROCKET_AMMO = new Item(new Item.Settings().group(FlytreGuns.TAB));
    public static final Item ENERGY_CELL = new Item(new Item.Settings().group(FlytreGuns.TAB));
    public static final Item MYSTERY_GUN = new MysteryGun();


    public static boolean MOB_AI_RELEASED = false;

    //Entity
    public static final EntityType<Bullet> BULLET = Registry.register(
            Registry.ENTITY_TYPE,
            new Identifier("fguns", "bullet"),
            FabricEntityTypeBuilder.<Bullet>create(SpawnGroup.MISC, Bullet::new).dimensions(new EntityDimensions(0.5F, 0.2F, true)).trackRangeChunks(4).trackedUpdateRate(20).build());

    //Workbench
    public static final WorkbenchBlock WORKBENCH = new WorkbenchBlock(FabricBlockSettings.of(Material.METAL).strength(3.0f));
    public static BlockEntityType<WorkbenchEntity> WORKBENCH_ENTITY;
    public static ScreenHandlerType<WorkbenchScreenHandler> WORKBENCH_SCREEN_HANDLER;
    public static RecipeType<WorkbenchRecipe> WORKBENCH_RECIPE;
    public static WorkbenchRecipeSerializer WORKBENCH_SERIALIZER;

    public static ConfigHandler<Config> CONFIG_HANDLER = new ConfigHandler<>(new Config(), "flytres_gun_mod.json");

    @Override
    public void onInitialize() {
        System.out.println("Flytre's Gun Mod is being loaded!");
        Registry.register(Registry.ITEM, new Identifier("fguns", "lethal_mark"), LETHAL_MARK);
        Registry.register(Registry.ITEM, new Identifier("fguns", "beamer"), BEAMER);
        Registry.register(Registry.ITEM, new Identifier("fguns", "laser_speed"), LASER_SPEED);
        Registry.register(Registry.ITEM, new Identifier("fguns", "hunter"), HUNTER);
        Registry.register(Registry.ITEM, new Identifier("fguns", "blaster"), BLASTER);
        Registry.register(Registry.ITEM, new Identifier("fguns", "shotgun"), SHOTGUN);
        Registry.register(Registry.ITEM, new Identifier("fguns", "rapidstrike"), RAPIDSTRIKE);
        Registry.register(Registry.ITEM, new Identifier("fguns", "seeker"), SEEKER);
        Registry.register(Registry.ITEM, new Identifier("fguns", "nightmare"), NIGHTMARE);
        Registry.register(Registry.ITEM, new Identifier("fguns", "triforce"), TRIFORCE);
        Registry.register(Registry.ITEM, new Identifier("fguns", "slimer"), SLIMER);

        Registry.register(Registry.ITEM, new Identifier("fguns", "basic_ammo"), BASIC_AMMO);
        Registry.register(Registry.ITEM, new Identifier("fguns", "shotgun_shell"), SHOTGUN_SHELL);
        Registry.register(Registry.ITEM, new Identifier("fguns", "sniper_ammo"), SNIPER_AMMO);
        Registry.register(Registry.ITEM, new Identifier("fguns", "rocket_ammo"), ROCKET_AMMO);
        Registry.register(Registry.ITEM, new Identifier("fguns", "energy_cell"), ENERGY_CELL);

        Registry.register(Registry.ITEM, new Identifier("fguns", "mystery_gun"), MYSTERY_GUN);

        Registry.register(Registry.ITEM, new Identifier("fguns", "rocket_launcher"), ROCKET_LAUNCHER);

        Registry.register(Registry.ITEM, new Identifier("fguns", "volt"), VOLT);

        Registry.register(Registry.ITEM, new Identifier("fguns", "minigun"), MINIGUN);

        Registry.register(Registry.BLOCK, new Identifier("fguns", "workbench"), WORKBENCH);
        Registry.register(Registry.ITEM, new Identifier("fguns", "workbench"), new BlockItem(WORKBENCH, new FabricItemSettings().group(TAB)));
        WORKBENCH_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier("fguns:workbench"), BlockEntityType.Builder.create(WorkbenchEntity::new, WORKBENCH).build(null));
        WORKBENCH_SCREEN_HANDLER = ScreenHandlerRegistry.registerSimple(new Identifier("fguns:workbench"), WorkbenchScreenHandler::new);
        WORKBENCH_RECIPE = Registry.register(Registry.RECIPE_TYPE, new Identifier("fguns:workbench"), new RecipeType<WorkbenchRecipe>() {
            public String toString() {
                return "fguns:workbench";
            }
        });
        WORKBENCH_SERIALIZER = RecipeSerializer.register("fguns:workbench", new WorkbenchRecipeSerializer(WorkbenchRecipe::new));


        Sounds.init();

        //config
        CustomGunConfigHandler.handleConfig();
        ConfigRegistry.registerServerConfig(CONFIG_HANDLER);
        CONFIG_HANDLER.handle();

        ServerPlayNetworking.registerGlobalReceiver(FlytreGuns.RELOAD_PACKET_ID, (server, player, handler, buf, responseSender) -> {
            server.execute(() -> GunItem.attemptEarlyReload(player));
        });

        ServerPlayNetworking.registerGlobalReceiver(FlytreGuns.FIRING_PATTERN_PACKET_ID, (server, player, handler, buf, responseSender) -> {
            server.execute(() -> GunItem.switchFiringPattern(player));
        });

        ServerPlayNetworking.registerGlobalReceiver(FlytreGuns.NEXT_RECIPE_PACKET_ID, (server, player, handler, buf, responseSender) -> {
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

        ServerPlayNetworking.registerGlobalReceiver(FlytreGuns.REQUEST_RECIPE_PACKET_ID, (server, player, handler, buf, responseSender) -> {
            server.execute(() -> {
                ScreenHandler screenHandler = player.currentScreenHandler;
                if (screenHandler instanceof WorkbenchScreenHandler) {
                    WorkbenchEntity entity = ((WorkbenchScreenHandler) screenHandler).getWorkbenchEntity();
                    if (entity != null) {
                        entity.sendRecipe(player);
                    }
                }
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(FlytreGuns.CRAFT_ITEM_PACKET_ITEM, (server, player, handler, buf, responseSender) -> {
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
                        }
                    }
                }
            });
        });
    }
}
