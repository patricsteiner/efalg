import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ParticleSwarmMaxSquareAlgorithm {

    private int delay;
    private Polygon polygon;
    private Swarm swarm;


    public ParticleSwarmMaxSquareAlgorithm(Polygon polygon, int delay) {
        this.polygon = polygon;
        this.delay = delay;
        initSwarm();
    }

    public void run() {
        int epochs = 10000;
        for (int i = 0; i < epochs; i++) {
            for (Particle particle : swarm.particles()) {
                particle.tryIncreaseFitness();
            }
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void initSwarm() {
        int nParticles = 100;
        List<Particle> particles = new ArrayList<>(nParticles);
        for (int i = 0; i < nParticles; i++) {
            double x, y;
            do { // pick random point in polygon
                x = Math.random() * polygon.width() + polygon.minX();
                y = Math.random() * polygon.height() + polygon.minY();
            } while (!polygon.contains(x, y));
            particles.add(new Particle(polygon, x, y));
        }
        swarm = new Swarm(particles);
    }

    public Polygon polygon() {
        return polygon;
    }

    public List<Square> squares() {
        return swarm.particles().stream().map(Particle::square).collect(Collectors.toList());
    }

}
