

public final class PixelRadianMap {
    // height and width of the viewing screen in pixels
    private final int width;
    private final int height;

    private double xScale;
    private double yScale;

    // translation in radians
    private double translateX;
    private double translateY;
    
    private double radWid;
    private double radHi;

    public PixelRadianMap(final int width, final int height) {
    	
    	final double scale = 0.3;
    	
        this.width = width;
        this.height = height;
        this.xScale = scale;
        this.yScale = scale * width / height;
        this.radWid = radianX(width);
        this.radHi = radianY(height);
        this.translateX = (1 - radWid) / 2;
        this.translateY = -0.2;
    }

    public double radianX(final double pixelX) {
        return pixelX * Math.PI / this.width / this.xScale + this.translateX;
    }

    public double radianY(final double pixelY) {
        return pixelY * Math.PI / this.height / this.yScale + this.translateY;
    }

    public double pixelX(final double radianX) {
        return (radianX - this.translateX) * this.width * this.xScale / Math.PI;
    }

    public double pixelY(final double radianY) {
        return (radianY - this.translateY) * this.height * this.yScale / Math.PI;
    }
    
    public void scaleBy(final double scaleFactor) {
        this.xScale *= scaleFactor;
        this.yScale *= scaleFactor;
    }
    
    public void translateX(final double translate) {
    	this.translateX = (translate - radWid) / 2;
    }
    
    public void translateY(final double translate) {
    	this.translateY = (translate - radHi) / 2;
    }
    
    public void panX(final double translate) {
    	this.translateX += translate;
    }
    
    public void panY(final double translate) {
    	this.translateY += translate;
    }
    
    public void resetLocation() {
    	this.xScale = 0.3;
    	this.yScale = 0.3 * width / height;
    	this.translateX = (1 - radWid) / 2;
        this.translateY = -0.2;
    }
    
}
