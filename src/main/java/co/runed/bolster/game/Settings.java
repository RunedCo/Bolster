package co.runed.bolster.game;

import co.runed.bolster.commands.CommandPlayerProperty;
import co.runed.bolster.managers.CommandManager;
import co.runed.bolster.managers.PlayerManager;
import co.runed.bolster.util.registries.Registries;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Settings {
    public static final Setting<Boolean> DEBUG_MODE = new Setting<>("debug_mode", false);
    public static final Setting<String> TEST_SETTING = new Setting<>("test_setting", "")
            .addOption("Test Option", "This is a test option!", new ItemStack(Material.ZOMBIE_HEAD), "test_option")
            .addOption("Test Option 2", "This is a test option 2!", new ItemStack(Material.BLAZE_ROD), "test_option_2")
            .addOption("Test Option 3", "This is a test option 3!", new ItemStack(Material.BLACK_BANNER), "test_option_3");

    public static void initialize() {
        Registries.SETTINGS.onRegister(entry -> CommandManager.getInstance().add(new CommandSetting(entry.create())));

        DEBUG_MODE.register();
        TEST_SETTING.register();
    }

    public static class CommandSetting extends CommandPlayerProperty {
        public CommandSetting(Setting<?> setting) {
            super("setting", "Setting", setting, "");
        }

        @Override
        public Object get(Player player) {
            return PlayerManager.getInstance().getPlayerData(player).getSettings().getOrDefault(property.getId(), property.getDefault());
        }

        @Override
        public void set(Player player, Object value) {
            PlayerManager.getInstance().getPlayerData(player).getSettings().put(property.getId(), value);
        }
    }
}
