in PerInstance {
    mat4 paco_Inject_Matrix;
    ivec2 paco_Inject_Lightmap;
};

const ivec2 paco_Inject_ConstantOverlay = ivec2(0, 10);

mat3 normScale(mat3 mtr) {
    vec3 scale = vec3(
        length(mtr[0]),
        length(mtr[1]),
        length(mtr[2])
    );

    mtr /= length(scale);

    return mtr;
}

// transforms
transform ModelViewMat = ModelViewMat * paco_Inject_Matrix;
// TODO: normalize matrix function
transform Normal = normalize(normScale(mat3(paco_Inject_Matrix)) * Normal);
replace UV1 = paco_Inject_ConstantOverlay;
replace UV2 = paco_Inject_Lightmap;
