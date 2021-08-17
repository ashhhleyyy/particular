package io.github.ashisbored.particular.renderer;

import io.github.ashisbored.particular.renderer.objects.ParticleObject;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ParticleScene {
    private final List<ParticleObject> particleObjects = new ArrayList<>();
    private final ServerWorld world;
    private final Identifier id;
    private final Set<ServerPlayerEntity> players = new HashSet<>();
    private final int renderInterval;

    public ParticleScene(ServerWorld world, Identifier id, int renderInterval) {
        this.world = world;
        this.id = id;
        this.renderInterval = renderInterval;
    }

    public Identifier getId() {
        return id;
    }

    public void unload() {
        this.players.clear();
    }

    public void addObject(ParticleObject obj) {
        this.particleObjects.add(obj);
    }

    public void tick() {
        Set<ServerPlayerEntity> players = new HashSet<>(this.world.getPlayers());
        Set<ServerPlayerEntity> current = new HashSet<>(this.players);
        current.removeAll(players);
        this.players.removeAll(current);
        this.players.addAll(players);

        if (this.world.getTime() % this.renderInterval == 0) {
            this.render();
        }
    }

    private void render() {
        for (ParticleObject particleObject : this.particleObjects) {
            particleObject.displayParticles(this.world);
        }
    }
}
