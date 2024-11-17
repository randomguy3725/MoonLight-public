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

    private final String gaussainBlur = """
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
            #ifdef GL_ES
            precision mediump float;
            #endif

            // glslsandbox uniforms
            uniform float time;
            uniform vec2 resolution;

            // shadertoy emulation
            #define iTime time
            #define iResolution resolution

            #define CLOUDS_ON


            const float STEPS = 120.0;
            const float STEPSIZE = 0.05;
            const float DRAWDIST = STEPS * STEPSIZE;

            const float PI = 3.1415926535897932384626433832795;
            const float TWOPI = 2.0 * PI;

            const int OCTAVES = 3;

            struct ray
            {
            	vec3 o; //origin
            	vec3 d;	//direction
            };

            vec3 calcCameraRayDir(float fov, vec2 fragCoord, vec2 resolution)\s
            {
            	float fx = tan(radians(fov) / 2.0) / resolution.x;
            	vec2 d = fx * (fragCoord * 2.0 - resolution);
            	vec3 rayDir = normalize(vec3(d, 1.0));
            	return rayDir;
            }

            float hash(vec3 p)
            {
                p  = fract( p*0.3183099 + .1 );
            	p *= 17.0;
                return fract( p.x*p.y*p.z*(p.x+p.y+p.z) );
            }

            float rand(float seed)
            {
            	return fract(sin(seed) * 1231534.9);
            }

            float rand(vec2 seed)\s
            {\s
                return rand(dot(seed, vec2(12.9898, 783.233)));
            }

            float noise( in vec3 x )
            {
            	x *= 2.0;
                vec3 p = floor(x);
                vec3 f = fract(x);
                f = f*f*(3.0-2.0*f);
            	
                return mix(mix(mix( hash(p+vec3(0,0,0)),\s
                                    hash(p+vec3(1,0,0)),f.x),
                               mix( hash(p+vec3(0,1,0)),\s
                                    hash(p+vec3(1,1,0)),f.x),f.y),
                           mix(mix( hash(p+vec3(0,0,1)),\s
                                    hash(p+vec3(1,0,1)),f.x),
                               mix( hash(p+vec3(0,1,1)),\s
                                    hash(p+vec3(1,1,1)),f.x),f.y),f.z);
            }

            float fbm(vec3 p)
            {
            	p *= 1.4;
            	float f = 0.0;
            	float weight = 0.5;
            	for(int i = 0; i < OCTAVES; ++i)
            	{
            		f += weight * noise( p );
            		p.z -= iTime * float(i) * 0.5;
            		weight *= 0.5;
            		p *= 2.0;
            	}
            	return f;
            }

            float density(vec3 p)
            {
                p.y += 1.2;
            	p.y += cos(p.x*1.4) * 0.2;
            	p.y += cos(p.z)*0.1;
                p *= 1.2;
            	p.z += iTime * 0.4;
            	float noise = fbm(p);
            	float clouds = noise*1.5 - p.y - 1.3;
            	return clamp(clouds, 0.0, 1.0);
            }

            vec3 clouds(vec3 p, float d, float l, vec3 bg)
            {
            	vec3 lPos = vec3(0,0, DRAWDIST*1.75);
            	vec3 lDir = lPos - p;

            	float dL = density(p + normalize(lDir) * 0.2);
            	float dG = clamp(d - dL, 0.0, 1.0);
            	dG *= 1.0 - smoothstep(2.0,8.0, length(lDir));
            	dG *= 70.0;
            	vec3 cL = vec3(0, 0.1, 0.1) + vec3(1.0) * dG;
            	vec3 cA = mix( vec3(1.0, 1.0, 1.0), vec3(1.0)*0.01, d);
            	
            	float a = 0.2;
            	float t = exp(-a * l);	
            	return mix(bg, cL * cA, t);
            }

            float stars(vec2 uv, float amount, float radius)
            {
            	uv = uv * amount;
            	vec2 gridID = floor(uv);
            	vec2 starPos = vec2(rand(gridID),rand(gridID+1.0));
            	starPos = (starPos - 0.5) * 2.0;
            	starPos = vec2(0.5) + starPos * (0.5 - radius * 2.0);
            	float stars = distance(fract(uv), starPos);
            	float size = rand(gridID)*radius;
            	stars = 1.0 - smoothstep(size, size + radius, stars);
            	return stars;
            }

            float gradient(vec2 uv)
            {
            	uv.x *= 0.8;
            	uv *= 1.0 + sin(iTime*10.0) * 0.01;
            	float g = clamp(1.0 - length(uv), 0.0, 1.0);
            	return clamp(g, 0.0, 1.0);
            }

            float circle(vec2 uv, float r)
            {
            	return length(uv)-r;
            }

            float smin(float a, float b, float k)
            {
            	float h = clamp( 0.5+0.5*(b-a)/k, 0.0, 1.0 );
                return mix( b, a, h ) - k*h*(1.0-h);
            }

            vec2 rotate(vec2 p, float angle)
            {
            	mat2 mat = mat2(cos(angle),-sin(angle),
            					sin(angle),cos(angle));
            	return p * mat;
            }

            float t(float scale, float k)
            {
            	float t = sin(iTime * scale);
            	t = (t + 1.0)/2.0;
            	t = mix(t,smoothstep(0.0, 1.0, t),k);
            	t = (t - 0.5)*2.0;
            	return t;
            }

            float ghost1(vec2 uv)
            {
            	float time = iTime * 6.0;
            	float t = t(6.0, 0.5);

            	uv.x += 0.5;
            	uv = rotate(uv, t*max(0.0, uv.y)*0.2);
            	uv.y -= 0.4 + sin(time * 2.0) * 0.1 * smoothstep(-0.5, 1.5, uv.y);
            	vec2 originalUV = uv;
            		
            	uv.x *= 1.0 + uv.y;
            	uv.y += max(0.0, -uv.y*0.8); 	
            	float body = circle(uv, 0.2);\s
            	
            	uv = originalUV;
            	uv += vec2(-0.2, 0.2);
            	uv = rotate(uv, -PI/4.0 + t*0.8*uv.x);
            	uv *= vec2(0.4, 2.0);
            	float arms = circle(uv, 0.1);
            	
            	uv = originalUV;
            	uv += vec2(0.2, 0.2);
            	uv = rotate(uv, PI/4.0 + t*0.8*(-uv.x));
            	uv *= vec2(0.4, 2.0);
            	arms = min(arms, circle(uv, 0.1));
            	
            	uv = originalUV;
            	uv.x -= 0.01;
            	uv.y += 0.05;
            	uv.y *= 1.0 + cos(time*2.0)*0.4;
            	float mouth = circle(uv, 0.02);
            	
            	uv = originalUV;
            	uv.x -= 0.11;
            	float eyeR = circle(uv, 0.02);
            	uv.x += 0.2;
            	float eyeL = circle(uv, 0.04);
            	
            	float d = body;
            	d = smin(arms,body, 0.1);
            	d = max(d, -eyeR);
            	d = max(d, -eyeL);
            	d = max(d, -mouth);
            	float threshold = mix(0.04, 0.06, (0.5 +sin(iTime)*0.5));
            	d = 1.0 - smoothstep(-threshold, threshold, d);	
            	return d;
            }

            float ghost2(vec2 uv)
            {
            	uv.x -= 0.4;	
            	uv.y += t(6.0, 0.5)*0.2* smoothstep(-1.0, 0.0, uv.y);
            	vec2 originalUV = uv;\s
            	
            	uv.x *= 1.0 + uv.y*0.4;
            	uv.y *= mix(0.0, 1.0, smoothstep(-0.1, 0.0, uv.y));
            	float body = circle(uv, 0.15);
            	
            	uv = originalUV;
            	uv.x -= 0.06;
            	float eyeR = circle(uv, 0.03);
            	uv.x += 0.14;
            	float eyeL = circle(uv, 0.025);
            	
            	float d = max(body,-eyeR);
            	d = max(d, -eyeL);
            	
            	float threshold = mix(0.04, 0.06, (0.5 +sin(iTime)*0.5));
            	d = 1.0 - smoothstep(-threshold, threshold, d);
            	d *= 0.6;
            	return d;
            }

            float ghosts(vec2 uv)
            {	
            	float d = ghost1(uv) + ghost2(uv);
            	return clamp(d, 0.0, 1.0);
            }

            vec3 tonemapping(vec3 color, float exposure, float gamma)
            {
            	color *= exposure/(1. + color / exposure);
            	color = pow(color, vec3(1. / gamma));
            	float lum = 0.3*color.r + 0.6*color.g + 0.1*color.b;
            	color = mix(color, color*color, 1.0 - smoothstep(0.0,0.4,lum));
            	return color;
            }


            void mainImage( out vec4 fragColor, in vec2 fragCoord )
            {
              	vec2 res = vec2(max(iResolution.x, iResolution.y));
            	vec2 uv = fragCoord.xy / res;
                uv = (uv-vec2(0.5))*2.0;
                uv.y += 0.5;
                uv *= 1.3;

            			
            	ray r;
            	r.o = vec3(0.0);
            	r.d = calcCameraRayDir(60.0,  gl_FragCoord.xy, res);
            		
            	float gradient = gradient(uv);
            	float moon = distance(uv, vec2(0.0,0.1));
            	moon = 1.0 - smoothstep(0.05, 0.08, moon);
            	
            	vec3 bg = mix(vec3(0.0, 0.1, 0.1),vec3(0.1, 0.3, 0.5), min(1.0, gradient*2.0));
            	bg = mix(bg, vec3(0.6, 0.9, 1.0), (max(0.0, gradient - 0.5)) * 2.0);
            	bg += vec3(0.8) * moon;
            	bg += vec3(0.4) * stars(uv,5.0,0.01);
            	bg += vec3(0.4) * stars(uv, 100.0, 0.04);
            	bg += vec3(0.4) * ghosts(uv) * (uv.y+1.0)*0.5;
            	
            	vec4 sum = vec4(0);	
            	float t = 0.0;
            	#ifdef CLOUDS_ON
            	for(int i = 0; i < int(STEPS); i++)
            	{
            		vec3 p = r.o + r.d * t;
            		float d = density(p);
            		if(d > 0.01)
            		{
            			float a = d * (1.0 - smoothstep(DRAWDIST / 2.0, DRAWDIST, t))*0.4;
            			vec3 c = clouds(p, d, t, bg);
            			sum += vec4(c * a, a) * ( 1.0 - sum.a );		
            			if(sum.a > 0.99) break;
            		}
            		t += STEPSIZE;
            	}	
            	#endif
            	vec4 c;
            	c = vec4(bg, 1.0) * (1.0 - sum.a) + sum;
            	c.rgb = tonemapping(c.rgb, 1.5,1.2);
            	fragColor = c;
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
                    uniform float radius, weights[256];

                    #define offset texelSize * direction

                    void main() {
                        vec3 color = texture2D(textureIn, gl_TexCoord[0].st).rgb * weights[0];
                        float totalWeight = weights[0];

                        for (float f = 1.0; f <= radius; f++) {
                            color += texture2D(textureIn, gl_TexCoord[0].st + f * offset).rgb * (weights[int(abs(f))]);
                            color += texture2D(textureIn, gl_TexCoord[0].st - f * offset).rgb * (weights[int(abs(f))]);

                            totalWeight += (weights[int(abs(f))]) * 2.0;
                        }

                        gl_FragColor = vec4(color / totalWeight, 1.0);
                    }""";

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
}
