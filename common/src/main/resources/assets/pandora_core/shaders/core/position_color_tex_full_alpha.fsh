#version 150

uniform sampler2D Sampler0;
uniform vec4 ColorModulator;

in vec2 texCoord0;
in vec4 vertexColor;

out vec4 fragColor;

void main() {
    vec4 color = texture(Sampler0, texCoord0) * vertexColor;
    // By checking for less than or equal to 0.0 we get the desired full alpha range
    if (color.a <= 0.0) {
        discard;
    }
    fragColor = color * ColorModulator;
}