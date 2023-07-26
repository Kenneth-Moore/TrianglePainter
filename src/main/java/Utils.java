

import java.util.Optional;

import geometry.TriangleBilliard;
import geometry.Vector2;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

public class Utils {
	
	private static final double OFFSET = 0.00000000005;

    public static final int numThreads = (int) (Runtime.getRuntime().availableProcessors() * 0.5);
	
	public static Shape smarterLine(final double sX, final double sY, final double eX, final double eY, 
    		final int width, final int height) {
		final Shape superLine = Shape.intersect(new Line(sX, sY, eX, eY), new Rectangle(width, height));
		superLine.setLayoutX(eX - (eX - sX + width) / 2);
		superLine.setLayoutY(eY - (eY - sY + height) / 2);
		return superLine;
	}
	
	public static void setUpEnterPress(final Node node, final Button button) {
		node.setOnKeyPressed(new EventHandler<KeyEvent>()
        {
            @Override
            public void handle(KeyEvent ke)
            {
                if (ke.getCode().equals(KeyCode.ENTER))
                {
                    button.fire();
                }
            }
        });
	}
	
    // the list pr (problem list) is in the order:
    // start is too left, start is too right, start is too low, start is too high,
    // end is too left, end is too right, end is too low, end is too high
    public static Optional<Line> smartLine(final double sX, final double sY, final double eX, final double eY, 
    		final int width, final int height) {
    	final boolean[] pr = {sX < 0, sX > width, sY < 0, sY > height, eX < 0, eX > width, eY < 0, eY > height};
    	
    	if (!pr[0] && !pr[1] && !pr[2] && !pr[3] && !pr[4] && !pr[5] && !pr[6] && !pr[7]) {
    		// the line is entirely inside
    		return Optional.of(new Line(sX, sY, eX, eY));
    	} else if ((pr[0] && pr[4]) || (pr[1] && pr[5]) || (pr[2] && pr[6]) || (pr[3] && pr[7])) {
    		// the line is completely outside (the line can be completely outside without satisfying
    		// this condition!)
			return Optional.empty();
		} else if ((!pr[0] && !pr[1] && !pr[2] && !pr[3])) {
			// the start is inside
			final Vector2 direct = Vector2.normal(Vector2.create(sX - eX, sY - eY));
			final Vector2 start = Vector2.create(sX, sY);
			final Vector2 lineEnd = onePointOffLine(start, direct, width, height);
			
			return Optional.of(new Line(sX, sY, lineEnd.x, lineEnd.y));
			
		} else if ((!pr[4] && !pr[5] && !pr[6] && !pr[7])) {
			// the end is inside
			final Vector2 direct = Vector2.normal(Vector2.create(eX - sX, eY - sY));
			final Vector2 start = Vector2.create(eX, eY);
			final Vector2 lineStart = onePointOffLine(start, direct, width, height);
			
			return Optional.of(new Line(lineStart.x, lineStart.y, eX, eY));
		} 
    	
    	//here we attempt to solve when a line has both ends outside but is partially inside
    	
    	
    	return Optional.empty();
    }
    
    // if you have a line which has one end on screen, you can use this to find the onscreen part
    // of that line
    private static Vector2 onePointOffLine(final Vector2 start, final Vector2 direct, 
    		final int width, final int height) {
    	final Vector2 end;

		final double angle = Math.atan2(direct.y, direct.x);
		
		if (Math.abs(angle - Math.PI) < OFFSET || Math.abs(angle + Math.PI) < OFFSET) {
			end = Vector2.create(width, start.y);
			
    	} else if (Math.abs(angle - Math.PI / 2) < OFFSET) {
    		end = Vector2.create(start.x, 0);
    		
    	} else if (Math.abs(angle) < OFFSET) {
    		end = Vector2.create(0, start.y);
    		
    	} else if (Math.abs(angle + Math.PI / 2) < OFFSET) {
    		end = Vector2.create(start.x, height);
    		
    	} else if (-Math.PI < angle && angle < -Math.PI / 2) {
    		final double arg0 = (height - start.y) / Math.sin(angle);
    	    final double arg1 = (width - start.x) / Math.cos(angle);
			final double len = trueMin(arg0, arg1);
			end = start.add(direct.scale(len));

		} else if (-Math.PI / 2 < angle && angle < 0) {
			final double arg0 = (height - start.y) / Math.sin(angle);
    	    final double arg1 = start.x / Math.cos(angle);
			final double len = -trueMin(Math.abs(arg0), Math.abs(arg1));
			end = start.add(direct.scale(len));

		} else if (0 < angle && angle < Math.PI / 2) { 
			final double arg0 = start.y / Math.sin(angle);
    	    final double arg1 = start.x / Math.cos(angle);
			final double len = -trueMin(Math.abs(arg0), Math.abs(arg1));
			end = start.add(direct.scale(len));

		} else if (Math.PI / 2 < angle && angle < Math.PI) {
			final double arg0 = start.y / Math.sin(angle);
    	    final double arg1 = (width - start.x) / Math.cos(angle);
			final double len = -trueMin(Math.abs(arg0), Math.abs(arg1));
			end = start.add(direct.scale(len));

		} else {
			throw new RuntimeException("Something went wrong in 'onScreenLine' method");
		}

		return end;
    }
    
    // If you have a line which has two points off the screen, you can use this to find
    // the on screen portion.
    
    
    public static double trueMin(double a, double b) {
    	final double result;
    	if (Math.abs(a) < Math.abs(b)) {
    		result = a;
    	} else {
    		result = b;
    	}
    	return result;
    }
    
    // l is a list of size 4 which represents a rectangle, and p is another point. if p lies
    // outside of that rectangle, l will be changed so that p is on a boundary.
    public static void compare(double[] l, Vector2 p) {
    	if (p.x > l[0]) {
    		l[0] = p.x;
    	} else if (p.x < l[1]) {
    		l[1] = p.x;
    	} if (p.y > l[2]) {
    		l[2] = p.y;
    	} else if (p.y < l[3]) {
    		l[3] = p.y;
    	}
    }
    
    public static Tooltip toolTip(final String text) {
        final Tooltip tip = new Tooltip(text);
        tip.setPrefWidth(300);
        tip.setWrapText(true);

        return tip;
    }
    
    public static String hex(final Color color) {
        final long rd = Math.round(color.getRed() * 255);
        final long gr = Math.round(color.getGreen() * 255);
        final long bl = Math.round(color.getBlue() * 255);

        final String hex = String.format("%02x%02x%02x", rd, gr, bl);

        return "-fx-base: #" + hex;
    }
    
    public static Vector2 midBilliard(final TriangleBilliard billiard) {
    	Vector2[] other = {billiard.vertexB, billiard.vertexC};
    	Vector2 L = billiard.vertexA;
    	Vector2 R = billiard.vertexA;
    	Vector2 D = billiard.vertexA;
    	Vector2 U = billiard.vertexA;
    	for (int i = 0; i < 2; i++) {
    		if (other[i].x < L.x) {
    			L = other[i];
    		} else if (other[i].x >= R.x) {
    			R = other[i];
    		} if (other[i].y < D.y) {
    			D = other[i];
    		} else if (other[i].y >= U.y) {
    			U = other[i];
    		}
    	}
    	return Vector2.create((R.x + L.x) / 2, (D.y + U.y) / 2);
    }
    
    public static void colorButton(final Button button, final Color color, final Color clicked) {
        button.setStyle(hex(color));
        button.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> button.setStyle(hex(clicked)));
        button.addEventHandler(MouseEvent.MOUSE_RELEASED, e -> button.setStyle(hex(color)));
    }
    
	public static double atan3(double y, double x, boolean left) {
		double result = Math.atan2(y, x);
		if (result < 0) {
			if (left) {
				result = 0;
			} else {
				result = Math.PI;
			}
		}
		return result;
	}
    
    // we break into a the private field of tool tip behaviour and change it around
    // based on a post on https://coderanch.com/t/622070/java/control-Tooltip-visible-time-duration
//    public static void setupCustomTooltipBehavior(final int openDelayInMillis, final int visibleDurationInMillis,
//                                                  final int closeDelayInMillis) {
//        try {
//
//            Class<?> TTBehaviourClass = null;
//            final Class<?>[] declaredClasses = Tooltip.class.getDeclaredClasses();
//            for (final Class<?> c : declaredClasses) {
//                if (c.getCanonicalName().equals("javafx.scene.control.Tooltip.TooltipBehavior")) {
//                    TTBehaviourClass = c;
//                    break;
//                }
//            }
//
//            if (TTBehaviourClass == null) {
//                return;
//            }
//            final Constructor<?> constructor = TTBehaviourClass.getDeclaredConstructor(
//                Duration.class, Duration.class, Duration.class, boolean.class);
//            if (constructor == null) {
//                return;
//            }
//            constructor.setAccessible(true);
//            final Object newTTBehaviour = constructor.newInstance(
//                new Duration(openDelayInMillis), new Duration(visibleDurationInMillis),
//                new Duration(closeDelayInMillis), false);
//            if (newTTBehaviour == null) {
//                return;
//            }
//            final Field ttbehaviourField = Tooltip.class.getDeclaredField("BEHAVIOR");
//            if (ttbehaviourField == null) {
//                return;
//            }
//
//            ttbehaviourField.setAccessible(true);
//            ttbehaviourField.set(Tooltip.class, newTTBehaviour);
//
//        } catch (final Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
}
