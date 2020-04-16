package it.unisannio.model;

import com.google.gson.Gson;

public class DevicePiece {
    private String idPiece;
    private String idDevice;

    public DevicePiece(){

    }

    public DevicePiece(String idPiece, String idDevice) {
        this.idPiece = idPiece;
        this.idDevice = idDevice;
    }

    public String getIdPiece() {
        return idPiece;
    }

    public void setIdPiece(String idPiece) {
        this.idPiece = idPiece;
    }

    public String getIdDevice() {
        return idDevice;
    }

    public void setIdDevice(String idDevice) {
        this.idDevice = idDevice;
    }

    public String toString(){
        return  new Gson().toJson(this);
    }
}
