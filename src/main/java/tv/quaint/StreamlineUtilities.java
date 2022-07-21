package tv.quaint;

import lombok.Getter;
import net.streamline.api.command.ModuleCommand;
import net.streamline.api.modules.ModuleUtils;
import net.streamline.api.modules.SimpleModule;
import net.streamline.api.modules.dependencies.Dependency;
import tv.quaint.configs.Configs;
import tv.quaint.configs.CustomPlaceholdersConfig;
import tv.quaint.executables.functions.FunctionHandler;
import tv.quaint.listeners.MainListener;

import java.io.File;
import java.util.Collections;
import java.util.List;

public class StreamlineUtilities extends SimpleModule {
    @Getter
    static StreamlineUtilities instance;
    @Getter
    static File executablesFolder;
    @Getter
    static File functionsFolder;
    @Getter
    static File scriptsFolder;
    @Getter
    static CustomPlaceholdersConfig customPlaceholdersConfig;
    @Getter
    static Configs configs;
    @Getter
    static MainListener mainListener;

    @Override
    public String identifier() {
        return "streamline-utils";
    }

    @Override
    public List<String> authors() {
        return List.of(
                "Quaint"
        );
    }

    @Override
    public List<Dependency> dependencies() {
        return Collections.emptyList();
    }

    @Override
    public List<ModuleCommand> commands() {
        return List.of(

        );
    }

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        executablesFolder = new File(getDataFolder(), "executables" + File.separator);
        executablesFolder.mkdirs();
        functionsFolder = new File(getExecutablesFolder(), "functions" + File.separator);
        functionsFolder.mkdirs();
        scriptsFolder = new File(getExecutablesFolder(), "scripts" + File.separator);
        scriptsFolder.mkdirs();

        FunctionHandler.loadFunctions(functionsFolder);
        FunctionHandler.enableAll();

        customPlaceholdersConfig = new CustomPlaceholdersConfig();
        configs = new Configs();

        mainListener = new MainListener();
    }

    @Override
    public void onDisable() {
        FunctionHandler.disableAll();
        FunctionHandler.unloadAll();

        mainListener.disable();
    }
}
