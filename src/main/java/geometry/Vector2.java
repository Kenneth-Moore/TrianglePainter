package geometry;

import java.util.Objects;

// This class is immutable
public final class Vector2 {
    public final double x;
    public final double y;

    private Vector2(final double x, final double y) {
        this.x = x;
        this.y = y;
    }

    // this - v
    public Vector2 sub(final Vector2 v) {
        final double newX = this.x - v.x;
        final double newY = this.y - v.y;
        return Vector2.create(newX, newY);
    }

    // this + v
    public Vector2 add(final Vector2 v) {
        final double newX = this.x + v.x;
        final double newY = this.y + v.y;
        return Vector2.create(newX, newY);
    }

    // scale * this
    public Vector2 scale(final double scale) {
        final double newX = scale * this.x;
        final double newY = scale * this.y;
        return Vector2.create(newX, newY);
    }
    
    public double norm() {
        return Math.hypot(this.x, this.y);
    }
    
    public static Vector2 normal(final Vector2 v) {
    	final double lenght = Math.sqrt(Math.pow(v.x, 2) + Math.pow(v.y, 2));
    	return v.scale(1 / lenght);
    }

    public static Vector2 create(final double x, final double y) {
        return new Vector2(x, y);
    }

    public static double dot(final Vector2 v, final Vector2 w) {
        return v.x * w.x + v.y * w.y;
    }

    public static double cross(final Vector2 v, final Vector2 w) {
        return v.x * w.y - v.y * w.x;
    }

    // a method to reflect one vector2 in a line made between 2 others.
    public static Vector2 reflect(final Vector2 l1, final Vector2 l2, final Vector2 reflectMe) {

        final Vector2 simple = reflectMe.sub(l2);
        final Vector2 refLine = l1.sub(l2);
        final Vector2 normal = refLine.scale(1 / Math.hypot(refLine.x, refLine.y));

        final double scale = Vector2.dot(simple, normal) * 2;

        return ((normal.scale(scale)).sub(simple)).add(l2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.x, this.y);
    }

    @Override
    public boolean equals(final Object obj) {
        final Vector2 other = (Vector2) obj;
        return this.x == other.x && this.y == other.y;
    }

    @Override
    public String toString() {
        return "(" + this.x + ", " + this.y + ")";
    }
}
