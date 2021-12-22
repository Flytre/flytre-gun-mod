package net.flytre.fguns;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.flytre.fguns.config.Config;
import net.flytre.fguns.config.CustomGunConfigHandler;
import net.flytre.fguns.entity.Bullet;
import net.flytre.fguns.gun.*;
import net.flytre.fguns.misc.Sounds;
import net.flytre.fguns.network.BulletVelocityS2CPacket;
import net.flytre.fguns.network.CraftGunC2SPacket;
import net.flytre.fguns.network.GunActionC2SPacket;
import net.flytre.fguns.workbench.WorkbenchBlock;
import net.flytre.fguns.workbench.WorkbenchRecipe;
import net.flytre.fguns.workbench.WorkbenchRecipeSerializer;
import net.flytre.fguns.workbench.WorkbenchScreenHandler;
import net.flytre.flytre_lib.api.base.util.BakeHelper;
import net.flytre.flytre_lib.api.base.util.PacketUtils;
import net.flytre.flytre_lib.api.config.ConfigHandler;
import net.flytre.flytre_lib.api.config.ConfigRegistry;
import net.minecraft.block.Material;
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

    public static final AbstractGun LETHAL_MARK;


    public static final ItemGroup TAB = FabricItemGroupBuilder.build(
            new Identifier("fguns", "all"),
            () -> new ItemStack(FlytreGuns.LETHAL_MARK));

    public static final Item BASIC_AMMO = new Item(new Item.Settings().group(FlytreGuns.TAB));
    public static final Item SHOTGUN_SHELL = new Item(new Item.Settings().group(FlytreGuns.TAB));
    public static final Item SNIPER_AMMO = new Item(new Item.Settings().group(FlytreGuns.TAB));
    public static final Item ROCKET_AMMO = new Item(new Item.Settings().group(FlytreGuns.TAB));
    public static final Item ENERGY_CELL = new Item(new Item.Settings().group(FlytreGuns.TAB));

    public static final AbstractGun BEAMER = new Pistol.Builder().damage(12).armorPen(.5).rps(1).dropoff(0.02).spray(3).velocity(1.2f).clipSize(6).reloadTime(3.0).scope(true).scopeZoomAmount(4).horizontalRecoil(1.4).build();
    public static final AbstractGun LASER_SPEED = new Pistol.Builder().damage(4).armorPen(.2).rps(4).dropoff(0.03).spray(6).velocity(0.9f).clipSize(20).reloadTime(1.4).build();


    public static final AbstractGun HUNTER = new Rifle.Builder().damage(5).armorPen(.4).rps(6).dropoff(0.04).spray(9).velocity(0.9f).clipSize(25).reloadTime(3.2).build();
    public static final AbstractGun BLASTER = new Rifle.Builder().damage(5).armorPen(.5).rps(4).dropoff(0.03).spray(7).velocity(0.9f).clipSize(30).reloadTime(3.0).build();
    public static final AbstractGun TRIFORCE = new Rifle.Builder().damage(6).armorPen(.4).rps(20).dropoff(0.01).spray(3).velocity(1.1f).clipSize(3).reloadTime(1.25).build();


    public static final AbstractGun RAPIDSTRIKE = new Smg.Builder().damage(4).armorPen(.3).rps(10).dropoff(0.06).spray(13).velocity(1.2f).clipSize(40).reloadTime(2.5).build();
    public static final AbstractGun SHOTGUN = new Shotgun.Builder().damage(4).armorPen(.4).rps(3).dropoff(0.06).spray(16).velocity(0.5f).clipSize(2).reloadTime(2).build();
    public static final AbstractGun FLAME_FLASH = new FlareGun.Builder().rps(1).spray(1).clipSize(3).reloadTime(2.1).build();
    public static final AbstractGun SLIMER = new SlimeGun.Builder().damage(4).armorPen(0.2).dropoff(0.01).spray(0).velocity(1.5f).clipSize(10).reloadTime(2.0).build();
    public static final AbstractGun ROCKET_LAUNCHER = new RocketLauncher.Builder().rps(0.33).spray(0).clipSize(2).reloadTime(2.5).build();


    public static final AbstractGun SEEKER = new Sniper.Builder().damage(13).armorPen(.75).rps(.2).clipSize(5).reloadTime(12).build();
    public static final AbstractGun NIGHTMARE = new Sniper.Builder().damage(22).armorPen(.90).rps(0.11).clipSize(1).reloadTime(9).build();
    public static final AbstractGun VOLT = new Shocker.Builder().damage(10).armorPen(0.4).rps(1.0).dropoff(0).spray(3).clipSize(3).reloadTime(1.25).build();
    public static final AbstractGun MINIGUN = new Minigun.Builder().damage(5).armorPen(0.4).rps(20).dropoff(0.04).spray(20).clipSize(80).reloadTime(6.5).build();

    public static final Item MYSTERY_GUN = new MysteryGun();


    public static final EntityType<Bullet> BULLET = Registry.register(
            Registry.ENTITY_TYPE,
            new Identifier("fguns", "bullet"),
            EntityType.Builder.<Bullet>create(Bullet::new, SpawnGroup.MISC).setDimensions(0.5F, 0.2F).maxTrackingRange(4).trackingTickInterval(20).build("fguns:bullet"));

    //Workbench
    public static final WorkbenchBlock WORKBENCH = new WorkbenchBlock(FabricBlockSettings.of(Material.METAL).strength(3.0f));
    public static ScreenHandlerType<WorkbenchScreenHandler> WORKBENCH_SCREEN_HANDLER;
    public static RecipeType<WorkbenchRecipe> WORKBENCH_RECIPE;
    public static WorkbenchRecipeSerializer WORKBENCH_SERIALIZER;


    public static ConfigHandler<Config> CONFIG = new ConfigHandler<>(new Config(), "flytres_gun_mod");
    public static DefaultParticleType FLARE_PARTICLES;

    static {
        LETHAL_MARK = new Pistol.Builder().damage(5).armorPen(.4).rps(2).dropoff(0.02).spray(3).clipSize(10).reloadTime(1.4).build();
    }

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
        WORKBENCH_SCREEN_HANDLER = ScreenHandlerRegistry.registerSimple(new Identifier("fguns:workbench"), WorkbenchScreenHandler::new);
        WORKBENCH_RECIPE = Registry.register(Registry.RECIPE_TYPE, new Identifier("fguns:workbench"), new RecipeType<WorkbenchRecipe>() {
            public String toString() {
                return "fguns:workbench";
            }
        });
        WORKBENCH_SERIALIZER = RecipeSerializer.register("fguns:workbench", new WorkbenchRecipeSerializer(WorkbenchRecipe::new));


        CustomGunConfigHandler.handleConfig();
        ConfigRegistry.registerServerConfig(CONFIG);
        CONFIG.handle();

        FLARE_PARTICLES = Registry.register(Registry.PARTICLE_TYPE, "fguns:flare", new DefaultParticleType(true) {
        });

        Sounds.init();

        BakeHelper.fullBake("fguns", "", null);

        PacketUtils.registerS2CPacket(BulletVelocityS2CPacket.class, BulletVelocityS2CPacket::new);
        PacketUtils.registerC2SPacket(GunActionC2SPacket.class, GunActionC2SPacket::new);
        PacketUtils.registerC2SPacket(CraftGunC2SPacket.class,CraftGunC2SPacket::new);
    }
}
