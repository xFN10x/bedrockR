package fn10.bedrockr.addons.source.supporting.block;

import fn10.bedrockr.addons.addon.jsonClasses.RP.BlockJSONEntry;
import fn10.bedrockr.addons.source.elementFiles.GlobalBuildingVariables;

import java.io.FileNotFoundException;
import java.util.UUID;

public class BlockTexture {
    public static final int ALL_FACES_MODE = 0;
    public static final int PILLAR_MODE = 1;
    public static final int PER_FACE_MODE = 2;
    private int mode;

    public UUID upTexID;
    public UUID downTexID;
    public UUID eastTexID;
    public UUID westTexID;
    public UUID northTexID;
    public UUID southTexID;

    public BlockJSONEntry.Textures convertBlockJsonTextures(GlobalBuildingVariables gbv) throws FileNotFoundException {
        switch (mode) {
            case ALL_FACES_MODE:
                String allName = gbv.addBlockTexture(gbv.Resource.getNameOfResourceFromUUID(upTexID.toString()));
                return new BlockJSONEntry.Textures(allName);
            case PILLAR_MODE:
                String up = gbv.addBlockTexture(gbv.Resource.getNameOfResourceFromUUID(upTexID.toString()));
                String down = gbv.addBlockTexture(gbv.Resource.getNameOfResourceFromUUID(downTexID.toString()));
                String north = gbv.addBlockTexture(gbv.Resource.getNameOfResourceFromUUID(northTexID.toString()));
                return new BlockJSONEntry.Textures(up, down, north);
            default:
                String up0 = gbv.addBlockTexture(gbv.Resource.getNameOfResourceFromUUID(upTexID.toString()));
                String down0 = gbv.addBlockTexture(gbv.Resource.getNameOfResourceFromUUID(downTexID.toString()));
                String north0 = gbv.addBlockTexture(gbv.Resource.getNameOfResourceFromUUID(northTexID.toString()));
                String south = gbv.addBlockTexture(gbv.Resource.getNameOfResourceFromUUID(southTexID.toString()));
                String east = gbv.addBlockTexture(gbv.Resource.getNameOfResourceFromUUID(eastTexID.toString()));
                String west = gbv.addBlockTexture(gbv.Resource.getNameOfResourceFromUUID(westTexID.toString()));
                return new BlockJSONEntry.Textures(up0, down0, north0, south, east, west);
        }
    }

    public BlockTexture(UUID allFace) {
        mode = 0;
        this.upTexID = allFace;
    }

    public BlockTexture(UUID top, UUID bottom, UUID sides) {
        this(top, bottom, sides, sides, sides, sides);
        mode = 1;
    }

    public BlockTexture(UUID top, UUID bottom, UUID north, UUID south, UUID east, UUID west) {
        mode = 2;
        this.upTexID = top;
        this.downTexID = bottom;
        this.northTexID = north;
        this.southTexID = south;
        this.eastTexID = east;
        this.westTexID = west;
    }

    public int getMode() {
        return mode;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof BlockTexture bt)
            return upTexID.equals(bt.upTexID) &&
                    downTexID.equals(bt.downTexID) &&
                    northTexID.equals(bt.northTexID) &&
                    southTexID.equals(bt.southTexID) &&
                    eastTexID.equals(bt.eastTexID) &&
                    westTexID.equals(bt.westTexID);
        else
            return super.equals(obj);
    }
}
