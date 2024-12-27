/*
 * MoonLight Hacked Client
 *
 * A free and open-source hacked client for Minecraft.
 * Developed using Minecraft's resources.
 *
 * Repository: https://github.com/randomguy3725/MoonLight
 *
 * Author(s): [Randumbguy & opZywl & lucas]
 */
package wtf.moonlight.utils.render.shader;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import wtf.moonlight.utils.InstanceAccess;

import java.io.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

public class ShaderUtils implements InstanceAccess {
    private final int programID;

    public ShaderUtils(String fragmentShaderLoc, String vertexShaderLoc) {
        int program = glCreateProgram();
        try {
            int fragmentShaderID;
            switch (fragmentShaderLoc) {
                case "shadow":
                    fragmentShaderID = createShader(new ByteArrayInputStream(bloom.getBytes()), GL_FRAGMENT_SHADER);
                    break;
                case "roundRectTexture":
                    fragmentShaderID = createShader(new ByteArrayInputStream(roundRectTexture.getBytes()), GL_FRAGMENT_SHADER);
                    break;
                case "roundRectOutline":
                    fragmentShaderID = createShader(new ByteArrayInputStream(roundRectOutline.getBytes()), GL_FRAGMENT_SHADER);
                    break;
                case "roundedRect":
                    fragmentShaderID = createShader(new ByteArrayInputStream(roundedRect.getBytes()), GL_FRAGMENT_SHADER);
                    break;
                case "roundedRectGradient":
                    fragmentShaderID = createShader(new ByteArrayInputStream(roundedRectGradient.getBytes()), GL_FRAGMENT_SHADER);
                    break;
                case "gradient":
                    fragmentShaderID = createShader(new ByteArrayInputStream(gradient.getBytes()), GL_FRAGMENT_SHADER);
                    break;
                case "mainmenu":
                    fragmentShaderID = createShader(new ByteArrayInputStream(mainmenu.getBytes()), GL_FRAGMENT_SHADER);
                    break;
                case "kawaseUp":
                    fragmentShaderID = createShader(new ByteArrayInputStream(kawaseUp.getBytes()), GL_FRAGMENT_SHADER);
                    break;
                case "kawaseDown":
                    fragmentShaderID = createShader(new ByteArrayInputStream(kawaseDown.getBytes()), GL_FRAGMENT_SHADER);
                    break;
                case "kawaseUpBloom":
                    fragmentShaderID = createShader(new ByteArrayInputStream(kawaseUpBloom.getBytes()), GL_FRAGMENT_SHADER);
                    break;
                case "kawaseDownBloom":
                    fragmentShaderID = createShader(new ByteArrayInputStream(kawaseDownBloom.getBytes()), GL_FRAGMENT_SHADER);
                    break;
                case "gaussianBlur":
                    fragmentShaderID = createShader(new ByteArrayInputStream(gaussianBlur.getBytes()), GL_FRAGMENT_SHADER);
                    break;
                case "cape":
                    fragmentShaderID = createShader(new ByteArrayInputStream(cape.getBytes()), GL_FRAGMENT_SHADER);
                    break;
                case "outline":
                    fragmentShaderID = createShader(new ByteArrayInputStream(outline.getBytes()), GL_FRAGMENT_SHADER);
                    break;
                case "glow":
                    fragmentShaderID = createShader(new ByteArrayInputStream(glow.getBytes()), GL_FRAGMENT_SHADER);
                    break;
                default:
                    fragmentShaderID = createShader(mc.getResourceManager().getResource(new ResourceLocation(fragmentShaderLoc)).getInputStream(), GL_FRAGMENT_SHADER);
                    break;
            }
            glAttachShader(program, fragmentShaderID);

            int vertexShaderID = createShader(mc.getResourceManager().getResource(new ResourceLocation(vertexShaderLoc)).getInputStream(), GL_VERTEX_SHADER);
            glAttachShader(program, vertexShaderID);


        } catch (IOException e) {
            e.printStackTrace();
        }

        glLinkProgram(program);
        int status = glGetProgrami(program, GL_LINK_STATUS);

        if (status == 0) {
            throw new IllegalStateException("Shader failed to link!");
        }
        this.programID = program;
    }

    public ShaderUtils(String fragmentShaderLoc) {
        this(fragmentShaderLoc, "moonlight/shader/vertex.vsh");
    }


    public void init() {
        glUseProgram(programID);
    }

    public void unload() {
        glUseProgram(0);
    }

    public int getUniform(String name) {
        return glGetUniformLocation(programID, name);
    }


    public void setUniformf(String name, float... args) {
        int loc = glGetUniformLocation(programID, name);
        switch (args.length) {
            case 1:
                glUniform1f(loc, args[0]);
                break;
            case 2:
                glUniform2f(loc, args[0], args[1]);
                break;
            case 3:
                glUniform3f(loc, args[0], args[1], args[2]);
                break;
            case 4:
                glUniform4f(loc, args[0], args[1], args[2], args[3]);
                break;
        }
    }

    public void setUniformi(String name, int... args) {
        int loc = glGetUniformLocation(programID, name);
        if (args.length > 1) glUniform2i(loc, args[0], args[1]);
        else glUniform1i(loc, args[0]);
    }

    public static void drawQuads(float x, float y, float width, float height) {
        glBegin(GL_QUADS);
        glTexCoord2f(0, 0);
        glVertex2f(x, y);
        glTexCoord2f(0, 1);
        glVertex2f(x, y + height);
        glTexCoord2f(1, 1);
        glVertex2f(x + width, y + height);
        glTexCoord2f(1, 0);
        glVertex2f(x + width, y);
        glEnd();
    }

    public static void drawQuads() {
        ScaledResolution sr = new ScaledResolution(mc);
        float width = (float) sr.getScaledWidth_double();
        float height = (float) sr.getScaledHeight_double();
        glBegin(GL_QUADS);
        glTexCoord2f(0, 1);
        glVertex2f(0, 0);
        glTexCoord2f(0, 0);
        glVertex2f(0, height);
        glTexCoord2f(1, 0);
        glVertex2f(width, height);
        glTexCoord2f(1, 1);
        glVertex2f(width, 0);
        glEnd();
    }

    public static void drawQuads(float width, float height) {
        drawQuads(0.0f, 0.0f, width, height);
    }

    public static void drawFixedQuads() {
        ScaledResolution sr = new ScaledResolution(mc);
        drawQuads((float) (mc.displayWidth / sr.getScaleFactor()), (float) mc.displayHeight / sr.getScaleFactor());
    }

    private int createShader(InputStream inputStream, int shaderType) {
        int shader = glCreateShader(shaderType);
        glShaderSource(shader, readInputStream(inputStream));
        glCompileShader(shader);


        if (glGetShaderi(shader, GL_COMPILE_STATUS) == 0) {
            System.out.println(glGetShaderInfoLog(shader, 4096));
            throw new IllegalStateException(String.format("Shader (%s) failed to compile!", shaderType));
        }

        return shader;
    }

    public static String readInputStream(InputStream inputStream) {
        StringBuilder stringBuilder = new StringBuilder();

        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = bufferedReader.readLine()) != null)
                stringBuilder.append(line).append('\n');

        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    private final String bloom = """
            #version 120
            
            uniform sampler2D inTexture, textureToCheck;
            uniform vec2 texelSize, direction;
            uniform float radius;
            uniform float weights[256];
            
            #define offset texelSize * direction
            
            void main() {
                if (direction.y > 0 && texture2D(textureToCheck, gl_TexCoord[0].st).a != 0.0) discard;
                float blr = texture2D(inTexture, gl_TexCoord[0].st).a * weights[0];
            
                for (float f = 1.0; f <= radius; f++) {
                    blr += texture2D(inTexture, gl_TexCoord[0].st + f * offset).a * (weights[int(abs(f))]);
                    blr += texture2D(inTexture, gl_TexCoord[0].st - f * offset).a * (weights[int(abs(f))]);
                }
            
                gl_FragColor = vec4(0.0, 0.0, 0.0, blr);
            }
            """;

    private final String roundRectTexture = """
            #version 120
            
            uniform vec2 location, rectSize;
            uniform sampler2D textureIn;
            uniform float radius, alpha;
            
            float roundedBoxSDF(vec2 centerPos, vec2 size, float radius) {
                return length(max(abs(centerPos) -size, 0.)) - radius;
            }
            
            
            void main() {
                float distance = roundedBoxSDF((rectSize * .5) - (gl_TexCoord[0].st * rectSize), (rectSize * .5) - radius - 1., radius);
                float smoothedAlpha =  (1.0-smoothstep(0.0, 2.0, distance)) * alpha;
                gl_FragColor = vec4(texture2D(textureIn, gl_TexCoord[0].st).rgb, smoothedAlpha);
            }""";

    private final String roundRectOutline = """
            #version 120
            
            uniform vec2 location, rectSize;
            uniform vec4 color, outlineColor;
            uniform float radius, outlineThickness;
            
            float roundedSDF(vec2 centerPos, vec2 size, float radius) {
                return length(max(abs(centerPos) - size + radius, 0.0)) - radius;
            }
            
            void main() {
                float distance = roundedSDF(gl_FragCoord.xy - location - (rectSize * .5), (rectSize * .5) + (outlineThickness *.5) - 1.0, radius);
            
                float blendAmount = smoothstep(0., 2., abs(distance) - (outlineThickness * .5));
            
                vec4 insideColor = (distance < 0.) ? color : vec4(outlineColor.rgb,  0.0);
                gl_FragColor = mix(outlineColor, insideColor, blendAmount);
            
            }""";

    private final String roundedRectGradient = """
            #version 120
            
            uniform vec2 location, rectSize;
            uniform vec4 color1, color2, color3, color4;
            uniform float radius;
            
            #define NOISE .5/255.0
            
            float roundSDF(vec2 p, vec2 b, float r) {
                return length(max(abs(p) - b , 0.0)) - r;
            }
            
            vec4 createGradient(vec2 coords, vec4 color1, vec4 color2, vec4 color3, vec4 color4){
                vec4 color = mix(mix(color1, color2, coords.y), mix(color3, color4, coords.y), coords.x);
                //Dithering the color
                // from https://shader-tutorial.dev/advanced/color-banding-dithering/
                color += mix(NOISE, -NOISE, fract(sin(dot(coords.xy, vec2(12.9898, 78.233))) * 43758.5453));
                return color;
            }
            
            void main() {
                vec2 st = gl_TexCoord[0].st;
                vec2 halfSize = rectSize * .5;
               \s
               // use the bottom leftColor as the alpha
                float smoothedAlpha =  (1.0-smoothstep(0.0, 2., roundSDF(halfSize - (gl_TexCoord[0].st * rectSize), halfSize - radius - 1., radius)));
                vec4 gradient = createGradient(st, color1, color2, color3, color4);    gl_FragColor = vec4(gradient.rgb, gradient.a * smoothedAlpha);
            }""";


    private final String roundedRect = """
            #version 120
            
            uniform vec2 location, rectSize;
            uniform vec4 color;
            uniform float radius;
            uniform bool blur;
            
            float roundSDF(vec2 p, vec2 b, float r) {
                return length(max(abs(p) - b, 0.0)) - r;
            }
            
            
            void main() {
                vec2 rectHalf = rectSize * .5;
                // Smooth the result (free antialiasing).
                float smoothedAlpha =  (1.0-smoothstep(0.0, 1.0, roundSDF(rectHalf - (gl_TexCoord[0].st * rectSize), rectHalf - radius - 1., radius))) * color.a;
                gl_FragColor = vec4(color.rgb, smoothedAlpha);// mix(quadColor, shadowColor, 0.0);
            
            }""";
    private final String kawaseUpBloom = """
            #version 120
            
            uniform sampler2D inTexture, textureToCheck;
            uniform vec2 halfpixel, offset, iResolution;
            uniform int check;
            
            void main() {
              //  if(check && texture2D(textureToCheck, gl_TexCoord[0].st).a > 0.0) discard;
                vec2 uv = vec2(gl_FragCoord.xy / iResolution);
            
                vec4 sum = texture2D(inTexture, uv + vec2(-halfpixel.x * 2.0, 0.0) * offset);
                sum.rgb *= sum.a;
                vec4 smpl1 =  texture2D(inTexture, uv + vec2(-halfpixel.x, halfpixel.y) * offset);
                smpl1.rgb *= smpl1.a;
                sum += smpl1 * 2.0;
                vec4 smp2 = texture2D(inTexture, uv + vec2(0.0, halfpixel.y * 2.0) * offset);
                smp2.rgb *= smp2.a;
                sum += smp2;
                vec4 smp3 = texture2D(inTexture, uv + vec2(halfpixel.x, halfpixel.y) * offset);
                smp3.rgb *= smp3.a;
                sum += smp3 * 2.0;
                vec4 smp4 = texture2D(inTexture, uv + vec2(halfpixel.x * 2.0, 0.0) * offset);
                smp4.rgb *= smp4.a;
                sum += smp4;
                vec4 smp5 = texture2D(inTexture, uv + vec2(halfpixel.x, -halfpixel.y) * offset);
                smp5.rgb *= smp5.a;
                sum += smp5 * 2.0;
                vec4 smp6 = texture2D(inTexture, uv + vec2(0.0, -halfpixel.y * 2.0) * offset);
                smp6.rgb *= smp6.a;
                sum += smp6;
                vec4 smp7 = texture2D(inTexture, uv + vec2(-halfpixel.x, -halfpixel.y) * offset);
                smp7.rgb *= smp7.a;
                sum += smp7 * 2.0;
                vec4 result = sum / 12.0;
                gl_FragColor = vec4(result.rgb / result.a, mix(result.a, result.a * (1.0 - texture2D(textureToCheck, gl_TexCoord[0].st).a),check));
            }""";

    private final String kawaseDownBloom = """
            #version 120
            
            uniform sampler2D inTexture;
            uniform vec2 offset, halfpixel, iResolution;
            
            void main() {
                vec2 uv = vec2(gl_FragCoord.xy / iResolution);
                vec4 sum = texture2D(inTexture, gl_TexCoord[0].st);
                sum.rgb *= sum.a;
                sum *= 4.0;
                vec4 smp1 = texture2D(inTexture, uv - halfpixel.xy * offset);
                smp1.rgb *= smp1.a;
                sum += smp1;
                vec4 smp2 = texture2D(inTexture, uv + halfpixel.xy * offset);
                smp2.rgb *= smp2.a;
                sum += smp2;
                vec4 smp3 = texture2D(inTexture, uv + vec2(halfpixel.x, -halfpixel.y) * offset);
                smp3.rgb *= smp3.a;
                sum += smp3;
                vec4 smp4 = texture2D(inTexture, uv - vec2(halfpixel.x, -halfpixel.y) * offset);
                smp4.rgb *= smp4.a;
                sum += smp4;
                vec4 result = sum / 8.0;
                gl_FragColor = vec4(result.rgb / result.a, result.a);
            }""";

    private final String kawaseUp = "#version 120\n" +
            "\n" +
            "uniform sampler2D inTexture, textureToCheck;\n" +
            "uniform vec2 halfpixel, offset, iResolution;\n" +
            "uniform int check;\n" +
            "\n" +
            "void main() {\n" +
            "    vec2 uv = vec2(gl_FragCoord.xy / iResolution);\n" +
            "    vec4 sum = texture2D(inTexture, uv + vec2(-halfpixel.x * 2.0, 0.0) * offset);\n" +
            "    sum += texture2D(inTexture, uv + vec2(-halfpixel.x, halfpixel.y) * offset) * 2.0;\n" +
            "    sum += texture2D(inTexture, uv + vec2(0.0, halfpixel.y * 2.0) * offset);\n" +
            "    sum += texture2D(inTexture, uv + vec2(halfpixel.x, halfpixel.y) * offset) * 2.0;\n" +
            "    sum += texture2D(inTexture, uv + vec2(halfpixel.x * 2.0, 0.0) * offset);\n" +
            "    sum += texture2D(inTexture, uv + vec2(halfpixel.x, -halfpixel.y) * offset) * 2.0;\n" +
            "    sum += texture2D(inTexture, uv + vec2(0.0, -halfpixel.y * 2.0) * offset);\n" +
            "    sum += texture2D(inTexture, uv + vec2(-halfpixel.x, -halfpixel.y) * offset) * 2.0;\n" +
            "\n" +
            "    gl_FragColor = vec4(sum.rgb /12.0, mix(1.0, texture2D(textureToCheck, gl_TexCoord[0].st).a, check));\n" +
            "}\n";

    private final String kawaseDown = "#version 120\n" +
            "\n" +
            "uniform sampler2D inTexture;\n" +
            "uniform vec2 offset, halfpixel, iResolution;\n" +
            "\n" +
            "void main() {\n" +
            "    vec2 uv = vec2(gl_FragCoord.xy / iResolution);\n" +
            "    vec4 sum = texture2D(inTexture, gl_TexCoord[0].st) * 4.0;\n" +
            "    sum += texture2D(inTexture, uv - halfpixel.xy * offset);\n" +
            "    sum += texture2D(inTexture, uv + halfpixel.xy * offset);\n" +
            "    sum += texture2D(inTexture, uv + vec2(halfpixel.x, -halfpixel.y) * offset);\n" +
            "    sum += texture2D(inTexture, uv - vec2(halfpixel.x, -halfpixel.y) * offset);\n" +
            "    gl_FragColor = vec4(sum.rgb * .125, 1.0);\n" +
            "}\n";

    private final String gradient = """
            #version 120
            
            uniform vec2 location, rectSize;
            uniform sampler2D tex;
            uniform vec4 color1, color2, color3, color4;
            
            #define NOISE .5/255.0
            
            vec3 createGradient(vec2 coords, vec4 color1, vec4 color2, vec4 color3, vec4 color4){
                vec3 color = mix(mix(color1.rgb, color2.rgb, coords.y), mix(color3.rgb, color4.rgb, coords.y), coords.x);
                //Dithering the color from https://shader-tutorial.dev/advanced/color-banding-dithering/
                color += mix(NOISE, -NOISE, fract(sin(dot(coords.xy, vec2(12.9898,78.233))) * 43758.5453));
                return color;
            }
            void main() {
                vec2 coords = (gl_FragCoord.xy - location) / rectSize;
                float texColorAlpha = texture2D(tex, gl_TexCoord[0].st).a;
                gl_FragColor = vec4(createGradient(coords, color1, color2, color3, color4).rgb, texColorAlpha);
            }""";

    private final String mainmenu = """
            uniform float TIME;
            uniform vec2 RESOLUTION;
            const float PI = 3.141592654;
            const float TAU = 3.141592654 * 2;
            
            const float gravity = 1.0;
            const float waterTension = 0.01;
            
            const vec3 skyCol1 = vec3(0.6, 0.35, 0.3).zyx*0.5;
            const vec3 skyCol2 = vec3(1.0, 0.3, 0.3).zyx*0.5 ;
            const vec3 sunCol1 = vec3(1.0,0.5,0.4).zyx;
            const vec3 sunCol2 = vec3(1.0,0.8,0.8).zyx;
            const vec3 seaCol1 = vec3(0.1,0.2,0.2)*0.2;
            const vec3 seaCol2 = vec3(0.2,0.9,0.6)*0.5;
            
            // License: Unknown, author: Unknown, found: don't remember
            float tanh_approx(float x) {
              //  Found this somewhere on the interwebs
              //  return tanh(x);
              float x2 = x*x;
              return clamp(x*(27.0 + x2)/(27.0+9.0*x2), -1.0, 1.0);
            }
            
            vec2 wave(in float t, in float a, in float w, in float p) {
              float x = t;
              float y = a*sin(t*w + p);
              return vec2(x, y);
            }
            
            vec2 dwave(in float t, in float a, in float w, in float p) {
              float dx = 1.0;
              float dy = a*w*cos(t*w + p);
              return vec2(dx, dy);
            }
            
            vec2 gravityWave(in float t, in float a, in float k, in float h) {
              float w = sqrt(gravity*k*tanh_approx(k*h));
              return wave(t, a ,k, w*TIME);
            }
            
            vec2 capillaryWave(in float t, in float a, in float k, in float h) {
              float w = sqrt((gravity*k + waterTension*k*k*k)*tanh_approx(k*h));
              return wave(t, a, k, w*TIME);
            }
            
            vec2 gravityWaveD(in float t, in float a, in float k, in float h) {
              float w = sqrt(gravity*k*tanh_approx(k*h));
              return dwave(t, a, k, w*TIME);
            }
            
            vec2 capillaryWaveD(in float t, in float a, in float k, in float h) {
              float w = sqrt((gravity*k + waterTension*k*k*k)*tanh_approx(k*h));
              return dwave(t, a, k, w*TIME);
            }
            
            void mrot(inout vec2 p, in float a) {
              float c = cos(a);
              float s = sin(a);
              p = vec2(c*p.x + s*p.y, -s*p.x + c*p.y);
            }
            
            vec4 sea(in vec2 p, in float ia) {
              float y = 0.0;
              vec3 d = vec3(0.0);
            
              const int maxIter = 8;
              const int midIter = 4;
            
              float kk = 1.0/1.3;
              float aa = 1.0/(kk*kk);
              float k = 1.0*pow(kk, -float(maxIter) + 1.0);
              float a = ia*0.25*pow(aa, -float(maxIter) + 1.0);
            
              float h = 25.0;
              p *= 0.5;
            
              vec2 waveDir = vec2(0.0, 1.0);
            
              for (int i = midIter; i < maxIter; ++i) {
                float t = dot(-waveDir, p) + float(i);
                y += capillaryWave(t, a, k, h).y;
                vec2 dw = capillaryWaveD(-t, a, k, h);
            
                d += vec3(waveDir.x, dw.y, waveDir.y);
            
                mrot(waveDir, PI/3.0);
            
                k *= kk;
                a *= aa;
              }
            
              waveDir = vec2(0.0, 1.0);
            
              for (int i = 0; i < midIter; ++i) {
                float t = dot(waveDir, p) + float(i);
                y += gravityWave(t, a, k, h).y;
                vec2 dw = gravityWaveD(t, a, k, h);
            
                vec2 d2 = vec2(0.0, dw.x);
            
                d += vec3(waveDir.x, dw.y, waveDir.y);
            
                mrot(waveDir, -step(2.0, float(i)));
            
                k *= kk;
                a *= aa;
              }
            
              vec3 t = normalize(d);
              vec3 nxz = normalize(vec3(t.z, 0.0, -t.x));
              vec3 nor = cross(t, nxz);
            
              return vec4(y, nor);
            }
            
            vec3 sunDirection() {
              vec3 dir = normalize(vec3(0, 0.06, 1));
              return dir;
            }
            
            vec3 skyColor(in vec3 rd) {
              vec3 sunDir = sunDirection();
              float sunDot = max(dot(rd, sunDir), 0.0);
              vec3 final = vec3(0.0);
              final += mix(skyCol1, skyCol2, rd.y);
              final += 0.5*sunCol1*pow(sunDot, 90.0);
              final += 4.0*sunCol2*pow(sunDot, 900.0);
              return final;
            }
            
            vec3 render(in vec3 ro, in vec3 rd) {
              vec3 col = vec3(0.0);
            
              float dsea = (0.0 - ro.y)/rd.y;
            
              vec3 sunDir = sunDirection();
            
              vec3 sky = skyColor(rd);
            
              if (dsea > 0.0) {
                vec3 p = ro + dsea*rd;
                vec4 s = sea(p.xz, 1.0);
                float h = s.x;   
                vec3 nor = s.yzw;
                nor = mix(nor, vec3(0.0, 1.0, 0.0), smoothstep(0.0, 200.0, dsea));
            
                float fre = clamp(1.0 - dot(-nor,rd), 0.0, 1.0);
                fre = fre*fre*fre;
                float dif = mix(0.25, 1.0, max(dot(nor,sunDir), 0.0));
            
                vec3 refl = skyColor(reflect(rd, nor));
                vec3 refr = seaCol1 + dif*sunCol1*seaCol2*0.1;
            
                col = mix(refr, 0.9*refl, fre);
            
                float atten = max(1.0 - dot(dsea,dsea) * 0.001, 0.0);
                col += seaCol2*(p.y - h) * 2.0 * atten;
            
                col = mix(col, sky, 1.0 - exp(-0.01*dsea));
            
              } else {
                col = sky;
              }
            
              return col;
            }
            
            void mainImage( out vec4 fragColor, in vec2 fragCoord ) {
              vec2 q = fragCoord/RESOLUTION.xy;
              vec2 p = -1.0 + 2.0*q;
              p.x *= RESOLUTION.x/RESOLUTION.y;
            
              vec3 ro = vec3(0.0, 10.0, 0.0);
              vec3 ww = normalize(vec3(0.0, -0.1, 1.0));
              vec3 uu = normalize(cross( vec3(0.0,1.0,0.0), ww));
              vec3 vv = normalize(cross(ww,uu));
              vec3 rd = normalize(p.x*uu + p.y*vv + 2.5*ww);
            
              vec3 col = render(ro, rd);
            
              fragColor = vec4(col,1.0);
            }
            
            void main(void)
            {
             mainImage(gl_FragColor, gl_FragCoord.xy);
            }
            """;

    private final String gaussianBlur = """
            #version 120
            
            uniform sampler2D textureIn;
            uniform vec2 texelSize, direction;
            uniform float radius;
            uniform float weights[256];
            
            #define offset texelSize * direction
            
            void main() {
                vec3 blr = texture2D(textureIn, gl_TexCoord[0].st).rgb * weights[0];
            
                for (float f = 1.0; f <= radius; f++) {
                    blr += texture2D(textureIn, gl_TexCoord[0].st + f * offset).rgb * (weights[int(abs(f))]);
                    blr += texture2D(textureIn, gl_TexCoord[0].st - f * offset).rgb * (weights[int(abs(f))]);
                }
            
                gl_FragColor = vec4(blr, 1.0);
            }
            """;

    private final String cape = """
            #extension GL_OES_standard_derivatives : enable
            
            #ifdef GL_ES
            precision highp float;
            #endif
            
            uniform float time;
            uniform vec2  resolution;
            uniform float zoom;
            
            #define PI 3.1415926535
            
            mat2 rotate3d(float angle)
            {
                return mat2(cos(angle), -sin(angle), sin(angle), cos(angle));
            }
            
            void main()
            {
                vec2 p = (gl_FragCoord.xy * 2.0 - resolution) / min(resolution.x, resolution.y);
                p = rotate3d((time * 2.0) * PI) * p;
                float t;
                if (sin(time) == 10.0)
                    t = 0.075 / abs(1.0 - length(p));
                else
                    t = 0.075 / abs(0.4/*sin(time)*/ - length(p));
                gl_FragColor = vec4(     ( 1. -exp( -vec3(t)  * vec3(0.13*(sin(time)+12.0), p.y*0.7, 3.0) )) , 1.0);
            }""";

    private final String glow = """
            #version 120
            
            uniform sampler2D textureIn, textureToCheck;
            uniform vec2 texelSize, direction;
            uniform vec3 color;
            uniform bool avoidTexture;
            uniform float exposure, radius;
            uniform float weights[256];
            
            #define offset direction * texelSize
            
            void main() {
                if (direction.y == 1 && avoidTexture) {
                    if (texture2D(textureToCheck, gl_TexCoord[0].st).a != 0.0) discard;
                }
            
                float innerAlpha = texture2D(textureIn, gl_TexCoord[0].st).a * weights[0];
            
                for (float r = 1.0; r <= radius; r ++) {
                    innerAlpha += texture2D(textureIn, gl_TexCoord[0].st + offset * r).a * weights[int(r)];
                    innerAlpha += texture2D(textureIn, gl_TexCoord[0].st - offset * r).a * weights[int(r)];
                }
            
                gl_FragColor = vec4(color, mix(innerAlpha, 1.0 - exp(-innerAlpha * exposure), step(0.0, direction.y)));
            }
            """;

    private final String outline = """
            #version 120
            
            uniform vec2 texelSize, direction;
            uniform sampler2D texture;
            uniform float radius;
            uniform vec3 color;
            
            #define offset direction * texelSize
            
            void main() {
                float centerAlpha = texture2D(texture, gl_TexCoord[0].xy).a;
                float innerAlpha = centerAlpha;
                for (float r = 1.0; r <= radius; r++) {
                    float alphaCurrent1 = texture2D(texture, gl_TexCoord[0].xy + offset * r).a;
                    float alphaCurrent2 = texture2D(texture, gl_TexCoord[0].xy - offset * r).a;
            
                    innerAlpha += alphaCurrent1 + alphaCurrent2;
                }
            
                gl_FragColor = vec4(color, innerAlpha) * step(0.0, -centerAlpha);
            }
            
            
            """;
}
