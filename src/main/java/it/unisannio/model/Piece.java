package it.unisannio.model;

import com.google.gson.Gson;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;

public class Piece {
    private int _id;
    private long count;

    public Piece(){

    }

    public Piece(int _id, long count){
        this._id = _id;
        this.count = count;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
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
