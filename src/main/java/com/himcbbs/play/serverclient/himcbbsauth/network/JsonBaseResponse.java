package com.himcbbs.play.serverclient.himcbbsauth.network;

public class JsonBaseResponse<T> {
    public int status;
    public String message;
    public T data;
}
