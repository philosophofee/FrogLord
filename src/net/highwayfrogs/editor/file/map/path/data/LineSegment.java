package net.highwayfrogs.editor.file.map.path.data;

import lombok.Getter;
import net.highwayfrogs.editor.file.map.path.Path;
import net.highwayfrogs.editor.file.map.path.PathInfo;
import net.highwayfrogs.editor.file.map.path.PathSegment;
import net.highwayfrogs.editor.file.map.path.PathType;
import net.highwayfrogs.editor.file.reader.DataReader;
import net.highwayfrogs.editor.file.standard.SVector;
import net.highwayfrogs.editor.file.writer.DataWriter;
import net.highwayfrogs.editor.gui.GUIEditorGrid;
import net.highwayfrogs.editor.gui.editor.MapUIController;

/**
 * Represents PATH_LINE.
 * Created by Kneesnap on 9/16/2018.
 */
@Getter
public class LineSegment extends PathSegment {
    private SVector start = new SVector();
    private SVector end = new SVector();

    public LineSegment() {
        super(PathType.LINE);
    }

    @Override
    protected void loadData(DataReader reader) {
        this.start.loadWithPadding(reader);
        this.end.loadWithPadding(reader);
    }

    @Override
    protected void saveData(DataWriter writer) {
        this.start.saveWithPadding(writer);
        this.end.saveWithPadding(writer);
    }

    @Override
    protected SVector calculatePosition(PathInfo info) {
        int deltaX = end.getX() - start.getX();
        int deltaY = end.getY() - start.getY();
        int deltaZ = end.getZ() - start.getZ();

        SVector pathRunnerPosition = new SVector();
        pathRunnerPosition.setX((short) (start.getX() + ((deltaX * info.getSegmentDistance()) / getLength())));
        pathRunnerPosition.setY((short) (start.getY() + ((deltaY * info.getSegmentDistance()) / getLength())));
        pathRunnerPosition.setZ((short) (start.getZ() + ((deltaZ * info.getSegmentDistance()) / getLength())));
        return pathRunnerPosition;
    }

    @Override
    public void setupEditor(Path path, MapUIController controller, GUIEditorGrid editor) {
        super.setupEditor(path, controller, editor);
        editor.addFloatSVector("Start", getStart(), () -> controller.getController().resetEntities());
        editor.addFloatSVector("End", getEnd(), () -> controller.getController().resetEntities());
    }
}
