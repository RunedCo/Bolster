package co.runed.bolster.commands;

import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandLightLevel extends CommandBase
{
    public CommandLightLevel()
    {
        super("lightlevel", null);
    }

    @Override
    public void run(CommandSender sender, Object[] args) throws WrapperCommandSyntaxException
    {
        if(sender instanceof Player)
        {
            Location loc = ((Player) sender).getLocation();

            sender.sendMessage("The light level at " + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ() +  " is " + loc.getBlock().getLightLevel());
            sender.sendMessage("The sky light level at " + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ() +  " is " + loc.getBlock().getLightFromSky());
            sender.sendMessage("The light level from blocks at " + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ() +  " is " + loc.getBlock().getLightFromBlocks());
        }
    }
}
