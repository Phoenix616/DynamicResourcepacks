package de.rene_zeidler.dynamicresourcepacks;

import java.util.HashMap;

import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;

public class ResourcepackManager {
	private DynamicResourcepacks plugin;
	private Configuration config;
	
	private HashMap<Player, String> currentPacks;
	
	private static ResourcepackManager instance;
	
	public ResourcepackManager(DynamicResourcepacks plugin) {
		ResourcepackManager.instance = this;
		
		this.plugin = plugin;
		this.config = this.plugin.getConfig();
		
		this.currentPacks = new HashMap<Player, String>();
	}
	
	public static ResourcepackManager getInstance() {
		return ResourcepackManager.instance;
	}
	
	public String getEmptyPackURL() {
		return this.config.getString("emptyPackURL");
	}
	
	public void setEmptyPackURL(String url) {
		this.config.set("emptyPackURL", url);
	}
	
	public void resendAll() {
		for(Player p : currentPacks.keySet())
			p.setResourcePack(currentPacks.get(p));
	}
	
	public void resend(Player player) {
		if(currentPacks.containsKey(player))
			player.setResourcePack(currentPacks.get(player));
		else
			player.setResourcePack(this.getEmptyPackURL());
	}
	
	public void setResourcepack(Player player, String url) {
		if(url == null) {
			this.clearResourcepack(player);
			return;
		}
		
		currentPacks.put(player, url);
		player.setResourcePack(url);
	}
	
	public void clearResourcepack(Player player) {
		currentPacks.remove(player);
		player.setResourcePack(this.getEmptyPackURL());
	}
	
	public String getResourcepack(Player player) {
		return currentPacks.get(player);
	}
	
	public void clear() {
		for(Player p : currentPacks.keySet())
			this.clearResourcepack(p);
	}
}
