package ar.edu.unlu.blackjack.Modelo;

import java.io.Serializable;
import java.util.HashMap;

public class AdministradorJugadorMasGanador implements Serializable {
    HashMap<String, Integer> nicknameJugadorGanador;
    Serializador serializador;
    public AdministradorJugadorMasGanador() {
        nicknameJugadorGanador = new HashMap<>();
        serializador = new Serializador();
    }

    public void escribir(){
        serializador.escribir("JugadorSerializado.bin", nicknameJugadorGanador);
    }

    public HashMap<String, Integer> leer(){
        return (HashMap<String, Integer>) serializador.leer("JugadorSerializado.bin");
    }

    public void agregarJugadorHashMap(String nickname, Integer partidasGanadas){
        nicknameJugadorGanador = leer(); // variable HashMap<String, Integer> -> return idem
        nicknameJugadorGanador.put(nickname, partidasGanadas);
    }

    /* actualizo el archivo, pero primero verifico si el nickname existe en el hashmap, si es asi, incrementa la cant de partidas ganadas
    y vuelve a escribir en el serializador.
    Si no se encuentra el nickname, se lo agrega y escribe el archivo bin.
    */
    public void actualizarArchivo(String nickname){
        boolean existe = false;
        nicknameJugadorGanador = leer();
        for (String nombreJugador : nicknameJugadorGanador.keySet()){
            if (nombreJugador.equals(nickname)){
                existe = true;
                int partidasGanadas = nicknameJugadorGanador.get(nickname) + 1;
                nicknameJugadorGanador.put(nickname, partidasGanadas);
            }
        }
        if (existe){
            // si existe sobrescribo el archivo directamente.
            escribir();
        }else{
            // si no existe lo agrego y lo guardo
            agregarJugadorHashMap(nickname, 1);
            escribir();
        }
    }
}
