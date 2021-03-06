package net.highwayfrogs.editor.system.mm3d.blocks;

import lombok.Getter;
import net.highwayfrogs.editor.file.reader.DataReader;
import net.highwayfrogs.editor.file.writer.DataWriter;
import net.highwayfrogs.editor.system.mm3d.MMDataBlockBody;
import net.highwayfrogs.editor.system.mm3d.MisfitModel3DObject;

/**
 * Holds ascii metadata.
 * Created by Kneesnap on 2/28/2019.
 */
@Getter
public class MMMetaDataBlock extends MMDataBlockBody {
    private String key;
    private String value;

    public MMMetaDataBlock(MisfitModel3DObject parent) {
        super(parent);
    }

    @Override
    public void load(DataReader reader) {
        this.key = reader.readNullTerminatedString();
        this.value = reader.readNullTerminatedString();
    }

    @Override
    public void save(DataWriter writer) {
        writer.writeTerminatorString(this.key);
        writer.writeTerminatorString(this.value);
    }
}
