package it.unisannio.model;

import com.google.gson.Gson;

public class Piece {
    private String _id;
    private long count;

    public Piece(){

    }

    public Piece(String _id, long count){
        this._id = _id;
        this.count = count;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public String toString(){
        return  new Gson().toJson(this);
    }
}
