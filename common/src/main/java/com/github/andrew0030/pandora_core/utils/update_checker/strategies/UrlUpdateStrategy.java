package com.github.andrew0030.pandora_core.utils.update_checker.strategies;

import com.github.andrew0030.pandora_core.utils.data_holders.ModDataHolder;

import java.util.Set;

public class UrlUpdateStrategy extends UpdateCheckStrategy {

    public UrlUpdateStrategy(Set<ModDataHolder> holders) {
        super(holders);
    }

    @Override
    public void performUpdateCheck() {
        for (ModDataHolder holder : this.holders) {
            holder.getSha512Hash();
        }
    }
}