//in PerInstance {
//    vec3 paco_Inject_Translation;
//    mat3 paco_Inject_Orientation;
//    ivec2 paco_Inject_Lightmap;
//};
//
//const ivec2 paco_Inject_ConstantOverlay = ivec2(0, 10);
//
//ivec3 pcg3d(ivec3 v) {
//    v = v * 1664525u + 1013904223u;
//    v.x += v.y*v.z;
//    v.y += v.z*v.x;
//    v.z += v.x*v.y;
//    v ^= v>>16u;
//    v.x += v.y*v.z;
//    v.y += v.z*v.x;
//    v.z += v.x*v.y;
//    return v;
//}
//
//vec4 combined = paco_translateMatr(ModelViewMat, paco_Inject_Translation);
//ivec4 seed = floatBitsToInt(combined);
//vec4 offset = (pcg3d(seed) / 200.0) % 1;
//
//// transforms
//ModelViewMat = paco_rotateMatr(combined + offset, paco_Inject_Orientation);
//Normal = Normal * paco_Inject_Orientation;
//UV1 = paco_Inject_ConstantOverlay;
//UV2 = paco_Inject_Lightmap;

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
