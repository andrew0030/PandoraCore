package com.github.andrew0030.pandora_core.config.registry;

import com.github.andrew0030.pandora_core.client.gui.screen.paco_config.selection.PaCoConfigSelectionScreen;
import com.github.andrew0030.pandora_core.config.manager.IConfigManager;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@ApiStatus.Internal
public class PaCoConfigRegistry {
    // A Map to store config manager instances associated with a unique object
    private static final Map<Object, IConfigManager> MANAGERS = new HashMap<>();
    // A Map to store config manager instances associated with their mod id
    private static final Map<String, List<IConfigManager>> MOD_MANAGERS = new LinkedHashMap<>();

    /**
     * Registers the given {@link IConfigManager} instance, using the given unique {@code object} as a key.
     *
     * @param uniqueKey The key that will be associated with the {@code manager}
     * @param manager   The {@link IConfigManager} instance that will be registered
     */
    public static void register(Object uniqueKey, IConfigManager manager) {
        if (MANAGERS.containsKey(uniqueKey))
            throw new IllegalStateException("Config manager for key " + uniqueKey + " is already registered!");
        MANAGERS.put(uniqueKey, manager);
        MOD_MANAGERS.computeIfAbsent(manager.getModId(), k -> new ArrayList<>()).add(manager);
    }

    /**
     * @return The {@link IConfigManager} instance associated with the given
     *         config {@code object}, or {@code null} if none is registered.
     */
    public static IConfigManager getManager(Object object) {
        return MANAGERS.get(object);
    }

    /** @return All registered {@link IConfigManager} instances. */
    public static Collection<IConfigManager> getManagers() {
        return MANAGERS.values();
    }

    /** @return A list of {@link IConfigManager} instances associated with the given {@code modId}. */
    public static List<IConfigManager> getManagersForMod(String modId) {
        return MOD_MANAGERS.getOrDefault(modId, Collections.emptyList());
    }

    /** Called when the game is shut down, allows for resource releasing. */
    public static void closeAll() {
        for (IConfigManager manager : PaCoConfigRegistry.getManagers())
            manager.close();
    }

    // TODO maybe remove this?
    public static Screen openConfigScreen(String modId, @Nullable TitleScreen titleScreen, @Nullable Screen previousScreen) {
        List<IConfigManager> managers = PaCoConfigRegistry.getManagersForMod(modId);
        if (managers.isEmpty()) return null; // TODO handle this is a better way...
//        if (managers.size() == 1) return new PaCoConfigScreen(managers.get(0), titleScreen, previousScreen);
        return new PaCoConfigSelectionScreen(managers, titleScreen, previousScreen);
    }
}