package co.runed.bolster.commands;

import co.runed.bolster.classes.TargetDummyClass;
import dev.jorel.commandapi.CommandAPICommand;
import org.bukkit.Location;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class CommandSummonDummy extends CommandBase
{
    public CommandSummonDummy()
    {
        super("dummy");
    }

    @Override
    public CommandAPICommand build()
    {
        return new CommandAPICommand(this.command)
                .withPermission("bolster.commands.dummy")
                .executesPlayer((sender, args) -> {
                    sender.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 60 * 20, 0, true, true, true));
                    sender.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 50 * 20, 1, true, true, true));
                    sender.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 40 * 20, 2, true, true, true));
                    sender.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 30 * 20, 3, true, true, true));
                    sender.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20 * 20, 4, true, true, true));
                    sender.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 10 * 20, 5, true, true, true));

//                    Location loc = sender.getLocation();
//
//                    TargetDummyClass.summon(loc);
//
//                    sender.sendMessage("Summoned Target Dummy");
                });
    }
}
