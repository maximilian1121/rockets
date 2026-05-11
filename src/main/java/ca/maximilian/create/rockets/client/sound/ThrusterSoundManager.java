package ca.maximilian.create.rockets.client.sound;

import ca.maximilian.create.rockets.content.blocks.thruster.AbstractThrusterBlockEntity;
import net.minecraft.client.Minecraft;

public class ThrusterSoundManager {

    private ThrusterSoundInstance soundInstance;

    public void tick(AbstractThrusterBlockEntity be) {
        float throttle = be.getIntensity();
        if (throttle > 0.01f && be.isActive()
            && (soundInstance == null || soundInstance.isStopped())) {
            soundInstance = new ThrusterSoundInstance(be);
            Minecraft.getInstance().getSoundManager().play(soundInstance);
        }
    }

    public void invalidate() {
        if (soundInstance != null) {
            soundInstance.stopSound();
        }
    }
}