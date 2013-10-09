package net.sourceforge.homesearch;

import winstone.Launcher;

import java.io.IOException;

/**
 * Author: Vitaly Sazanovich
 * Email: vitaly.sazanovich@gmail.com
 * Date: 09/10/13
 * Time: 10:33
 */
public class HomeSearchMain  {
      public static void main(String... args){
          try {
//              Launcher.main(new String[]{"--webroot=src/main/web","--preferredClassLoader=net.sourceforge.homesearch.HomeSearchClassLoader"});
              Launcher.main(new String[]{"--webroot=src/main/web","--ajp13Port=-1"});
          } catch (IOException e) {
              e.printStackTrace();
          }
      }
}
