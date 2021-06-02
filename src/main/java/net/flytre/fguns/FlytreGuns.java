package net.flytre.fguns;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.flytre.fguns.config.Config;
import net.flytre.fguns.config.CustomGunConfigHandler;
import net.flytre.fguns.entity.Bullet;
import net.flytre.fguns.gun.*;
import net.flytre.fguns.workbench.*;
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
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class FlytreGuns implements ModInitializer {


    public static final AbstractGun LETHAL_MARK = new Pistol.Builder().setDamage(5).setArmorPen(.4).setRps(2).setDropoff(0.02).setSpray(3).setRange(30).setClipSize(10).setReloadTime(1.4).build();
    public static final ItemGroup TAB = FabricItemGroupBuilder.build(
            new Identifier("fguns", "all"),
            () -> new ItemStack(FlytreGuns.LETHAL_MARK));
    //Item
    public static final Item BASIC_AMMO = new Item(new Item.Settings().group(FlytreGuns.TAB));
    public static final Item SHOTGUN_SHELL = new Item(new Item.Settings().group(FlytreGuns.TAB));
    public static final Item SNIPER_AMMO = new Item(new Item.Settings().group(FlytreGuns.TAB));
    public static final Item ROCKET_AMMO = new Item(new Item.Settings().group(FlytreGuns.TAB));
    public static final Item ENERGY_CELL = new Item(new Item.Settings().group(FlytreGuns.TAB));
    public static final AbstractGun BEAMER = new Pistol.Builder().setDamage(12).setArmorPen(.5).setRps(1).setDropoff(0.02).setSpray(3).setRange(30).setClipSize(6).setReloadTime(3.0).setScope(true).setScopeZoom(4).setHorizontalRecoil(1.4).build();
    public static final AbstractGun LASER_SPEED = new Pistol.Builder().setDamage(4).setArmorPen(.2).setRps(4).setDropoff(0.03).setSpray(6).setRange(25).setClipSize(20).setReloadTime(1.4).build();

    public static final AbstractGun HUNTER = new Rifle.Builder().setDamage(5).setArmorPen(.4).setRps(6).setDropoff(0.04).setSpray(9).setRange(25).setClipSize(25).setReloadTime(3.2).build();
    public static final AbstractGun BLASTER = new Rifle.Builder().setDamage(5).setArmorPen(.5).setRps(4).setDropoff(0.03).setSpray(7).setRange(25).setClipSize(30).setReloadTime(3.0).build();
    public static final AbstractGun TRIFORCE = new Rifle.Builder().setDamage(6).setArmorPen(.4).setRps(20).setDropoff(0.01).setSpray(3).setRange(35).setClipSize(3).setReloadTime(1.25).build();

    public static final AbstractGun RAPIDSTRIKE = new Smg.Builder().setDamage(4).setArmorPen(.3).setRps(10).setDropoff(0.06).setSpray(13).setRange(20).setClipSize(40).setReloadTime(2.5).build();
    public static final AbstractGun SHOTGUN = new Shotgun.Builder().setDamage(4).setArmorPen(.4).setRps(3).setDropoff(0.06).setSpray(16).setRange(12).setClipSize(2).setReloadTime(2).build();
    public static final AbstractGun FLAME_FLASH = new FlareGun.Builder().setRps(1).setSpray(1).setRange(25).setClipSize(3).setReloadTime(2.1).build();
    public static final AbstractGun SLIMER = new SlimeGun.Builder().setDamage(4).setArmorPen(0.2).setRange(4).setDropoff(0.01).setSpray(0).setRange(40).setClipSize(10).setReloadTime(2.0).build();
    public static final AbstractGun ROCKET_LAUNCHER = new RocketLauncher.Builder().setRps(0.33).setSpray(0).setRange(40).setClipSize(2).setReloadTime(2.5).build();


    public static final AbstractGun SEEKER = new Sniper.Builder().setDamage(13).setArmorPen(.75).setRps(.2).setClipSize(5).setReloadTime(12).build();
    public static final AbstractGun NIGHTMARE = new Sniper.Builder().setDamage(22).setArmorPen(.90).setRps(0.11).setClipSize(1).setReloadTime(9).build();
    public static final AbstractGun VOLT = new Shocker.Builder().setDamage(10).setArmorPen(0.4).setRps(1.0).setDropoff(0).setSpray(3).setRange(30).setClipSize(3).setReloadTime(1.25).build();
    public static final AbstractGun MINIGUN = new Minigun.Builder().setDamage(5).setArmorPen(0.4).setRps(20).setDropoff(0.04).setSpray(20).setRange(30).setClipSize(80).setReloadTime(6.5).build();

    public static final Item MYSTERY_GUN = new MysteryGun();

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

    //MISC
    public static ConfigHandler<Config> CONFIG_HANDLER = new ConfigHandler<>(new Config(), "flytres_gun_mod");
    public static DefaultParticleType FLARE_PARTICLES;

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

        Registry.register(Registry.ITEM, new Identifier("fguns", "flame_flash"), FLAME_FLASH);


        Registry.register(Registry.BLOCK, new Identifier("fguns", "workbench"), WORKBENCH);
        Registry.register(Registry.ITEM, new Identifier("fguns", "workbench"), new BlockItem(WORKBENCH, new FabricItemSettings().group(TAB)));
        WORKBENCH_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier("fguns:workbench"), FabricBlockEntityTypeBuilder.create(WorkbenchEntity::new, WORKBENCH).build(null));
        WORKBENCH_SCREEN_HANDLER = ScreenHandlerRegistry.registerSimple(new Identifier("fguns:workbench"), WorkbenchScreenHandler::new);
        WORKBENCH_RECIPE = Registry.register(Registry.RECIPE_TYPE, new Identifier("fguns:workbench"), new RecipeType<WorkbenchRecipe>() {
            public String toString() {
                return "fguns:workbench";
            }
        });
        WORKBENCH_SERIALIZER = RecipeSerializer.register("fguns:workbench", new WorkbenchRecipeSerializer(WorkbenchRecipe::new));


        FLARE_PARTICLES = Registry.register(Registry.PARTICLE_TYPE, "fguns:flare", new DefaultParticleType(true) {
        });

        Sounds.init();

        //config
        CustomGunConfigHandler.handleConfig();
        ConfigRegistry.registerServerConfig(CONFIG_HANDLER);
        CONFIG_HANDLER.handle();


        Packets.init();
    }
}
