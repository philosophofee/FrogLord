package net.highwayfrogs.editor.file.map.zone;

import lombok.Getter;
import lombok.Setter;
import net.highwayfrogs.editor.Constants;
import net.highwayfrogs.editor.file.GameObject;
import net.highwayfrogs.editor.file.reader.DataReader;
import net.highwayfrogs.editor.file.writer.DataWriter;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the "ZONE" struct.
 * This is written right after the pointers at the start.
 * Zone Header:
 * 4 Byte: "ZONE"
 * 2 Byte: zoneCount
 * <4 Byte * zoneCount> pointer to zones.
 * Zone:
 * Zone Struct
 * Camera Zone Data
 * Zone Region Data
 * Created by Kneesnap on 8/22/2018.
 */
@Getter
@Setter
public class Zone extends GameObject {
    private ZoneType type;
    private short xMin;
    private short zMin;
    private short xMax;
    private short zMax;
    private List<ZoneRegion> regions = new ArrayList<>();
    private CameraZone cameraZone;

    private static final int BASE_SIZE = 6 * Constants.SHORT_SIZE + Constants.INTEGER_SIZE; // This does not include the size of camera data, or zone region data.

    @Override
    public void load(DataReader reader) {
        short zoneId = reader.readShort();
        this.type = ZoneType.values()[zoneId];
        short regionCount = reader.readShort();
        this.xMin = reader.readShort();
        this.zMin = reader.readShort();
        this.xMax = reader.readShort();
        this.zMax = reader.readShort();
        int regionOffset = reader.readInt();

        // Read Camera-Zone data.
        if (type == ZoneType.CAMERA) {
            this.cameraZone = new CameraZone();
            this.cameraZone.load(reader);
        }

        // Read region data.
        reader.jumpTemp(regionOffset);
        for (int i = 0; i < regionCount; i++) {
            ZoneRegion region = new ZoneRegion();
            region.load(reader);
            getRegions().add(region);
        }
        reader.jumpReturn();
    }

    @Override
    public void save(DataWriter writer) {
        writer.writeShort((short) type.ordinal());
        writer.writeShort((short) getRegionCount());
        writer.writeShort(getXMin());
        writer.writeShort(getZMin());
        writer.writeShort(getXMax());
        writer.writeShort(getZMax());

        boolean writeCamera = (type == ZoneType.CAMERA);

        int regionOffset = writer.getIndex();
        regionOffset += Constants.INTEGER_SIZE; // After the offset bytes.
        if (writeCamera)
            regionOffset += CameraZone.BYTE_SIZE;

        writer.writeInt(regionOffset); // The location regions are stored at. This is still written even if there are no regions.

        // Save camera.
        if (writeCamera)
            this.cameraZone.save(writer);

        // Save regions.
        for (ZoneRegion region : getRegions())
            region.save(writer);
    }

    /**
     * Get the amount of regions.
     * @return regionCount
     */
    public int getRegionCount() {
        return getRegions().size();
    }

    /**
     * Get the amount of bytes this zone will take up.
     * @return byteSize
     */
    public int getByteSize() {
        return BASE_SIZE + (getRegionCount() * ZoneRegion.REGION_SIZE) + CameraZone.BYTE_SIZE;
    }
}
