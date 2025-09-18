#version 150

uniform sampler2D Sampler0;
uniform vec4 ColorModulator;

in vec2 texCoord0;
in vec4 vertexColor;

out vec4 fragColor;

void main() {
    // Attempt 1 (Nope):
    // This code did not fix the repeating texture issue on its own.
//    vec2 uv = clamp(texCoord0, vec2(0.0), vec2(1.0)); // Clamps the coordinates to 0 - 1 in order to simulate "GL_CLAMP_TO_EDGES"
//    vec4 color = texture(Sampler0, uv) * vertexColor; // Calculates color with the clamped UV coordinates

    // Attempt 2 (Fix):
    // By offseting the Sampler by half-texels, we effectively simulate "GL_CLAMP_TO_EDGES".
//    ivec2 texSize = textureSize(Sampler0, 0); // Base size
//    vec2 halfTexel = 0.5 / vec2(texSize);     // Normalized half-texel
//    vec2 uv = clamp(texCoord0, halfTexel, vec2(1.0) - halfTexel);
//    vec4 color = texture(Sampler0, uv) * vertexColor;

    // For the time being the "fix" mentioned above is implemented using raw GL code to set
    // the texture wrap mode to "GL_CLAMP_TO_EDGES". Should that code ever break or cause
    // issues, I will likely swap to the code above.

    vec4 color = texture(Sampler0, texCoord0) * vertexColor;
    // By checking for less than or equal to 0.0 we get the desired full alpha range
    if (color.a <= 0.0) {
        discard;
    }
    fragColor = color * ColorModulator;
}