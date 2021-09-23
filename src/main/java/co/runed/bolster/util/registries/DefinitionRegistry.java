package co.runed.bolster.util.registries;

import co.runed.dayroom.util.Identifiable;
import org.bukkit.plugin.Plugin;

public class DefinitionRegistry<T extends Identifiable> extends Registry<Definition<T>> {
    public DefinitionRegistry(Plugin plugin) {
        this(plugin, null);
    }

    public DefinitionRegistry(Plugin plugin, String folderName) {
        super(plugin, folderName);
    }
}
