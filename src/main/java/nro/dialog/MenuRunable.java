package nro.dialog;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Văn Tuấn - 0337766460
 */
@Setter
@Getter
public abstract class MenuRunable implements Runnable {
    private int indexSelected;
}
