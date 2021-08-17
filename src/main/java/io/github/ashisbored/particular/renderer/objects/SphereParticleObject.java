package io.github.ashisbored.particular.renderer.objects;

import io.github.ashisbored.particular.renderer.ParticleScene;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

import java.util.function.Supplier;

public class SphereParticleObject extends ParticleObject {
    private final double radius;
    private final int density;

    public SphereParticleObject(ParticleScene scene, Supplier<ParticleEffect> effect, Vec3d pos, double radius) {
        this(scene, effect, pos, radius, -1);
    }

    public SphereParticleObject(ParticleScene scene, Supplier<ParticleEffect> effect, Vec3d pos, double radius, int density) {
        super(scene, effect, pos);
        this.radius = radius;
        this.density = density;
    }

    @Override
    public void render(ServerWorld world) {
        int density = this.density > 0 ? this.density : (int) (10 * radius * radius + 1);
        this.addParticle(world, this.pos.x, this.pos.y, this.pos.z, density, radius, radius, radius, 0);
    }
}
