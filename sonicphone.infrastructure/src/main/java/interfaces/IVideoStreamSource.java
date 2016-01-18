package interfaces;

/**
 * Schnittstelle, welche aus einer Datenquelle einzelne AccessUnits extrahiert
 */
public interface IVideoStreamSource
{
    /**
     * @return Anzahl der verfügbaren AccessUnits
     */
    int available();

    /**
     * @return Die nächste verfügbare AccessUnit
     */
    byte[] getNextAccessUnit();

    void connect();
}
