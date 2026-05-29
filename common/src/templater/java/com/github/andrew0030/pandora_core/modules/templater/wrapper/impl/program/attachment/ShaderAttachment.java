package com.github.andrew0030.pandora_core.modules.templater.wrapper.impl.program.attachment;

import com.github.andrew0030.pandora_core.modules.templater.TemplateTransformation;
import com.github.andrew0030.pandora_core.modules.templater.itf.INamedShader;
import com.github.andrew0030.pandora_core.modules.templater.transformer.TransformationProcessor;
import com.github.andrew0030.pandora_core.modules.templater.transformer.VariableMapper;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL32;
import org.lwjgl.opengl.GL40;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class ShaderAttachment {
    int id;
    AttachmentType type;

    public ShaderAttachment(
		    String source, AttachmentType type,
		    TemplateTransformation transformation,
		    INamedShader vanilla, boolean processSource,
		    VariableMapper mapper, TransformationProcessor processor,
		    ResourceLocation location, String dumpMeta
    ) {
        this.type = type;

        if (processSource && processor != null && transformation != null) {
            source = processor.process(mapper, source, transformation);
        }

        id = GL20.glCreateShader(
                switch (type) {
                    case VERTEX -> GL20.GL_VERTEX_SHADER;
                    case FRAGMENT -> GL20.GL_FRAGMENT_SHADER;
                    case GEOMETRY -> GL32.GL_GEOMETRY_SHADER;
                    case TESSELATION_EVAL -> GL40.GL_TESS_EVALUATION_SHADER;
                    case TESSELATION_CONTROL -> GL40.GL_TESS_CONTROL_SHADER;
                }
        );
		
//        GL20.glShaderSource(id, source.replace("out struct", "out").replace(";]", "]"));
        GL20.glShaderSource(id, source);
//        GL20.glShaderSource(id, source);
        GL20.glCompileShader(id);

        if (GlStateManager.glGetShaderi(id, 35713) == 0) {
            String $$7 = StringUtils.trim(GlStateManager.glGetShaderInfoLog(id, 32768));
            GL20.glDeleteShader(id);
            try {
	            dumpShader(location, source, type, dumpMeta + "failed/");
                throw new IOException("Couldn't compile " + location.toString() + " from " + vanilla.pandoraCore$getName() + " program (" + vanilla.pandoraCore$getName() + ", " + location + ") : " + $$7);
            } catch (Throwable err) {
                throw new RuntimeException(err);
            }
        }
		
	    dumpShader(location, source, type, dumpMeta);
    }
	
	private void dumpShader(ResourceLocation location, String source, AttachmentType type, String dumpMeta) {
		String pth = "paco_shader_dump/" + dumpMeta + location.getNamespace() + "/" + location.getPath() + "." + type.strName;
		File fl = new File(pth);
		
		try {
			if (!fl.exists()) {
				fl.getParentFile().mkdirs();
				fl.createNewFile();
			}
			
			FileOutputStream fs = new FileOutputStream(fl);
			fs.write(source.getBytes(StandardCharsets.UTF_8));
			fs.flush();
			fs.close();
		} catch (Throwable err) {
			err.printStackTrace();
		}
	}
	
	public void delete() {
        GL20.glDeleteShader(id);
    }

    public int getId() {
        return id;
    }
}
