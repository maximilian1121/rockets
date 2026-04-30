package ca.maximilian.create_rockets.client.sound;

import ca.maximilian.create_rockets.ModBlock.AbstractThrusterBlockEntity;
import ca.maximilian.create_rockets.index.CreateRocketsSounds;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;

public class ThrusterSoundInstance extends AbstractTickableSoundInstance {
    private final AbstractThrusterBlockEntity blockEntity;

    public ThrusterSoundInstance(AbstractThrusterBlockEntity blockEntity) {
        super(CreateRocketsSounds.THRUSTER_SOUND.get(), SoundSource.BLOCKS, SoundInstance.createUnseededRandom());
        this.blockEntity = blockEntity;
        this.looping = true;
        this.delay = 0;
        this.volume = 0.01f;
        this.pitch = 1.0f;
        this.updatePosition();
    }

    @Override
    public void tick() {
        if (this.blockEntity.isRemoved() || !this.blockEntity.isActive()) {
            this.stopSound();
            return;
        }

        this.updatePosition();
        
        float throttle = this.blockEntity.getIntensity();
        float t = Mth.clamp(throttle, 0.0f, 1.0f);
        this.volume = Mth.clamp((float) Math.pow(t, 1.8f) * 1.4f, 0.0f, 1.4f);
        this.pitch = Mth.clamp(0.5f + (float) Math.pow(t, 2.2f) * 1.3f, 0.5f, 1.8f);
        
        if (this.volume <= 0.01f) {
            this.volume = 0;
        }
    }

    public void stopSound() {
        this.stop();
    }

    private void updatePosition() {
        this.x = (double)((float)this.blockEntity.getBlockPos().getX() + 0.5F);
        this.y = (double)((float)this.blockEntity.getBlockPos().getY() + 0.5F);
        this.z = (double)((float)this.blockEntity.getBlockPos().getZ() + 0.5F);
    }
}
