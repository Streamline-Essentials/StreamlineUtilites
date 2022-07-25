package tv.quaint.executables;

import lombok.Getter;
import lombok.Setter;
import tv.quaint.StreamlineUtilities;
import tv.quaint.executables.aliases.AliasGetter;
import tv.quaint.executables.aliases.StreamAlias;
import tv.quaint.executables.functions.StreamFunction;

import java.io.File;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ExecutableHandler {
    @Getter @Setter
    private static TreeMap<String, StreamFunction> loadedFunctions = new TreeMap<>();

    public static void loadFunctions(File folder) {
        if (! folder.isDirectory()) return;
        if (! folder.exists()) return;

        File[] files = folder.listFiles();
        if (files == null) return;
        List<StreamFunction> toLoad = new ArrayList<>();
        for (File file : files) {
            toLoad.add(new StreamFunction(file.getParentFile(), file.getName()));
        }
        loadTheseFunctions(toLoad);
    }

    public static void loadFunction(StreamFunction function) {
        getLoadedFunctions().put(function.getIdentifier(), function);
        StreamlineUtilities.getInstance().logInfo("Loaded function with identifier of '" + function.getIdentifier() + "'!");
    }

    public static void unloadFunction(StreamFunction function) {
        getLoadedFunctions().remove(function.getIdentifier());
        StreamlineUtilities.getInstance().logInfo("Unloaded function with identifier of '" + function.getIdentifier() + "'!");
    }

    public static void loadTheseFunctions(Collection<StreamFunction> functions) {
        AtomicInteger count = new AtomicInteger();
        functions.forEach((streamFunction) -> {
            if (streamFunction.load()) count.getAndIncrement();
        });

        StreamlineUtilities.getInstance().logInfo("Loaded " + count + " functions!");
    }

    public static void unloadAllFunctions() {
        unloadTheseFunctions(new ArrayList<>(getLoadedFunctions().values()));
    }

    public static void unloadTheseFunctions(Collection<StreamFunction> functions) {
        AtomicInteger count = new AtomicInteger();
        functions.forEach((streamFunction) -> {
            if (streamFunction.unload()) count.getAndIncrement();
        });

        StreamlineUtilities.getInstance().logInfo("Unloaded " + count + " functions!");
    }

    public static boolean isFunctionLoaded(StreamFunction function) {
        return getLoadedFunctions().containsValue(function);
    }

    public static TreeMap<String, StreamFunction> getEnabledFunctions() {
        TreeMap<String, StreamFunction> r = new TreeMap<>();

        for (StreamFunction function : getLoadedFunctions().values()) {
            if (function.isEnabled()) r.put(function.getIdentifier(), function);
        }

        return r;
    }

    public static int reloadFunctions() {
        unloadTheseFunctions(new ArrayList<>(getLoadedFunctions().values()));
        loadFunctions(StreamlineUtilities.getFunctionsFolder());
        enableAllFunctions();
        return getLoadedFunctions().size();
    }

    public static void enableAllFunctions() {
        enableTheseFunctions(getLoadedFunctions().values());
    }

    public static void enableTheseFunctions(Collection<StreamFunction> functions) {
        AtomicInteger count = new AtomicInteger();
        functions.forEach((streamFunction) -> {
            if (streamFunction.enable()) count.getAndIncrement();
        });

        StreamlineUtilities.getInstance().logInfo("Enabled " + count + " functions!");
    }

    public static void disableAllFunctions() {
        disableTheseFunctions(getLoadedFunctions().values());
    }

    public static void disableTheseFunctions(Collection<StreamFunction> functions) {
        AtomicInteger count = new AtomicInteger();
        functions.forEach((streamFunction) -> {
            if (streamFunction.disable()) count.getAndIncrement();
        });

        StreamlineUtilities.getInstance().logInfo("Disabled " + count + " functions!");
    }

    public static StreamFunction getFunction(String identifier) {
        return getLoadedFunctions().get(identifier);
    }

    public static StreamFunction getEnabledFunction(String identifier) {
        return getEnabledFunctions().get(identifier);
    }

    public static List<String> getFunctionIdentifiers() {
        List<String> r = new ArrayList<>();

        getLoadedFunctions().forEach((s, function) -> r.add(s));

        return r;
    }

    public static List<String> getEnabledFunctionIdentifiers() {
        List<String> r = new ArrayList<>();

        getEnabledFunctions().forEach((s, function) -> r.add(s));

        return r;
    }

    public static boolean isFunctionLoadedByName(String name) {
        return getLoadedFunctions().containsKey(name);
    }

    public static boolean isFunctionEnabledByName(String name) {
        return getEnabledFunctions().containsKey(name);
    }

    @Getter @Setter
    private static TreeMap<String, AliasGetter> loadedAliasGetters = new TreeMap<>();

    public static void loadGetter(AliasGetter getter) {
        getLoadedAliasGetters().put(getter.getIdentifier(), getter);
    }

    public static void unloadGetter(AliasGetter getter) {
        getLoadedAliasGetters().remove(getter.getIdentifier(), getter);
    }

    public static void unloadGetter(String identifier) {
        getLoadedAliasGetters().remove(identifier);
    }

    public static AliasGetter getGetter(String identifier) {
        return getLoadedAliasGetters().get(identifier);
    }

    public static List<String> getGetterAndGet(String identifier) {
        return getGetter(identifier).get();
    }

    public static boolean isGetterLoaded(String identifier) {
        return getLoadedAliasGetters().containsKey(identifier);
    }

    public static boolean isGetterLoaded(AliasGetter getter) {
        return getLoadedAliasGetters().containsValue(getter);
    }

    @Getter @Setter
    private static TreeMap<String, StreamAlias> loadedAliases = new TreeMap<>();

    public static int reloadAliases() {
        unloadTheseAliases(new ArrayList<>(getLoadedAliases().values()));
        loadAllAliases(StreamlineUtilities.getAliasesFolder());
        return getLoadedAliases().size();
    }

    public static void loadAllAliases(File folder) {
        if (! folder.isDirectory()) return;
        if (! folder.exists()) return;

        File[] files = folder.listFiles();
        if (files == null) return;
        Arrays.stream(files).forEach(a -> {
            if (! a.getName().endsWith(".yml")) return;

            loadAlias(new StreamAlias(a.getName().substring(0, a.getName().lastIndexOf(".")), folder));
        });
    }


    public static void loadTheseAliases(List<StreamAlias> these) {
        these.forEach(ExecutableHandler::loadAlias);
    }

    public static void loadAlias(StreamAlias alias) {
        getLoadedAliases().put(alias.getIdentifier(), alias);
        alias.register();
    }

    public static void unloadTheseAliases(List<StreamAlias> these) {
        these.forEach(ExecutableHandler::unloadAlias);
    }

    public static void unloadAlias(StreamAlias alias) {
        unloadAlias(alias.getIdentifier());
    }

    public static void unloadAlias(String identifier) {
        getLoadedAliases().remove(identifier).unregister();
    }

    public static void unloadAllAliases() {
        List<StreamAlias> aliases = new ArrayList<>(getLoadedAliases().values());

        aliases.forEach(a -> {
            unloadAlias(a.getIdentifier());
        });
    }
}
