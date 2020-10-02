package interfaces;

import enums.ECauseArretUrgence;
import enums.EStatusMoteur;

import java.util.TreeSet;

public interface IMoteur {
    void monter();
    void descendre();
    void arretProchainNiveau();
    void arretUrgence(ECauseArretUrgence cause);
}
