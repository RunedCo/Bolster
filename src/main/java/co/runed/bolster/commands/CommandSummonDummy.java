package co.runed.bolster.commands;

import co.runed.bolster.classes.TargetDummyClass;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandSummonDummy extends CommandBase
{
    public CommandSummonDummy()
    {
        super("dummy", "bolster.commands.dummy");
    }

    @Override
    public void run(CommandSender sender, Object[] args) throws WrapperCommandSyntaxException
    {
        if(sender instanceof Player)
        {
            Location loc = ((Player) sender).getLocation();

            TargetDummyClass.summon(loc);

            sender.sendMessage("Summoned Target Dummy.");
        }
    }
}
