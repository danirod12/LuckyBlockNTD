package com.github.danirod12.luckyblock.api.customitem;

import com.cryptomorin.xseries.XBlock;
import com.cryptomorin.xseries.XMaterial;
import com.github.danirod12.luckyblock.api.LuckyBlockAPI;
import com.github.danirod12.luckyblock.api.event.CustomItemAddedEvent;
import com.github.danirod12.luckyblock.api.event.CustomItemFactoryReloadEvent;
import com.github.danirod12.luckyblock.api.util.Config;
import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.utils.MinecraftVersion;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.entity.WitherSkull;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.stream.Stream;

// TODO rework
public class CustomItemFactory {

    public static final String TAG_IDENTIFIER_NAME = "bekker_item_identifier";
    public static final String TAG_LUCKYBLOCK_TYPE = "luckyblock_type";
    private static final Collection<BekkerItemStack> STORAGE = new ArrayList<>();

    public static boolean chillyPants = false, rageArmor = false;
    public static boolean solid;
    public static int rageArmorPercentage;

    public static void register(BekkerItemStack item) {
        BekkerItemStack origin = fetchCustomItem(item);
        if (origin != null) {
            STORAGE.remove(origin);
        }
        STORAGE.add(item);
        LuckyBlockAPI.getLogger().log(Level.INFO, "Registered a new custom item - "
                + item.getClass().getSimpleName() + " (" + item.getEvents().size() + " events)");
        Bukkit.getPluginManager().callEvent(new CustomItemAddedEvent(item));
    }

    private static void register(BekkerItemStackBuilder builder) {
        if (!builder.getIdentifier().getTagValue().split("-")[0]
                .equalsIgnoreCase(LuckyBlockAPI.getInstance().getName())) {
            throw new UnsupportedOperationException("Only system items can be registered using this method.");
        }
        BekkerItemStack stack = builder.build();
        if (stack != null) {
            STORAGE.add(stack);
        }
    }

    public static boolean compare(ItemStack item, String identifier) {
        return compare(item, TAG_IDENTIFIER_NAME, identifier);
    }

    public static boolean compare(ItemStack item, String tagName, String identifier) {
        String id = parseValue(item, tagName);
        return id != null && id.equalsIgnoreCase(identifier);
    }

    public static String parseValue(ItemStack item, String tagName) {
        //TODO wrapper
        if (item == null || item.getType().name().contains("AIR") || item.getAmount() == 0) {
            return null;
        }
        if (!NBT.get(item, nbt -> (Boolean) nbt.hasTag(tagName))) {
            return null;
        }
        return NBT.get(item, nbt -> (String) nbt.getString(tagName));
    }

    public static BekkerItemStack fetchCustomItem(ItemStack item) {
        //TODO wrapper
        String identifier = parseValue(item, CustomItemFactory.TAG_IDENTIFIER_NAME);
        if (identifier == null) {
            return null;
        }
        return fetchCustomItem(identifier);
    }

    public static void loadSystem(Map<String, String> messages) {
        STORAGE.clear();

        Config customItems = new Config(LuckyBlockAPI.getInstance(),
                "configuration.other", null, "custom_items");
        customItems.copyMissedFields(added -> LuckyBlockAPI.getLogger().log(Level.INFO,
                "A new custom item here (" + added + "). §aEnabling!"));

        // check if new items is missed
        if (customItems.getBoolean("magic_wool.enabled")) {
            try {
                register(new BekkerItemStackBuilder(XMaterial.WHITE_WOOL.parseItem())
                        .addUnsafeEnchantment(Enchantment.DURABILITY, 1).setSerialID("magic_wool")
                        .hideEnchantments()
                        .setName(messages.get("magic_wool"))
                        .registerEvent(ItemEvent.PLACE, n -> new BukkitRunnable() {
                            final Block block = n.getBlock();
                            int rounds = ThreadLocalRandom.current().nextInt(5, 15);
                            final XMaterial[] materials = Stream.of(XMaterial.values())
                                    .filter(xm -> xm.name().endsWith("_WOOL")).toArray(XMaterial[]::new);

                            @Override
                            public void run() {
                                if (rounds < 0 || !block.getType().name().contains("WOOL")) {
                                    cancel();
                                    return;
                                }

                                XBlock.setType(block, materials[ThreadLocalRandom.current().nextInt(materials.length)]);
                                rounds--;
                            }
                        }.runTaskTimer(LuckyBlockAPI.getInstance(), 2L, 5L)));
            } catch (Throwable th) {
                th.printStackTrace();
            }
        }
        if (customItems.getBoolean("sword_of_justice.enabled")) {
            try {
                double h = customItems.get().getDouble("sword_of_justice.heal");
                if (h <= 0) {
                    h = 1;
                }
                final double heal = h;
                register(new BekkerItemStackBuilder(Material.IRON_SWORD)
                        .addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 2)
                        .setSerialID("sword_of_justice")
                        .setName(messages.get("sword_of_justice"))
                        .registerEvent(ItemEvent.HIT, n -> {
                            if (n.getType() != HitEvent.Type.DAMAGER) {
                                return;
                            }
                            final Player player = (Player) n.getDamager();
                            player.setHealth(Math.min(player.getMaxHealth(), heal + player.getHealth()));
                        }));
            } catch (Throwable th) {
                th.printStackTrace();
            }
        }
        if (customItems.getBoolean("axe_of_perun.enabled")) {
            try {
                double damage = Math.max(.1D, customItems.get().getDouble("axe_of_perun.damage"));
                register(new BekkerItemStackBuilder(Material.DIAMOND_AXE)
                        .addUnsafeEnchantment(Enchantment.DURABILITY, 1)
                        .setSerialID("axe_of_perun")
                        .setName(messages.get("axe_of_perun"))
                        .registerEvent(ItemEvent.HIT, n -> {
                            if (n.getType() != HitEvent.Type.DAMAGER || !(n.getVictim() instanceof Damageable)) {
                                return;
                            }
                            n.getVictim().getWorld().strikeLightningEffect(n.getVictim().getLocation());
                            ((Damageable) n.getVictim()).damage(damage);
                        }));
            } catch (Throwable th) {
                th.printStackTrace();
            }
        }
        if (customItems.getBoolean("mystery_meat.enabled")) {
            try {
                register(new BekkerItemStackBuilder(XMaterial.BEEF.parseItem())
                        .addUnsafeEnchantment(Enchantment.DURABILITY, 1)
                        .hideEnchantments().setSerialID("mystery_meat")
                        .setName(messages.get("mystery_meat"))
                        .registerEvent(ItemEvent.CONSUME, n -> {

                            final Player player = n.getPlayer();
                            final PotionEffectType type = PotionEffectType.values()[ThreadLocalRandom.current()
                                    .nextInt(PotionEffectType.values().length)];
                            try {
                                player.addPotionEffect(new PotionEffect(type, ThreadLocalRandom.current()
                                        .nextInt(5, 45) * 20, 1));
                            } catch (Exception ex) {
                                LuckyBlockAPI.getLogger().log(Level.WARNING, " > PotionEffectType - "
                                        + type + ", " + ex.getLocalizedMessage());
                                ex.printStackTrace();
                            }
                        }));
            } catch (Throwable th) {
                th.printStackTrace();
            }
        }
        if (customItems.getBoolean("chilly_pants.enabled")) {
            solid = customItems.get().getBoolean("chilly_pants.only_solid");
            try {
                register(new BekkerItemStackBuilder(Material.LEATHER_LEGGINGS)
                        .setSerialID("chilly_pants")
                        .setName(messages.get("chilly_pants")));
                chillyPants = true;
            } catch (Throwable th) {
                th.printStackTrace();
            }
        }
        if (customItems.getBoolean("rage_armor.enabled")) {
            rageArmorPercentage = customItems.get().getInt("rage_armor.percentage");
            try {
                register(new BekkerItemStackBuilder(Material.LEATHER_LEGGINGS)
                        .setSerialID("rage_armor_leggings")
                        .setName(messages.get("rage_armor")));
                register(new BekkerItemStackBuilder(Material.LEATHER_BOOTS)
                        .setSerialID("rage_armor_boots")
                        .setName(messages.get("rage_armor")));
                register(new BekkerItemStackBuilder(Material.LEATHER_CHESTPLATE)
                        .setSerialID("rage_armor_chestplate")
                        .setName(messages.get("rage_armor")));
                register(new BekkerItemStackBuilder(Material.LEATHER_HELMET)
                        .setSerialID("rage_armor_helmet")
                        .setName(messages.get("rage_armor")));
                rageArmor = true;
            } catch (Throwable th) {
                th.printStackTrace();
            }
        }
        if (customItems.getBoolean("wither_blast_rod.enabled")) {
            final float witherSkullYield = (float) customItems.getDouble("wither_blast_rod.yield");
            try {
                register(new BekkerItemStackBuilder(Material.BLAZE_ROD)
                        .setSerialID("wither_blast_rod")
                        .addUnsafeEnchantment(Enchantment.DURABILITY, 1)
                        .hideEnchantments()
                        .setName(messages.get("wither_blast_rod"))
                        .registerEvent(ItemEvent.INTERACT, event -> {
                            withdrawItem(event.getPlayer());
                            WitherSkull skull = event.getPlayer().launchProjectile(WitherSkull.class);
                            skull.setYield(witherSkullYield);
                        }));
            } catch (Throwable th) {
                th.printStackTrace();
            }
        }
        if (MinecraftVersion.isAtLeastVersion(MinecraftVersion.MC1_13_R1)
                && customItems.getBoolean("carrot_corrupter.enabled")) {
            try {
                register(new BekkerItemStackBuilder(Material.CARROT).addUnsafeEnchantment(Enchantment.DURABILITY, 1)
                        .hideEnchantments()
                        .setSerialID("carrot_corrupter")
                        .setName(messages.get("carrot_corrupter"))
                        .registerEvent(ItemEvent.HIT, n -> {
                            if (n.getType() != HitEvent.Type.DAMAGER) {
                                return;
                            }
                            if (!(n.getVictim() instanceof Player)) {
                                return;
                            }

                            final Player victim = (Player) n.getVictim();
                            List<Integer> slots = new ArrayList<>();
                            for (int slot = 0; slot < 9; slot++) {
                                if (!isEmpty(victim.getInventory().getItem(slot))) {
                                    slots.add(slot);
                                }
                            }
                            if (!slots.isEmpty()) {
                                victim.getInventory()
                                        .setItem(slots.get(ThreadLocalRandom.current().nextInt(slots.size())),
                                                new ItemStack(Material.CARROT));
                            } else {
                                victim.getInventory().setItem(ThreadLocalRandom.current().nextInt(9),
                                        new ItemStack(Material.CARROT));
                            }
                            withdrawItem((Player) n.getDamager());
                        }));
            } catch (Throwable th) {
                th.printStackTrace();
            }
        }

        final int internal;
        LuckyBlockAPI.getLogger().log(Level.INFO,
                "Loaded " + (internal = STORAGE.size()) + " internal custom items...");
        Bukkit.getPluginManager().callEvent(new CustomItemFactoryReloadEvent(new ArrayList<>(STORAGE),
                CustomItemFactoryReloadEvent.Action.PRELOAD));

        if (STORAGE.size() > internal) {
            LuckyBlockAPI.getLogger().log(Level.INFO,
                    "Loaded " + (STORAGE.size() - internal) + " external custom items...");
        }
        Bukkit.getPluginManager().callEvent(new CustomItemFactoryReloadEvent(new ArrayList<>(STORAGE),
                CustomItemFactoryReloadEvent.Action.LOADED));

        LuckyBlockAPI.getLogger().log(Level.INFO, "Loaded " + STORAGE.size() + " custom items... (Total)");
    }

    public static void withdrawItem(Player damager) {
        if (damager.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        PlayerInventory inventory = damager.getInventory();
        ItemStack stack = inventory.getItem(inventory.getHeldItemSlot());
        if (stack.getAmount() > 1) {
            stack.setAmount(stack.getAmount() - 1);
            inventory.setItem(inventory.getHeldItemSlot(), stack);
        } else {
            inventory.setItem(inventory.getHeldItemSlot(), null);
        }
    }

    public static boolean isEmpty(ItemStack stack) {
        return stack == null || stack.getType() == Material.AIR;
    }

//    public static void reloadSystem() throws Throwable {
//
//        ItemTag adapter = LBMain.getItemTagAdapter();
//        if(LuckyBlockAPI.getInstance().factory != null) {
//
//            new ArrayList<>(storage).stream().filter(item -> {
//
//                String identifier = adapter.getTagString(adapter.getTag(adapter.asNMSCopy(item)),
//                                                            CustomItemFactory.TAG_IDENTIFIER_NAME);
//                return identifier.split("-")[0].equalsIgnoreCase(LuckyBlockAPI.getInstance().getName());
//
//            }).forEach(n -> storage.remove(n));
//
//            loadSystem();
//
//        }
//
//    }

    public static BekkerItemStack fetchCustomItem(String string) {
        for (BekkerItemStack stack : STORAGE) {
            if (stack.equals(string)) {
                return stack;
            }
        }
        return null;
    }

    public static Collection<BekkerItemStack> copy() {
        return new ArrayList<>(STORAGE);
    }

    public static boolean isRageArmor(Player player) {
        PlayerInventory playerInventory = player.getInventory();
        return compare(playerInventory.getHelmet(), "ntdluckyblock-rage_armor_helmet")
                && compare(playerInventory.getLeggings(), "ntdluckyblock-rage_armor_leggings")
                && compare(playerInventory.getChestplate(), "ntdluckyblock-rage_armor_chestplate")
                && compare(playerInventory.getBoots(), "ntdluckyblock-rage_armor_boots");
    }
}
