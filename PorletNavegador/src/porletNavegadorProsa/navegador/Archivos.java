

package porletNavegadorProsa.navegador;

import java.util.Date;

/**
 *
 * @author Lianet
 */
public class Archivos {
    private String nombre;
    private String cveMD5;
    private Date fecha;
    private long tamano;
    private boolean isDirectory;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public boolean getIsDirectory() {
        return isDirectory;
    }

    public void setIsDirectory(boolean isDirectory) {
        this.isDirectory = isDirectory;
    }

    public String getCveMD5() {
        return cveMD5;
    }

    public void setCveMD5(String cveMD5) {
        this.cveMD5 = cveMD5;
    }

    public String getFecha() {
        return fecha.toString();
    }

    public void setFecha(long fecha) {
        this.fecha= new Date(fecha);
    }

    public long getTamano() {
        return tamano;
    }

    public void setTamano(long tamano) {

        this.tamano = Math.round(Math.ceil(tamano/1024.0));
    }

}
