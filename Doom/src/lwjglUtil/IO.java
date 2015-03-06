/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lwjglUtil;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Harald
 */
public class IO {
   public  static String readFile(String path, Charset encoding) 
  throws IOException 
{
  byte[] encoded = Files.readAllBytes(Paths.get(path));
  return new String(encoded, encoding);
}
    public static String readFile(String path){
       try {
           return  readFile(path, StandardCharsets.UTF_8);
       } catch (IOException ex) {
           Logger.getLogger(IO.class.getName()).log(Level.SEVERE, null, ex);
       }
       return null;
    }
}
