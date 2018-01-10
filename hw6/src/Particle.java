public class Particle {


    private double x, y;
    private double velocityX, velocityY;
    private double acceleration;
    private Polygon polygon;
    private Square square;
    private double bestX, bestY;
    private double bestSize;

    public Particle(Polygon polygon, double x, double y) {
        this.polygon = polygon;
        this.x = x;
        this.y = y;
        square = new Square(polygon, x, y, polygon.width() / 1000);
    }

    public Square square() {
        return square;
    }

    public void tryIncreaseFitness() {
        if (square.canGrow()) square.grow();
        else if (square.angle() < 90 && !square.frozen()) {
            if (square.size() > square.maxSize()) {
                square.maxSize(square.size());
                square.bestAngle(square.angle());
            }
            square.tilt();
        } else {
            square.freeze();
            // start finetuning?
        }
    }
}
