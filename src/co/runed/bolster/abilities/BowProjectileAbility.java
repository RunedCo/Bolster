package co.runed.bolster.abilities;

import org.bukkit.util.Vector;

public abstract class BowProjectileAbility extends Ability {
    float force;
    Vector velocity;

    public float getForce() {
        return this.force;
    }

    public void setForce(float force) {
        this.force = force;
    }

    public Vector getVelocity() {
        return this.velocity;
    }

    public void setVelocity(Vector velocity) {
        this.velocity = velocity;
    }

    @Override
    public void onPostActivate() {
        this.setVelocity(null);
        this.setForce(0);
    }
}
