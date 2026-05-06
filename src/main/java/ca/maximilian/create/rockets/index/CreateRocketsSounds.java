package ca.maximilian.create.rockets.index;

import ca.maximilian.create.rockets.Constants;
import lombok.experimental.UtilityClass;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@UtilityClass
public class CreateRocketsSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(Registries.SOUND_EVENT, Constants.MOD_ID);

    public static final DeferredHolder<SoundEvent, SoundEvent> THRUSTER_SOUND =
            SOUND_EVENTS.register(
                    "thruster_sound",
                    () ->
                            SoundEvent.createVariableRangeEvent(
                                    ResourceLocation.fromNamespaceAndPath(
                                            Constants.MOD_ID, "thruster_sound")));
}
