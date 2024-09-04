package sesac.server.common.util;

import java.beans.PropertyEditorSupport;
import sesac.server.common.exception.BaseException;
import sesac.server.common.exception.GlobalErrorCode;

public class EnumConverter<T extends Enum<T>> extends PropertyEditorSupport {

    private final Class<T> enumType;

    protected EnumConverter(Class<T> enumType) {
        this.enumType = enumType;
    }

    @Override
    public void setAsText(String text) {
        try {
            setValue(Enum.valueOf(enumType, text.toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new BaseException(GlobalErrorCode.NOT_FOUND_PAGE);
        }
    }
}