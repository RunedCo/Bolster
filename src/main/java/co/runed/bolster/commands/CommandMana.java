package co.runed.bolster.commands;

import co.runed.bolster.Bolster;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.FloatArgument;
import dev.jorel.commandapi.arguments.PlayerArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class CommandMana extends CommandBase
{
    List<String> operations = new ArrayList<>();
    List<String> types = new ArrayList<>();


    public CommandMana()
    {
        super("mana", "bolster.admin", null, null);

        operations.add("set");
        operations.add("add");
        operations.add("subtract");
        operations.add("get");

        types.add("current");
        types.add("max");

        LinkedHashMap<String, Argument> arguments = new LinkedHashMap<>();
        arguments.put("player", new PlayerArgument());
        arguments.put("operation", new StringArgument().overrideSuggestions(operations.toArray(new String[0])));
        arguments.put("type", new StringArgument().overrideSuggestions(types.toArray(new String[0])));
        arguments.put("amount", new FloatArgument());

        this.arguments = arguments;
    }

    @Override
    public void run(CommandSender sender, Object[] args)
    {
        Player player = (Player)args[0];
        String operation = (String)args[1];
        String type = (String)args[2];

        if(operation.equals("get")) {
            if(type.equals("max")) {
                player.sendMessage("Your maximum mana is " + Bolster.getManaManager().getMaximumMana(player));
            } else {
                player.sendMessage("You have " + Bolster.getManaManager().getCurrentMana(player) + " mana");
            }

            return;
        }

        float amount = (float)args[3];

        if(operation.equals("subtract")) amount = amount * -1;

        if(type.equals("max")) {
            if(operation.equals("add") || operation.equals("subtract")) {
                amount = Bolster.getManaManager().getMaximumMana(player) + amount;
            }

            Bolster.getManaManager().setMaximumMana(player, amount);
        } else {
            if(operation.equals("add") || operation.equals("subtract")) {
                amount = Bolster.getManaManager().getCurrentMana(player) + amount;
            }

            Bolster.getManaManager().setCurrentMana(player, amount);
        }
    }
}
