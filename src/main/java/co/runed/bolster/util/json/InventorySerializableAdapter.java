package co.runed.bolster.util.json;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class InventorySerializableAdapter implements JsonSerializer<Inventory>, JsonDeserializer<Inventory>
{
    private final String NAME_KEY = "name";
    private final String SIZE_KEY = "size";
    private final String CONTENTS_KEY = "contents";

    final Type contentsMapType = new TypeToken<Map<Integer, ItemStack>>()
    {
    }.getType();


    @Override
    public Inventory deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
    {
        InventoryHolder holder = new InventoryHolder();

        if (!json.isJsonObject()) return null;

        JsonObject jsonObject = json.getAsJsonObject();
        if (jsonObject.has(NAME_KEY))
        {
            holder.name = jsonObject.getAsJsonPrimitive(NAME_KEY).getAsString();
        }

        if (jsonObject.has(SIZE_KEY))
        {
            holder.size = getInventorySize(jsonObject.getAsJsonPrimitive(SIZE_KEY).getAsInt());
        }

        if (jsonObject.has(CONTENTS_KEY))
        {
            holder.contents = context.deserialize(jsonObject.getAsJsonObject(CONTENTS_KEY), contentsMapType);
        }

        Inventory inventory = holder.name != null ? Bukkit.createInventory(null, holder.size, holder.name) : Bukkit.createInventory(null, holder.size);

        for (Map.Entry<Integer, ItemStack> entry : holder.contents.entrySet())
        {
            inventory.setItem(entry.getKey(), entry.getValue());
        }

        return inventory;
    }

    @Override
    public JsonElement serialize(Inventory src, Type typeOfSrc, JsonSerializationContext context)
    {
        InventoryHolder inventoryHolder = new InventoryHolder();
        inventoryHolder.size = src.getSize();
        ItemStack[] contents = src.getContents();

        for (int i = 0; i < contents.length; i++)
        {
            ItemStack itemStack = contents[i];
            if (itemStack == null || itemStack.getType() == Material.AIR) continue;

            inventoryHolder.contents.put(i, itemStack);
        }

        return context.serialize(inventoryHolder);
    }

    private int getInventorySize(int max)
    {
        if (max <= 0) return 9;
        int quotient = (int) Math.ceil(max / 9.0);
        return quotient > 5 ? 54 : quotient * 9;
    }

    private static class InventoryHolder
    {
        private String name = null;
        private int size = 9;
        private Map<Integer, ItemStack> contents = new HashMap<>();
    }
}