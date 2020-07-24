package co.runed.bolster;

import co.runed.bolster.classes.BolsterClass;
import co.runed.bolster.properties.Properties;
import co.runed.bolster.util.PlayerUtil;
import org.bukkit.entity.Player;

import java.io.IOException;

public class BolsterPlayer
{
    BolsterClass bolsterClass;

    Player player;
    Properties properties = new Properties();

    public BolsterPlayer(Player player)
    {
        this.player = player;
    }

    public void setBukkitPlayer(Player player)
    {
        this.player = player;
    }

    public Player getBukkitPlayer()
    {
        return this.player;
    }

    public boolean isOnline()
    {
        return this.player.isOnline();
    }

    public Properties getProperties()
    {
        return this.properties;
    }

    public void sendToServer(String server)
    {
        try
        {
            PlayerUtil.sendPlayerToServer(this.getBukkitPlayer(), server);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
