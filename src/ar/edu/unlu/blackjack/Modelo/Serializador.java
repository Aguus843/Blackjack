package ar.edu.unlu.blackjack.Modelo;

import java.io.*;

public class Serializador implements Serializable {

    public void escribir(String nombreArchivo, Object objeto) {
        try (FileOutputStream fos = new FileOutputStream(nombreArchivo);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(objeto);
        } catch (IOException e) {
            throw new RuntimeException("Error al escribir el archivo: " + nombreArchivo, e);
        }
    }

    // Devuelve null si el archivo no existe todavía (primer uso)
    public Object leer(String nombreArchivo) {
        File archivo = new File(nombreArchivo);
        if (!archivo.exists()) {
            return null;
        }
        try (FileInputStream fis = new FileInputStream(archivo);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            return ois.readObject();
        } catch (FileNotFoundException e) {
            return null;
        } catch (ClassNotFoundException | IOException e) {
            throw new RuntimeException("Error al leer el archivo: " + nombreArchivo, e);
        }
    }
}