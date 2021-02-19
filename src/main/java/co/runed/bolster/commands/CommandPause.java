package co.runed.bolster.commands;

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
                    player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 10 * 20, 5, true, true, true));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20 * 20, 4, true, true, true));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 30 * 20, 3, true, true, true));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 40 * 20, 2, true, true, true));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 50 * 20, 1, true, true, true));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 60 * 20, 0, true, true, true));
//                    GameMode gameMode = Bolster.getInstance().getActiveGameMode();
//                    gameMode.setPaused(!gameMode.isPaused());
                });
    }
}
