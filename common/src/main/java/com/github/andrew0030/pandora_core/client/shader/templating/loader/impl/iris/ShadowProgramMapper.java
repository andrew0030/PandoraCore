package com.github.andrew0030.pandora_core.client.shader.templating.loader.impl.iris;

import net.irisshaders.iris.pipeline.programs.ShaderKey;
import net.irisshaders.iris.shaderpack.loading.ProgramId;

import java.util.HashMap;

public class ShadowProgramMapper {
    private static HashMap<ShaderKey, ShaderKey> MAP = new HashMap<>();
    private static HashMap<String, ShaderKey> NAME_MAP = new HashMap<>();

    static {
        for (ShaderKey value : ShaderKey.values()) {
            NAME_MAP.put(value.getName(), value);

            // TODO: check translucent?
            if (
                    value.getProgram() == ProgramId.Entities ||
                            value.getProgram() == ProgramId.EntitiesTrans
            ) {
                MAP.put(value, ShaderKey.SHADOW_ENTITIES_CUTOUT);
            } else if (
                    value.getProgram() == ProgramId.Particles ||
                            value.getProgram() == ProgramId.ParticlesTrans
            ) {
                MAP.put(value, ShaderKey.SHADOW_PARTICLES);
            }
            // TODO: others
        }
    }

    public static ShaderKey getShadow(ShaderKey of) {
        return MAP.getOrDefault(of, null);
    }

    public static ShaderKey getKey(String name) {
         return NAME_MAP.get(name);
    }
}
