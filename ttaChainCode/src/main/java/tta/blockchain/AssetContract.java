package tta.blockchain;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.Contract;
import org.hyperledger.fabric.contract.annotation.Default;
import org.hyperledger.fabric.contract.annotation.Transaction;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;

import java.lang.reflect.Type;

@Contract(name = "AssetContract")
@Default
public class AssetContract implements ContractInterface {

    private final Gson gson = new Gson();
    private final Type assetType = new TypeToken<Asset>() {}.getType();

    // ====== Helper methods ======

    private boolean assetExists(ChaincodeStub stub, String assetId) {
        String data = stub.getStringState(assetId);
        return data != null && !data.isEmpty();
    }

    private Asset getAssetOrThrow(ChaincodeStub stub, String assetId) {
        String data = stub.getStringState(assetId);
        if (data == null || data.isEmpty()) {
            throw new ChaincodeException("Asset not found: " + assetId);
        }
        return gson.fromJson(data, assetType);
    }

    // ====== Transactions ======

    /**
     * 자산 생성
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void createAsset(Context ctx, String assetId, String owner, String value) {
        ChaincodeStub stub = ctx.getStub();

        if (assetExists(stub, assetId)) {
            throw new ChaincodeException("Asset already exists: " + assetId);
        }

        Asset asset = new Asset(assetId, owner, value);
        String json = gson.toJson(asset);
        stub.putStringState(assetId, json);
    }

    /**
     * 자산 조회 (읽기 전용)
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public String readAsset(Context ctx, String assetId) {
        ChaincodeStub stub = ctx.getStub();
        Asset asset = getAssetOrThrow(stub, assetId);
        return gson.toJson(asset);
    }

    /**
     * 자산 소유자 변경
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void transferAsset(Context ctx, String assetId, String newOwner) {
        ChaincodeStub stub = ctx.getStub();
        Asset asset = getAssetOrThrow(stub, assetId);
        asset.setOwner(newOwner);

        String json = gson.toJson(asset);
        stub.putStringState(assetId, json);
    }

    /**
     * 자산 값 수정
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void updateAssetValue(Context ctx, String assetId, String newValue) {
        ChaincodeStub stub = ctx.getStub();
        Asset asset = getAssetOrThrow(stub, assetId);
        asset.setValue(newValue);

        String json = gson.toJson(asset);
        stub.putStringState(assetId, json);
    }

    /**
     * 자산 삭제
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void deleteAsset(Context ctx, String assetId) {
        ChaincodeStub stub = ctx.getStub();
        if (!assetExists(stub, assetId)) {
            throw new ChaincodeException("Asset not found: " + assetId);
        }
        stub.delState(assetId);
    }
}
