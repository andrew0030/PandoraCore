package com.github.andrew0030.pandora_core.utils.data_holders;

import net.fabricmc.loader.api.metadata.CustomValue;
import net.fabricmc.loader.api.metadata.ModMetadata;

import java.util.ArrayList;
import java.util.List;

public class FabricModDataHolder extends ModDataHolder {
    private final ModMetadata metadata;
    private final List<String> icons = new ArrayList<>();

    public FabricModDataHolder(ModMetadata metadata) {
        this.metadata = metadata;
        /* We check for all valid mod icons and add them to the list */
        // Pandora Core
        CustomValue pandoracoreVal = metadata.getCustomValue("pandoracore");
        if(pandoracoreVal != null && pandoracoreVal.getType() == CustomValue.CvType.OBJECT) {
            CustomValue.CvObject pandoracoreObj = pandoracoreVal.getAsObject();
            CustomValue iconValue = pandoracoreObj.get("icon");
            if(iconValue != null && iconValue.getType() == CustomValue.CvType.STRING) {
                this.icons.add(iconValue.getAsString());
            }
        }
        // Catalogue
        CustomValue catalogueVal = metadata.getCustomValue("catalogue");
        if(catalogueVal != null && catalogueVal.getType() == CustomValue.CvType.OBJECT) {
            CustomValue.CvObject catalogueObj = catalogueVal.getAsObject();
            CustomValue iconValue = catalogueObj.get("icon");
            if(iconValue != null && iconValue.getType() == CustomValue.CvType.OBJECT) {
                CustomValue.CvObject iconObj = iconValue.getAsObject();
                CustomValue imageValue = iconObj.get("image");
                if(imageValue != null && imageValue.getType() == CustomValue.CvType.STRING) {
                    this.icons.add(imageValue.getAsString());
                }
            }
        }
        // Fabric
        metadata.getIconPath(0).ifPresent(this.icons::add);
    }

    @Override
    public String getModId() {
        return this.metadata.getId();
    }

    @Override
    public String getModName() {
        return this.metadata.getName();
    }

    @Override
    public String getModVersion() {
        return this.metadata.getVersion().getFriendlyString();
    }

    @Override
    public List<String> getModIconFiles() {
        return this.icons;
    }
}