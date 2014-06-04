package de.rene_zeidler.dynamicresourcepacks.resourcepacks;

public class FixedAssignment extends ResourcepackAssignment {
	private String name;
	private Resourcepack resourcepack;
	private boolean removeOnDisconnect;
	private boolean keepAfterRestart;
	
	public FixedAssignment(String name, int priority,
			Resourcepack resourcepack, boolean removeOnDisconnect,
			boolean keepAfterRestart, long timeAdded) {
		this.name = name;
		this.priority = priority;
		this.resourcepack = resourcepack;
		this.removeOnDisconnect = removeOnDisconnect;
		this.keepAfterRestart = keepAfterRestart;
		this.timeAdded = timeAdded; System.currentTimeMillis();
	}
	
	public FixedAssignment(String name, int priority,
			Resourcepack resourcepack, boolean removeOnDisconnect,
			boolean keepAfterRestart) {
		this(name, priority, resourcepack, removeOnDisconnect, keepAfterRestart, System.currentTimeMillis());
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public Resourcepack getResourcepack() {
		return this.resourcepack;
	}

	@Override
	public boolean removeOnDisconnect() {
		return this.removeOnDisconnect;
	}

	@Override
	public boolean keepAfterRestart() {
		return this.keepAfterRestart;
	}

	@Override
	public boolean isStillValid() {
		return true;
	}
}
