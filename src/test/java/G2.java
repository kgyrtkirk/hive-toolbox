/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.File;
import java.io.PrintStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Ignore;
import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimaps;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;

@Ignore
public class G2 {

  static class FNK implements Function<File, String> {

    @Override
    public String apply(File input) {
      return input.getName();
    }

  }

  @Test
  public void main() throws Exception {

    List<String> l0 = Files.readAllLines(new File("/tmp/_li_f2").toPath(), Charsets.ISO_8859_1);
    List<File> l = Lists.transform(l0, new Function<String, File>() {

      @Override
      public File apply(String input) {
        return new File(input);
      }

    });

    System.out.println(l.size());
    l.removeIf(f -> !f.exists());
    System.out.println(l.size());

    ImmutableListMultimap<String, File> mm = Multimaps.index(l, new FNK());

    PrintStream fos = new PrintStream(new File("/tmp/chk_l2.html"));

    int cnt = 0;
    for (String key : mm.keySet()) {
      ImmutableList<File> vals = mm.get(key);
      //      if (vals.size() >2 )
      //        continue;
      if (vals.size() == 1) {
        continue;
      }
      if (!key.matches(".*[jJ][pP][gG]$")) {
        continue;
      }
      //      if(vals.size()<=2)
      //        continue;
      //      if(!key.startsWith("P107"))
//        continue;
      //      if(key.startsWith("PTDC")) {
      //        continue;
      //      }
      if (allContains(vals, "kinga/2015")) {
        continue;
      }
      //      if(!key.startsWith("DSC"))
//        continue;
      //      if(!key.startsWith("IMG_"))
//        continue;
      fos.printf("<br>%s\n", key);
      for (File s : vals) {
        fos.printf("<img src=\"%s\" width=100>\n", s);
      }
      System.out.println(key);
      cnt++;
      purge(vals);
      //      if (cnt > 10)
      //        break;
    }
    fos.close();
    System.out.println(cnt);
  }

  private boolean allContains(ImmutableList<File> vals, String string) {
    for (File file : vals) {

      if (!file.getPath().contains(string)) {
        return false;
      }
    }
    return true;
  }

  private void purge(ImmutableList<File> vals) throws Exception {
    ArrayList<File> pix = new ArrayList<>();
    ArrayList<File> junk = new ArrayList<>();
    for (File string : vals) {
      String path = string.getPath();
      boolean startsWith = path.startsWith("/mnt/work/reshape/all-sync-crap/priv-pictures/")
          || path.startsWith("/mnt/work/reshape/all-sync-crap/helga-kepek/")
          || path.startsWith("/mnt/work/reshape/all-sync-crap/priv-unsorted-backup-pics")
          || path.startsWith("/mnt/work/reshape/all-sync-crap/some.pics.seems.like.dsc/");

      startsWith = false;
      if ((path.startsWith("/mnt/work/sync/pix/") || path.startsWith("/mnt/work/sync/helga-doktori/nevvaltoztatas/"))
          && !path.contains("probably-redownload-sync") && !path.contains("pictures_temp")) {
        pix.add(string);
      } else {
        junk.add(string);
      }
    }
    // try hashcode approach
    //    if(pix.size()>1)
    //      throw new RuntimeException("more than 1 in pix");

    if (pix.size() == 1) {
      HashCode pixH = hashFile(pix.iterator().next());
      Set<HashCode> known = new HashSet<>();
      known.add(pixH);
      for (File file : junk) {
        HashCode fH = hashFile(file);
        if (known.contains(fH)) {
          System.out.println("redundant");
          //          file.delete();
          continue;
        }
        known.add(fH);
        if (pixH.equals(fH)) {
          System.out.println("rempve " + file);
          //          file.delete();
        }

      }
    } else {
      if (pix.size() == 0) {
        Set<HashCode> known = new HashSet<>();
        for (File file : junk) {
          HashCode fH = hashFile(file);
          if (known.contains(fH)) {
            System.out.println("redundant");
            //                      file.delete();
            continue;
          }
          known.add(fH);
        }
      }
    }

    if (pix.size() == 1 && junk.size() == 1) {
      System.out.println("keep:" + pix);
      for (File object : junk) {
        System.out.println("del: " + object);
        //        object.delete();
      }

    } else {
      System.out.println("NOpurge!: " + pix + " ; " + junk);

    }
  }

  public static HashCode hashFile(File f) throws Exception {
    return com.google.common.io.Files.hash(f, Hashing.md5());
  }

}
