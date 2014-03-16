package de.rene_zeidler.dynamicresourcepacks;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;

import de.rene_zeidler.dynamicresourcepacks.Resourcepack.Permission;

/**
 * Stores the URL of the resourcepack for players and sends it to the player.
 * 
 * @author René Zeidler
 * @version 0.0.2
 */
public class ResourcepackManager {
	private DynamicResourcepacks plugin;
	private Configuration config;
	
	private static final Resourcepack EMPTY_PACK = new Resourcepack("empty", "Empty (no pack selected)", "", Permission.NONE, Permission.NONE);
	
	private HashMap<String, Resourcepack> packs;
	private HashMap<Player, Resourcepack> currentPacks;
	
	public ResourcepackManager(DynamicResourcepacks plugin) {
		this.plugin = plugin;
		this.config = this.plugin.getConfig();
		
		this.currentPacks = new HashMap<Player, Resourcepack>();
		this.packs        = new HashMap<String, Resourcepack>();
		this.packs.put(EMPTY_PACK.getName(), EMPTY_PACK);
	}
	
	public String getEmptyPackURL() {
		return EMPTY_PACK.getURL();
	}
	
	public void setEmptyPackURL(String url) {
		this.config.set("emptyPackURL", url);
		EMPTY_PACK.setURL(url);
	}
	
	public void sendResourcepack(Player player, Resourcepack pack) {
		player.setResourcePack(pack.getURL());
	}
	
	public void resendAll() {
		for(Player player : this.currentPacks.keySet())
			this.sendResourcepack(player, this.currentPacks.get(player));
	}
	
	public void resend(Player player) {
		if(this.currentPacks.containsKey(player))
			this.sendResourcepack(player, this.currentPacks.get(player));
		else
			this.sendResourcepack(player, EMPTY_PACK);
	}
	
	public void setResourcepack(Player player, Resourcepack pack) {
		if(pack == null) {
			this.clearResourcepack(player);
			return;
		}
		
		this.currentPacks.put(player, pack);
		this.sendResourcepack(player, pack);
	}
	
	public void clearResourcepack(Player player) {
		this.currentPacks.remove(player);
		this.sendResourcepack(player, EMPTY_PACK);
	}
	
	public Resourcepack getResourcepack(Player player) {
		return this.currentPacks.get(player);
	}
	
	public boolean hasResourcepack(Player player) {
		return this.currentPacks.containsKey(player);
	}
	
	public Resourcepack getResourcepackForName(String name) {
		return this.packs.get(name);
	}
	
	public void addResourcepack(Resourcepack pack) {
		this.packs.put(pack.getName(), pack);
	}
	
	public void removeResourcepack(String name) {
		this.packs.remove(name);
		
		Iterator<Entry<Player, Resourcepack>> i = this.currentPacks.entrySet().iterator();
		while(i.hasNext()) {
			Entry<Player, Resourcepack> e = i.next();
			if(e.getValue().getName().equalsIgnoreCase(name))  this.clearResourcepack(e.getKey());
		}
	}
	
	public void renameResourcepack(String oldName, String newName) {
		if(!this.packs.containsKey(oldName)) return;
		Resourcepack pack = this.packs.get(oldName);
		this.packs.remove(oldName);
		pack.setName(newName);
		this.packs.put(newName, pack);
	}
	
	public Resourcepack getResoucepackForURL(String url) {
		for(Resourcepack pack : this.packs.values()) {
			if(pack.getURL() == url) return pack;
		}
		return null;
	}
	
	public boolean resourcepackExists(String name) {
		return this.packs.containsKey(name);
	}
	
	public HashMap<String, Resourcepack> getResourcepacks() {
		return this.packs;
	}
	
	public HashMap<Player, Resourcepack> getCurrentResourcepacks() {
		return this.currentPacks;
	}
	
	public void clearSelectedPacks() {
		for(Player p : this.currentPacks.keySet())
			this.clearResourcepack(p);
	}
	
	public static boolean isValidURL(String url) {
		return (url != null && !url.isEmpty() && (url.startsWith("http://") || url.startsWith("https://")));
	}
}
