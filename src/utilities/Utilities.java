package utilities;
import java.io.File;
import java.util.Arrays;
import java.util.Comparator;


public class Utilities {

    /**
    * Function sorts files by their Id on the end of the file name. 
    * (e.q. img_1.png, img_2.png, ...)
    * 
    * @param sortAsc sorting Flag [true=ASC|false=DESC]
    */
   public static void sortFilesByIdName(final boolean sortAsc, File[] files) {

       Comparator<File> comparator = new Comparator<File>() {

           @Override
           public int compare(File o1, File o2) {
               if (sortAsc) {
                   return getFileId(o1).compareTo(getFileId(o2));
               } else {
                   return -1 * getFileId(o1).compareTo(getFileId(o2));
               }
           }
       };
       Arrays.sort(files, comparator);
   }

   /**
    * Helper method to determine file Id for sorting. File name has following
    * structure: img_11.png
    *
    * @param file
    * @return
    */
   private static Integer getFileId(File file) {
       String[] divided = file.getName().split("\\.");
       return Integer.parseInt(divided[0]);
   }
}
