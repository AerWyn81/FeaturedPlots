package fr.aerwyn81.featuredplots.utils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
        if (!(is.getItemMeta() instanceof SkullMeta meta)) {
            return this;
        }

        try {
            meta.setOwner(owner);
            is.setItemMeta(meta);
        } catch (ClassCastException ignored) {
        }

        return this;
    }

    /**
     * Set the head texture to a head
     *
     * @param url complete MoJang texture url
     */
    public ItemBuilder setSkullTexture(String url) {
        if (!(is.getItemMeta() instanceof SkullMeta meta)) {
            return this;
        }

        if (url.isEmpty()) {
            return this;
        }

        try {
            PlayerProfile profile = Bukkit.createPlayerProfile(UUID.randomUUID());
            PlayerTextures textures = profile.getTextures();

            var textureURL = "https://textures.minecraft.net/texture/";

            if (!url.startsWith(textureURL))
                url = textureURL + url;

            textures.setSkin(new URL(url));

            meta.setOwnerProfile(profile);
            is.setItemMeta(meta);
            return new ItemBuilder(is);
        } catch (Exception ex) {
            return this;
        }
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
     * Get the PersistentDataContainer for the item
     *
     * @param key   key for the PersistentDataContainer
     * @param value {@link String} value
     */
    public ItemBuilder setPersistentDataContainer(NamespacedKey key, String value) {
        ItemMeta im = is.getItemMeta();
        im.getPersistentDataContainer().set(key, PersistentDataType.STRING, value);
        is.setItemMeta(im);
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