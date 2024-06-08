#version 150

uniform sampler2D DiffuseSampler;

in vec2 texCoord;
in vec2 sampleStep;

uniform float Radius;
uniform float RadiusMultiplier;

out vec4 fragColor;

void main() {
    vec4 blurred = vec4(0.0);
    float actualRadius = round(Radius * RadiusMultiplier);
    float totalWeight = actualRadius * 2 + 1;

    // Sample from -actualRadius to +actualRadius with steps of 1.0
    for (float a = -actualRadius; a <= actualRadius; a += 1.0) {
        vec2 offset = sampleStep * a;
        blurred += texture(DiffuseSampler, texCoord + offset);
    }

    // Average the collected samples
    fragColor = blurred / totalWeight;
}