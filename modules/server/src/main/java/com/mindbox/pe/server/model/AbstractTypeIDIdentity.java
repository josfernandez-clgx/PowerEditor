package com.mindbox.pe.server.model;

/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 3.0.0
 */
public abstract class AbstractTypeIDIdentity {

	public static int[] toIDArray(AbstractTypeIDIdentity[] entities) {
		if (entities == null) return null;
		int[] array = new int[entities.length];
		for (int i = 0; i < array.length; i++) {
			array[i] = entities[i].id;
		}
		return array;
	}

	private final int id;
	private final int type;

	protected AbstractTypeIDIdentity(int type, int id) {
		if (type < 0) {
			throw new IllegalArgumentException("Invalid type: " + type);
		}
		if (id <= 0) {
			throw new IllegalArgumentException("Invalid id: " + id);
		}
		this.id = id;
		this.type = type;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj instanceof AbstractTypeIDIdentity) {
			return id == ((AbstractTypeIDIdentity) obj).id && type == ((AbstractTypeIDIdentity) obj).type;
		}
		else {
			return false;
		}
	}

	protected final int getId() {
		return id;
	}

	protected final int getType() {
		return type;
	}

	@Override
	public int hashCode() {
		return (type + "." + id).hashCode();
	}

	@Override
	public String toString() {
		return getClass().getName() + "[type=" + type + ",id=" + id + "]";
	}
}