package co.runed.bolster.game;

import co.runed.bolster.util.registries.Registries;
import co.runed.dayroom.properties.Property;

public class Setting<T> extends Property<T> {
    private boolean showInMenu = false;
    private boolean usePermission = false;
    private String permission = "";

    public Setting(String id) {
        this(id, null);
    }

    public Setting(String id, T defaultValue) {
        super(id, defaultValue);

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

    public Setting<T> register() {
        Registries.SETTINGS.register(this);

        return this;
    }
}
