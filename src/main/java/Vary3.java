

import geometry.TriangleBilliard;

public final class Vary3 {
    private static final double OFFSET = 0.05;
    
	private static void recurseFireAway(
	    final int min, final int max, final double specMin, final double specMax, final double initPosition,
	    final int depth, final SideSum sideSum, final TriangleBilliard billiard) {
	
		if (depth >= max) {
		    return;
		}
	
		if (depth > min) {
			// here we check if we have reached a periodic path
	
		    if (Math.abs(sideSum.sum()) < OFFSET && billiard.side == 2 && billiard.orient == 1) {
	
		        final double perfectAngle = Math.atan2(billiard.vertexA.y, billiard.vertexA.x + initPosition);
	
		        if (specMax > perfectAngle && perfectAngle > specMin) {
		            // wait longer
		        }
		    }
		}
	
		final double specialAngle = billiard.getSpecialAngle(0.5);
	
		if (specMax > specialAngle) {
		    // go left
		    final TriangleBilliard leftBilliard = TriangleBilliard.getNext(billiard, true);
		    final int leftSwap = 3 - billiard.side - leftBilliard.side;
	
		    sideSum.add(leftSwap);
	
		    recurseFireAway(min, max, Math.max(specialAngle, specMin), specMax, initPosition,
		                    depth + 1, sideSum, leftBilliard);
	
		    sideSum.sub(leftSwap);
		}
	
		if (specMin < specialAngle) {
		    // go right
		    final TriangleBilliard rightBilliard = TriangleBilliard.getNext(billiard, false);
		    final int rightSwap = 3 - billiard.side - rightBilliard.side;
	
		    sideSum.sub(rightSwap);
	
		    recurseFireAway(min, max, specMin, Math.min(specialAngle, specMax), initPosition,
		                    depth + 1, sideSum, rightBilliard);
	
		    sideSum.add(rightSwap);
		}
	}
	
	public static void fireAway(final int movesMin, final int movesMax,
            final double xAngle, final double yAngle, final double pos) {
		
        final TriangleBilliard billiard = TriangleBilliard.create(xAngle, yAngle, pos);
        final SideSum sideSum = new SideSum(xAngle, yAngle);
        
        recurseFireAway(movesMin, movesMax, 0, Math.PI, pos, 0, sideSum, billiard);
	}
	
}
