//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.audio.lwjgl3audio;

import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL10;
import paulscode.sound.*;

import javax.sound.sampled.AudioFormat;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.LinkedList;

public class SourceLWJGL3OpenAL extends Source {
    private ChannelLWJGL3OpenAL channelOpenAL;
    private IntBuffer myBuffer;
    private FloatBuffer listenerPosition;
    private FloatBuffer sourcePosition;
    private FloatBuffer sourceVelocity;

    public SourceLWJGL3OpenAL(FloatBuffer listenerPosition, IntBuffer myBuffer, boolean priority, boolean toStream, boolean toLoop, String sourcename, FilenameURL filenameURL, SoundBuffer soundBuffer, float x, float y, float z, int attModel, float distOrRoll, boolean temporary) {
        super(priority, toStream, toLoop, sourcename, filenameURL, soundBuffer, x, y, z, attModel, distOrRoll, temporary);
        this.channelOpenAL = (ChannelLWJGL3OpenAL) this.channel;
        if (this.codec != null) {
            this.codec.reverseByteOrder(true);
        }

        this.listenerPosition = listenerPosition;
        this.myBuffer = myBuffer;
        this.libraryType = SoundEngine.class;
        this.pitch = 1.0F;
        this.resetALInformation();
    }

    public SourceLWJGL3OpenAL(FloatBuffer listenerPosition, IntBuffer myBuffer, Source old, SoundBuffer soundBuffer) {
        super(old, soundBuffer);
        this.channelOpenAL = (ChannelLWJGL3OpenAL) this.channel;
        if (this.codec != null) {
            this.codec.reverseByteOrder(true);
        }

        this.listenerPosition = listenerPosition;
        this.myBuffer = myBuffer;
        this.libraryType = SoundEngine.class;
        this.pitch = 1.0F;
        this.resetALInformation();
    }

    public SourceLWJGL3OpenAL(FloatBuffer listenerPosition, AudioFormat audioFormat, boolean priority, String sourcename, float x, float y, float z, int attModel, float distOrRoll) {
        super(audioFormat, priority, sourcename, x, y, z, attModel, distOrRoll);
        this.channelOpenAL = (ChannelLWJGL3OpenAL) this.channel;
        this.listenerPosition = listenerPosition;
        this.libraryType = SoundEngine.class;
        this.pitch = 1.0F;
        this.resetALInformation();
    }

    public void cleanup() {
        super.cleanup();
    }

    public void changeSource(FloatBuffer listenerPosition, IntBuffer myBuffer, boolean priority, boolean toStream, boolean toLoop, String sourcename, FilenameURL filenameURL, SoundBuffer soundBuffer, float x, float y, float z, int attModel, float distOrRoll, boolean temporary) {
        super.changeSource(priority, toStream, toLoop, sourcename, filenameURL, soundBuffer, x, y, z, attModel, distOrRoll, temporary);
        this.listenerPosition = listenerPosition;
        this.myBuffer = myBuffer;
        this.pitch = 1.0F;
        this.resetALInformation();
    }

    public boolean incrementSoundSequence() {
        if (!this.toStream) {
            this.errorMessage("Method 'incrementSoundSequence' may only be used for streaming sources.");
            return false;
        } else {
            synchronized (this.soundSequenceLock) {
                if (this.soundSequenceQueue != null && this.soundSequenceQueue.size() > 0) {
                    this.filenameURL = this.soundSequenceQueue.remove(0);
                    if (this.codec != null) {
                        this.codec.cleanup();
                    }

                    this.codec = SoundSystemConfig.getCodec(this.filenameURL.getFilename());
                    if (this.codec != null) {
                        this.codec.reverseByteOrder(true);
                        if (this.codec.getAudioFormat() == null) {
                            this.codec.initialize(this.filenameURL.getURL());
                        }

                        AudioFormat audioFormat = this.codec.getAudioFormat();
                        if (audioFormat == null) {
                            this.errorMessage("Audio Format null in method 'incrementSoundSequence'");
                            return false;
                        }

                        short soundFormat = 0;
                        if (audioFormat.getChannels() == 1) {
                            if (audioFormat.getSampleSizeInBits() == 8) {
                                soundFormat = 4352;
                            } else {
                                if (audioFormat.getSampleSizeInBits() != 16) {
                                    this.errorMessage("Illegal sample size in method 'incrementSoundSequence'");
                                    return false;
                                }

                                soundFormat = 4353;
                            }
                        } else {
                            if (audioFormat.getChannels() != 2) {
                                this.errorMessage("Audio data neither mono nor stereo in method 'incrementSoundSequence'");
                                return false;
                            }

                            if (audioFormat.getSampleSizeInBits() == 8) {
                                soundFormat = 4354;
                            } else {
                                if (audioFormat.getSampleSizeInBits() != 16) {
                                    this.errorMessage("Illegal sample size in method 'incrementSoundSequence'");
                                    return false;
                                }

                                soundFormat = 4355;
                            }
                        }

                        this.channelOpenAL.setFormat(soundFormat, (int) audioFormat.getSampleRate());
                        this.preLoad = true;
                    }

                    return true;
                } else {
                    return false;
                }
            }
        }
    }

    public void listenerMoved() {
        this.positionChanged();
    }

    public void setPosition(float x, float y, float z) {
        super.setPosition(x, y, z);
        if (this.sourcePosition == null) {
            this.resetALInformation();
        } else {
            this.positionChanged();
        }

        this.sourcePosition.put(0, x);
        this.sourcePosition.put(1, y);
        this.sourcePosition.put(2, z);
        if (this.channel != null && this.channel.attachedSource == this && this.channelOpenAL != null && this.channelOpenAL.ALSource != null) {
            AL10.alSourcefv(this.channelOpenAL.ALSource.get(0), 4100, this.sourcePosition);
            this.checkALError();
        }

    }

    public void positionChanged() {
        this.calculateDistance();
        this.calculateGain();
        if (this.channel != null && this.channel.attachedSource == this && this.channelOpenAL != null && this.channelOpenAL.ALSource != null) {
            AL10.alSourcef(this.channelOpenAL.ALSource.get(0), 4106, this.gain * this.sourceVolume * Math.abs(this.fadeOutGain) * this.fadeInGain);
            this.checkALError();
        }

        this.checkPitch();
    }

    private void checkPitch() {
        if (this.channel != null && this.channel.attachedSource == this && SoundEngine.alPitchSupported() && this.channelOpenAL != null && this.channelOpenAL.ALSource != null) {
            AL10.alSourcef(this.channelOpenAL.ALSource.get(0), 4099, this.pitch);
            this.checkALError();
        }

    }

    public void setLooping(boolean lp) {
        super.setLooping(lp);
        if (this.channel != null && this.channel.attachedSource == this && this.channelOpenAL != null && this.channelOpenAL.ALSource != null) {
            if (lp) {
                AL10.alSourcei(this.channelOpenAL.ALSource.get(0), 4103, 1);
            } else {
                AL10.alSourcei(this.channelOpenAL.ALSource.get(0), 4103, 0);
            }

            this.checkALError();
        }

    }

    public void setAttenuation(int model) {
        super.setAttenuation(model);
        if (this.channel != null && this.channel.attachedSource == this && this.channelOpenAL != null && this.channelOpenAL.ALSource != null) {
            if (model == 1) {
                AL10.alSourcef(this.channelOpenAL.ALSource.get(0), 4129, this.distOrRoll);
            } else {
                AL10.alSourcef(this.channelOpenAL.ALSource.get(0), 4129, 0.0F);
            }

            this.checkALError();
        }

    }

    public void setDistOrRoll(float dr) {
        super.setDistOrRoll(dr);
        if (this.channel != null && this.channel.attachedSource == this && this.channelOpenAL != null && this.channelOpenAL.ALSource != null) {
            if (this.attModel == 1) {
                AL10.alSourcef(this.channelOpenAL.ALSource.get(0), 4129, dr);
            } else {
                AL10.alSourcef(this.channelOpenAL.ALSource.get(0), 4129, 0.0F);
            }

            this.checkALError();
        }

    }

    public void setVelocity(float x, float y, float z) {
        super.setVelocity(x, y, z);
        this.sourceVelocity = BufferUtils.createFloatBuffer(3).put(new float[]{x, y, z});
        this.sourceVelocity.flip();
        if (this.channel != null && this.channel.attachedSource == this && this.channelOpenAL != null && this.channelOpenAL.ALSource != null) {
            AL10.alSourcefv(this.channelOpenAL.ALSource.get(0), 4102, this.sourceVelocity);
            this.checkALError();
        }

    }

    public void setPitch(float value) {
        super.setPitch(value);
        this.checkPitch();
    }

    public void play(Channel c) {
        if (!this.active()) {
            if (this.toLoop) {
                this.toPlay = true;
            }

        } else if (c == null) {
            this.errorMessage("Unable to play source, because channel was null");
        } else {
            boolean newChannel = this.channel != c;
            if (this.channel != null && this.channel.attachedSource != this) {
                newChannel = true;
            }

            boolean wasPaused = this.paused();
            super.play(c);
            this.channelOpenAL = (ChannelLWJGL3OpenAL) this.channel;
            if (newChannel) {
                this.setPosition(this.position.x, this.position.y, this.position.z);
                this.checkPitch();
                if (this.channelOpenAL != null && this.channelOpenAL.ALSource != null) {
                    if (SoundEngine.alPitchSupported()) {
                        AL10.alSourcef(this.channelOpenAL.ALSource.get(0), 4099, this.pitch);
                        this.checkALError();
                    }

                    AL10.alSourcefv(this.channelOpenAL.ALSource.get(0), 4100, this.sourcePosition);
                    this.checkALError();
                    AL10.alSourcefv(this.channelOpenAL.ALSource.get(0), 4102, this.sourceVelocity);
                    this.checkALError();
                    if (this.attModel == 1) {
                        AL10.alSourcef(this.channelOpenAL.ALSource.get(0), 4129, this.distOrRoll);
                    } else {
                        AL10.alSourcef(this.channelOpenAL.ALSource.get(0), 4129, 0.0F);
                    }

                    this.checkALError();
                    if (this.toLoop && !this.toStream) {
                        AL10.alSourcei(this.channelOpenAL.ALSource.get(0), 4103, 1);
                    } else {
                        AL10.alSourcei(this.channelOpenAL.ALSource.get(0), 4103, 0);
                    }

                    this.checkALError();
                }

                if (!this.toStream) {
                    if (this.myBuffer == null) {
                        this.errorMessage("No sound buffer to play");
                        return;
                    }

                    this.channelOpenAL.attachBuffer(this.myBuffer);
                }
            }

            if (!this.playing()) {
                if (this.toStream && !wasPaused) {
                    if (this.codec == null) {
                        this.errorMessage("Decoder null in method 'play'");
                        return;
                    }

                    if (this.codec.getAudioFormat() == null) {
                        this.codec.initialize(this.filenameURL.getURL());
                    }

                    AudioFormat audioFormat = this.codec.getAudioFormat();
                    if (audioFormat == null) {
                        this.errorMessage("Audio Format null in method 'play'");
                        return;
                    }


                    short soundFormat = 0;
                    if (audioFormat.getChannels() == 1) {
                        if (audioFormat.getSampleSizeInBits() == 8) {
                            soundFormat = 4352;
                        } else {
                            if (audioFormat.getSampleSizeInBits() != 16) {
                                this.errorMessage("Illegal sample size in method 'play'");
                                return;
                            }

                            soundFormat = 4353;
                        }
                    } else {
                        if (audioFormat.getChannels() != 2) {
                            this.errorMessage("Audio data neither mono nor stereo in method 'play'");
                            return;
                        }

                        if (audioFormat.getSampleSizeInBits() == 8) {
                            soundFormat = 4354;
                        } else {
                            if (audioFormat.getSampleSizeInBits() != 16) {
                                this.errorMessage("Illegal sample size in method 'play'");
                                return;
                            }

                            soundFormat = 4355;
                        }
                    }

                    this.channelOpenAL.setFormat(soundFormat, (int) audioFormat.getSampleRate());
                    this.preLoad = true;
                }

                this.channel.play();
                if (this.pitch != 1.0F) {
                    this.checkPitch();
                }
            }

        }
    }

    public boolean preLoad() {
        if (this.codec == null) {
            return false;
        } else {
            this.codec.initialize(this.filenameURL.getURL());
            LinkedList<byte[]> preLoadBuffers = new LinkedList();

            for (int i = 0; i < SoundSystemConfig.getNumberStreamingBuffers(); ++i) {
                this.soundBuffer = this.codec.read();
                if (this.soundBuffer == null || this.soundBuffer.audioData == null) {
                    break;
                }

                preLoadBuffers.add(this.soundBuffer.audioData);
            }

            this.positionChanged();
            this.channel.preLoadBuffers(preLoadBuffers);
            this.preLoad = false;
            return true;
        }
    }

    private void resetALInformation() {
        this.sourcePosition = BufferUtils.createFloatBuffer(3).put(new float[]{this.position.x, this.position.y, this.position.z});
        this.sourceVelocity = BufferUtils.createFloatBuffer(3).put(new float[]{this.velocity.x, this.velocity.y, this.velocity.z});
        this.sourcePosition.flip();
        this.sourceVelocity.flip();
        this.positionChanged();
    }

    private void calculateDistance() {
        if (this.listenerPosition != null) {
            double dX = this.position.x - this.listenerPosition.get(0);
            double dY = this.position.y - this.listenerPosition.get(1);
            double dZ = this.position.z - this.listenerPosition.get(2);
            this.distanceFromListener = (float) Math.sqrt(dX * dX + dY * dY + dZ * dZ);
        }

    }

    private void calculateGain() {
        if (this.attModel == 2) {
            if (this.distanceFromListener <= 0.0F) {
                this.gain = 1.0F;
            } else if (this.distanceFromListener >= this.distOrRoll) {
                this.gain = 0.0F;
            } else {
                this.gain = 1.0F - this.distanceFromListener / this.distOrRoll;
            }

            if (this.gain > 1.0F) {
                this.gain = 1.0F;
            }

            if (this.gain < 0.0F) {
                this.gain = 0.0F;
            }
        } else {
            this.gain = 1.0F;
        }

    }

    private boolean checkALError() {
        switch (AL10.alGetError()) {
            case 0:
                return false;
            case 40961:
                this.errorMessage("Invalid name parameter.");
                return true;
            case 40962:
                this.errorMessage("Invalid parameter.");
                return true;
            case 40963:
                this.errorMessage("Invalid enumerated parameter value.");
                return true;
            case 40964:
                this.errorMessage("Illegal call.");
                return true;
            case 40965:
                this.errorMessage("Unable to allocate memory.");
                return true;
            default:
                this.errorMessage("An unrecognized error occurred.");
                return true;
        }
    }
}
