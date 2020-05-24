package co.runed.bolster.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.ipvp.canvas.Menu;
import org.ipvp.canvas.type.ChestMenu;

public class CommandItems extends CommandBase
{
    public CommandItems()
    {
        super("items", "bolster.admin", null, null);
    }

    @Override
    public void run(CommandSender sender, Object[] args)
    {
        if(sender instanceof Player) {
            Menu menu = ChestMenu.builder(4)
                    .title("Menu")
                    .build();

            menu.open((Player)sender);
        }
    }
}
