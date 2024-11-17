package net.minecraft.client.audio.lwjgl3audio;

import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALCCapabilities;
import paulscode.sound.*;

import javax.sound.sampled.AudioFormat;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import static org.lwjgl.openal.ALC10.*;

public class SoundEngine extends Library {
    private static final boolean GET = false;
    private static final boolean SET = true;
    private static final boolean XXX = false;
    private FloatBuffer listenerPositionAL = null;
    private FloatBuffer listenerOrientation = null;
    private FloatBuffer listenerVelocity = null;
    private HashMap<String, IntBuffer> ALBufferMap = null;
    private static boolean alPitchSupported = true;

    long device;

    public SoundEngine() throws SoundSystemException {
        this.ALBufferMap = new HashMap();
        this.reverseByteOrder = true;
    }

    public void init() throws SoundSystemException {
        boolean errors = false;

        device = alcOpenDevice((ByteBuffer) null);
        ALCCapabilities deviceCaps = ALC.createCapabilities(device);

        long context = alcCreateContext(device, (IntBuffer) null);
        alcMakeContextCurrent(context);
        AL.createCapabilities(deviceCaps);
        errors = this.checkALError();

        if (errors) {
            this.importantMessage("OpenAL did not initialize properly!");
        } else {
            this.message("OpenAL initialized.");
        }

        this.listenerPositionAL = BufferUtils.createFloatBuffer(3).put(new float[]{this.listener.position.x, this.listener.position.y, this.listener.position.z});
        this.listenerOrientation = BufferUtils.createFloatBuffer(6).put(new float[]{this.listener.lookAt.x, this.listener.lookAt.y, this.listener.lookAt.z, this.listener.up.x, this.listener.up.y, this.listener.up.z});
        this.listenerVelocity = BufferUtils.createFloatBuffer(3).put(new float[]{0.0F, 0.0F, 0.0F});
        this.listenerPositionAL.flip();
        this.listenerOrientation.flip();
        this.listenerVelocity.flip();
        AL10.alListenerfv(4100, this.listenerPositionAL);
        errors = this.checkALError() || errors;
        AL10.alListenerfv(4111, this.listenerOrientation);
        errors = this.checkALError() || errors;
        AL10.alListenerfv(4102, this.listenerVelocity);
        errors = this.checkALError() || errors;
        AL10.alDopplerFactor(SoundSystemConfig.getDopplerFactor());
        errors = this.checkALError() || errors;
        AL10.alDopplerVelocity(SoundSystemConfig.getDopplerVelocity());
        errors = this.checkALError() || errors;
        if (errors) {
            this.importantMessage("OpenAL did not initialize properly!");
            throw new Exception("Problem encountered while loading OpenAL or creating the listener.  Probable cause:  OpenAL not supported", 101);
        } else {
            super.init();
            ChannelLWJGL3OpenAL channel = (ChannelLWJGL3OpenAL) this.normalChannels.get(1);

            try {
                AL10.alSourcef(channel.ALSource.get(0), 4099, 1.0F);
                if (this.checkALError()) {
                    alPitchSupported(true, false);
                    throw new Exception("OpenAL: AL_PITCH not supported.", 108);
                } else {
                    alPitchSupported(true, true);
                }
            } catch (java.lang.Exception var4) {
                alPitchSupported(true, false);
                throw new Exception("OpenAL: AL_PITCH not supported.", 108);
            }
        }
    }

    // TODO somethinghere
    public static boolean libraryCompatible() {
        return true;
    }

    protected Channel createChannel(int type) {
        IntBuffer ALSource = BufferUtils.createIntBuffer(1);

        try {
            AL10.alGenSources(ALSource);
        } catch (java.lang.Exception var5) {
            AL10.alGetError();
            return null;
        }

        if (AL10.alGetError() != 0) {
            return null;
        } else {
            ChannelLWJGL3OpenAL channel = new ChannelLWJGL3OpenAL(type, ALSource);
            return channel;
        }
    }

    public void cleanup() {
        super.cleanup();
        Set<String> keys = this.bufferMap.keySet();
        Iterator iter = keys.iterator();

        while (iter.hasNext()) {
            String filename = (String) iter.next();
            IntBuffer buffer = this.ALBufferMap.get(filename);
            if (buffer != null) {
                AL10.alDeleteBuffers(buffer);
                this.checkALError();
                buffer.clear();
            }
        }

        this.bufferMap.clear();
        alcCloseDevice(device);
        this.bufferMap = null;
        this.listenerPositionAL = null;
        this.listenerOrientation = null;
        this.listenerVelocity = null;
    }

    public boolean loadSound(FilenameURL filenameURL) {
        if (this.bufferMap == null) {
            this.bufferMap = new HashMap();
            this.importantMessage("Buffer Map was null in method 'loadSound'");
        }

        if (this.ALBufferMap == null) {
            this.ALBufferMap = new HashMap();
            this.importantMessage("Open AL Buffer Map was null in method'loadSound'");
        }

        if (this.errorCheck(filenameURL == null, "Filename/URL not specified in method 'loadSound'")) {
            return false;
        } else if (this.bufferMap.get(filenameURL.getFilename()) != null) {
            return true;
        } else {
            ICodec codec = SoundSystemConfig.getCodec(filenameURL.getFilename());
            if (this.errorCheck(codec == null, "No codec found for file '" + filenameURL.getFilename() + "' in method 'loadSound'")) {
                return false;
            } else {
                codec.reverseByteOrder(true);
                URL url = filenameURL.getURL();
                if (this.errorCheck(url == null, "Unable to open file '" + filenameURL.getFilename() + "' in method 'loadSound'")) {
                    return false;
                } else {
                    codec.initialize(url);
                    SoundBuffer buffer = codec.readAll();
                    codec.cleanup();
                    codec = null;
                    if (this.errorCheck(buffer == null, "Sound buffer null in method 'loadSound'")) {
                        return false;
                    } else {
                        this.bufferMap.put(filenameURL.getFilename(), buffer);
                        AudioFormat audioFormat = buffer.audioFormat;
                        short soundFormat = 0;
                        if (audioFormat.getChannels() == 1) {
                            if (audioFormat.getSampleSizeInBits() == 8) {
                                soundFormat = 4352;
                            } else {
                                if (audioFormat.getSampleSizeInBits() != 16) {
                                    this.errorMessage("Illegal sample size in method 'loadSound'");
                                    return false;
                                }

                                soundFormat = 4353;
                            }
                        } else {
                            if (audioFormat.getChannels() != 2) {
                                this.errorMessage("File neither mono nor stereo in method 'loadSound'");
                                return false;
                            }

                            if (audioFormat.getSampleSizeInBits() == 8) {
                                soundFormat = 4354;
                            } else {
                                if (audioFormat.getSampleSizeInBits() != 16) {
                                    this.errorMessage("Illegal sample size in method 'loadSound'");
                                    return false;
                                }

                                soundFormat = 4355;
                            }
                        }

                        IntBuffer intBuffer = BufferUtils.createIntBuffer(1);
                        AL10.alGenBuffers(intBuffer);
                        if (this.errorCheck(AL10.alGetError() != 0, "alGenBuffers error when loading " + filenameURL.getFilename())) {
                            return false;
                        } else {
                            AL10.alBufferData(intBuffer.get(0), soundFormat, BufferUtils.createByteBuffer(buffer.audioData.length).put(buffer.audioData).flip(), (int) audioFormat.getSampleRate());
                            if (this.errorCheck(AL10.alGetError() != 0, "alBufferData error when loading " + filenameURL.getFilename()) && this.errorCheck(intBuffer == null, "Sound buffer was not created for " + filenameURL.getFilename())) {
                                return false;
                            } else {
                                this.ALBufferMap.put(filenameURL.getFilename(), intBuffer);
                                return true;
                            }
                        }
                    }
                }
            }
        }
    }

    public boolean loadSound(SoundBuffer buffer, String identifier) {
        if (this.bufferMap == null) {
            this.bufferMap = new HashMap();
            this.importantMessage("Buffer Map was null in method 'loadSound'");
        }

        if (this.ALBufferMap == null) {
            this.ALBufferMap = new HashMap();
            this.importantMessage("Open AL Buffer Map was null in method'loadSound'");
        }

        if (this.errorCheck(identifier == null, "Identifier not specified in method 'loadSound'")) {
            return false;
        } else if (this.bufferMap.get(identifier) != null) {
            return true;
        } else if (this.errorCheck(buffer == null, "Sound buffer null in method 'loadSound'")) {
            return false;
        } else {
            this.bufferMap.put(identifier, buffer);
            AudioFormat audioFormat = buffer.audioFormat;
            short soundFormat = 0;
            if (audioFormat.getChannels() == 1) {
                if (audioFormat.getSampleSizeInBits() == 8) {
                    soundFormat = 4352;
                } else {
                    if (audioFormat.getSampleSizeInBits() != 16) {
                        this.errorMessage("Illegal sample size in method 'loadSound'");
                        return false;
                    }

                    soundFormat = 4353;
                }
            } else {
                if (audioFormat.getChannels() != 2) {
                    this.errorMessage("File neither mono nor stereo in method 'loadSound'");
                    return false;
                }

                if (audioFormat.getSampleSizeInBits() == 8) {
                    soundFormat = 4354;
                } else {
                    if (audioFormat.getSampleSizeInBits() != 16) {
                        this.errorMessage("Illegal sample size in method 'loadSound'");
                        return false;
                    }

                    soundFormat = 4355;
                }
            }

            IntBuffer intBuffer = BufferUtils.createIntBuffer(1);
            AL10.alGenBuffers(intBuffer);
            if (this.errorCheck(AL10.alGetError() != 0, "alGenBuffers error when saving " + identifier)) {
                return false;
            } else {
                AL10.alBufferData(intBuffer.get(0), soundFormat, BufferUtils.createByteBuffer(buffer.audioData.length).put(buffer.audioData).flip(), (int) audioFormat.getSampleRate());
                if (this.errorCheck(AL10.alGetError() != 0, "alBufferData error when saving " + identifier) && this.errorCheck(intBuffer == null, "Sound buffer was not created for " + identifier)) {
                    return false;
                } else {
                    this.ALBufferMap.put(identifier, intBuffer);
                    return true;
                }
            }
        }
    }

    public void unloadSound(String filename) {
        this.ALBufferMap.remove(filename);
        super.unloadSound(filename);
    }

    public void setMasterVolume(float value) {
        super.setMasterVolume(value);
        AL10.alListenerf(4106, value);
        this.checkALError();
    }

    public void newSource(boolean priority, boolean toStream, boolean toLoop, String sourcename, FilenameURL filenameURL, float x, float y, float z, int attModel, float distOrRoll) {
        IntBuffer myBuffer = null;
        if (!toStream) {
            myBuffer = this.ALBufferMap.get(filenameURL.getFilename());
            if (myBuffer == null && !this.loadSound(filenameURL)) {
                this.errorMessage("Source '" + sourcename + "' was not created " + "because an error occurred while loading " + filenameURL.getFilename());
                return;
            }

            myBuffer = this.ALBufferMap.get(filenameURL.getFilename());
            if (myBuffer == null) {
                this.errorMessage("Source '" + sourcename + "' was not created " + "because a sound buffer was not found for " + filenameURL.getFilename());
                return;
            }
        }

        SoundBuffer buffer = null;
        if (!toStream) {
            buffer = this.bufferMap.get(filenameURL.getFilename());
            if (buffer == null && !this.loadSound(filenameURL)) {
                this.errorMessage("Source '" + sourcename + "' was not created " + "because an error occurred while loading " + filenameURL.getFilename());
                return;
            }

            buffer = this.bufferMap.get(filenameURL.getFilename());
            if (buffer == null) {
                this.errorMessage("Source '" + sourcename + "' was not created " + "because audio data was not found for " + filenameURL.getFilename());
                return;
            }
        }

        this.sourceMap.put(sourcename, new SourceLWJGL3OpenAL(this.listenerPositionAL, myBuffer, priority, toStream, toLoop, sourcename, filenameURL, buffer, x, y, z, attModel, distOrRoll, false));
    }

    public void rawDataStream(AudioFormat audioFormat, boolean priority, String sourcename, float x, float y, float z, int attModel, float distOrRoll) {
        this.sourceMap.put(sourcename, new SourceLWJGL3OpenAL(this.listenerPositionAL, audioFormat, priority, sourcename, x, y, z, attModel, distOrRoll));
    }

    public void quickPlay(boolean priority, boolean toStream, boolean toLoop, String sourcename, FilenameURL filenameURL, float x, float y, float z, int attModel, float distOrRoll, boolean temporary) {
        IntBuffer myBuffer = null;
        if (!toStream) {
            myBuffer = this.ALBufferMap.get(filenameURL.getFilename());
            if (myBuffer == null) {
                this.loadSound(filenameURL);
            }

            myBuffer = this.ALBufferMap.get(filenameURL.getFilename());
            if (myBuffer == null) {
                this.errorMessage("Sound buffer was not created for " + filenameURL.getFilename());
                return;
            }
        }

        SoundBuffer buffer = null;
        if (!toStream) {
            buffer = this.bufferMap.get(filenameURL.getFilename());
            if (buffer == null && !this.loadSound(filenameURL)) {
                this.errorMessage("Source '" + sourcename + "' was not created " + "because an error occurred while loading " + filenameURL.getFilename());
                return;
            }

            buffer = this.bufferMap.get(filenameURL.getFilename());
            if (buffer == null) {
                this.errorMessage("Source '" + sourcename + "' was not created " + "because audio data was not found for " + filenameURL.getFilename());
                return;
            }
        }

        SourceLWJGL3OpenAL s = new SourceLWJGL3OpenAL(this.listenerPositionAL, myBuffer, priority, toStream, toLoop, sourcename, filenameURL, buffer, x, y, z, attModel, distOrRoll, false);
        this.sourceMap.put(sourcename, s);
        this.play(s);
        if (temporary) {
            s.setTemporary(true);
        }

    }

    public void copySources(HashMap<String, Source> srcMap) {
        if (srcMap != null) {
            Set<String> keys = srcMap.keySet();
            Iterator<String> iter = keys.iterator();
            if (this.bufferMap == null) {
                this.bufferMap = new HashMap();
                this.importantMessage("Buffer Map was null in method 'copySources'");
            }

            if (this.ALBufferMap == null) {
                this.ALBufferMap = new HashMap();
                this.importantMessage("Open AL Buffer Map was null in method'copySources'");
            }

            this.sourceMap.clear();

            while (true) {
                String sourcename;
                Source source;
                SoundBuffer buffer;
                do {
                    do {
                        if (!iter.hasNext()) {
                            return;
                        }

                        sourcename = iter.next();
                        source = srcMap.get(sourcename);
                    } while (source == null);

                    buffer = null;
                    if (!source.toStream) {
                        this.loadSound(source.filenameURL);
                        buffer = this.bufferMap.get(source.filenameURL.getFilename());
                    }
                } while (!source.toStream && buffer == null);

                this.sourceMap.put(sourcename, new SourceLWJGL3OpenAL(this.listenerPositionAL, this.ALBufferMap.get(source.filenameURL.getFilename()), source, buffer));
            }
        }
    }

    public void setListenerPosition(float x, float y, float z) {
        super.setListenerPosition(x, y, z);
        this.listenerPositionAL.put(0, x);
        this.listenerPositionAL.put(1, y);
        this.listenerPositionAL.put(2, z);
        AL10.alListenerfv(4100, this.listenerPositionAL);
        this.checkALError();
    }

    public void setListenerAngle(float angle) {
        super.setListenerAngle(angle);
        this.listenerOrientation.put(0, this.listener.lookAt.x);
        this.listenerOrientation.put(2, this.listener.lookAt.z);
        AL10.alListenerfv(4111, this.listenerOrientation);
        this.checkALError();
    }

    public void setListenerOrientation(float lookX, float lookY, float lookZ, float upX, float upY, float upZ) {
        super.setListenerOrientation(lookX, lookY, lookZ, upX, upY, upZ);
        this.listenerOrientation.put(0, lookX);
        this.listenerOrientation.put(1, lookY);
        this.listenerOrientation.put(2, lookZ);
        this.listenerOrientation.put(3, upX);
        this.listenerOrientation.put(4, upY);
        this.listenerOrientation.put(5, upZ);
        AL10.alListenerfv(4111, this.listenerOrientation);
        this.checkALError();
    }

    public void setListenerData(ListenerData l) {
        super.setListenerData(l);
        this.listenerPositionAL.put(0, l.position.x);
        this.listenerPositionAL.put(1, l.position.y);
        this.listenerPositionAL.put(2, l.position.z);
        AL10.alListenerfv(4100, this.listenerPositionAL);
        this.checkALError();
        this.listenerOrientation.put(0, l.lookAt.x);
        this.listenerOrientation.put(1, l.lookAt.y);
        this.listenerOrientation.put(2, l.lookAt.z);
        this.listenerOrientation.put(3, l.up.x);
        this.listenerOrientation.put(4, l.up.y);
        this.listenerOrientation.put(5, l.up.z);
        AL10.alListenerfv(4111, this.listenerOrientation);
        this.checkALError();
        this.listenerVelocity.put(0, l.velocity.x);
        this.listenerVelocity.put(1, l.velocity.y);
        this.listenerVelocity.put(2, l.velocity.z);
        AL10.alListenerfv(4102, this.listenerVelocity);
        this.checkALError();
    }

    public void setListenerVelocity(float x, float y, float z) {
        super.setListenerVelocity(x, y, z);
        this.listenerVelocity.put(0, this.listener.velocity.x);
        this.listenerVelocity.put(1, this.listener.velocity.y);
        this.listenerVelocity.put(2, this.listener.velocity.z);
        AL10.alListenerfv(4102, this.listenerVelocity);
    }

    public void dopplerChanged() {
        super.dopplerChanged();
        AL10.alDopplerFactor(SoundSystemConfig.getDopplerFactor());
        this.checkALError();
        AL10.alDopplerVelocity(SoundSystemConfig.getDopplerVelocity());
        this.checkALError();
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

    public static boolean alPitchSupported() {
        return alPitchSupported(false, false);
    }

    private static synchronized boolean alPitchSupported(boolean action, boolean value) {
        if (action) {
            alPitchSupported = value;
        }

        return alPitchSupported;
    }

    public static String getTitle() {
        return "LWJGL OpenAL";
    }

    public static String getDescription() {
        return "The LWJGL binding of OpenAL.  For more information, see http://www.lwjgl.org";
    }

    public String getClassName() {
        return "LibraryLWJGLOpenAL";
    }

    public static class Exception extends SoundSystemException {
        public static final int CREATE = 101;
        public static final int INVALID_NAME = 102;
        public static final int INVALID_ENUM = 103;
        public static final int INVALID_VALUE = 104;
        public static final int INVALID_OPERATION = 105;
        public static final int OUT_OF_MEMORY = 106;
        public static final int LISTENER = 107;
        public static final int NO_AL_PITCH = 108;

        public Exception(String message) {
            super(message);
        }

        public Exception(String message, int type) {
            super(message, type);
        }
    }
}
