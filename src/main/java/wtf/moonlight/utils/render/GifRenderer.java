package wtf.moonlight.utils.render;

import com.madgag.gif.fmsware.GifDecoder;
import lombok.Getter;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL12;
import wtf.moonlight.utils.InstanceAccess;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

@Getter
public class GifRenderer implements InstanceAccess {
    private final ResourceLocation location;

    private final ArrayList<ImageFrame> frames;

    private int index, textureId;
    private long lastUpdate;

    public GifRenderer(ResourceLocation location) {
        this.location = location;

        this.frames = decodeGif(location);
        this.textureId = loadTextureFromBufferedImage(frames.get(index).getImage());
        this.lastUpdate = System.currentTimeMillis();
    }

    public GifRenderer(InputStream stream) {
        this.location = null;
        this.frames = decodeGif(stream);

        this.textureId = loadTextureFromBufferedImage(frames.get(index).getImage());
        this.lastUpdate = System.currentTimeMillis();
    }
    public void drawTexture(float x, float y, float width, float height) {
        GlStateManager.enableTexture2D();

        glBindTexture(GL_TEXTURE_2D, textureId);
        glBegin(GL_QUADS);
        {
            glTexCoord2f(0, 0);
            glVertex2f(x, y);
            glTexCoord2f(0, 1);
            glVertex2f(x, y + height);
            glTexCoord2f(1, 1);
            glVertex2f(x + width, y + height);
            glTexCoord2f(1, 0);
            glVertex2f(x + width, y);
        }
        glEnd();

        GlStateManager.bindTexture(textureId);
    }

    public void update() {
        if (!(System.currentTimeMillis() - lastUpdate >= frames.get(index).getDelay())) return;

        this.index++;

        if (this.index >= this.frames.size()) this.index = 0;

        deleteTexture(this.textureId);
        this.textureId = loadTextureFromBufferedImage(frames.get(index).getImage());
        this.lastUpdate = System.currentTimeMillis();
    }

    public void scale(float scale) {
        List<ImageFrame> frames = new ArrayList<>();

        for (ImageFrame frame : this.frames) {
            frames.add(new ImageFrame(scale(frame.getImage(), scale), frame.getIndex(), frame.getDelay()));
        }

        this.frames.clear();
        this.frames.addAll(frames);
    }
    public static ArrayList<ImageFrame> decodeGif(InputStream stream) {
        GifDecoder decoder = new GifDecoder();
        decoder.read(stream);

        ImageFrame[] frames = new ImageFrame[decoder.getFrameCount()];
        for(int i = 0; i < decoder.getFrameCount(); i++) {
            frames[i] = new ImageFrame(decoder.getFrame(i), i, decoder.getDelay(i));
        }

        return new ArrayList<>(Arrays.asList(frames));
    }

    public static ArrayList<ImageFrame> decodeGif(ResourceLocation location) {
        try {
            return decodeGif(mc.getResourceManager().getResource(location).getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }

    public void setGifSize(int width, int height) {
        List<ImageFrame> frames = new ArrayList<>();

        for (ImageFrame frame : this.frames) {
            frames.add(new ImageFrame(resize(frame.getImage(), width, height), frame.getIndex(), frame.getDelay()));
        }

        this.frames.clear();
        this.frames.addAll(frames);
    }

    public int getWidth() {
        return this.frames.get(this.index).getImage().getWidth();
    }

    public int getHeight() {
        return this.frames.get(this.index).getImage().getHeight();
    }

    public void deleteTexture() {
        deleteTexture(this.textureId);
    }
    public static void deleteTexture(int textureID) {
        glDeleteTextures(textureID);
    }

    public static int loadTextureFromBufferedImage(BufferedImage image) {
        int[] pixels = new int[image.getWidth() * image.getHeight()];
        image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());

        ByteBuffer buffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * 4);

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int pixel = pixels[y * image.getWidth() + x];
                buffer.put((byte) ((pixel >> 16) & 0xFF));
                buffer.put((byte) ((pixel >> 8) & 0xFF));
                buffer.put((byte) (pixel & 0xFF));
                buffer.put((byte) ((pixel >> 24) & 0xFF));
            }
        }

        buffer.flip();

        int textureID = glGenTextures();

        glBindTexture(GL_TEXTURE_2D, textureID);
        {
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);

            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, image.getWidth(), image.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
        }
        glBindTexture(GL_TEXTURE_2D, 0);
        return textureID;
    }
    public static BufferedImage scale(BufferedImage image, float scale) {
        int width = (int) (image.getWidth() * scale);
        int height = (int) (image.getHeight() * scale);

        BufferedImage scaledImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        scaledImage.getGraphics().drawImage(image, 0, 0, width, height, null);

        return scaledImage;
    }

    public static BufferedImage resize(BufferedImage image, int width, int height) {
        BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        resizedImage.getGraphics().drawImage(image, 0, 0, width, height, null);

        return resizedImage;
    }

    @Getter
    public static class ImageFrame {

        private final BufferedImage image;
        private final int index, delay, width, height;

        public ImageFrame(BufferedImage image, int index, int delay) {
            this.image = image;
            this.index = index;
            this.delay = delay;
            this.width = image.getWidth();
            this.height = image.getHeight();
        }
    }
}
