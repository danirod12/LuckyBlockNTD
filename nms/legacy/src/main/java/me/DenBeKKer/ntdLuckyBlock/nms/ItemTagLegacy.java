package me.DenBeKKer.ntdLuckyBlock.nms;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;

public class ItemTagLegacy implements ItemTag {

    private final Class<?> CraftItemStack, NBTTagCompound;

    public ItemTagLegacy() throws UnsupportedOperationException {

        String nms_version = Bukkit.getServer().getClass().getPackage().getName();
        nms_version = nms_version.substring(nms_version.lastIndexOf('.') + 1);

        Class<?> clazz;
        try {
            clazz = Class.forName("org.bukkit.craftbukkit." + nms_version + ".inventory.CraftItemStack");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new UnsupportedOperationException();
        }
        this.CraftItemStack = clazz;

        try {
            clazz = Class.forName("net.minecraft.server." + nms_version + ".NBTTagCompound");
        } catch (Exception ex) {
            try {
                clazz = Class.forName("net.minecraft.nbt.NBTTagCompound");
            } catch (Exception ex2) {
                ex.printStackTrace();
                ex2.printStackTrace();
                throw new UnsupportedOperationException();
            }
        }
        this.NBTTagCompound = clazz;

        // version verification
        try {
            this.asNMSCopy(new ItemStack(Material.STONE)).getClass().getMethod("getTag");
        } catch (NoSuchMethodException exception) {
            throw new UnsupportedOperationException();
        }

    }

    @Override
    public ItemStack asBukkitCopy(Object nmsItem) {
        try {
            return (ItemStack) CraftItemStack.getMethod("asBukkitCopy", nmsItem.getClass()).invoke(null, nmsItem);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException |
                 NoSuchMethodException | SecurityException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public Object newTag() {
        try {
            return NBTTagCompound.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Object asNMSCopy(ItemStack origin) {
        try {
            return CraftItemStack.getMethod("asNMSCopy", ItemStack.class).invoke(null, origin);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException |
                 NoSuchMethodException | SecurityException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String getTagString(Object tag, String element) {
        try {
            return (String) tag.getClass().getMethod("getString", String.class).invoke(tag, element);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException |
                 NoSuchMethodException | SecurityException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void setTagString(Object tag, String element, String value) {
        try {
            tag.getClass().getMethod("setString", String.class, String.class).invoke(tag, element, value);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException |
                 NoSuchMethodException | SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Object getTag(Object nmsItem) {
        try {
            return nmsItem.getClass().getMethod("getTag").invoke(nmsItem);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException |
                 NoSuchMethodException | SecurityException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void setTag(Object nmsItem, Object newTag) {
        try {
            nmsItem.getClass().getMethod("setTag", newTag.getClass()).invoke(nmsItem, newTag);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException |
                 NoSuchMethodException | SecurityException e) {
            e.printStackTrace();
        }
    }

}
