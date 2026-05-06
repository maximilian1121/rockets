package ca.maximilian.create.rockets.index;

import ca.maximilian.create.rockets.Constants;
import lombok.experimental.UtilityClass;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@UtilityClass
public final class CreateRocketsParticleTypes {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES =
            DeferredRegister.create(Registries.PARTICLE_TYPE, Constants.MOD_ID);

    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> LARGE_SMOKE =
            PARTICLE_TYPES.register("large_smoke", () -> new SimpleParticleType(true));
}
