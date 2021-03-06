package net.highwayfrogs.editor.file.mof.animation;

import lombok.Getter;
import net.highwayfrogs.editor.Constants;
import net.highwayfrogs.editor.utils.Utils;
import net.highwayfrogs.editor.file.GameObject;
import net.highwayfrogs.editor.file.reader.DataReader;
import net.highwayfrogs.editor.file.writer.DataWriter;

/**
 * Represents the MR_ANIM_MODEL_SET struct.
 * Created by Kneesnap on 8/25/2018.
 */
@Getter
public class MOFAnimationModelSet extends GameObject {
    private int type;
    private MOFAnimationModel model;
    private MOFAnimationCelSet celSet;
    // BBOX Set is always empty, so we don't keep it.

    public static final int CEL_SET_COUNT = 1;
    public static final int FLAG_HIERARCHICAL = Constants.BIT_FLAG_0;
    private static final int FORCED_MODEL_COUNT = 1;

    @Override
    public void load(DataReader reader) {
        this.type = reader.readInt();

        short modelCount = reader.readUnsignedByteAsShort();
        short celsetCount = reader.readUnsignedByteAsShort();
        short bboxCount = reader.readUnsignedByteAsShort();
        reader.skipByte(); // Padding.

        int modelPointer = reader.readInt();
        int celsetPointer = reader.readInt(); // Right after struct.
        int bboxPointer = reader.readInt();

        Utils.verify(bboxCount == 0, "The ModelSet has a non-zero BBOX count. (%d, %d)", bboxCount, bboxPointer);
        Utils.verify(modelCount == FORCED_MODEL_COUNT, "FrogLord does not currently support MOFs with more than one model! (%d)", modelCount);
        Utils.verify(celsetCount == CEL_SET_COUNT, "FrogLord does not support MOFs with more than one cel-set! (%d)", celsetCount);

        // Read Celset.
        reader.jumpTemp(celsetPointer);
        this.celSet = new MOFAnimationCelSet();
        this.celSet.load(reader);
        reader.jumpReturn();

        // Read Models. (After Celset so it can reference cel sets loaded previously.)
        reader.jumpTemp(modelPointer);
        this.model = new MOFAnimationModel(this);
        this.model.load(reader);
        reader.jumpReturn();
    }

    @Override
    public void save(DataWriter writer) {
        writer.writeInt(this.type);

        writer.writeUnsignedByte((short) FORCED_MODEL_COUNT);
        writer.writeUnsignedByte((short) CEL_SET_COUNT);
        writer.writeNull(2); // BBOX Count + 1 byte of padding.

        int modelSetPointer = writer.writeNullPointer();
        int celSetPointer = writer.writeNullPointer(); // Right after model.
        writer.writeNullPointer(); // BBOX Pointer

        // Write models.
        writer.writeAddressTo(modelSetPointer);
        this.model.save(writer);

        // Write Celset.
        writer.writeAddressTo(celSetPointer);
        this.celSet.save(writer);

        // Writes Cel Set Pointers. MUST BE CALLED AFTER
        this.model.writeCelPointer(writer);
    }
}
