package com.github.andrew0030.pandora_core.client.shader.templating;

import com.github.andrew0030.pandora_core.client.shader.templating.action.Injection;
import com.github.andrew0030.pandora_core.client.shader.templating.action.InsertionAction;
import com.github.andrew0030.pandora_core.client.shader.templating.action.TransformVar;
import com.github.andrew0030.pandora_core.utils.collection.ReadOnlyList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TemplateTransformation {
    protected List<InsertionAction> actions = new ArrayList<>();
    protected HashMap<String, String> templates = new HashMap<>();
    protected HashMap<String, String> varTypes = new HashMap<>();
    protected HashMap<String, String> funcCache = new HashMap<>();

    public TemplateTransformation() {
    }

    public void lock() {
        boolean hasQuatTransform = false;
        for (InsertionAction action : actions) {
            if (action instanceof Injection inject) {
                inject.resolveTypes(varTypes);
            }
            if (action instanceof TransformVar transform) {
                hasQuatTransform = transform.hasQuatRot();
                funcCache.put("rotateQuat", "paco_rotateQuat_" + generateSnowflake());
            }
        }
        if (hasQuatTransform) {
            String qName = funcCache.get("rotateQuat");
            String qFunc = """
                    vec4 %func%(const vec4 point, const vec4 quat) {
                        const float
                        // === squares ===
                        xx = quat.x * quat.x,
                        yy = quat.y * quat.y,
                        zz = quat.z * quat.z,
                        ww = quat.w * quat.w,
                        // === pairs ===
                        xy = quat.x * quat.y,
                        xz = quat.x * quat.z,
                        yz = quat.y * quat.z,
                        xw = quat.x * quat.w,
                        zw = quat.z * quat.w,
                        yw = quat.y * quat.w,
                        // === ks ===
                        k = 1.0 / (xx + yy + zz + ww),
                        two_k = 2.0 * k;
                        return vec4(
                            fma(point.xxx,
                                vec3(
                                    (xx - yy - zz + ww) * k,
                                    two_k * (xy + zw),
                                    two_k * (xz - yw)
                                ),
                                fma(
                                    point.yyy,
                                    vec3(
                                        two_k * (xy - zw),
                                        (yy - xx - zz + ww) * k,
                                        two_k * (yz + xw)
                                    ),
                                    point.z * vec3(
                                        two_k * (xz + yw),
                                        two_k * (yz - xw),
                                        (zz - xx - yy + ww) * k
                                    )
                                )
                            ),
                            point.w
                        );
                    }vec3 %func%(const vec3 point,const vec4 quat){return %func%(vec4(point,0.0),quat).xyz;}
                    """.replace("%func%", qName);
            actions.add(0, new InsertionAction() {
                @Override
                public String headInjection(TemplateTransformation transformation) {
                    return qFunc;
                }
            });
        }
        actions = new ReadOnlyList<>(actions);
    }

    public List<InsertionAction> getActions() {
        return actions;
    }

    public String getVarType(String to) {
        return varTypes.get(to);
    }

    int sid = 0;

    public String generateSnowflake() {
        return "paco_" + hashCode() + "_" + (sid++);
    }

    public String getFunc(String func) {
        return funcCache.get(func);
    }

    public String beforeHInject() {
        return """
                #define paco_per_instance %paco_per_instance%
                """.replace("%paco_per_instance%", "in");
    }
    public String afterHInject() {
        return """
                #undef paco_per_instance
                """;
    }
}
