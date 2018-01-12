import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This algorithm tries to find the maximum possible square in a polygon by using particles, each wrapping a square, that
 * follow a swarm behaviour.
 * Basically what happens is this:
 * - Every particle starts at a random location within the polygon.
 * - Every particle contains a square that is growing and rotating until it has reached the maximum size for the particles location.
 * - When a square can't grow any further, the particle moves according to its velocity, which depends on the swarms optimum
 *   value as well as the particles optimum and also some other factors (for details see Particle.java).
 * - When a particle moves outside the polygon, reset it's size to make sure it notices that it is in a bad spot and goes back.
 * - Repeat this for a given amount of epochs and the squares will converge.
 */
public class ParticleSwarmMaxSquareAlgorithm {

    private int particles;
    private final int epochs;
    private final int delay;
    private final Polygon polygon;
    private Swarm swarm;

    public ParticleSwarmMaxSquareAlgorithm(Polygon polygon, int particles, int epochs, int delay) {
        this.polygon = polygon;
        this.particles = particles;
        this.epochs = epochs;
        this.delay = delay;
        initSwarm();
    }

    public Square run() {
        for (int i = 0; i < epochs; i++) {
            for (Particle particle : swarm.particles()) {
                particle.maximizeFitnessAtCurrentLocation();
                particle.move();
            }
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Square maxSquare = new Square(polygon, swarm.bestX(), swarm.bestY(), swarm.bestSize());
        maxSquare.angle(swarm.bestAngle());
        return maxSquare;
    }

    private void initSwarm() {
        swarm = new Swarm();
        for (int i = 0; i < particles; i++) {
            double x, y;
            do {
                x = Math.random() * polygon.width() + polygon.minX();
                y = Math.random() * polygon.height() + polygon.minY();
            } while (!polygon.contains(x, y));
            swarm.addNewParticle(polygon, x, y);
        }
    }

    public List<Square> squares() {
        return swarm.particles().stream().map(Particle::square).collect(Collectors.toList());
    }

}