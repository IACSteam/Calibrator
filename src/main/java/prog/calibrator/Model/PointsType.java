package prog.calibrator.Model;

import java.util.Objects;

public class PointsType<X, Y> {

    private X rawItem;
    private Y realItem;

    public PointsType(X rawItem, Y realItem) {
        this.rawItem = rawItem;
        this.realItem = realItem;
    }

    public X getRawItem() {
        return rawItem;
    }

    public void setRawItem(X rawItem) {
        this.rawItem = rawItem;
    }

    public Y getRealItem() {
        return realItem;
    }

    public void setRealItem(Y realItem) {
        this.realItem = realItem;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PointsType<?, ?> that = (PointsType<?, ?>) o;
        return Objects.equals(rawItem, that.rawItem);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rawItem);
    }
}
