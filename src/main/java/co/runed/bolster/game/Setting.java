package co.runed.bolster.game;

import co.runed.bolster.util.registries.Registries;
import co.runed.dayroom.properties.Property;
import co.runed.dayroom.util.Nameable;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class Setting<T> extends Property<T> implements Nameable {
    private String name;
    private String description;
    private boolean showInMenu = false;
    private boolean usePermission = false;
    private String permission = "";
    private ItemStack icon = new ItemStack(Material.TNT);
    private List<SettingOption<T>> options = new ArrayList<>();

    public Setting(String id) {
        this(id, null);
    }

    public Setting(String id, T defaultValue) {
        super(id, defaultValue);

        // TODO hardcoded
        permission = "bolster.settings.edit." + id;
    }

    public Setting<T> setShowInMenu(boolean showInMenu) {
        this.showInMenu = showInMenu;

        return this;
    }

    public Setting<T> setPermission(String permission) {
        this.permission = permission;

        return this;
    }

    public Setting<T> addOption(String name, String description, ItemStack icon, T value) {
        var option = new SettingOption<T>();
        option.name = name;
        option.description = description;
        option.icon = icon;
        option.value = value;

        options.add(option);

        return this;
    }

    public Setting<T> register() {
        Registries.SETTINGS.register(this);

        return this;
    }

    public boolean hasOptions() {
        return options.size() > 0;
    }

    public List<SettingOption<T>> getOptions() {
        return options;
    }

    public ItemStack getIcon(T value) {
        if (hasOptions()) {
            for (var option : options) {
                if (option.value.equals(value)) return option.icon;
            }
        }

        return new ItemStack(Material.GOLD_NUGGET);
    }

    @Override
    public String getName() {
        return name == null ? getId() : name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public static class SettingOption<J> {
        public ItemStack icon;
        public String name;
        public String description;
        public J value;
    }
}
