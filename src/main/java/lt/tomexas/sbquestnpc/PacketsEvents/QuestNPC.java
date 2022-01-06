package lt.tomexas.sbquestnpc.PacketsEvents;

import com.bgsoftware.superiorskyblock.api.island.Island;
import lt.tomexas.sbquestnpc.Skyblock;
import net.minecraft.network.chat.ChatComponentText;
import net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy;
import net.minecraft.network.protocol.game.PacketPlayOutEntityHeadRotation;
import net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity;
import net.minecraft.server.level.WorldServer;
import net.minecraft.server.network.PlayerConnection;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.npc.EntityVillager;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class QuestNPC {

    private final Skyblock plugin;
    private final String name = "§eKeliautojas Jonas Havaras";

    public QuestNPC (Skyblock plugin) {
        this.plugin = plugin;
    }

    public void createNPC(Island island, Location loc) {
        WorldServer world = ((CraftWorld) island.getCenter(World.Environment.NORMAL).getWorld()).getHandle();
        EntityVillager npc = new EntityVillager(EntityTypes.aV, world, VillagerType.c);

        npc.setLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
        npc.setCustomNameVisible(true);

        if (island.getOwner().hasPermission("questnpc.completed"))
            npc.setCustomName(new ChatComponentText(name));
        else
            npc.setCustomName(new ChatComponentText("§f§l???"));

        npc.setSilent(true);
        npc.setNoAI(true);
        npc.setInvulnerable(true);
        npc.setVillagerData(new VillagerData(VillagerType.c, VillagerProfession.d, 1));

        Map<EntityVillager, Location> map = new HashMap<>();
        map.put(npc, loc);
        this.plugin.NPCs.put(island, map);
    }

    public void addNPCPacket(Island island) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            Map<EntityVillager, Location> map = this.plugin.NPCs.get(island);
            for (EntityVillager npc : map.keySet()) {
                PlayerConnection connection = ((CraftPlayer) player).getHandle().b;
                connection.sendPacket(new PacketPlayOutSpawnEntity(npc));
                connection.sendPacket(new PacketPlayOutEntityMetadata(npc.getId(), npc.getDataWatcher(), true));
                connection.sendPacket(new PacketPlayOutEntityHeadRotation(npc, (byte) (npc.getBukkitYaw() * 256 / 360)));
            }
        }
    }

    public void destroyNPC(Island island) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            Map<EntityVillager, Location> map = this.plugin.NPCs.get(island);
            for (EntityVillager npc : map.keySet()) {
                PlayerConnection connection = ((CraftPlayer) player).getHandle().b;
                connection.sendPacket(new PacketPlayOutEntityDestroy(npc.getId()));
            }
        }
        this.plugin.NPCs.remove(island);
    }

    public void removeNPCPacket(Island island) {
        if (this.plugin.NPCs.get(island) == null) return;
        for (Player player : Bukkit.getOnlinePlayers()) {
            Map<EntityVillager, Location> map = this.plugin.NPCs.get(island);
            for (EntityVillager npc : map.keySet()) {
                PlayerConnection connection = ((CraftPlayer) player).getHandle().b;
                connection.sendPacket(new PacketPlayOutEntityDestroy(npc.getId()));
            }
        }
    }

    public Location defaultNPCLoc(Island island) {
        Location loc = island.getCenter(World.Environment.NORMAL);
        loc = new Location(loc.getWorld(), loc.getX()+1, loc.getY(), loc.getZ()-3, loc.getYaw(), loc.getPitch());
        return loc;
    }

    public String getName() {
        return name;
    }
}
