package io.github.ashisbored.ash-servjam;

import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServjamMod implements ModInitializer {
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MOD_ID = "ash-servjam";

    @Override
    public void onInitialize() {
        LOGGER.info("Ash's servjam mod initialising...");
    }
}
