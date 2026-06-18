package com.github.andrew0030.pandora_core.modules.templater.compat;

import com.github.andrew0030.pandora_core.modules.templater.TemplateTransformation;
import com.github.andrew0030.pandora_core.modules.templater.transformer.VariableMapper;
import tfc.glsl.GlslFile;
import tfc.glsl.base.GlslStatement;
import tfc.glsl.base.GlslValue;
import tfc.glsl.base.StatementType;
import tfc.glsl.base.ValueType;
import tfc.glsl.meta.VarSpecifier;
import tfc.glsl.segments.GlslCodeSegment;
import tfc.glsl.statements.AssignmentStatement;
import tfc.glsl.statements.MethodCallStatement;
import tfc.glsl.statements.VarDefStatement;
import tfc.glsl.value.*;
import tfc.glsl.visitor.*;

public class ImmersivePortalsPrePatcher extends CompatPrePatcher {
	@Override
	public void apply(VariableMapper mapper, GlslFile tree, TemplateTransformation transformation) {
		
		boolean[] patched = new boolean[]{false};
		String snowflake = transformation.generateSnowflake();
		
		{
			GlslValueVisitor statementVisitor = new GlslValueVisitorAdapter() {
				@Override
				public void visitCall(MethodCallValue mc) {
					if (mc.getName().asString().equals("dot")) {
						GlslValue value = mc.getParams()[0];
						if (value.getValueType().equals(ValueType.ACCESS_MEMBER)) {
							GlslValue val = ((AccessMemberValue) value).getObject();
							
							if (val.getValueType().equals(ValueType.TOKEN)) {
								TokenValue token = (TokenValue) val;
								if (mapper.mapFrom(null, token.getText()).equals("Position")) {
									System.out.println("LOCATED!");
									patched[0] = true;
									token.setText("paco_" + snowflake + "_premulPos");
								}
							}
						}
					}
				}
			};
			
			GlslTreeVisitor visitor = new GlslTreeVisitor(null, null, new GlslSegmentVisitorAdapter() {
				@Override
				public void visitCode(GlslCodeSegment segment) {
					if (!segment.getName().equals("main")) return;
					if (!segment.getParams().isEmpty()) return;
					
					for (GlslStatement statement : segment.getStatements()) {
						if (statement.getStatementType().equals(StatementType.ASSIGNMENT)) {
							AssignmentStatement assignmentStatement = (AssignmentStatement) statement;
							statementVisitor.visitValue(assignmentStatement.getValue());
						}
					}
					
					if (patched[0]) {
						VarDefStatement varDef = new VarDefStatement(
								new VarSpecifier("vec4", "paco_" + snowflake + "_premulPos")
						);
						varDef.setValue(
								new OperationValue(
										new TokenValue(mapper.mapTo(null, "ModelViewMat")),
										"*",
										new MethodCallValue(
												new TokenValue("vec4"),
												new TokenValue(mapper.mapTo(null, "Position")),
												new ConstantValue(1.0f)
										)
								)
						);
						segment.getStatements().add(0, varDef);
					}
					
					super.visitCode(segment);
				}
			});
			
			visitor.visit(tree);
		}
		
//		if (!patched[0]) {
//			// 	gl_ClipDistance[0] = dot(paco_paco_121764047_0_premulPos.xyz, imm_ptl_ClippingEquation.xyz) + imm_ptl_ClippingEquation.w;
//
//			GlslTreeVisitor visitor = new GlslTreeVisitor(null, null, new GlslSegmentVisitorAdapter() {
//				@Override
//				public void visitCode(GlslCodeSegment segment) {
//					if (!segment.getName().equals("main")) return;
//					if (!segment.getParams().isEmpty()) return;
//
//					AssignmentStatement assignment = new AssignmentStatement(
//							new AccessArrayValue(new TokenValue("gl_ClipDistance"), new ConstantValue(0)),
//							new OperationValue(
//									new MethodCallValue(
//											new TokenValue("dot"),
//											new AccessMemberValue(new TokenValue("paco_" + snowflake + "_premulPos"), new TokenValue("xyz")),
//											new AccessMemberValue(new TokenValue("imm_ptl_ClippingEquation"), new TokenValue("xyz"))
//									),
//									"+",
//									new AccessMemberValue(new TokenValue("imm_ptl_ClippingEquation"), new TokenValue("w"))
//							)
//					);
//					segment.getStatements().add(1, assignment);
//
//					super.visitCode(segment);
//				}
//			});
//
//			visitor.visit(tree);
//
//		}
		
	}
}
