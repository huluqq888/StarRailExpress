package org.agmas.noellesroles.voice;

import de.maxhenkel.voicechat.api.VoicechatPlugin;
import de.maxhenkel.voicechat.api.events.EventRegistration;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;

/**
 * Voicechat plugin for Helium Buzz effect.
 * Registers the client-side audio processing on the client environment.
 */
public class HeliumBuzzVoicechatPlugin implements VoicechatPlugin {

    @Override
    public String getPluginId() {
        return "noellesroles_helium_buzz";
    }

    @Override
    public void registerEvents(EventRegistration r) {
        // Only register on client side
        if (FabricLoader.getInstance().getEnvironmentType() != EnvType.CLIENT) {
            return;
        }
        try {
            Class<?> cls = Class.forName("org.agmas.noellesroles.voice.client.HeliumBuzzClientReceiver");
            cls.getMethod("register", EventRegistration.class).invoke(null, r);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Failed to wire HeliumBuzz client receiver", e);
        }
    }
}
