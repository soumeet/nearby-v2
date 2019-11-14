package com.globsyn.nearbyv2;

import java.util.List;
import java.util.ArrayList;

public class VenueSingleton {
    private List<Venue> datasource ;
    private static VenueSingleton venueSingleton;
    private final static int NO_DATA = 0;
    public List<Venue> getDatasource() {
        return datasource;
    }
    public void setDatasource(List<Venue> datasource) {
        this.datasource = datasource;
    }
    public boolean isEmpty(){
        if (venueSingleton!=null){
            if (venueSingleton.datasource.size() == NO_DATA){
                return true;
            }else {
                return false;
            }
        }
        return false;
    }
    public static VenueSingleton create(){
        if (venueSingleton == null){
            venueSingleton = new VenueSingleton();
            venueSingleton.setDatasource(new ArrayList<Venue>());
        }
        return venueSingleton;
    }
}