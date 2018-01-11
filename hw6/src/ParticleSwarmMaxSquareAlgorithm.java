import java.util.List;
import java.util.stream.Collectors;

public class ParticleSwarmMaxSquareAlgorithm {

    private final int delay;
    private final Polygon polygon;
    private Swarm swarm;

    public ParticleSwarmMaxSquareAlgorithm(Polygon polygon, int delay) {
        this.polygon = polygon;
        this.delay = delay;
        initSwarm();
    }

    public void run() {
        int epochs = 100; // TODO define better termination criteria, maybe fine tune after convergence.
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
    }

    private void initSwarm() {
        int nParticles = 200;
        swarm = new Swarm();
        for (int i = 0; i < nParticles; i++) {
            double x, y;
            do { // pick random point in polygon
                x = Math.random() * polygon.width() + polygon.minX();
                y = Math.random() * polygon.height() + polygon.minY();
            } while (!polygon.contains(x, y));
            swarm.addNewParticle(polygon, x, y);
        }
    }

    public Polygon polygon() {
        return polygon;
    }

    public List<Square> squares() {
        return swarm.particles().stream().map(Particle::square).collect(Collectors.toList());
    }

}