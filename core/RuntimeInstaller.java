package ru.simplemc.updater.core;

import org.json.simple.JSONObject;
import org.rauschig.jarchivelib.Archiver;
import ru.simplemc.updater.util.SystemUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import static org.rauschig.jarchivelib.ArchiverFactory.createArchiver;

public class RuntimeInstaller {

    private final File runtimesStorage;
    private final File runtimeDirectory;
    private final File runtimeExecutableFile;
    private final File runtimeSchemeJSON;

    public RuntimeInstaller() {

        this.runtimesStorage = new File(PathUtil.getStorageDirectoryPath() + File.separator + "runtime");
        this.runtimeDirectory = new File(runtimesStorage, (SystemUtil.isMac() ? "jre1.8.0_51.jre" : "jre1.8.0_51"));
        this.runtimeExecutableFile = new File(runtimeDirectory + (SystemUtil.isMac() ? File.separator + "Contents" + File.separator + "Home" + File.separator : File.separator) + "bin", SystemUtil.isWindows() ? "java.exe" : "java");
        this.runtimeSchemeJSON = new File(runtimesStorage, runtimeDirectory.getName() + ".json");

        if (!this.runtimesStorage.exists() && this.runtimesStorage.mkdirs())
            System.out.println("Create runtimes storage directory");
    }

    public void install(File archiveFile) {

        try {
            Archiver archiver = createArchiver(archiveFile);
            archiver.extract(archiveFile, archiveFile.getParentFile());
        } catch (Exception e) {
            MessageUtil.printException("Неудалось распаковать архив (tar.gz)", "Неудалось распаковать архив!\nОбратитесь за помощью: vk.com/goodvise", e);
            e.printStackTrace();
        }

        if (archiveFile.delete())
            System.out.println("Delete unpacked archive: " + archiveFile.getName());

        this.createJSONScheme();
    }

    public void uninstall() {
        PathUtil.recursiveDelete(runtimeDirectory);
        PathUtil.recursiveDelete(runtimeSchemeJSON);
    }

    public void createJSONScheme() {

        JSONObject jsonObject = new JSONObject();

        for (File file : FilesScanner.walk(runtimeDirectory)) {
            String correctedFilePath = file.getPath().replace(runtimeDirectory.getPath(), "");
            jsonObject.put(correctedFilePath, file.length());
        }

        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(runtimeSchemeJSON));
            outputStreamWriter.write(jsonObject.toJSONString());
            outputStreamWriter.close();
        } catch (IOException e) {
            MessageUtil.printWarning("Неудалось сохранить схему JRE.", "Произошла ошибка при сохранении файла-карты для JRE, чтобы в дальнейшем ее быстро проверять.");
            e.printStackTrace();
        }
    }

    public File getRuntimeSchemeJSON() {
        return runtimeSchemeJSON;
    }

    public File getRuntimesStorage() {
        return runtimesStorage;
    }

    public File getRuntimeDirectory() {
        return runtimeDirectory;
    }

    public File getRuntimeExecutableFile() {
        return runtimeExecutableFile;
    }

}
