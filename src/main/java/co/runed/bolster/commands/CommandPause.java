package co.runed.bolster.commands;

import co.runed.bolster.Bolster;
import co.runed.bolster.game.GameMode;
import dev.jorel.commandapi.CommandAPICommand;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class CommandPause extends CommandBase
{
    public CommandPause()
    {
        super("pause");
    }

    @Override
    public CommandAPICommand build()
    {
        return new CommandAPICommand(this.command)
                .withPermission("game.admin")
                .executesPlayer((player, args) -> {
                    GameMode gameMode = Bolster.getInstance().getActiveGameMode();
                    gameMode.setPaused(!gameMode.isPaused());
                });
    }
}
