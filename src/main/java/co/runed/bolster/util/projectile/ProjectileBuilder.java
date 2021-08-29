package co.runed.bolster.util.projectile;

import co.runed.bolster.Bolster;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ProjectileBuilder {
    Entity projectile;
    LivingEntity caster;
    List<Supplier<LivingEntity>> onHitEntity = new ArrayList<>();
    List<Supplier<Block>> onHitBlock = new ArrayList<>();

    public ProjectileBuilder projectile(Entity projectile) {
        this.projectile = projectile;

        return this;
    }

    public ProjectileBuilder onHitEntity(Supplier<LivingEntity> onHit) {
        this.onHitEntity.add(onHit);

        return this;
    }

    public ProjectileBuilder onHitBlock(Supplier<Block> onHit) {
        this.onHitBlock.add(onHit);

        return this;
    }

    public EntityProjectile build() {
        var entityProjectile = new EntityProjectile(Bolster.getInstance(), caster, projectile);

        return entityProjectile;
    }
}
