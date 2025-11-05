//in PerInstance {
//    vec3 paco_Inject_Translation;
//    mat3 paco_Inject_Orientation;
//    ivec2 paco_Inject_Lightmap;
//};
//
//const ivec2 paco_Inject_ConstantOverlay = ivec2(0, 10);
//
//#extension GL_ARB_shader_bit_encoding : enable
//#extension GL_ARB_gpu_shader5 : enable
//
//ivec3 pcg3d(ivec3 v) {
//    v = ivec3(uvec3(v) * 1664525u + 1013904223u);
//    v.x += v.y*v.z;
//    v.y += v.z*v.x;
//    v.z += v.x*v.y;
//    v ^= v >> 16u;
//    v.x += v.y*v.z;
//    v.y += v.z*v.x;
//    v.z += v.x*v.y;
//    return v;
//}
//
//mat4 alterMat(mat4 mat) {
//    mat4 combined = paco_translateMatr(mat, paco_Inject_Translation);
//    ivec4 seed = floatBitsToInt(combined[0]) + floatBitsToInt(combined[1]) + floatBitsToInt(combined[2]) + floatBitsToInt(combined[3]);
//    vec3 v = ((pcg3d(seed.xyz) % 400) / 200.0);
//    vec4 offset = vec4(v - 1, 0.0);
//    return paco_translateMatr(combined, offset);
//}
//
//// transforms
//transform ModelViewMat = paco_rotateMatr(alterMat(ModelViewMat), paco_Inject_Orientation);
//transform Normal = Normal * paco_Inject_Orientation;
//replace UV1 = paco_Inject_ConstantOverlay;
//replace UV2 = paco_Inject_Lightmap;

in PerInstance {
    vec3 paco_Inject_Translation;
    mat3 paco_Inject_Orientation;
    ivec2 paco_Inject_Lightmap;
};

const ivec2 paco_Inject_ConstantOverlay = ivec2(0, 10);

// transforms
transform ModelViewMat = paco_rotateMatr(paco_translateMatr(ModelViewMat, paco_Inject_Translation), paco_Inject_Orientation);
transform Normal = Normal * paco_Inject_Orientation;
replace UV1 = paco_Inject_ConstantOverlay;
replace UV2 = paco_Inject_Lightmap;
