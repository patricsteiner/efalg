public class Particle {

    private final Swarm swarm;
    private final double lazyness;
    private final double cognitiveBias;
    private final double socialBias;
    private final Polygon polygon;
    private final Square square;
    private double x, y;
    private double velocityX, velocityY;
    private double bestX, bestY;
    private double bestSize;
    private double bestAngle;

    public Particle(Swarm swarm, Polygon polygon, double x, double y) {
        this.swarm = swarm;
        this.polygon = polygon;
        this.x = x;
        this.y = y;
        bestX = x;
        bestY = y;
        square = new Square(polygon, x, y, polygon.width() / 1000);
        velocityX = 0;
        velocityY = 0;
        lazyness = 0.01;
        cognitiveBias = .2;
        socialBias = 1;
    }

    public Square square() {
        return square;
    }

    public void maximizeFitnessAtCurrentLocation() {
        square.rotateAndGrow();
        if (square.size() > bestSize) {
            bestSize = square.size();
            bestX = x;
            bestY = y;
            bestAngle = square.angle();
        }
        if (bestSize >= swarm.bestSize()) {
            swarm.bestSize(bestSize);
            swarm.bestX(x);
            swarm.bestY(y);
            swarm.bestAngle(bestAngle);
        }
    }

    public void move() {
        double r1 = Math.random();
        double r2 = Math.random();
        velocityX = lazyness * velocityX + cognitiveBias * r1 * (bestX - x) + socialBias * r2 * (swarm.bestX() - x);
        velocityY = lazyness * velocityY + cognitiveBias * r1 * (bestY - y) + socialBias * r2 * (swarm.bestY() - y);
        x += velocityX;
        y += velocityY;
        square.center(x, y);
    }

}
