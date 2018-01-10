import java.util.List;

public class Swarm {

    private double bestX, bestY;
    private double bestSize;
    private List<Particle> particles;

    Swarm(List<Particle> particles) {
        this.particles = particles;
    }

    List<Particle> particles() {
        return particles;
    }

    public double bestX() {
        return bestX;
    }

    public void setBestX(double bestX) {
        this.bestX = bestX;
    }

    public double bestY() {
        return bestY;
    }

    public void setBestY(double bestY) {
        this.bestY = bestY;
    }

    public double bestSize() {
        return bestSize;
    }

    public void setBestSize(double bestSize) {
        this.bestSize = bestSize;
    }

}
