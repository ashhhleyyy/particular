package io.github.ashisbored.particular;

import io.github.ashisbored.particular.command.ParticularCommand;
import io.github.ashisbored.particular.renderer.SceneManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Particular implements ModInitializer {
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MOD_ID = "particular";

    @Override
    public void onInitialize() {
        LOGGER.info("Particular initialising...");
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            ParticularCommand.register(dispatcher);
        });
        SceneManager.init();
    }
}
