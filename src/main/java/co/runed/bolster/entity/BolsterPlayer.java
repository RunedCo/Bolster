package co.runed.bolster.entity;

import co.runed.bolster.util.PlayerUtil;
import org.bukkit.entity.Player;

import java.io.IOException;

public class BolsterPlayer extends BolsterLivingEntity<Player>
{
    public BolsterPlayer(Player player)
    {
        super(player);
    }

    public void sendToServer(String server)
    {
        try
        {
            PlayerUtil.sendPlayerToServer(this.bukkit(), server);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void sendActionBar(String message)
    {
        PlayerUtil.sendActionBar(this.bukkit(), message);
    }
}
