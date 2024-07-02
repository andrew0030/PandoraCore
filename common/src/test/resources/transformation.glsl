#paco_templated vanilla rendertype_entity_solid
#paco_templated iris gbuffers_entities

// PER_INSTANCE
// when a variable is missmatched between vec2 and ivec2, paco assumes the data is meant to be interpreted bitwise
// the reason for this, is because lightmap coords in vanilla are written bitwise and used as an ivec2 in some shaders, but a vec2 in others
#paco_inject
    paco_per_instance vec2 modId_Inject_Lightmap;
    paco_per_instance vec3 modId_Inject_Translation; // translation
    paco_per_instance vec4 modId_Inject_Orientation; // quat4f
#paco_end

#paco_replace UV2 modId_Inject_Lightmap

#paco_transform Position: rotateQuat modId_Inject_Orientation, add modId_Inject_Translation
#paco_transform Normal: rotateQuat modId_Inject_Orientation
