package ar.edu.unlu.blackjack.Modelo;

import java.io.*;

public class Serializador implements Serializable {

    public void escribir(String nombreArchivo, Object Serializar){
        try {
            FileOutputStream fos = new FileOutputStream(nombreArchivo);
            var objectoutputstream = new ObjectOutputStream(fos);
            objectoutputstream.writeObject(Serializar);
            fos.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Object leer(String nombreArchivo){
        try{
            FileInputStream fileInputStream = new FileInputStream(nombreArchivo);
            var objectInputStream =new ObjectInputStream(fileInputStream);
            var inputStream = objectInputStream.readObject();
            fileInputStream.close();
            return inputStream;
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
