package io.github.ashisbored.particular.renderer.objects;

import io.github.ashisbored.particular.renderer.ParticleScene;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

import java.util.function.Supplier;

public class LineParticleObject extends ParticleObject {
    public LineParticleObject(ParticleScene scene, Supplier<ParticleEffect> effect, Vec3d start, Vec3d end, int steps) {
        super(scene, effect, Vec3d.ZERO);
        Vec3d line = start.subtract(end);
        Vec3d step = new Vec3d(line.x / steps, line.y / steps, line.z / steps);
        Vec3d currentPos = start;
        for (int i = 0; i < steps; i++) {
            this.addChild(new SphereParticleObject(scene, effect, currentPos, 0, 2));
            currentPos = currentPos.add(step);
        }
        this.addChild(new SphereParticleObject(scene, effect, currentPos, 0, 2));
    }

    @Override
    public void render(ServerWorld world) {
        // we just need the children to be rendered
    }
}
