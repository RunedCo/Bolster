package co.runed.bolster.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CommandModelData extends CommandBase
{
    public CommandModelData()
    {
        super("custommodeldata", "bolster.cmd", new String[]{"cmd"}, null);
    }

    @Override
    public void run(CommandSender sender, Object[] args)
    {
        if(sender instanceof Player)
        {
            Player player = (Player) sender;
            int modelData = (int) args[0];

            ItemStack item = player.getInventory().getItemInMainHand();
            ItemMeta meta = item.getItemMeta();

            meta.setCustomModelData(modelData);

            item.setItemMeta(meta);
        }
    }
}
