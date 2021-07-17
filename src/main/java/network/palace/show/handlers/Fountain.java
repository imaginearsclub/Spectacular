package network.palace.show.handlers;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.util.Vector;

public class Fountain {
    public double duration;
    public Location loc;
    public Material mat;
    public Vector force;

    public Fountain(Location loc, double duration, Material mat, Vector force) {
        this.loc = loc;
        this.duration = duration;
        this.mat = mat;
        this.force = force;
    }

    public Location getLocation() {
        return loc;
    }

    public void setLocation(Location loc) {
        this.loc = loc;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public Vector getForce() {
        return force;
    }

    public void setForce(Vector force) {
        this.force = force;
    }

    public Material getMat() {
        return mat;
    }

    public void setMat(Material mat) {
        this.mat = mat;
    }
}