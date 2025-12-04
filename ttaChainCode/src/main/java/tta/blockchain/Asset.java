package tta.blockchain;

import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

@DataType()
public class Asset {

    @Property()
    private String assetId;

    @Property()
    private String owner;

    @Property()
    private String value;

    public Asset() {
        // Fabric이 JSON 역직렬화할 때 필요
    }

    public Asset(String assetId, String owner, String value) {
        this.assetId = assetId;
        this.owner = owner;
        this.value = value;
    }

    public String getAssetId() {
        return assetId;
    }

    public String getOwner() {
        return owner;
    }

    public String getValue() {
        return value;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
