#version 420 core

#extension GL_ARB_compute_shader : enable
#extension GL_ARB_shader_storage_buffer_object : enable

layout(local_size_x = 1, local_size_y = 1) in;

layout(std140, binding = 0) buffer InputBuffer { vec3 inCoords[]; };
layout(std140, binding = 1) buffer LodBuffer { int ranges[]; };
layout(std140, binding = 2) buffer OutputBuffer { int outData[]; };

uniform mat4 projMatrix;
uniform mat4 modelMatrix;
uniform float modelSize;
uniform int lodLevels;

void main() {
    uint index = gl_GlobalInvocationID.x;
    vec4 pos = projMatrix * modelMatrix * vec4(inCoords[index], 1.0);
    float lodScale = modelSize / pos.w;
    lodScale = clamp(lodScale, 0, lodLevels - 1);
    uint relIndex = index << 1;
    int lod = int(lodScale * lodLevels) << 1;
    outData[relIndex] = ranges[lod];
    outData[relIndex + 1] = ranges[lod + 1];
}
