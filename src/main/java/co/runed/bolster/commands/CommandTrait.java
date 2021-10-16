package co.runed.bolster.commands;

import co.runed.bolster.Permissions;
import co.runed.bolster.entity.BolsterEntity;
import co.runed.bolster.fx.Glyphs;
import co.runed.bolster.game.traits.Trait;
import co.runed.dayroom.util.Identifiable;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandTrait extends CommandPlayerProperty {
    private final Trait<?> trait;

    public CommandTrait(Trait<?> trait) {
        super("trait", "Trait", trait, Permissions.COMMAND_TRAIT);

        this.trait = trait;
    }

    @Override
    public Object get(Player player) {
        var bolsterEntity = BolsterEntity.from(player);

        return bolsterEntity.getTrait(trait);
    }

    @Override
    public void postGet(CommandSender sender, Object value, Player player) {
        var bolsterEntity = BolsterEntity.from(player);

        for (var provider : bolsterEntity.getTraitProviders()) {
            if (!provider.getTraits().contains(trait)) continue;

            var provName = provider.getClass().getName();

            if (provider instanceof Identifiable identifiable) {
                provName = identifiable.getId();
            }

            sender.sendMessage(Component.text("    " + Glyphs.BULLET + " " + provName + ": " + provider.getTrait(trait)));
        }
    }

    @Override
    public void set(Player player, Object value) {
        var bolsterEntity = BolsterEntity.from(player);
        bolsterEntity.getBackingTraits().setUnsafe(trait, value);
    }
}
