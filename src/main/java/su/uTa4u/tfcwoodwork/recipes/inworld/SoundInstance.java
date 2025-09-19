package su.uTa4u.tfcwoodwork.recipes.inworld;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public record SoundInstance(SoundEvent sound, float volume, float pitch) {
    public static final Codec<SoundInstance> CODEC = RecordCodecBuilder.create((inst) -> inst.group(
            SoundEvent.DIRECT_CODEC.fieldOf("sound").forGetter(SoundInstance::sound),
            Codec.floatRange(0.0f, 1.0f).fieldOf("volume").forGetter(SoundInstance::volume),
            Codec.floatRange(0.0f, 1.0f).fieldOf("pitch").forGetter(SoundInstance::pitch)
    ).apply(inst, SoundInstance::new));

    public static final StreamCodec<ByteBuf, SoundInstance> STREAM_CODEC = StreamCodec.composite(
            SoundEvent.DIRECT_STREAM_CODEC, SoundInstance::sound,
            ByteBufCodecs.FLOAT, SoundInstance::volume,
            ByteBufCodecs.FLOAT, SoundInstance::pitch,
            SoundInstance::new
    );

    public void play(Level level, Player player, BlockPos pos) {
        level.playSound(player, pos, this.sound(), SoundSource.BLOCKS, this.volume(), this.pitch());
    }
}
