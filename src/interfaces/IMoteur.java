package interfaces;

import enums.ECauseArretUrgence;

public interface IMoteur {
    void monter();
    void descendre();
    void arretProchainNiveau();
    void arretUrgence(ECauseArretUrgence cause);
    void addListener(ISCC listener);
}
