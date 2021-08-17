package io.github.ashisbored.particular.renderer;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;

public class SceneManager {
    private static final Multimap<ServerWorld, ParticleScene> scenes = ArrayListMultimap.create();
    private static final Object2ObjectMap<Identifier, ParticleScene> byId = new Object2ObjectOpenHashMap<>();

    private SceneManager() { }

    public static ParticleScene create(ServerWorld world, Identifier id, int renderInterval) {
        if (byId.containsKey(id)) {
            throw new IllegalArgumentException("Scene with ID " + id + " already exists!");
        }
        ParticleScene scene = new ParticleScene(world, id, renderInterval);
        scenes.put(world, scene);
        byId.put(id, scene);
        return scene;
    }

    public static boolean isUnusedId(Identifier id) {
        return !byId.containsKey(id);
    }

    public static void init() {
        ServerWorldEvents.UNLOAD.register((server, world) -> {
            scenes.get(world).forEach(ParticleScene::unload);
            scenes.get(world).forEach(scene -> byId.remove(scene.getId()));
            scenes.removeAll(world);
        });
        ServerTickEvents.END_WORLD_TICK.register(world -> {
            scenes.get(world).forEach(ParticleScene::tick);
        });
    }

    public static boolean removeScene(Identifier id) {
        ParticleScene removed = byId.remove(id);
        if (removed == null) {
            return false;
        }
        scenes.entries().removeIf(serverWorldParticleSceneEntry -> serverWorldParticleSceneEntry.getValue() == removed);
        return true;
    }

    public static ParticleScene getById(Identifier sceneId) {
        return byId.get(sceneId);
    }
}
