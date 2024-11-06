package com.luzpaez.growplant;

public class Recordatorio {
    private String id;            // ID del recordatorio (Generado automáticamente por Firebase)
    private String descripcion;   // Descripción del recordatorio
    private String fecha;         // Fecha en que se debe recordar
    private Plant planta;         // Objeto Plant que representa la planta asociada

    // Constructor
    public Recordatorio(String id, String descripcion, String fecha, Plant planta) {
        this.id = id;
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.planta = planta;
    }

    // Getters y Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public Plant getPlanta() {
        return planta;
    }

    public void setPlanta(Plant planta) {
        this.planta = planta;
    }
}
