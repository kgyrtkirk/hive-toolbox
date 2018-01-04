import java.io.IOException;
import java.io.InputStream;
import de.schlichtherle.truezip.file.TFile;
import de.schlichtherle.truezip.file.TFileInputStream;

public class TrueZipTest {

  //  @Test
  //  public void asd() throws Exception {
  //    boolean integrityOk = true;
  //    TFile zipFile = null;
  //    try {
  //      //      zipFile = new TFile(slave.getRoots().getFile(path), new TArchiveDetector("zip", new CheckedZipDriver(IOPoolLocator.SINGLETON)));
  //      String tgz = "http://104.198.109.242/logs/PreCommit-HIVE-Build-7247/test-results.tar.gz";
  //      //"/tmp/a.tar.gz"
  //      TPath p = new TPath(new URI(tgz));
  //      //      p.toNonArchivePath()
  //      TFile[] zipEntries = zipFile.listFiles(TArchiveDetector.NULL);
  //      if ((zipEntries == null) || (zipEntries.length == 0)) {
  //        throw new RuntimeException("err");
  //      } else {
  //        check(zipEntries);
  //      }
  //      //    } catch (IOException e) {
  //      //      integrityOk = false;
  //    } finally {
  //      if (zipFile != null) {
  //        //        try {
  //        TFile.umount(zipFile, true);
  //        //        } catch (FsSyncException e) {
  //        //           Already closed
  //        //        }
  //      }
  //    }
  //
  //  }

  private void check(TFile[] zipEntries) throws IOException {
    InputStream entryStream = null;
    for (TFile entry : zipEntries) {
      if (entry.isDirectory()) {
        check(entry.listFiles());
      } else {
        try {
          entryStream = new TFileInputStream(entry);
          byte[] buff = new byte[65536];
          while (entryStream.read(buff) != -1) {
            // do nothing, we are only checking for crc
          }
        } catch (IOException e) {
          throw new IOException(e);
        } finally {
          if (entryStream != null) {
            entryStream.close();
          }
        }
      }
    }
  }

}
