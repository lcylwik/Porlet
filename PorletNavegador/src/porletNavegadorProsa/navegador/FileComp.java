

package porletNavegadorProsa.navegador;

import java.io.File;
import java.util.Comparator;

/**
 *
 * @author Lianet
 */
public class FileComp implements Comparator{
    int mode=1;

    FileComp (int mode){
      this.mode=mode;
    }
    public int compare(Object o1, Object o2){
      File f1 = (File)o1;
      File f2 = (File)o2;
      if (f1.isDirectory()){
        if (f2.isDirectory()){
          switch(mode){
            case 1:return f1.getAbsolutePath().toUpperCase().compareTo(f2.getAbsolutePath().toUpperCase());
            case 2:return new Long(f1.length()).compareTo(new Long(f2.length()));
            case 3:return new Long(f1.lastModified()).compareTo(new Long(f2.lastModified()));
            default:return 1;
          }
        }
        else {
              return -1;
          }
      }
      else if (f2.isDirectory()) {
            return 1;
        }
      else{
          switch(mode){
            case 1:return f1.getAbsolutePath().toUpperCase().compareTo(f2.getAbsolutePath().toUpperCase());
            case 2:return new Long(f1.length()).compareTo(new Long(f2.length()));
            case 3:return new Long(f1.lastModified()).compareTo(new Long(f2.lastModified()));
            default:return 1;
          }
      }
    }
}

