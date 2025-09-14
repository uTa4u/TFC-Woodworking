package su.uTa4u.tfcwoodwork.sounds;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import su.uTa4u.tfcwoodwork.TFCWoodworking;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS;

    public static final DeferredHolder<SoundEvent, SoundEvent> LOG_CHOP;
    public static final DeferredHolder<SoundEvent, SoundEvent> LOG_SAWED;

    static {
        SOUND_EVENTS = DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, TFCWoodworking.MOD_ID);
        LOG_CHOP = registerSoundEvent("wood_chop");
        LOG_SAWED = registerSoundEvent("wood_sawed");
    }

    private static DeferredHolder<SoundEvent, SoundEvent> registerSoundEvent(String name) {
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(TFCWoodworking.getResource(name)));
    }
}
