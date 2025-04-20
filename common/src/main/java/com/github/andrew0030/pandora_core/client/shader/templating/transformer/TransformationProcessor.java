package com.github.andrew0030.pandora_core.client.shader.templating.transformer;

import com.github.andrew0030.pandora_core.client.shader.templating.TemplateTransformation;

public abstract class TransformationProcessor {
    public static final String TRANSFORM_INJECT = """
            %type% PACO_INJECT_TMP_%snowflake% = %transform%;
            #undef %variable%
            #define %variable% PACO_INJECT_TMP_%snowflake%
            #undef PACO_INJECT_TMP_%snowflake%""";

    public abstract String process(VariableMapper mapper, String source, TemplateTransformation transformation);
}
