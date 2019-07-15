package de.tu_clausthal.in.propra.recyclingsystem;

public class RecyclingObject {

    private String creatorID;
    private String objectID;
    private String objectType;
    private float pfand;
    private boolean status;
    private String hash;
    private String prevhash;

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

    public float getPfand() {
        return pfand;
    }

    public void setPfand(float pfand) {
        this.pfand = pfand;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getPrevhash() {
        return prevhash;
    }

    public void setPrevhash(String prevhash) {
        this.prevhash = prevhash;
    }
}
