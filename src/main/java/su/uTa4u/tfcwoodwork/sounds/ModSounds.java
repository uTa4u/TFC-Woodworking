package su.uTa4u.tfcwoodwork.sounds;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import su.uTa4u.tfcwoodwork.TFCWoodworking;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS;

    public static final RegistryObject<SoundEvent> LOG_CHOP;
    public static final RegistryObject<SoundEvent> LOG_SAWED;

    static {
        SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, TFCWoodworking.MOD_ID);
        LOG_CHOP = registerSoundEvent("wood_chop");
        LOG_SAWED = registerSoundEvent("wood_sawed");
    }

    private static RegistryObject<SoundEvent> registerSoundEvent(String name) {
        return SOUND_EVENTS.register(name, () ->
                SoundEvent.createVariableRangeEvent(new ResourceLocation(TFCWoodworking.MOD_ID, name)));
    }
}
