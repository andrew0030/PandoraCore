in PerInstance {
    mat4 paco_Inject_Matrix;
    ivec2 paco_Inject_Lightmap;
};

const ivec2 paco_Inject_ConstantOverlay = ivec2(0, 10);

// transforms
transform ModelViewMat = ModelViewMat * paco_Inject_Matrix;
// TODO: normalize matrix function
//transform Normal = Normal * normalize(mat3(paco_Inject_Matrix));
replace UV1 = paco_Inject_ConstantOverlay;
replace UV2 = paco_Inject_Lightmap;
