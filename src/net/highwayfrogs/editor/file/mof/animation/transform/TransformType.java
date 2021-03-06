package net.highwayfrogs.editor.file.mof.animation.transform;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.function.Supplier;

/**
 * Represents transform data types.
 * Created by Kneesnap on 1/5/2019.
 */
@AllArgsConstructor
public enum TransformType {
    NORMAL((byte) 0x30, MR_MAT34::new), // '0'
    BYTE((byte) 0x31, MR_MAT34B::new), // '1'
    QUAT_BYTE((byte) 0x32, MR_QUATB_TRANS::new), // '2'
    QUAT((byte) 0x33, null), // '3'
    QUAT_SCALE_BYTE((byte) 0x34, null), // '4'
    QUAT_SCALE((byte) 0x35, null); // '5'

    @Getter private final byte byteValue;
    private final Supplier<TransformObject> maker;

    /**
     * Make a transform of this type.
     * @return mofTransform
     */
    public TransformObject makeTransform() {
        if (maker == null) // The null ones are not used by the retail game files.
            throw new RuntimeException("FrogLord does not have a transform parser for " + name() + ".");
        return maker.get();
    }

    /**
     * Return the TransformType based on a byte value.
     * @param byteValue The byte value to get.
     * @return type
     */
    public static TransformType getType(byte byteValue) {
        for (TransformType type : values())
            if (type.getByteValue() == byteValue)
                return type;
        throw new RuntimeException("Unknown TransformType for byte: " + byteValue);
    }
}
