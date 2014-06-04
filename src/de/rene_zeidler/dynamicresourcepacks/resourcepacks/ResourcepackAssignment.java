package de.rene_zeidler.dynamicresourcepacks.resourcepacks;

import org.bukkit.configuration.ConfigurationSection;

import de.rene_zeidler.dynamicresourcepacks.DynamicResourcepacks;

public abstract class ResourcepackAssignment implements Comparable<ResourcepackAssignment> {
	protected int priority;
	protected long timeAdded;

	/**
	 * Returns the name for this assignment that is used in commands to reference it
	 * @return
	 */
	public abstract String getName();
	
	/**
	 * Returns the priority for this assignment (higher values override)
	 * @return
	 */
	public int getPriority() {
		return this.priority;
	}
	
	/**
	 * Returns the resourcepack for this assignment
	 * @return
	 */
	public abstract Resourcepack getResourcepack();
	
	/**
	 * When true, the assignment gets removed automatically when the player disconnects
	 * @return
	 */
	public abstract boolean removeOnDisconnect();
	
	/**
	 * When true, this assignment is kept after a server restart (i.e. is saved in the config)
	 * @return
	 */
	public abstract boolean keepAfterRestart();
	
	/**
	 * Gets checked when the assignment is at the top of the priority list
	 * When false, this assignment is removed
	 * @return
	 */
	public abstract boolean isStillValid();
	
	/**
	 * The time when this assignment was added
	 * When the priority of two assignments is the same, the newer one overrides the older one
	 * @return
	 */
	public long timeAdded() {
		return this.timeAdded;
	}
	
	public int compareTo(ResourcepackAssignment assignment) {
		int c = assignment.getPriority() - this.getPriority();
		if(c == 0) {
			if(assignment.timeAdded() > this.timeAdded())
				c = 1;
			else if(assignment.timeAdded() < this.timeAdded())
				c = -1;
		}
		return c;
	}
	
	public static ResourcepackAssignment fromConfig(DynamicResourcepacks plugin, String name, ConfigurationSection section) {
		ResourcepackManager packManager = plugin.getResourcepackManager();
		
		String type = section.getString("type");
		String packStr = section.getString("pack");
		if(packStr == null || !packManager.resourcepackExists(packStr)) return null;
		Resourcepack resourcepack = packManager.getResourcepackForName(packStr);
		int priority = section.getInt("priority");
		long timeAdded = section.getLong("timeAdded");
		boolean removeOnDisconnect = section.getBoolean("removeOnDisconnect");
		
		ResourcepackAssignment assignment;
		//if(type.equals("---")) {
			
		//} else {
			assignment = new FixedAssignment(name, priority, resourcepack, removeOnDisconnect, true, timeAdded);
		//}
		
		return assignment;
	}
}
