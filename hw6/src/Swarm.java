import java.util.ArrayList;
import java.util.List;

/**
 * Represents a swarm of Particles. Remembers the best found positions of its particles.
 */
public class Swarm {

    private double bestX, bestY;
    private double bestSize;
    private double bestAngle;
    private final List<Particle> particles = new ArrayList<>();

    public void addNewParticle(Polygon polygon, double x, double y) {
        particles.add(new Particle(this, polygon, x, y));
    }

    public List<Particle> particles() {
        return particles;
    }

    public double bestX() {
        return bestX;
    }

    public void bestX(double bestX) {
        this.bestX = bestX;
    }

    public double bestY() {
        return bestY;
    }

    public void bestY(double bestY) {
        this.bestY = bestY;
    }

    public double bestSize() {
        return bestSize;
    }

    public void bestSize(double bestSize) {
        this.bestSize = bestSize;
    }

    public double bestAngle() {
        return bestAngle;
    }

    public void bestAngle(double bestAngle) {
        this.bestAngle = bestAngle;
    }

}
