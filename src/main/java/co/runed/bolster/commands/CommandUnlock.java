package co.runed.bolster.commands;

import co.runed.bolster.Permissions;
import co.runed.bolster.game.shop.Shop;
import co.runed.bolster.game.shop.ShopItem;
import co.runed.bolster.util.registries.Registries;
import co.runed.bolster.util.registries.Registry;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.PlayerArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CommandUnlock extends CommandBase
{
    public CommandUnlock()
    {
        super("unlock");
    }

    private String[] getShopSuggestions(CommandSender sender)
    {
        return Registries.SHOPS.getEntries().values().stream().map(Registry.Entry::getId).toArray(String[]::new);
    }

    private String[] getItemSuggestions(CommandSender sender, Object[] args)
    {
        if (args == null)
        {
            List<String> allItems = new ArrayList<>();

            for (Shop shop : Registries.SHOPS.getEntries().values().stream().map(Registry.Entry::create).toArray(Shop[]::new))
            {
                allItems.addAll(shop.getItems().keySet());
            }

            return allItems.toArray(new String[0]);
        }

        Shop shop = Registries.SHOPS.get((String) args[1]);

        return shop.getItems().keySet().toArray(new String[0]);
    }

    @Override
    public CommandAPICommand build()
    {
        return new CommandAPICommand(this.command)
                .withPermission(Permissions.COMMAND_UNLOCK)
                .withArguments(new PlayerArgument("player"))
                .withArguments(new StringArgument("shop").overrideSuggestions(this::getShopSuggestions))
                .withArguments(new StringArgument("shop_item").overrideSuggestions(this::getItemSuggestions))
                .executes((sender, args) -> {
                    Player player = (Player) args[0];
                    String id = (String) args[1];
                    String shopItemId = (String) args[2];

                    Shop shop = Registries.SHOPS.get(id);
                    ShopItem shopItem = shop.getItem(shopItemId);

                    if (shopItem != null)
                    {
                        if (shopItem.isUnlockable())
                        {
                            shopItem.unlock(player);
                            sender.sendMessage("Unlocked " + shopItem.getName() + " from shop " + shop.getName() + " for player " + player.getName());
                        }
                        else
                        {
                            sender.sendMessage(shopItem.getName() + " is not unlockable!");
                        }
                    }
                    else
                    {
                        sender.sendMessage("Invalid shop item " + shopItem.getName() + " from shop " + shop.getName());
                    }
                });
    }
}
