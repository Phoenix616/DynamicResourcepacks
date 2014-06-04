package de.rene_zeidler.dynamicresourcepacks.resourcepacks;

import java.util.Iterator;
import java.util.PriorityQueue;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import de.rene_zeidler.dynamicresourcepacks.DynamicResourcepacks;

public class LocalPlayer {
	private DynamicResourcepacks plugin;
	
	private OfflinePlayer player;
	
	private boolean locked;
	private PriorityQueue<ResourcepackAssignment> assignments;
	private ResourcepackAssignment lastAssignment;
	private String lastSentURL;
	
	private boolean shouldUpdate = true;
	
	public LocalPlayer(DynamicResourcepacks plugin, OfflinePlayer player) {
		this.plugin = plugin;
		
		this.player = player;
		
		this.assignments = new PriorityQueue<ResourcepackAssignment>();
	}
	
	public LocalPlayer(DynamicResourcepacks plugin, OfflinePlayer player, ConfigurationSection section) {
		this(plugin, player);
		
		this.loadFromConfig(section);
	}
	
	public void loadFromConfig(ConfigurationSection section) {
		this.stopUpdates();
		
		//TODO: Load from config
		
		this.startUpdates();
	}
	
	public boolean isLocked() {
		return locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	public boolean addAssignment(ResourcepackAssignment assignment) {
		if(assignment == null)
			return false;
		else {
			this.assignments.add(assignment);
			this.update();
			return true;
		}
	}
	
	public boolean removeAssignment(ResourcepackAssignment assignment) {
		if(this.assignments.remove(assignment)) {
			this.update();
			return true;
		} else
			return false;
	}
	
	/**
	 * Removes all resourcepacks from the assignment queue with the given name
	 * @param name
	 * @return
	 */
	public boolean removeAssignmentsByName(String name) {
		Iterator<ResourcepackAssignment> iter = this.assignments.iterator();
		boolean removedSomething = false;
		
		while(iter.hasNext()) {
			if(iter.next().getName().equals(name)) {
				iter.remove();
				removedSomething = true;
			}
		}
		
		if(removedSomething) {
			this.update();
			return true;
		} else
			return false;
	}
	
	/**
	 * Returns the currently active assignment
	 * @return
	 */
	public ResourcepackAssignment getTopAssignment() {
		ResourcepackAssignment assignment = this.assignments.peek();
		if(assignment == null)
			return null;
		else if(assignment.isStillValid())
			return assignment;
		else {
			this.assignments.remove();
			return this.getTopAssignment();
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public Resourcepack getResourcepack() {
		ResourcepackAssignment assignment = this.getTopAssignment();
		if(assignment == null)
			return null;
		else
			return assignment.getResourcepack();
	}
	
	/**
	 * Sends the initial resourcepack (if any resourcepack is set)
	 */
	public void onLogin() {
		this.update();
	}
	
	/**
	 * Updates the player's assignments.
	 * All assignments that return true for removeOnDisconnect() will get removed.
	 */
	public void onDisconnect() {
		this.lastSentURL = null;
		Iterator<ResourcepackAssignment> iter = this.assignments.iterator();
		while(iter.hasNext())
			if(iter.next().removeOnDisconnect())
				iter.remove();
	}
	
	/**
	 * Updates the player's resourcepack to match the current top assignment
	 */
	public void update() {
		if(this.shouldUpdate) {
			ResourcepackAssignment topAssignment = this.getTopAssignment();
			if(topAssignment != this.lastAssignment) {
				this.lastAssignment = topAssignment;
				this.sendResourcepack(topAssignment.getResourcepack());
			}
		}
	}
	
	/**
	 * Temporarily stops updating the player's assignments and consequently don't
	 * send any new resourcepacks to the player.
	 * 
	 * Use this when you add multiple assignments at once that could appear at the
	 * top of the assignment queue.
	 */
	public void stopUpdates() {
		this.shouldUpdate = false;
	}
	
	/**
	 * Starts updates again and update the player immediately.
	 */
	public void startUpdates() {
		this.shouldUpdate = true;
		this.update();
	}
	
	/**
	 * Sends the given Resourcepack to the player
	 * If the player is offline, nothing happens
	 * If the last URL that was sent to the player matches the new URL, nothing happens
	 * @param pack the Resourcepack
	 */
	private void sendResourcepack(Resourcepack pack) {
		Player onlinePlayer = this.player.getPlayer();
		if(onlinePlayer != null) {
			if(!pack.getURL().equals(this.lastSentURL)) {
				this.lastSentURL = pack.getURL();
				onlinePlayer.setResourcePack(pack.getURL());
			}
		}
	}
	
	/**
	 * Resends the URL that was last sent to the player
	 * If the player is offline, nothing happens
	 * If no pack was sent before, nothing happens
	 */
	public void resend() {
		if(this.lastSentURL != null) {
			Player onlinePlayer = this.player.getPlayer();
			if(onlinePlayer != null)
				onlinePlayer.setResourcePack(this.lastSentURL);
		}
	}
	
	/**
	 * Returns the LocalPlayer object for the given Bukkit OfflinePlayer object or
	 * creates one if it doesn't exists.
	 * 
	 * @param plugin the DynamicResourcepacks instance
	 * @param player the Bukkit player
	 * @return the LocalPlayer
	 */
	public static LocalPlayer forPlayer(DynamicResourcepacks plugin, OfflinePlayer player) {
		return plugin.getPlayerManager().getLocalPlayer(player);
	}
}
