package ru.simplemc.updater.downloader.file;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import ru.simplemc.updater.gui.utils.MessageUtils;
import ru.simplemc.updater.utils.CompressedUtils;
import ru.simplemc.updater.utils.FileUtils;
import ru.simplemc.updater.utils.OSUtils;
import ru.simplemc.updater.utils.ProgramUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DownloaderRuntimeArchiveFile extends DownloaderFile {

    private final Path runtimeDirectory;
    private final Path runtimeExecutableFile;
    private final Path runtimeFilesScheme;

    public DownloaderRuntimeArchiveFile(JSONObject fileInfoJSON) throws ClassCastException {
        super(fileInfoJSON);
        Path runtimesStorage = Paths.get(ProgramUtils.getStoragePath() + "/runtime");
        this.runtimeDirectory = Paths.get(runtimesStorage + "/" + (OSUtils.isMacOS() ? "jre1.8.0_51.jre" : "jre1.8.0_51"));
        this.runtimeExecutableFile = Paths.get(runtimeDirectory + (OSUtils.isMacOS() ? "/Contents/Home/bin/" : "/bin/") + (OSUtils.isWindows() ? "java.exe" : "java"));
        this.runtimeFilesScheme = Paths.get(runtimesStorage + "/" + runtimeDirectory.getFileName().toString() + ".json");
    }

    /**
     * Распаковывает архив с файлами
     */
    public void unpack() throws IOException {

        if (getPath().getFileName().toString().endsWith(".zip")) {
            CompressedUtils.unZipArchive(getPath().toFile());
        } else
            CompressedUtils.decompressTarGzip(getPath().toFile());

        Files.deleteIfExists(this.getPath());
        this.createFilesScheme();
    }

    /**
     * Создает файл-схему из файлов JRE (необходимо для более рациональной и быстрой проверки)
     *
     * @throws IOException - возвращает при невозможности записи файла-схемы
     */
    private void createFilesScheme() throws IOException {

        JSONObject filesScheme = new JSONObject();

        Files.walk(runtimeDirectory).filter(Files::isRegularFile).forEach(path -> {
            try {
                filesScheme.put(path.toString().replace(runtimeDirectory.toString(), ""), Files.size(path));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        Files.write(runtimeFilesScheme, filesScheme.toString().getBytes());
    }

    @Override
    public void prepareBeforeDownload() throws IOException {

        super.prepareBeforeDownload();

        if (Files.exists(runtimeDirectory)) {
            FileUtils.deleteFilesRecursive(runtimeDirectory);
        }
    }

    @Override
    public boolean isInvalid() {

        if (!Files.exists(this.runtimeDirectory) || !Files.exists(this.runtimeExecutableFile) || !Files.exists(this.runtimeFilesScheme)) {
            return true;
        }

        try {

            JSONObject jsonObject = (JSONObject) new JSONParser().parse(new String(Files.readAllBytes(this.runtimeFilesScheme)));

            if (jsonObject.size() < 10) {
                MessageUtils.printWarning("Обнаружена проблема", "Недостаточно исполняемых файлов Java!");
                return true;
            }

            for (Object filePath : jsonObject.keySet()) {

                Path runtimeFilePath = Paths.get(runtimeDirectory + "/" + filePath);

                if (!Files.exists(runtimeFilePath) || Files.size(runtimeFilePath) != Long.parseLong(jsonObject.get(filePath).toString())) {
                    MessageUtils.printWarning("Обнаружена проблема", "Найден невалидный файл Java:\n" + runtimeFilePath);
                    return true;
                }
            }

        } catch (IOException | ParseException e) {
            MessageUtils.printFullStackTraceWithExit("Не удалось проверить исполняемые файлы Java!", e);
            return true;
        }

        return false;
    }

    /**
     * @return Возвращает путь до исполняемого файла JRE
     */
    public Path getRuntimeExecutableFile() {
        return runtimeExecutableFile;
    }
}
