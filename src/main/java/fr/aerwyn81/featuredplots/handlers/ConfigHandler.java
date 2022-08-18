package fr.aerwyn81.featuredplots.handlers;

import fr.aerwyn81.featuredplots.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

/**
 * Used to load and have in one place all the config.yml keys
 */
@SuppressWarnings({"ConstantConditions", "DuplicatedCode"})
public class ConfigHandler {

    private final File configFile;
    private FileConfiguration config;

    public ConfigHandler(File configFile) {
        this.configFile = configFile;
    }

    public void loadConfiguration() {
        this.config = YamlConfiguration.loadConfiguration(configFile);
    }

    public String getLanguage() {
        return config.getString("language", "en").toLowerCase();
    }

    public ItemBuilder getBorderIcon() {
        var defaultIcon = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE);
        var path = "gui.borderIcon";

        if (!config.contains(path)) {
            return defaultIcon;
        }

        try {
            var type = Material.valueOf(config.getString(path + ".type", defaultIcon.toItemStack().getType().name()));
            if (type != Material.PLAYER_HEAD) {
                return new ItemBuilder(type);
            }

            return new ItemBuilder(Material.PLAYER_HEAD).setSkullTexture(config.getString(path + ".textureId", ""));
        } catch (Exception e) {
            return defaultIcon;
        }
    }

    public ItemBuilder getPreviousIcon() {
        var defaultIcon = new ItemBuilder(Material.PLAYER_HEAD).setSkullTexture("8652e2b936ca8026bd28651d7c9f2819d2e923697734d18dfdb13550f8fdad5f");
        var path = "gui.previousIcon";

        if (!config.contains(path)) {
            return defaultIcon;
        }

        try {
            var type = Material.valueOf(config.getString(path + ".type", defaultIcon.toItemStack().getType().name()));
            if (type != Material.PLAYER_HEAD) {
                return new ItemBuilder(type);
            }

            return defaultIcon.setSkullTexture(config.getString(path + ".textureId", ""));
        } catch (Exception e) {
            return defaultIcon;
        }
    }

    public ItemBuilder getNextIcon() {
        var defaultIcon = new ItemBuilder(Material.PLAYER_HEAD).setSkullTexture("2a3b8f681daad8bf436cae8da3fe8131f62a162ab81af639c3e0644aa6abac2f");
        var path = "gui.nextIcon";

        if (!config.contains(path)) {
            return defaultIcon;
        }

        try {
            var type = Material.valueOf(config.getString(path + ".type", defaultIcon.toItemStack().getType().name()));
            if (type != Material.PLAYER_HEAD) {
                return new ItemBuilder(type);
            }

            return defaultIcon.setSkullTexture(config.getString(path + ".textureId", ""));
        } catch (Exception e) {
            return defaultIcon;
        }
    }

    public ItemBuilder getInfoIcon() {
        var defaultIcon = new ItemBuilder(Material.PLAYER_HEAD).setSkullTexture("5db532b5cced46b4b535ece16eced7bbc5cac55594d61e8b8f8eac4299c9fc");
        var path = "gui.infoIcon";

        if (!config.contains(path)) {
            return defaultIcon;
        }

        try {
            var type = Material.valueOf(config.getString(path + ".type", defaultIcon.toItemStack().getType().name()));
            if (type != Material.PLAYER_HEAD) {
                return new ItemBuilder(type);
            }

            return defaultIcon.setSkullTexture(config.getString(path + ".textureId", ""));
        } catch (Exception e) {
            return defaultIcon;
        }
    }

    public boolean isDisplayInfoIcon() {
        return config.getBoolean("gui.infoIcon.display", true);
    }

    public ItemBuilder getBackIcon() {
        var defaultIcon = new ItemBuilder(Material.PLAYER_HEAD).setSkullTexture("865426a33df58b465f0601dd8b9bec3690b2193d1f9503c2caab78f6c2438");
        var path = "gui.backIcon";

        if (!config.contains(path)) {
            return defaultIcon;
        }

        try {
            var type = Material.valueOf(config.getString(path + ".type", defaultIcon.toItemStack().getType().name()));
            if (type != Material.PLAYER_HEAD) {
                return new ItemBuilder(type);
            }

            return defaultIcon.setSkullTexture(config.getString(path + ".textureId", ""));
        } catch (Exception e) {
            return defaultIcon;
        }
    }

    public ItemBuilder getCloseIcon() {
        var defaultIcon = new ItemBuilder(Material.PLAYER_HEAD).setSkullTexture("5a6787ba32564e7c2f3a0ce64498ecbb23b89845e5a66b5cec7736f729ed37");
        var path = "gui.closeIcon";

        if (!config.contains(path)) {
            return defaultIcon;
        }

        try {
            var type = Material.valueOf(config.getString(path + ".type", defaultIcon.toItemStack().getType().name()));
            if (type != Material.PLAYER_HEAD) {
                return new ItemBuilder(type);
            }

            return defaultIcon.setSkullTexture(config.getString(path + ".textureId", ""));
        } catch (Exception e) {
            return defaultIcon;
        }
    }
}
