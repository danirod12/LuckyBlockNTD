package me.DenBeKKer.ntdLuckyBlock.engine.loader;

import me.DenBeKKer.ntdLuckyBlock.api.exception.DependencyNotFoundException;
import me.DenBeKKer.ntdLuckyBlock.api.loader.StringLoader;
import me.DenBeKKer.ntdLuckyBlock.api.model.LuckyDrop;
import me.DenBeKKer.ntdLuckyBlock.api.model.LuckyDropType;
import me.DenBeKKer.ntdLuckyBlock.api.model.SpecialDropType;
import me.DenBeKKer.ntdLuckyBlock.customitem.BekkerItemStack;
import me.DenBeKKer.ntdLuckyBlock.customitem.CustomItemFactory;
import me.DenBeKKer.ntdLuckyBlock.engine.LuckyBlockEngine;
import me.DenBeKKer.ntdLuckyBlock.engine.drop.*;
import me.DenBeKKer.ntdLuckyBlock.engine.drop.special.*;
import me.DenBeKKer.ntdLuckyBlock.hook.Hook;
import me.DenBeKKer.ntdLuckyBlock.hook.sk89q.WorldEditProvider;
import me.DenBeKKer.ntdLuckyBlock.util.Misc;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LegacyLoader implements StringLoader {

    private final LuckyBlockEngine engine;
    private final WorldEditProvider worldEditProvider;

    public LegacyLoader(LuckyBlockEngine engine, WorldEditProvider worldEditProvider) {
        this.engine = engine;
        this.worldEditProvider = worldEditProvider;
    }

    // TODO forward down?
    @Override
    public LuckyDrop deserialize(String drop) {
        String[] baseData = drop.split(" : ");
        LuckyDropType type = LuckyDropType.valueOf(baseData[0].toUpperCase());

        switch (type) {
            case COMMAND:
                return new CommandDrop(baseData[1]);
            case CONSOLE:
                return new ConsoleDrop(baseData[1]);
            case OPPED:
                return new OppedDrop(baseData[1]);
            case MESSAGE:
                return new MessageDrop(baseData[1]);
            case ENTITY: {
                int amount = 1;
                try {
                    amount = Integer.parseInt(baseData[2]);
                } catch (Exception ignored) {
                }
                if (amount < 1) {
                    amount = 1;
                }
                return new EntityDrop(EntityType.valueOf(baseData[1].toUpperCase()), amount);
            }
            case ITEM: {
                ItemStack item = new ItemStack(Material.valueOf(baseData[1].toUpperCase()),
                        Integer.parseInt(baseData[2]), (short) Integer.parseInt(baseData[3]));

                if (baseData.length >= 5 && !baseData[4].equals("null")) {
                    ItemMeta meta = item.getItemMeta();
                    assert meta != null;
                    meta.setDisplayName(Misc.setColors(baseData[4]));
                    item.setItemMeta(meta);
                }
                for (int index = 5; index < baseData.length - 1; index += 2) {
                    item.addUnsafeEnchantment(Enchantment.getByName(baseData[index].toUpperCase()),
                            Integer.parseInt(baseData[index + 1]));
                }

                return new ItemDrop(item);
            }
            case LUCKY_BLOCK_ITEM: {
                int amount = 1;
                try {
                    amount = Integer.parseInt(baseData[2]);
                } catch (Exception ignored) {
                }
                if (amount < 1) {
                    amount = 1;
                }
                if (baseData[1].equalsIgnoreCase("random")) {
                    return new RandomLuckyItemDrop(amount);
                }
                return new LuckyItemDrop(engine.get(baseData[1]), amount);
            }
            case CUSTOM_ITEM: {
                int amount = 1;
                try {
                    amount = Integer.parseInt(baseData[2]);
                } catch (Exception ignored) {
                }
                if (amount < 1) {
                    amount = 1;
                }
                BekkerItemStack item = CustomItemFactory.fetchCustomItem(baseData[1].contains("-")
                        ? baseData[1] : "ntdluckyblock-" + baseData[1]);
                if (item == null) {
                    throw new IllegalArgumentException("BekkerItemStack for name " + baseData[1] + " was not found");
                }
                ItemStack stack = item.clone();
                stack.setAmount(amount);
                return new ItemDrop(stack);
            }
            case SCHEMATIC: {
                if (!Hook.WorldEdit.isEnabled()) {
                    throw new DependencyNotFoundException("WorldEdit");
                }

                boolean setAsBlock;
                if (baseData[2].equalsIgnoreCase("block")) {
                    setAsBlock = true;
                } else if (baseData[2].equalsIgnoreCase("player")) {
                    setAsBlock = false;
                } else {
                    throw new IllegalArgumentException("Schematic format allow only player and block arguments");
                }
                return new SchematicDrop(getSchematicFile(baseData), setAsBlock,
                        baseData.length > 3 && baseData[3].equalsIgnoreCase("true"));
            }
            case SPECIAL: {
                SpecialDropType special = SpecialDropType.valueOf(baseData[1].toUpperCase());
                switch (special) {
                    case DIAMOND_COLUMN: {
                        List<Material> list = new ArrayList<>();
                        for (int i = 2; i < baseData.length; i++) {
                            list.add(Material.valueOf(baseData[i].toUpperCase()));
                        }
                        return new DiamondColumnSpecial(list);
                    }
                    case LIGHTNING:
                        return new LightningSpecial(apply(special, baseData[2]));
                    case PIG:
                        return new PigSpecial(apply(special, baseData[2]));
                    case WATER_BUCKET:
                        return new WaterBucketSpecial(apply(special, baseData[2]));
                    case EXPERIENCE_EXPLOSION:
                        return new ExperienceExplosionSpecial(apply(special, baseData[2]));
                    case TNT_EXPLOSION:
                        return new TntExplosionSpecial(apply(special, baseData[2]));
                    case TNT_COLUMN:
                        return new TntColumnSpecial(apply(special, baseData[2]));
                    default:
                        throw new RuntimeException(special.name() + " not implemented");
                }
            }
            default:
                throw new RuntimeException(type.name() + " not implemented");
        }
    }

    private File getSchematicFile(String[] baseData) {
        String fileName = baseData[1].endsWith(".schem") ? baseData[1] : baseData[1] + ".schem";

        File file = new File(this.worldEditProvider.getFolder(), fileName);
        if (!file.exists()) {
            fileName = baseData[1].endsWith(".schematic") ? baseData[1] : baseData[1] + ".schematic";
            file = new File(this.worldEditProvider.getFolder(), fileName);

            if (!file.exists()) {
                throw new IllegalArgumentException("Schematic " + file.getPath() + " not found");
            }
        }
        return file;
    }

    public int apply(SpecialDropType special, String value) {
        int plainValue;
        try {
            plainValue = Integer.parseInt(value);
        } catch (Exception ignored) {
            return special.defaultValue();
        }
        return plainValue < 1 ? special.defaultValue() : plainValue;
    }

    // TODO forward down?
    @Override
    public String serialize(LuckyDrop drop) {
        if (drop instanceof CommandDrop) {
            return "COMMAND : " + ((CommandDrop) drop).getCommand();
        } else if (drop instanceof ConsoleDrop) {
            return "CONSOLE : " + ((ConsoleDrop) drop).getCommand();
        } else if (drop instanceof EntityDrop) {
            return "ENTITY : " + ((EntityDrop) drop).getEntity() + " : " + ((EntityDrop) drop).getAmount();
        } else if (drop instanceof ItemDrop) {
            ItemStack stack = ((ItemDrop) drop).getItemCopy();
            StringBuilder item = new StringBuilder("ITEM : " + stack.getType().name() + " : "
                    + stack.getAmount() + " : " + stack.getDurability());
            ItemMeta meta = stack.getItemMeta();
            if (meta == null) {
                return item.toString();
            }
            if (meta.hasDisplayName() || !meta.getEnchants().isEmpty()) {
                item.append(" : ").append(meta.getDisplayName());
            }
            for (Map.Entry<Enchantment, Integer> entry : meta.getEnchants().entrySet()) {
                item.append(" : ").append(entry.getKey().getName()).append(" : ").append(entry.getValue());
            }
            return item.toString();
        } else if (drop instanceof LuckyItemDrop) {
            return "LUCKY_BLOCK_ITEM : " + ((LuckyItemDrop) drop).getKey().getKey()
                    + " : " + ((LuckyItemDrop) drop).getAmount();
        } else if (drop instanceof MessageDrop) {
            return "MESSAGE : " + ((MessageDrop) drop).getMessage();
        } else if (drop instanceof OppedDrop) {
            return "OPPED : " + ((OppedDrop) drop).getCommand();
        } else if (drop instanceof RandomLuckyItemDrop) {
            return "LUCKY_BLOCK_ITEM : RANDOM : " + ((RandomLuckyItemDrop) drop).getAmount();
        } else if (drop instanceof SchematicDrop) {
            return "SCHEMATIC : " + ((SchematicDrop) drop).getFile().getName() + " : "
                    + (((SchematicDrop) drop).atBlock() ? "BLOCK" : "PLAYER");
        } else if (drop instanceof WaterBucketSpecial) {
            return "SPECIAL : WATER_BUCKET : " + ((WaterBucketSpecial) drop).getHeight();
        } else if (drop instanceof TntExplosionSpecial) {
            return "SPECIAL : TNT_EXPLOSION : " + ((TntExplosionSpecial) drop).getAmount();
        } else if (drop instanceof TntColumnSpecial) {
            return "SPECIAL : TNT_COLUMN : " + ((TntColumnSpecial) drop).getAmount();
        } else if (drop instanceof PigSpecial) {
            return "SPECIAL : PIG : " + ((PigSpecial) drop).getAmount();
        } else if (drop instanceof LightningSpecial) {
            return "SPECIAL : LIGHTNING : " + ((LightningSpecial) drop).getAmount();
        } else if (drop instanceof ExperienceExplosionSpecial) {
            return "SPECIAL : EXPERIENCE_EXPLOSION : " + ((ExperienceExplosionSpecial) drop).getAmount();
        }
        throw new RuntimeException(drop.getClass().getName() + " legacy save logic not implemented");
    }
}
