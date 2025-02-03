package one.tranic.t.base.player;

/**
 * Represents a location in a specific world with precise x, y, and z coordinates.
 * <p>
 * This class is utilized for storing and transferring positional data typically
 * associated with a player or an object in a Minecraft-like environment.
 */
public record Location(String world, double x, double y, double z, float yaw, float pitch) {
}
