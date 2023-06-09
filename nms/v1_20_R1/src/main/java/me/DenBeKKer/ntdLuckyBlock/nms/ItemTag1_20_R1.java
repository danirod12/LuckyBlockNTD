package me.DenBeKKer.ntdLuckyBlock.nms;

import net.minecraft.nbt.CompoundTag;
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

public class ItemTag1_20_R1 implements ItemTag {

    @Override
    public ItemStack asBukkitCopy(Object nmsItem) {
        return CraftItemStack.asBukkitCopy((net.minecraft.world.item.ItemStack) nmsItem);
    }

    @Override
    public Object newTag() {
        return new CompoundTag();
    }

    @Override
    public Object asNMSCopy(ItemStack origin) {
        return CraftItemStack.asNMSCopy(origin);
    }

    @Override
    public String getTagString(Object tag, String element) {
        return ((CompoundTag) tag).getString(element);
    }

    @Override
    public void setTagString(Object tag, String element, String value) {
        ((CompoundTag) tag).putString(element, value);
    }

    @Override
    public Object getTag(Object nmsItem) {
        return ((net.minecraft.world.item.ItemStack) nmsItem).getTag();
    }

    @Override
    public void setTag(Object nmsItem, Object newTag) {
        ((net.minecraft.world.item.ItemStack) nmsItem).setTag((CompoundTag) newTag);
    }

}
