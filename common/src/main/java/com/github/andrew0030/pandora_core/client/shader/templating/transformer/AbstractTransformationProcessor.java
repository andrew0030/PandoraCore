package com.github.andrew0030.pandora_core.client.shader.templating.transformer;

import com.github.andrew0030.pandora_core.client.shader.templating.TemplateTransformation;
import com.github.andrew0030.pandora_core.client.utils.shader.ShaderFile;

public abstract class AbstractTransformationProcessor {
    public static final String TRANSFORM_INJECT = """
            \n%type% PACO_INJECT_TMP_%snowflake% = %transform%;
            #undef %variable%
            #define %variable% PACO_INJECT_TMP_%snowflake%
            #undef PACO_INJECT_TMP_%snowflake%""";

    public abstract ShaderFile process(ShaderFile source, TemplateTransformation transformation);
}
