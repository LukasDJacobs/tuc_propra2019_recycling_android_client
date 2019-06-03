package de.tu_clausthal.in.propra.recyclingsystem;

public class RecyclingObject {

    private String creatorID;
    private String objectID;
    private String objectType;
    private String pfand;
    private String status;
    private String verschrottet;
    private String prevhash;
    private String sighash;
    private String recyclerID;

    public String getCreatorID() {
        return creatorID;
    }

    public void setCreatorID(String creatorID) {
        this.creatorID = creatorID;
    }

    public String getObjectID() {
        return objectID;
    }

    public void setObjectID(String objectID) {
        this.objectID = objectID;
    }

    public String getObjectType() {
        return objectType;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    public String getPfand() {
        return pfand;
    }

    public void setPfand(String pfand) {
        this.pfand = pfand;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getVerschrottet() {
        return verschrottet;
    }

    public void setVerschrottet(String verschrottet) {
        this.verschrottet = verschrottet;
    }

    public String getPrevhash() {
        return prevhash;
    }

    public void setPrevhash(String prevhash) {
        this.prevhash = prevhash;
    }

    public String getSighash() {
        return sighash;
    }

    public void setSighash(String sighash) {
        this.sighash = sighash;
    }

    public String getRecyclerID() {
        return recyclerID;
    }

    public void setRecyclerID(String recyclerID) {
        this.recyclerID = recyclerID;
    }
}
