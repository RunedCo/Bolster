package co.runed.bolster.fx;

import de.slikey.effectlib.Effect;
import de.slikey.effectlib.EffectManager;
import de.slikey.effectlib.EffectType;
import de.slikey.effectlib.util.VectorUtils;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.util.Vector;

public class CustomCircleEffect extends Effect
{
    public Particle particle;
    public double xRotation;
    public double yRotation;
    public double zRotation;
    public double angularVelocityX;
    public double angularVelocityY;
    public double angularVelocityZ;
    public float radius;
    protected float step;
    public double xSubtract;
    public double ySubtract;
    public double zSubtract;
    public boolean enableRotation;
    public int particles;
    public boolean wholeCircle;

    public CustomCircleEffect(EffectManager effectManager)
    {
        super(effectManager);
        this.particle = Particle.VILLAGER_HAPPY;
        this.zRotation = 0.0D;
        this.angularVelocityX = 0.015707963267948967D;
        this.angularVelocityY = 0.018479956785822312D;
        this.angularVelocityZ = 0.02026833970057931D;
        this.radius = 0.4F;
        this.step = 0.0F;
        this.enableRotation = true;
        this.particles = 20;
        this.wholeCircle = false;
        this.type = EffectType.REPEATING;
        this.period = 2;
        this.iterations = 50;
    }

    public void reset()
    {
        this.step = 0.0F;
    }

    public void onRun()
    {
        Location location = this.getLocation();
        location.subtract(this.xSubtract, this.ySubtract, this.zSubtract);
        double inc = 6.283185307179586D / (double) this.particles;
        int steps = this.wholeCircle ? this.particles : 1;

        for (int i = 0; i < steps; ++i)
        {
            double angle = (double) this.step * inc;
            Vector v = new Vector();
            v.setX(Math.cos(angle) * (double) this.radius);
            v.setZ(Math.sin(angle) * (double) this.radius);
            VectorUtils.rotateVector(v, this.xRotation, this.yRotation, this.zRotation);
            if (this.enableRotation)
            {
                VectorUtils.rotateVector(v, this.angularVelocityX * (double) this.step, this.angularVelocityY * (double) this.step, this.angularVelocityZ * (double) this.step);
            }

            this.display(this.particle, location.clone().add(v), 0.0F, this.particleCount);
            ++this.step;
        }

    }
}
