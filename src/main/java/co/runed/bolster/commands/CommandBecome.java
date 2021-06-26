package co.runed.bolster.commands;

import co.runed.bolster.classes.BolsterClass;
import co.runed.bolster.entity.BolsterEntity;
import co.runed.bolster.util.registries.Registries;
import co.runed.bolster.util.registries.Registry;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.StringArgument;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;

public class CommandBecome extends CommandBase
{
    public CommandBecome()
    {
        super("become");
    }

    private String[] getSuggestions(CommandSender sender)
    {
        return Registries.CLASSES.getEntries().values().stream().map(Registry.Entry::getId).toArray(String[]::new);
    }

    @Override
    public CommandAPICommand build()
    {
        return new CommandAPICommand(this.command)
                .withPermission("bolster.commands.become")
                .withArguments(new StringArgument("class").overrideSuggestions(this::getSuggestions))
                .executesEntity((sender, args) -> {
                    String id = (String) args[0];
                    if (!(sender instanceof LivingEntity)) return;
                    LivingEntity livingEntity = (LivingEntity) sender;
                    BolsterClass bolsterClass = Registries.CLASSES.get(id).create();

                    BolsterEntity.from(livingEntity).setBolsterClass(bolsterClass);
                    sender.sendMessage("Became " + bolsterClass.getName());
                });
    }
}

