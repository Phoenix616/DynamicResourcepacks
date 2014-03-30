package de.rene_zeidler.dynamicresourcepacks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.permissions.Permissible;

import de.rene_zeidler.dynamicresourcepacks.Resourcepack.Permission;

/**
 * Stores the URL of the resourcepack for players and sends it to the player.
 * 
 * @author Ren� Zeidler
 * @version 0.1.0
 */
public class ResourcepackManager {
	private DynamicResourcepacks plugin;
	private Configuration config;
	
	private static final Resourcepack EMPTY_PACK = new Resourcepack("empty", "Empty (no pack selected)", "", "Default", Permission.NONE, Permission.GENERAL);
	
	private HashMap<String, Resourcepack> packs;
	private HashMap<Player, String> currentPacks;
	
	public ResourcepackManager(DynamicResourcepacks plugin) {
		this.plugin = plugin;
		this.config = this.plugin.getConfig();
		
		this.currentPacks = new HashMap<Player, String>();
		this.packs        = new HashMap<String, Resourcepack>();
		EMPTY_PACK.setURL(this.config.getString("emptyPackURL"));
		this.packs.put(EMPTY_PACK.getName(), EMPTY_PACK);
	}
	
	/**
	 * Returns the URL of the empty pack (used to clear a player's resourcepack).
	 * @return URL
	 */
	public String getEmptyPackURL() {
		return EMPTY_PACK.getURL();
	}
	
	/**
	 * Sets the URL of the empty pack to the given URL and adjusts the config value.
	 * Does not automatically save the config.
	 * @param url New URL
	 */
	public void setEmptyPackURL(String url) {
		this.config.set("emptyPackURL", url);
		EMPTY_PACK.setURL(url);
	}
	
	/**
	 * Directly sends the given resourcepack to the player.
	 * If either player or pack are null or the URL is empty, nothing happens.
	 * @param player The player to send the pack to
	 * @param pack The pack to send to the player
	 */
	protected void sendResourcepack(Player player, Resourcepack pack) {
		if(player != null && pack != null && pack.getURL() != null && !pack.getURL().isEmpty()) {
			player.setResourcePack(pack.getURL());
			this.plugin.getLogger().info("Sending resourcepack " + pack.getName() + " (" + pack.getURL() + ") to player " +  player.getName());;
		}
	}

	/**
	 * Directly sends the given resourcepack with the given name to the player.
	 * If there exists no resourcepack with the given name, nothing happens.
	 * @param player The player to send the pack to
	 * @param name The name of the pack to send to the player
	 */
	protected void sendResourcepack(Player player, String name) {
		Resourcepack pack = this.getResourcepackForName(name);
		if(pack != null)
			this.sendResourcepack(player, pack);
	}
	
	/**
	 * Resends the currently selected resourcepack to every player.
	 */
	public void resendAll() {
		for(Player player : this.currentPacks.keySet())
			this.sendResourcepack(player, this.currentPacks.get(player));
	}
	
	/**
	 * Resends the empty pack to all players without a selected resourcepack.
	 * It is not recommended to use this for other purposes than debugging, use {@link #resend(Player)} instead.
	 */
	public void resendEmpty() {
		for(Player player : this.plugin.getServer().getOnlinePlayers())
			if(!this.currentPacks.containsKey(player))
				this.sendResourcepack(player, this.currentPacks.get(player));
	}
	
	/**
	 * Resends the currently selected resourcepack to the given player, or if no resourcepack is selected, the empty pack.
	 * @param player Player to resend the pack to
	 */
	public void resend(Player player) {
		if(this.currentPacks.containsKey(player))
			this.sendResourcepack(player, this.currentPacks.get(player));
		else
			this.sendResourcepack(player, EMPTY_PACK);
	}
	
	/**
	 * Resends the given resourcepack to all players that currently use this pack.
	 * @param pack Resourcepack to resend
	 */
	public void resend(Resourcepack pack) {
		if(pack != null) this.resend(pack.getName());
	}
	
	/**
	 * Resends the given resourcepack to all players that currently use this pack.
	 * @param pack Name of the resourcepack to resend
	 */
	public void resend(String name) {
		Iterator<Entry<Player, String>> i = this.currentPacks.entrySet().iterator();
		while(i.hasNext()) {
			Entry<Player, String> e = i.next();
			if(e.getValue().equalsIgnoreCase(name))
				this.resend(e.getKey());
		}
	}
	
	/**
	 * Sets the selected resourcepack and sends it to the player.
	 * If the pack is null, the pack is cleared and the empty pack gets send.
	 * If the given pack is not in the list of resourcepacks it will be automatically added.
	 * @param player The player
	 * @param pack Resourcepack to set
	 */
	public void setResourcepack(Player player, Resourcepack pack) {
		if(pack == null) {
			this.clearResourcepack(player);
			return;
		}
		
		if(!this.packs.containsKey(pack.getName()))
			this.addResourcepack(pack);
		
		this.currentPacks.put(player, pack.getName());
		this.sendResourcepack(player, pack);
	}
	
	/**
	 * Clears the resourcepack of the given player and sends the empty pack.
	 * @param player The player
	 */
	public void clearResourcepack(Player player) {
		if(this.currentPacks.containsKey(player)) {
			this.currentPacks.remove(player);
			this.sendResourcepack(player, EMPTY_PACK);
		}
	}
	
	/**
	 * Returns the selected resourcepack for the given player.
	 * Returns null if no pack is selected.
	 * @param player The player to check
	 * @return The selected resourcepack or null
	 */
	public Resourcepack getResourcepack(Player player) {
		return this.getResourcepackForName(this.currentPacks.get(player));
	}
	
	/**
	 * Returns true if the player has a selected resourcepack, otherwise false.
	 * @param player The player to check
	 * @return True if the player has a selected resourcepack
	 */
	public boolean hasResourcepack(Player player) {
		return this.currentPacks.containsKey(player);
	}
	
	/**
	 * Returns the resourcepack for the given name.
	 * Returns null of no pack with the given name exists.
	 * @param name Name of the resourcepack
	 * @return The resourcepack or null
	 */
	public Resourcepack getResourcepackForName(String name) {
		return this.packs.get(name);
	}
	
	/**
	 * Adds the given resourcepack to the list of registered resourcepacks.
	 * @param pack The resourcepack to add
	 */
	public void addResourcepack(Resourcepack pack) {
		this.packs.put(pack.getName(), pack);
	}
	
	/**
	 * Removes the given resourcepack and send the empty pack to all players that currently use it.
	 * @param name
	 */
	public void removeResourcepack(Resourcepack pack) {
		this.packs.remove(pack.getName());
		
		Iterator<Entry<Player, String>> i = this.currentPacks.entrySet().iterator();
		while(i.hasNext()) {
			Entry<Player, String> e = i.next();
			if(e.getValue().equalsIgnoreCase(pack.getName()))
				this.clearResourcepack(e.getKey());
		}
	}
	
	/**
	 * Renames the resourcepack with the given name to the new name.
	 * @param oldName The old name
	 * @param newName The new name
	 */
	public void renameResourcepack(String oldName, String newName) {
		if(!this.packs.containsKey(oldName) || newName == null) return;
		Resourcepack pack = this.packs.get(oldName);
		this.packs.remove(oldName);
		pack.setName(newName);
		this.packs.put(newName, pack);
		
		for(Player player : this.currentPacks.keySet())
			if(this.currentPacks.get(player) == oldName)
				this.currentPacks.put(player, newName);
	}
	
	/**
	 * Renames the given resourcepack to the new name.
	 * @param pack The resourcepack to rename
	 * @param newName The new name
	 */
	public void renameResourcepack(Resourcepack pack, String newName) {
		if(pack != null)
			this.renameResourcepack(pack.getName(), newName);
	}
	
	/**
	 * Returns the resourcepack with the given URL.
	 * Returns null if no resourcepack with the given URL exists.
	 * @param url The URL to check
	 * @return The resourcepack
	 */
	public Resourcepack getResoucepackForURL(String url) {
		for(Resourcepack pack : this.packs.values())
			if(pack.getURL() == url)
				return pack;
		return null;
	}
	
	/**
	 * Returns true if a resourcepack with the given name exists, otherwise false.
	 * @param name The name to check
	 * @return True if a resourcepack with the given name exists
	 */
	public boolean resourcepackExists(String name) {
		return this.packs.containsKey(name);
	}
	
	/**
	 * Returns a list of all resourcepacks.
	 * @return The list
	 */
	public List<Resourcepack> getResourcepacks() {
		return new ArrayList<Resourcepack>(this.packs.values());
	}
	
	/**
	 * Returns all selectable resourcepacks for a player.
	 * (the player has the corresponding use self permission)
	 * @param player The player
	 * @return The list
	 */
	public List<Resourcepack> getSelectableResourcepacks(Permissible player) {
		ArrayList<Resourcepack> packs = new ArrayList<Resourcepack>();
		for(Resourcepack pack : this.packs.values())
			if(pack.checkUseSelfPermission(player))
				packs.add(pack);
		return packs;
	}
	
	/**
	 * Returns all usable resourcepacks for a player.
	 * (the player has the corresponding general permission)
	 * @param player The player
	 * @return The list
	 */
	public List<Resourcepack> getUsableResourcepacks(Permissible player) {
		ArrayList<Resourcepack> packs = new ArrayList<Resourcepack>();
		for(Resourcepack pack : this.packs.values())
			if(pack.checkGeneralPermission(player))
				packs.add(pack);
		return packs;
	}
	
	/**
	 * Returns the HashMap of players and the names of the currently selected resourcepack.
	 * @return The HashMap
	 */
	public HashMap<Player, String> getCurrentResourcepacks() {
		return this.currentPacks;
	}
	
	/**
	 * Clears the resourcepack for all players that currently have a resourcepack selected.
	 */
	public void clearSelectedPacks() {
		for(Player p : this.currentPacks.keySet())
			this.clearResourcepack(p);
		
	}
	
	/**
	 * Saves all resourcepack settings and selected resourcepacks in the config.
	 * (The config itself is not automatically saved to the disk!)
	 */
	public void saveConfig() {
		this.saveConfigPacks();
		this.saveConfigPlayers();
	}
	
	/**
	 * Saves all resourcepack settings in the config.
	 * (The config itself is not automatically saved to the disk!)
	 */
	public void saveConfigPacks() {
		this.config.set("emptyPackURL", EMPTY_PACK.getURL());
		ConfigurationSection section = this.config.createSection("resourcepacks");
		for(Resourcepack pack : this.packs.values()) {
			if(pack == EMPTY_PACK) continue;
			ConfigurationSection p = section.createSection(pack.getName());
			p.set("displayName", pack.getDisplayName());
			p.set("url", pack.getURL());
			p.set("addedBy", pack.getAddedBy());
			p.set("generalPermission", pack.getGeneralPermission().toString());
			p.set("useSelfPermission", pack.getUseSelfPermission().toString());
		}
	}
	
	/**
	 * Saves all currently selected resourcepacks in the config.
	 * (The config itself is not automatically saved to the disk!)
	 */
	public void saveConfigPlayers() {
		ConfigurationSection section = this.config.createSection("players");
		for(Player player : this.currentPacks.keySet()) {
			ConfigurationSection p = section.createSection(player.getName());
			p.set("pack", this.currentPacks.get(player));
			p.set("locked", this.getLocked(player));
		}
	}
	
	/**
	 * Saves all the selected resourcepacks of the given player in the config.
	 * (The config itself is not automatically saved to the disk!)
	 */
	public void saveConfigForPlayer(Player player) {
		ConfigurationSection section = this.config.createSection("players");
		ConfigurationSection p = section.createSection(player.getName());
		p.set("pack", this.currentPacks.get(player));
		p.set("locked", this.getLocked(player));
	}
	
	/**
	 * Populates the resourcepack list and selected resourcepacks of players with
	 * the values stored in the config. Any unsaved changes will be discarded, use
	 * {@link #saveConfig()} to save them before.
	 */
	public void loadFromConfig() {
		this.packs.clear();
		this.packs.put(EMPTY_PACK.getName(), EMPTY_PACK);
		
		ConfigurationSection section = this.config.getConfigurationSection("resourcepacks");
		if(section != null) {
			for(String name : section.getKeys(false)) {
				ConfigurationSection p = section.getConfigurationSection(name);
				Resourcepack pack = new Resourcepack(
						name,
						p.getString("displayName"),
						p.getString("url"),
						p.getString("addedBy"),
						Permission.valueOf(p.getString("generalPermission")),
						Permission.valueOf(p.getString("useSelfPermission")));
				this.packs.put(name, pack);
			}
		}
		
		section = this.config.getConfigurationSection("players");
		if(section != null) {
			for(String name : section.getKeys(false)) {
				ConfigurationSection p = section.getConfigurationSection(name);
				Player player = Bukkit.getPlayerExact(name);
				if(player != null) {
					this.setResourcepack(player, this.getResourcepackForName(p.getString("pack")));
					this.setLocked(player, p.getBoolean("locked"));
				}
			}
		}
	}
	
	/**
	 * Initializes the given player with the state stored in the config.
	 * @param player The player
	 */
	public void loadPlayerFromConfig(Player player) {
		if(player == null) return;
		ConfigurationSection p = this.config.getConfigurationSection("players." + player.getName());
		if(p != null) {
			this.setResourcepack(player, this.getResourcepackForName(p.getString("pack")));
			this.setLocked(player, p.getBoolean("locked"));
		}
	}
	
	/**
	 * Sets the locked status of the player to the given value
	 * @param player The player
	 * @param locked The status
	 */
	public void setLocked(Player player, boolean locked) {
		player.setMetadata("resourcepackLocked", new FixedMetadataValue(this.plugin, locked));
	}
	
	/**
	 * Returns the locked status of the given player
	 * @param player The player
	 * @return The status
	 */
	public boolean getLocked(Player player) {
		List<MetadataValue> values = player.getMetadata("resourcepackLocked");
		for (MetadataValue value : values)
			if (value.getOwningPlugin().getDescription().getName().equals(this.plugin.getDescription().getName()))
				return value.asBoolean();
		return false;
	}
	
	/**
	 * Checks if the given String is a valid URL.
	 * @param url The String to check
	 * @return True if the String is a valid URL
	 */
	public static boolean isValidURL(String url) {
		return (url != null && !url.isEmpty() && (url.startsWith("http://") || url.startsWith("https://")));
	}
}