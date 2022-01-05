package lt.tomexas.sbquestnpc.Items;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.List;

public class CreateNewItem extends ItemStack {

    protected CreateNewItem (Material material, int count, String name, List<String> lore) {
        if (count < 1) count = 1;

        this.setType(material);
        this.setAmount(count);

        ItemMeta meta = this.getItemMeta();
        if (!name.isEmpty())
            meta.setDisplayName(name);
        if (!lore.isEmpty())
            for (int i = 0; i < lore.size(); i++)
                lore.set(i, ChatColor.translateAlternateColorCodes('&', lore.get(i)));

            meta.setLore(lore);

        this.setItemMeta(meta);
    }
}
