package communication;

import java.io.Serializable;

public class DataCarrier <T extends Serializable> implements Serializable {

    private DC info;
    private T data;
    private boolean request;
    private static final boolean REQUEST = true;
    private static final boolean RESPONSE = false;

    private DataCarrier(){

    }

    public DataCarrier(boolean request){
        this(DC.NO_INFO, null, request);
    }

    public DataCarrier(DC info, boolean request){
        this(info, null, request);
    }

    public DataCarrier(T data, boolean request){
        this(DC.NO_INFO, data, request);
    }

    public DataCarrier(DC info, T data, boolean request){
        this.info = info;
        this.data = data;
        this.request = request;
    }

    public DC getInfo() {
        return info;
    }

    public void setInfo(DC info) {
        this.info = info;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public boolean isRequest() {
        return request;
    }

    public void setRequest(boolean request) {
        this.request = request;
    }
}
