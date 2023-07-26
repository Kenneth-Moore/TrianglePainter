// This is a triangle along with some billiard methods
// Use it when you need a trianglular billiard that is convenient to unfold

package geometry;

import java.util.ArrayList;

public class TriangleBilliard {

    // side and orient are the side the shot is coming from and the triangle's orientation
    // with respect to the original triangle in the unfolding. Side is either 0, 1, or 2,
    // and orient is either 1 or -1.
    public final int side;
    public final int orient;

    // vertices of the triangle
    public final Vector2 vertexA;
    public final Vector2 vertexB;
    public final Vector2 vertexC;

    // these a and b angles are those 2 first angles used to contstruct the triangle
    public final double aAngle;
    public final double bAngle;

    private TriangleBilliard(final Vector2 vertexA, final Vector2 vertexB, final Vector2 vertexC, final int whatSide,
                             final int orient, final double aAngle, final double bAngle) {

        if (aAngle + bAngle > Math.PI) {throw new RuntimeException(
        		"Error: Angles given to a TriangleBilliard sum to over pi radians");
        }
        
        this.vertexA = vertexA;
        this.vertexB = vertexB;
        this.vertexC = vertexC;
        this.side = whatSide;
        this.orient = orient;
        this.aAngle = aAngle;
        this.bAngle = bAngle;
    }

    public static TriangleBilliard create(
        final double xAngle, final double yAngle, final double base) {

        final double baseWidth = Math.sin(xAngle + yAngle);

        final Vector2 vertexA = Vector2.create(0, 0);
        Vector2 vertexB = Vector2.create(1, 0);

        final double cx = Math.sin(yAngle) * Math.cos(xAngle) + vertexA.x;
        final double cy = Math.sin(yAngle) * Math.sin(xAngle) + vertexA.y;

        vertexB = Vector2.create(base, 0);
        final Vector2 vertexC = Vector2.create(cx, cy).scale(base/baseWidth);

        return new TriangleBilliard(vertexA, vertexB, vertexC, 2, 1, xAngle, yAngle);
    }

    // calculates the next unfolded triangle of this triangle
    public static TriangleBilliard getNext(TriangleBilliard initial, final boolean left) {

        final Vector2 oldA = initial.vertexA;
        final Vector2 oldB = initial.vertexB;
        final Vector2 oldC = initial.vertexC;

        final int newSide;
        final Vector2 newA;
        final Vector2 newB;
        final Vector2 newC;

        if (left) {
            newA = oldA;
            newB = oldC;
            newC = Vector2.reflect(oldA, oldC, oldB);
            newSide = mod3(initial.side + initial.orient * 2);

        } else {
            newA = oldC;
            newB = oldB;
            newC = Vector2.reflect(oldB, oldC, oldA);
            newSide = mod3(initial.side + initial.orient);
        }

        return new TriangleBilliard(newA, newB, newC, newSide, -initial.orient, initial.aAngle, initial.bAngle);
    }
    
    public static TriangleBilliard rotate(TriangleBilliard initial, final boolean left) {
    	final Vector2 oldA = initial.vertexA;
        final Vector2 oldB = initial.vertexB;
        final Vector2 oldC = initial.vertexC;

        final int newSide;
        final Vector2 newA;
        final Vector2 newB;
        final Vector2 newC;

        if (left) {
            newA = oldA;
            newB = oldC;
            newC = oldB;
            newSide = mod3(initial.side + initial.orient * 2);

        } else {
            newA = oldC;
            newB = oldB;
            newC = oldA;
            newSide = mod3(initial.side + initial.orient);
        }

        return new TriangleBilliard(newA, newB, newC, newSide, -initial.orient, initial.aAngle, initial.bAngle);
    }
    
    public double getSpecialAngle(final double pos) {
    	double ans = Math.atan2(vertexC.y, vertexC.x - pos);
    	if (ans < - (Math.PI/2)) {
    		ans = (-ans) + (2 * (Math.PI + ans));
    	}
    	
        return ans;
    }

    public static int mod3(int value) {
        while (value >= 3) {
            value -= 3;
        }
        while (value < 0) {
            value += 3;
        }
        return value;
    }
    
    public TriangleBilliard copy() {
    	return new TriangleBilliard(vertexA, vertexB, vertexC, side, orient, aAngle, bAngle);
    }
    
    public ArrayList<Vector2> getPoints() {
    	final ArrayList<Vector2> points = new ArrayList<>();
    	points.add(vertexA); 
    	points.add(vertexB); 
    	points.add(vertexC); 
    	return points;
    }

    @Override
    public String toString() {
        return this.side + "/" + this.orient + 
        		" (" + this.vertexA + ", " + this.vertexB + ", " + this.vertexC + ")";
    }
}
