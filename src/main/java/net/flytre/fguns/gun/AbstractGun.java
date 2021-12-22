package net.flytre.fguns.gun;

import com.google.gson.annotations.SerializedName;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.flytre.fguns.FlytreGuns;
import net.flytre.fguns.config.Config;
import net.flytre.fguns.entity.Bullet;
import net.flytre.fguns.misc.Sounds;
import net.flytre.flytre_lib.api.base.util.InventoryUtils;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public abstract class AbstractGun extends Item {

    public static final List<AbstractGun> GUNS = new ArrayList<>();
    private static final Random RANDOM = new Random();
    private final double damage;
    private final double armorPen;
    private final double rps;
    private final double dropoff;
    private final int spray;
    private final float velocity;
    private final int clipSize;
    private final double reloadTime;
    private final BulletProperties bulletProperties;
    private final boolean scope;
    private final double scopeZoom;
    private final SoundEvent fireSound;
    private final Item ammoItem;
    private final double horizontalRecoil;
    private final double verticalRecoil;
    private String name;


    protected AbstractGun(Builder<?> builder) {

        super(new Settings().maxCount(1).group(FlytreGuns.TAB));
        this.damage = builder.damage;
        this.armorPen = builder.armorPen;
        this.rps = builder.rps;
        this.dropoff = builder.dropoff;
        this.spray = builder.spray;
        this.velocity = builder.velocity * 1.75f;
        this.clipSize = builder.clipSize;
        this.reloadTime = builder.reloadTime;
        this.bulletProperties = builder.bulletProperties;
        this.scope = builder.scope;
        this.scopeZoom = builder.scopeZoom;
        this.fireSound = builder.fireSound;
        this.ammoItem = builder.ammoItem == null ? FlytreGuns.BASIC_AMMO : builder.ammoItem;
        this.horizontalRecoil = builder.horizontalRecoil;
        this.verticalRecoil = builder.verticalRecoil;
        GUNS.add(this);
    }

    public static AbstractGun randomGun() {
        int size = GUNS.size();
        int item = RANDOM.nextInt(size); // In real life, the Random object should be rather more shared than this
        return GUNS.get(item);
    }

    public static void attemptEarlyReload(PlayerEntity player) {


        ItemStack stack = InventoryUtils.getHoldingStack(player, i -> i.getItem() instanceof AbstractGun);
        if (stack == null)
            return;
        AbstractGun gun = (AbstractGun) stack.getItem();


        GunNBTSerializer serializer = new GunNBTSerializer(stack.getOrCreateNbt(), gun);
        //reload!
        if (gun.hasAmmo(player) && serializer.clip != gun.getClipSize()) {
            serializer.reload = (int) (gun.getReloadTime() * 20);
            serializer.partialClip = serializer.clip;
            serializer.clip = 0;
            serializer.toTag(stack.getOrCreateNbt());
        }

    }

    public static void switchFiringPattern(ServerPlayerEntity player) {
        //get the gun
        ItemStack stack = InventoryUtils.getHoldingStack(player, i -> i.getItem() instanceof AbstractGun);
        if (stack == null)
            return;
        AbstractGun gun = (AbstractGun) stack.getItem();

        GunNBTSerializer serializer = new GunNBTSerializer(stack.getOrCreateNbt(), gun);
        serializer.mode = gun.getNextMode(serializer.mode);
        serializer.toTag(stack.getOrCreateNbt());
    }

    public static AbstractGun getRandomEquipmentGun() {
        List<AbstractGun> tier0 = List.of(FlytreGuns.LASER_SPEED, FlytreGuns.LETHAL_MARK);
        List<AbstractGun> tier1 = List.of(FlytreGuns.BEAMER, FlytreGuns.SLIMER);
        List<AbstractGun> tier2 = Collections.singletonList(FlytreGuns.SEEKER);
        List<AbstractGun> tier3 = List.of(FlytreGuns.NIGHTMARE, FlytreGuns.TRIFORCE);
        List<AbstractGun> tier4 = List.of(FlytreGuns.SHOTGUN, FlytreGuns.BLASTER, FlytreGuns.RAPIDSTRIKE);
        List<AbstractGun> tier5 = List.of(FlytreGuns.HUNTER, FlytreGuns.ROCKET_LAUNCHER, FlytreGuns.VOLT, FlytreGuns.MINIGUN);
        double r = Math.random();
        if (r > FlytreGuns.CONFIG.getConfig().mobGunSpawnChance)
            return null;
        r = Math.random();
        if (r < 0.56)
            return randomElement(tier0);
        if (r < 0.70)
            return randomElement(tier1);
        if (r < 0.82)
            return randomElement(tier2);
        if (r < 0.89)
            return randomElement(tier3);
        if (r < 0.95)
            return randomElement(tier4);
        return randomElement(tier5);
    }

    private static <T> T randomElement(List<T> list) {
        return list.get((int) (Math.random() * list.size()));
    }

    public int getNextMode(int current) {
        return current + 1 > 2 ? 0 : 2;
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {

        if (!(entity instanceof PlayerEntity))
            return;

        PlayerEntity player = (PlayerEntity) entity;

        GunNBTSerializer serializer = new GunNBTSerializer(stack.getOrCreateNbt(), this);
        if (serializer.reload != -1 || serializer.clip == 0) { //If you should reload

            if (serializer.reload >= 0) {
                double ammoCalc = (double) getClipSize() / ((int) (getReloadTime() * 20));
                ammoCalc *= (int) (getReloadTime() * 20) - serializer.reload;
                while (serializer.partialClip < Math.floor(ammoCalc)) {
                    if (hasAmmo(player)) {
                        removeAmmo(player);
                        serializer.partialClip++;
                    } else {
                        serializer.clip = serializer.partialClip;
                        serializer.reload = -1;
                        serializer.partialClip = 0;
                        break;
                    }
                }
            }

            if (serializer.reload == 0) {
                serializer.clip = serializer.partialClip;
                serializer.reload = -1;
                serializer.partialClip = 0;
            } else if (serializer.reload != -1) {
                serializer.reload--;
            } else if (hasAmmo(player) && serializer.clip < getClipSize()) {
                serializer.reload = (int) (getReloadTime() * 20);
                serializer.partialClip = serializer.clip;
                serializer.clip = 0;
            }

        }

        if (serializer.cooldown > 0)
            serializer.cooldown--;

        serializer.fallFlying = player.isFallFlying();

        serializer.toTag(stack.getOrCreateNbt());

    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        if (remainingUseTicks == 100)
            action(world, user, user.getMainHandStack() == stack ? Hand.MAIN_HAND : Hand.OFF_HAND, null, true);
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 100;
    }

    public TypedActionResult<ItemStack> action(World world, LivingEntity hitman, Hand hand, LivingEntity target, boolean semi) {

        ItemStack stack = hitman.getStackInHand(hand);

        if (hitman.isFallFlying() && hitman instanceof PlayerEntity)
            return TypedActionResult.pass(stack);

        GunNBTSerializer serializer = new GunNBTSerializer(stack.getOrCreateNbt(), this);
        int maxCd = (int) (20 / rps - 1);

        if (world != null && !world.isClient) {
            if (serializer.clip > 0 && serializer.reload == -1 && serializer.cooldown <= 0) {
                fireBullet(world, hitman, hand, target, semi);
                playSound(world, hitman);
                serializer.cooldown = maxCd;
                serializer.clip--;
            } else if (serializer.clip == 0 && serializer.cooldown == 0) {
                world.playSound(
                        null,
                        hitman.getBlockPos(),
                        Sounds.DRY_FIRE_EVENT,
                        SoundCategory.PLAYERS,
                        1f,
                        1f
                );
                serializer.cooldown = maxCd;
            }
            serializer.toTag(stack.getOrCreateNbt());
            return TypedActionResult.pass(stack);
        }
        serializer.toTag(stack.getOrCreateNbt());
        return TypedActionResult.pass(hitman.getStackInHand(hand));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        GunNBTSerializer serializer = new GunNBTSerializer(stack.getOrCreateNbt(), this);
        if (serializer.mode == 2) {
            user.setCurrentHand(hand);
            return TypedActionResult.pass(stack);
        } else
            return action(world, user, hand, null, false);
    }

    public void playSound(World world, LivingEntity user) {
        world.playSound(
                null,
                user.getBlockPos(),
                getFireSound(),
                SoundCategory.PLAYERS,
                1f,
                1f
        );
    }

    protected void removeAmmo(PlayerEntity playerEntity) {
        if (!playerEntity.isCreative()) {
            for (ItemStack item : playerEntity.getInventory().main) {
                if (item.getItem() == ammoItem) {
                    item.decrement(1);
                    break;
                }
            }
        }
    }

    public void fireBullet(World world, LivingEntity user, Hand hand, @Nullable LivingEntity target, boolean semi) {
        Bullet bulletEntity = new Bullet(user, world);
        bulletEntity.setPos(user.getX(), user.getEyeY(), user.getZ());
        Vec3d vel = getRotationVector(user.getPitch(), user.getYaw()).multiply(-0.03 * horizontalRecoil, -0.023 * verticalRecoil, -0.03 * horizontalRecoil);
        user.addVelocity(vel.x, vel.y, vel.z);
        user.velocityModified = true;
        setBulletVector(bulletEntity, user, target, semi);
        bulletEntity.setInitialPos(new Vec3d(user.getX(), user.getEyeY(), user.getZ()));
        Config config = FlytreGuns.CONFIG.getConfig();
        bulletEntity.setProperties(user instanceof PlayerEntity ? damage * config.playerDamageModifier : damage * config.mobDamageModifier, armorPen, dropoff);

        if (EnchantmentHelper.get(user.getStackInHand(hand)).getOrDefault(Enchantments.FLAME, 0) > 0)
            bulletEntity.setFireTicks(1000);

        bulletSetup(world, user, hand, bulletEntity);
        world.spawnEntity(bulletEntity);
    }

    public void bulletSetup(World world, LivingEntity user, Hand hand, Bullet bullet) {
        bullet.setVelocity(bullet.getVelocity().multiply(velocity));
        if (getBulletProperties() != BulletProperties.NONE)
            bullet.setProperties(getBulletProperties());
    }

    public double getDamage() {
        return damage;
    }

    public double getArmorPen() {
        return armorPen;
    }

    public double getRps() {
        return rps;
    }

    public double getDropoff() {
        return dropoff;
    }

    public int getSpray() {
        return spray;
    }

    public float getVelocity() {
        return velocity;
    }

    public int getClipSize() {
        return clipSize;
    }

    public double getReloadTime() {
        return reloadTime;
    }

    public BulletProperties getBulletProperties() {
        return bulletProperties;
    }

    public boolean hasScope() {
        return scope;
    }

    public double getScopeZoom() {
        return scopeZoom;
    }

    public SoundEvent getFireSound() {
        return fireSound;
    }

    public Item getAmmoItem() {
        return ammoItem;
    }

    public double getHorizontalRecoil() {
        return horizontalRecoil;
    }

    public double getVerticalRecoil() {
        return verticalRecoil;
    }

    @Environment(EnvType.CLIENT)
    public Text getName() {
        return new TranslatableText(name == null ? this.getTranslationKey() : name);
    }

    public void setName(String name) {
        this.name = name;
    }

    protected String getOrCreateTranslationKey() {
        if (name != null)
            return name;
        return super.getOrCreateTranslationKey();
    }

    public boolean hasAmmo(PlayerEntity player) {
        if (!player.isCreative()) {
            for (ItemStack item : player.getInventory().main) {
                if (!item.isEmpty() && item.getItem() == ammoItem) {
                    return true;
                }
            }
        }
        return player.isCreative();
    }

    public int getEffectiveSpray(LivingEntity user) {
        return spray;
    }


    public void setBulletVector(Bullet bullet, LivingEntity user, LivingEntity target, boolean semi) {
        float tSpray = getEffectiveSpray(user);
        if (semi)
            tSpray *= 2.0 / 3.0;

        if (user.isSneaking())
            tSpray = Math.max(0, Math.min((tSpray * 2) / 5, tSpray - 5));
        if (user instanceof PlayerEntity) {
            float f = -MathHelper.sin(user.getYaw() * 0.017453292F) * MathHelper.cos(user.getPitch() * 0.017453292F);
            float g = -MathHelper.sin(user.getPitch() * 0.017453292F);
            float h = MathHelper.cos(user.getYaw() * 0.017453292F) * MathHelper.cos(user.getPitch() * 0.017453292F);
            bullet.setVelocity(f, g, h, 3.0F, tSpray);
        } else {
            double d = target.getX() - user.getX();
            double e = target.getBodyY(0.33333D) - bullet.getY();
            double f = target.getZ() - user.getZ();
            double g = MathHelper.sqrt((float) (d * d + f * f));
            bullet.setVelocity(d, e + g * 0.03D, f, 3.0F, tSpray);
        }
    }

    protected Vec3d getRotationVector(float pitch, float yaw) {
        float f = pitch * 0.017453292F;
        float g = -yaw * 0.017453292F;
        float h = MathHelper.cos(g);
        float i = MathHelper.sin(g);
        float j = MathHelper.cos(f);
        float k = MathHelper.sin(f);
        return new Vec3d(i * j, -k, h * j);
    }

    @Environment(EnvType.CLIENT)
    public Text getDamageLine() {
        return new TranslatableText("text.fguns.tooltip.damage", String.format("%.1f", getDamage()));
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {

        if (!(stack.getItem() instanceof AbstractGun))
            return;

        AbstractGun g = (AbstractGun) stack.getItem();

        tooltip.add(getDamageLine());

        int fireSpeed = (20 / (int) (20 / g.getRps()));
        tooltip.add(new TranslatableText("text.fguns.tooltip.rps", fireSpeed == 0 ? String.format("%.1f", g.getRps()) : fireSpeed));

        tooltip.add(new TranslatableText("text.fguns.tooltip.velocity", g.getVelocity() * 100f));

        if (g.getArmorPen() > 0)
            tooltip.add(new TranslatableText("text.fguns.tooltip.armor_pierce", (int) (g.getArmorPen() * 100)));

        if (g.getDropoff() > 0)
            if (g.getDropoff() > 0)
                tooltip.add(new TranslatableText("text.fguns.tooltip.dropoff", (int) (100 * (g.getDropoff() * 100)) / 100));
            else if (g.getDropoff() < 0)
                tooltip.add(new TranslatableText("text.fguns.tooltip.dropoff.increase", ((int) (100 * (Math.abs(g.getDropoff()) * 100))) / 100));

        tooltip.add(new LiteralText("§7Ammo: §c").append(new TranslatableText(getAmmoItem().getTranslationKey())));

        super.appendTooltip(stack, world, tooltip, context);
    }


    @Environment(EnvType.CLIENT)
    public List<Text> sidebarInfo() {
        List<Text> result = new ArrayList<>();
        result.add(getDamageLine());

        int fireSpeed = (20 / (int) (20 / getRps()));
        result.add(new TranslatableText("text.fguns.tooltip.rps", fireSpeed == 0 ? String.format("%.1f", getRps()) : fireSpeed));
        result.add(new TranslatableText("text.fguns.tooltip.velocity", getVelocity() * 100f));

        if (getArmorPen() > 0)
            result.add(new TranslatableText("text.fguns.tooltip.armor_pierce", (int) (getArmorPen() * 100)));

        if (getDropoff() > 0)
            if (getDropoff() > 0)
                result.add(new TranslatableText("text.fguns.tooltip.dropoff", (int) (100 * (getDropoff() * 100)) / 100));
            else if (getDropoff() < 0)
                result.add(new TranslatableText("text.fguns.tooltip.dropoff.increase", ((int) (100 * (Math.abs(getDropoff()) * 100))) / 100));

        result.add(new TranslatableText("text.fguns.tooltip.clip", clipSize, reloadTime));

        return result;
    }

    public static class GunNBTSerializer {
        public int clip;
        public int mode;
        public int reload;
        public int partialClip;
        public int cooldown;
        private boolean fallFlying;

        public GunNBTSerializer(NbtCompound tag, AbstractGun gun) {
            this.clip = tag.contains("clip") ? tag.getInt("clip") : gun.getClipSize();
            this.reload = tag.contains("reload") ? tag.getInt("reload") : -1;
            this.cooldown = tag.contains("cooldown") ? tag.getInt("cooldown") : 0;
            this.mode = tag.contains("mode") ? tag.getInt("mode") : 0;
            this.partialClip = tag.contains("partialClip") ? tag.getInt("partialClip") : 0;
            this.fallFlying = tag.contains("fallFlying") && tag.getBoolean("fallFlying");
        }

        public boolean shouldRenderCustomPose() {
            return reload != -1 || fallFlying;
        }


        public void toTag(NbtCompound tag) {
            tag.putInt("clip", clip);
            tag.putInt("reload", reload);
            tag.putInt("cooldown", cooldown);
            tag.putInt("mode", mode);
            tag.putInt("partialClip", partialClip);
            tag.putBoolean("fallFlying", fallFlying);
        }
    }


    public abstract static class Builder<T extends Builder<T>> {

        @SerializedName("bullet_properties")
        protected BulletProperties bulletProperties;
        protected double damage = 1.0;
        @SerializedName("armor_penetration")
        protected double armorPen = 0;
        protected double rps = 1;
        protected double dropoff = 0;
        protected int spray = 0;
        protected float velocity = 1.0f;
        @SerializedName("clip_size")
        protected int clipSize = 10;
        @SerializedName("reload_time")
        protected double reloadTime = 1.0;
        protected boolean scope = true;
        @SerializedName("scope_zoom")
        protected double scopeZoom = 5;
        @SerializedName("horizontal_recoil")
        protected double horizontalRecoil = 1;
        @SerializedName("vertical_recoil")
        protected double verticalRecoil = 0.3;
        protected SoundEvent fireSound = Sounds.PISTOL_FIRE_EVENT;
        protected Item ammoItem = FlytreGuns.BASIC_AMMO;

        public Builder() {
            this.bulletProperties = BulletProperties.NONE;
        }

        public T damage(double damage) {
            this.damage = damage;
            return self();
        }

        public T armorPen(double armorPen) {
            this.armorPen = armorPen;
            return self();
        }

        public T rps(double rps) {
            this.rps = rps;
            return self();
        }

        public T dropoff(double dropoff) {
            this.dropoff = dropoff;
            return self();
        }

        public T spray(int spray) {
            this.spray = spray;
            return self();
        }

        public T velocity(float velocity) {
            this.velocity = velocity;
            return self();
        }

        public T clipSize(int clipSize) {
            this.clipSize = clipSize;
            return self();
        }

        public T reloadTime(double reloadTime) {
            this.reloadTime = reloadTime;
            return self();
        }

        public T scope(boolean scope) {
            this.scope = scope;
            return self();
        }

        public T scopeZoomAmount(double scopeZoom) {
            this.scopeZoom = scopeZoom;
            return self();
        }

        public T sound(SoundEvent fireSound) {
            this.fireSound = fireSound;
            return self();
        }

        public T ammo(Item ammoItem) {
            this.ammoItem = ammoItem;
            return self();
        }

        public T horizontalRecoil(double horizontalRecoil) {
            this.horizontalRecoil = horizontalRecoil;
            return self();
        }

        public T verticalRecoil(double verticalRecoil) {
            this.verticalRecoil = verticalRecoil;
            return self();
        }

        protected abstract T self();

        public abstract AbstractGun build();
    }
}
