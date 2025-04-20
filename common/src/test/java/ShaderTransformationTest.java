import com.github.andrew0030.pandora_core.client.shader.templating.TemplateTransformation;
import com.github.andrew0030.pandora_core.client.shader.templating.TemplateTransformationParser;
import com.github.andrew0030.pandora_core.client.shader.templating.transformer.VariableMapper;
import com.github.andrew0030.pandora_core.client.shader.templating.transformer.impl.DefaultTransformationProcessor;

import java.io.InputStream;

public class ShaderTransformationTest {
    public static void main(String[] args) {
        ClassLoader ldr = ShaderTransformationTest.class.getClassLoader();
        String file = load(ldr.getResourceAsStream("rendertype_entity_solid.vsh"));

        TemplateTransformationParser parser = new TemplateTransformationParser();
        DefaultTransformationProcessor processor = new DefaultTransformationProcessor();
        TemplateTransformation transformation = parser.parse(null, load(ldr.getResourceAsStream("transformation.glsl")));

        String result = processor.process(new VariableMapper() {
        }, file, transformation);
        System.out.println(result);
    }

    public static String load(InputStream res) {
        try {
            byte[] dat = res.readAllBytes();
            try {
                res.close();
            } catch (Throwable ignored) {
            }
            return new String(dat);
        } catch (Throwable err) {
            throw new RuntimeException(err);
        }
    }
}
