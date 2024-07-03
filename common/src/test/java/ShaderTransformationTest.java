import com.github.andrew0030.pandora_core.client.shader.templating.TemplateTransformation;
import com.github.andrew0030.pandora_core.client.shader.templating.TemplateTransformationParser;
import com.github.andrew0030.pandora_core.client.shader.templating.transformer.impl.DefaultTransformationProcessor;
import com.github.andrew0030.pandora_core.client.utils.shader.ShaderFile;
import com.github.andrew0030.pandora_core.client.utils.shader.ShaderParser;

import java.io.InputStream;

public class ShaderTransformationTest {
    public static void main(String[] args) {
        ClassLoader ldr = ShaderTransformationTest.class.getClassLoader();
        ShaderFile file = ShaderParser.parse(load(ldr.getResourceAsStream("rendertype_entity_solid.vsh")));

        TemplateTransformationParser parser = new TemplateTransformationParser();
        DefaultTransformationProcessor processor = new DefaultTransformationProcessor();
        TemplateTransformation transformation = parser.parse(null, load(ldr.getResourceAsStream("transformation.glsl")));

        ShaderFile result = processor.process(file, transformation);
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
