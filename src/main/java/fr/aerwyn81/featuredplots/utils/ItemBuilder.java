package fr.aerwyn81.featuredplots.utils;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ItemBuilder {
    private final ItemStack is;

    /**
     * Create a new ItemBuilder from a Material
     *
     * @param material {@link Material}
     */
    public ItemBuilder(Material material) {
        this.is = new ItemStack(material);
    }

    /**
     * Create a new ItemBuilder from an existing ItemStack
     *
     * @param itemStack {@link ItemStack}
     */
    public ItemBuilder(ItemStack itemStack) {
        this.is = itemStack;
    }

    /**
     * Clone ItemBuilder
     *
     * @return cloned instance {@link ItemBuilder}
     */
    public ItemBuilder clone() {
        return new ItemBuilder(is);
    }

    /**
     * Set the name of the item
     *
     * @param name name
     */
    public ItemBuilder setName(String name) {
        ItemMeta im = is.getItemMeta();
        im.setDisplayName(name);
        is.setItemMeta(im);

        return this;
    }

    /**
     * Remove enchant from the item
     *
     * @param ench enchantment to remove
     */
    public ItemBuilder removeEnchantment(Enchantment ench) {
        is.removeEnchantment(ench);

        return this;
    }

    /**
     * Set the skull owner for the item.
     *
     * @param owner name of the skull's owner
     */
    public ItemBuilder setSkullOwner(String owner) {
        try {
            SkullMeta im = (SkullMeta) is.getItemMeta();
            im.setOwner(owner);
            is.setItemMeta(im);
        } catch (ClassCastException ignored) {
        }

        return this;
    }

    /**
     * Add enchant to the item
     *
     * @param ench  enchant to add
     * @param level level
     */
    public ItemBuilder addEnchant(Enchantment ench, int level) {
        ItemMeta im = is.getItemMeta();
        im.addEnchant(ench, level, true);
        is.setItemMeta(im);
        return this;
    }

    /**
     * Add multiple enchants at once
     *
     * @param enchantments enchants to add
     */
    public ItemBuilder addEnchantments(Map<Enchantment, Integer> enchantments) {
        is.addEnchantments(enchantments);
        return this;
    }

    /**
     * Set the lore
     *
     * @param lore lore
     */
    public ItemBuilder setLore(String... lore) {
        ItemMeta im = is.getItemMeta();
        im.setLore(Arrays.asList(lore));
        is.setItemMeta(im);
        return this;
    }

    /**
     * Set the lore
     *
     * @param lore lore
     */
    public ItemBuilder setLore(List<String> lore) {
        ItemMeta im = is.getItemMeta();
        im.setLore(lore);
        is.setItemMeta(im);
        return this;
    }

    /**
     * Set the skull owner of the skull
     *
     * @param name name of the player
     */
    public ItemBuilder skullOwner(String name) {
        if (!(is.getItemMeta() instanceof SkullMeta meta)) {
            return this;
        }

        is.setDurability((byte) 3);
        meta.setOwner(name);
        is.setItemMeta(meta);

        return this;
    }

    /**
     * Get itemstack from the ItemBuilder
     *
     * @return itemstack created by the ItemBuilder
     */
    public ItemStack toItemStack() {
        return is;
    }
}