package io.github.ashisbored.particular.renderer.objects;

import io.github.ashisbored.particular.renderer.ParticleScene;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public abstract class ParticleObject {
    private final ParticleScene scene;
    private final Supplier<ParticleEffect> effect;
    private final List<ParticleObject> children = new ArrayList<>();
    protected Vec3d pos;

    protected ParticleObject(ParticleScene scene, Supplier<ParticleEffect> effect, Vec3d pos) {
        this.scene = scene;
        this.effect = effect;
        this.pos = pos;
    }

    protected void addChild(ParticleObject child) {
        this.children.add(child);
    }

    public void moveTo(Vec3d newPos) {
        this.pos = newPos;
    }

    public void moveTo(double x, double y, double z) {
        this.pos = new Vec3d(x, y, z);
    }

    public void offsetBy(Vec3d offset) {
        this.pos = this.pos.add(offset);
    }

    public void offsetBy(double x, double y, double z) {
        this.pos = this.pos.add(x, y, z);
    }

    protected ParticleEffect getParticleEffect(ServerWorld world) {
        return this.effect.get();
    }

    protected void addParticle(ServerWorld world, double x, double y, double z, int count, double deltaX, double deltaY, double deltaZ, double speed) {
        world.spawnParticles(this.getParticleEffect(world), x, y, z, count, deltaX, deltaY, deltaZ, speed);
    }

    /**
     * Called every world tick to update the {@link ParticleObject}s state.
     * @param world The world this {@link ParticleObject} is in
     */
    protected void tick(ServerWorld world) { }

    /**
     * Called every {@link ParticleScene#renderInterval} ticks to display particles using {@link ParticleObject#addParticle}.
     * @param world The world this {@link ParticleObject} is in
     */
    @SuppressWarnings("JavadocReference")
    protected abstract void render(ServerWorld world);

    public void displayParticles(ServerWorld world) {
        this.render(world);

        for (ParticleObject child : this.children) {
            child.displayParticles(world);
        }
    }
}
