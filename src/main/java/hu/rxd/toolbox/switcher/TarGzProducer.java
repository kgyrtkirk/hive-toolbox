package hu.rxd.toolbox.switcher;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;


public class TarGzProducer {

  public URL createTarGzip(Path inputDirectoryPath) throws IOException {
    File outputFile = new File("/path/to/filename.tar.gz");

    try (FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
            GzipCompressorOutputStream gzipOutputStream = new GzipCompressorOutputStream(bufferedOutputStream);
            TarArchiveOutputStream tarArchiveOutputStream = new TarArchiveOutputStream(gzipOutputStream)) {

        tarArchiveOutputStream.setBigNumberMode(TarArchiveOutputStream.BIGNUMBER_POSIX);
        tarArchiveOutputStream.setLongFileMode(TarArchiveOutputStream.LONGFILE_GNU);

        List<File> files = new ArrayList<>(FileUtils.listFiles(
          inputDirectoryPath.toFile(),
                new RegexFileFilter("^(.*?)"),
                DirectoryFileFilter.DIRECTORY
        ));

        for (int i = 0; i < files.size(); i++) {
            File currentFile = files.get(i);

            String relativeFilePath = new File(inputDirectoryPath.toUri()).toURI().relativize(
                    new File(currentFile.getAbsolutePath()).toURI()).getPath();

            TarArchiveEntry tarEntry = new TarArchiveEntry(currentFile, relativeFilePath);
            tarEntry.setSize(currentFile.length());

            tarArchiveOutputStream.putArchiveEntry(tarEntry);
            tarArchiveOutputStream.write(IOUtils.toByteArray(new FileInputStream(currentFile)));
            tarArchiveOutputStream.closeArchiveEntry();
        }
        tarArchiveOutputStream.close();
        return outputFile.toURI().toURL();
    }
}
}
