package me.DenBeKKer.ntdLuckyBlock.nms;

import org.bukkit.inventory.ItemStack;

public interface ItemTag {

    ItemStack asBukkitCopy(Object nmsItem);

    Object newTag();

    Object asNMSCopy(ItemStack origin);

    String getTagString(Object tag, String element);

    void setTagString(Object tag, String element, String value);

    Object getTag(Object nmsItem);

    void setTag(Object nmsItem, Object newTag);
}
