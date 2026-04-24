package ca.maximilian.create_rockets.index;

import ca.maximilian.create_rockets.CreateRockets;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class CreateRocketsSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(Registries.SOUND_EVENT, CreateRockets.MODID);

    public static final DeferredHolder<SoundEvent, SoundEvent> THRUSTER_SOUND =
            SOUND_EVENTS.register("thruster_sound",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResourceLocation.fromNamespaceAndPath(CreateRockets.MODID, "thruster_sound")
                    ));
}