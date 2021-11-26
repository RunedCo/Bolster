package co.runed.bolster.util;

import co.runed.bolster.game.Team;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.stream.Collectors;

public class AOE {
    public static EntityAOE entities(Location location, double radius) {
        return new EntityAOE(location, radius);
    }

    public static LivingEntityAOE livingEntities(Location location, double radius) {
        return new LivingEntityAOE(location, radius);
    }

    public static TeamAOE team(Team team, Location location, double radius) {
        return new TeamAOE(team, location, radius);
    }

    public static PlayerAOE players(Location location, double radius) {
        return new PlayerAOE(location, radius);
    }

    public static BlockAOE blocks(Location location, double radius) {
        return new BlockAOE(location, radius);
    }

    public static class EntityAOE extends Target<Entity> {
        Location location;
        double radius;

        public EntityAOE(Location location, double radius) {
            this.location = location;
            this.radius = radius;
        }

        @Override
        Collection<Entity> getTargets() {
            return BukkitUtil.getEntitiesRadius(location, radius);
        }
    }

    public static class LivingEntityAOE extends Target<LivingEntity> {
        Location location;
        double radius;

        public LivingEntityAOE(Location location, double radius) {
            this.location = location;
            this.radius = radius;
        }

        @Override
        Collection<LivingEntity> getTargets() {
            return BukkitUtil.getEntitiesRadius(location, radius).stream()
                    .filter(e -> e instanceof LivingEntity)
                    .map(e -> (LivingEntity) e)
                    .collect(Collectors.toList());
        }
    }

    public static class TeamAOE extends Target<LivingEntity> {
        Team team;
        Location location;
        double radius;

        public TeamAOE(Team team, Location location, double radius) {
            this.team = team;
            this.location = location;
            this.radius = radius;
        }

        @Override
        Collection<LivingEntity> getTargets() {
            return BukkitUtil.getEntitiesRadius(location, radius).stream()
                    .filter(e -> e instanceof LivingEntity livingEntity && team.isInTeam(livingEntity))
                    .map(e -> (LivingEntity) e)
                    .collect(Collectors.toList());
        }
    }

    public static class PlayerAOE extends Target<Player> {
        Location location;
        double radius;

        public PlayerAOE(Location location, double radius) {
            this.location = location;
            this.radius = radius;
        }

        @Override
        Collection<Player> getTargets() {
            return BukkitUtil.getEntitiesRadius(location, radius).stream()
                    .filter(e -> e instanceof Player)
                    .map(e -> (Player) e)
                    .collect(Collectors.toList());
        }
    }

    public static class BlockAOE extends Target<Block> {
        Location location;
        double radius;

        public BlockAOE(Location location, double radius) {
            this.location = location;
            this.radius = radius;
        }

        @Override
        Collection<Block> getTargets() {
            return BukkitUtil.getBlocksRadius(location, radius, radius, radius);
        }

    }
}
