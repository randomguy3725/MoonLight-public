package org.lwjglx.opengl;

public class PixelFormat {
    private int bpp;
    private int alpha;
    private int depth;
    private int stencil;
    private int samples;
    private int colorSamples;
    private int num_aux_buffers;
    private int accum_bpp;
    private int accum_alpha;
    private boolean stereo;
    private boolean floating_point;
    private boolean floating_point_packed;
    private boolean sRGB;

    public PixelFormat() {
        this(0, 8, 0);
    }

    public PixelFormat(int alpha, int depth, int stencil) {
        this(alpha, depth, stencil, 0);
    }

    public PixelFormat(int alpha, int depth, int stencil, int samples) {
        this(0, alpha, depth, stencil, samples);
    }

    public PixelFormat(int bpp, int alpha, int depth, int stencil, int samples) {
        this(bpp, alpha, depth, stencil, samples, 0, 0, 0, false);
    }

    public PixelFormat(int bpp, int alpha, int depth, int stencil, int samples, int num_aux_buffers, int accum_bpp, int accum_alpha, boolean stereo) {
        this(bpp, alpha, depth, stencil, samples, num_aux_buffers, accum_bpp, accum_alpha, stereo, false);
    }

    public PixelFormat(int bpp, int alpha, int depth, int stencil, int samples, int num_aux_buffers, int accum_bpp, int accum_alpha, boolean stereo, boolean floating_point) {
        this.bpp = bpp;
        this.alpha = alpha;
        this.depth = depth;
        this.stencil = stencil;
        this.samples = samples;
        this.num_aux_buffers = num_aux_buffers;
        this.accum_bpp = accum_bpp;
        this.accum_alpha = accum_alpha;
        this.stereo = stereo;
        this.floating_point = floating_point;
        this.floating_point_packed = false;
        this.sRGB = false;
    }

    private PixelFormat(PixelFormat pf) {
        this.bpp = pf.bpp;
        this.alpha = pf.alpha;
        this.depth = pf.depth;
        this.stencil = pf.stencil;
        this.samples = pf.samples;
        this.colorSamples = pf.colorSamples;
        this.num_aux_buffers = pf.num_aux_buffers;
        this.accum_bpp = pf.accum_bpp;
        this.accum_alpha = pf.accum_alpha;
        this.stereo = pf.stereo;
        this.floating_point = pf.floating_point;
        this.floating_point_packed = pf.floating_point_packed;
        this.sRGB = pf.sRGB;
    }

    public int getBitsPerPixel() {
        return this.bpp;
    }

    public PixelFormat withBitsPerPixel(int bpp) {
        if (bpp < 0) {
            throw new IllegalArgumentException("Invalid number of bits per pixel specified: " + bpp);
        } else {
            PixelFormat pf = new PixelFormat(this);
            pf.bpp = bpp;
            return pf;
        }
    }

    public int getAlphaBits() {
        return this.alpha;
    }

    public PixelFormat withAlphaBits(int alpha) {
        if (alpha < 0) {
            throw new IllegalArgumentException("Invalid number of alpha bits specified: " + alpha);
        } else {
            PixelFormat pf = new PixelFormat(this);
            pf.alpha = alpha;
            return pf;
        }
    }

    public int getDepthBits() {
        return this.depth;
    }

    public PixelFormat withDepthBits(int depth) {
        if (depth < 0) {
            throw new IllegalArgumentException("Invalid number of depth bits specified: " + depth);
        } else {
            PixelFormat pf = new PixelFormat(this);
            pf.depth = depth;
            return pf;
        }
    }

    public int getStencilBits() {
        return this.stencil;
    }

    public PixelFormat withStencilBits(int stencil) {
        if (stencil < 0) {
            throw new IllegalArgumentException("Invalid number of stencil bits specified: " + stencil);
        } else {
            PixelFormat pf = new PixelFormat(this);
            pf.stencil = stencil;
            return pf;
        }
    }

    public int getSamples() {
        return this.samples;
    }

    public PixelFormat withSamples(int samples) {
        if (samples < 0) {
            throw new IllegalArgumentException("Invalid number of samples specified: " + samples);
        } else {
            PixelFormat pf = new PixelFormat(this);
            pf.samples = samples;
            return pf;
        }
    }

    public PixelFormat withCoverageSamples(int colorSamples) {
        return this.withCoverageSamples(colorSamples, this.samples);
    }

    public PixelFormat withCoverageSamples(int colorSamples, int coverageSamples) {
        if (coverageSamples >= 0 && colorSamples >= 0 && (coverageSamples != 0 || 0 >= colorSamples) && coverageSamples >= colorSamples) {
            PixelFormat pf = new PixelFormat(this);
            pf.samples = coverageSamples;
            pf.colorSamples = colorSamples;
            return pf;
        } else {
            throw new IllegalArgumentException("Invalid number of coverage samples specified: " + coverageSamples + " - " + colorSamples);
        }
    }

    public int getAuxBuffers() {
        return this.num_aux_buffers;
    }

    public PixelFormat withAuxBuffers(int num_aux_buffers) {
        if (num_aux_buffers < 0) {
            throw new IllegalArgumentException("Invalid number of auxiliary buffers specified: " + num_aux_buffers);
        } else {
            PixelFormat pf = new PixelFormat(this);
            pf.num_aux_buffers = num_aux_buffers;
            return pf;
        }
    }

    public int getAccumulationBitsPerPixel() {
        return this.accum_bpp;
    }

    public PixelFormat withAccumulationBitsPerPixel(int accum_bpp) {
        if (accum_bpp < 0) {
            throw new IllegalArgumentException("Invalid number of bits per pixel in the accumulation buffer specified: " + accum_bpp);
        } else {
            PixelFormat pf = new PixelFormat(this);
            pf.accum_bpp = accum_bpp;
            return pf;
        }
    }

    public int getAccumulationAlpha() {
        return this.accum_alpha;
    }

    public PixelFormat withAccumulationAlpha(int accum_alpha) {
        if (accum_alpha < 0) {
            throw new IllegalArgumentException("Invalid number of alpha bits in the accumulation buffer specified: " + accum_alpha);
        } else {
            PixelFormat pf = new PixelFormat(this);
            pf.accum_alpha = accum_alpha;
            return pf;
        }
    }

    public boolean isStereo() {
        return this.stereo;
    }

    public PixelFormat withStereo(boolean stereo) {
        PixelFormat pf = new PixelFormat(this);
        pf.stereo = stereo;
        return pf;
    }

    public boolean isFloatingPoint() {
        return this.floating_point;
    }

    public PixelFormat withFloatingPoint(boolean floating_point) {
        PixelFormat pf = new PixelFormat(this);
        pf.floating_point = floating_point;
        if (floating_point) {
            pf.floating_point_packed = false;
        }

        return pf;
    }

    public PixelFormat withFloatingPointPacked(boolean floating_point_packed) {
        PixelFormat pf = new PixelFormat(this);
        pf.floating_point_packed = floating_point_packed;
        if (floating_point_packed) {
            pf.floating_point = false;
        }

        return pf;
    }

    public boolean isSRGB() {
        return this.sRGB;
    }

    public PixelFormat withSRGB(boolean sRGB) {
        PixelFormat pf = new PixelFormat(this);
        pf.sRGB = sRGB;
        return pf;
    }
}
