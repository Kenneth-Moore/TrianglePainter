

public final class SideSum {

    private final int[] coeffs;

    private final double x;
    private final double y;
    private final double z;

    public SideSum(final double x, final double y) {
        this.coeffs = new int[3];

        this.x = x;
        this.y = y;
        this.z = Math.PI - x - y;
    }

    public void add(final int num) {
        this.coeffs[num] += 1;
    }

    public void sub(final int num) {
        this.coeffs[num] -= 1;
    }

    public double sum() {
        return this.coeffs[0] * this.x + this.coeffs[1] * this.y + this.coeffs[2] * this.z;
    }
    
    @Override
    public String toString() {
    	return coeffs[0] + " " + coeffs[1] + " " + coeffs[2];
    }
}
