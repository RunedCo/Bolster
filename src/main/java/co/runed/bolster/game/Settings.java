package co.runed.bolster.game;

import co.runed.bolster.commands.CommandPlayerProperty;
import co.runed.bolster.managers.CommandManager;
import co.runed.bolster.managers.PlayerManager;
import co.runed.bolster.util.registries.Registries;
import co.runed.dayroom.properties.Property;
import org.bukkit.entity.Player;

public class Settings {
    public static final Property<Boolean> DEBUG_MODE = new Property<>("debug_mode", false);

    public static void initialize() {
        Registries.SETTINGS.onRegister(entry -> CommandManager.getInstance().add(new CommandSetting(entry.create())));

        Registries.SETTINGS.register(DEBUG_MODE);
    }

    public static class CommandSetting extends CommandPlayerProperty {
        public CommandSetting(Property<?> property) {
            super("setting", "Setting", property, "");
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
