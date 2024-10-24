package me.DenBeKKer.ntdLuckyBlock.nms;

import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.component.CustomData;
import org.bukkit.craftbukkit.v1_21_R2.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public class ItemTag1_21_R2 implements ItemTag {

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
        Optional<? extends CustomData> optional =
                ((net.minecraft.world.item.ItemStack) nmsItem).getComponentsPatch().get(DataComponents.CUSTOM_DATA);
        if (optional != null && optional.isPresent()) {
            return optional.get().copyTag();
        }
        return null;
    }

    @Override
    public void setTag(Object nmsItem, Object newTag) {
        ((net.minecraft.world.item.ItemStack) nmsItem).applyComponents(DataComponentPatch.builder()
                .set(DataComponents.CUSTOM_DATA, CustomData.of((CompoundTag) newTag)).build());
    }
}
