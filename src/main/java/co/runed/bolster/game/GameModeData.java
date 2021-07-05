package co.runed.bolster.game;

import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Map;

public class GameModeData
{
    public Map<String, Inventory> inventories = new HashMap<>();

    public GameModeData()
    {

    }

    public void setInventory(String id, Inventory inventory)
    {
        this.inventories.put(id, inventory);
    }
}
