package interfaces;

import enums.EDirection;

public interface ISCC {
    void requeteCabine(double niveauDestination);
    void requeteEtage(double niveauSource, EDirection direction);
    void declencherArretUrgence();
    void stopperArretUrgence();
}
