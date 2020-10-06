package interfaces;

import enums.EDirection;

public interface ISCC {
    void requeteCabine(int niveauDestination);
    void requeteEtage(int niveauSource, EDirection direction);
    void declencherArretUrgence();
    void stopperArretUrgence();
}
