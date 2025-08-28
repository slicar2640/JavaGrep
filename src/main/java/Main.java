import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Scanner;
import java.util.stream.Stream;

import grep.Pattern;
import grep.PatternCompiler;

public class Main {
  public static void main(String[] args) {
    if (args.length == 2) {
      Pattern pattern = new PatternCompiler(args[1]).compile();
      Scanner scanner = new Scanner(System.in);
      String inputLine = scanner.nextLine();

      if (pattern.matches(inputLine)) {
        System.exit(0);
      } else {
        System.exit(1);
      }
      scanner.close();
    } else {
      String firstFlag = args[0];
      if (firstFlag.equals("-E")) {
        Pattern pattern = new PatternCompiler(args[1]).compile();
        searchTextFiles(pattern, Arrays.copyOfRange(args, 2, args.length));
      } else if (firstFlag.equals("-r")) {
        Pattern pattern = new PatternCompiler(args[2]).compile(); // skip -E
        handleRecursive(pattern, args[3]); // skip -E
      }
    }
  }

  private static void searchTextFiles(Pattern pattern, String... filepaths) {
    boolean foundOne = false;
    for (int i = 0; i < filepaths.length; i++) {
      String filepath = filepaths[i];
      try (Scanner scanner = new Scanner(new File(filepath))) {
        while (scanner.hasNextLine()) {
          String inputLine = scanner.nextLine();
          if (pattern.matches(inputLine)) {
            foundOne = true;
            if (filepaths.length > 1) {
              System.out.println(filepath + ":" + inputLine);
            } else {
              System.out.println(inputLine);
            }
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
        System.exit(2);
      }
    }
    if (foundOne) {
      System.exit(0);
    } else {
      System.exit(1);
    }
  }

  private static void handleRecursive(Pattern pattern, String filepath) {
    Path startPath = Paths.get(filepath);
    if (!Files.exists(startPath)) {
      System.err.println("Error: path does not exist: " + filepath);
      System.exit(2);
    }
    boolean foundOne = false;
    if (Files.isDirectory(startPath)) {
      try (Stream<Path> stream = Files.walk(startPath)) {
        Iterator<Path> iterator = stream.filter(Files::isRegularFile).iterator();
        while (iterator.hasNext()) {
          Path currentPath = iterator.next();
          try (Scanner scanner = new Scanner(currentPath)) {
            while (scanner.hasNextLine()) {
              String inputLine = scanner.nextLine();
              if (pattern.matches(inputLine)) {
                foundOne = true;
                System.out.println(currentPath.toString() + ":" + inputLine);
              }
            }
          } catch (Exception e) {
            e.printStackTrace();
            System.exit(2);
          }
        }
      } catch (IOException e) {
        e.printStackTrace();
        System.exit(2);
      }
    } else {
      searchTextFiles(pattern, filepath);
    }
    if (foundOne) {
      System.exit(0);
    } else {
      System.exit(1);
    }
  }
}
