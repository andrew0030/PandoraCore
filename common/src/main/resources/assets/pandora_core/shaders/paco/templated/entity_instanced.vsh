// TODO: remove this eventually

// when a variable is missmatched between vec2 and ivec2, paco assumes the data is meant to be interpreted bitwise
// the reason for this, is because lightmap coords in vanilla are written bitwise and used as an ivec2 in some shaders, but a vec2 in others

// PER_INSTANCE
#paco_inject
//#extension GL_ARB_gpu_shader5 : enable
//    paco_per_instance vec2 paco_Inject_Lightmap;
//    paco_per_instance vec2 paco_Inject_Overlay;
    paco_per_instance vec3 paco_Inject_Translation; // translation
//    paco_per_instance vec4 paco_Inject_Orientation; // quat4f
#paco_end

//#paco_replace UV1 paco_Inject_Overlay
//#paco_replace UV2 paco_Inject_Lightmap

#paco_transform Position: add paco_Inject_Translation
//#paco_transform Position: rotateQuat paco_Inject_Orientation, add paco_Inject_Translation
//#paco_transform Normal: rotateQuat paco_Inject_Orientation
