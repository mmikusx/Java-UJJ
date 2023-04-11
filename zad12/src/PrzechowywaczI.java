import java.sql.Connection;
import java.util.Optional;

public interface PrzechowywaczI {

	/**
	 * Metoda ustawia połączenie do bazy danych typu SQLite.
	 * 
	 * @param connection referencja do utworzonego połączenia do bazy danych.
	 */
	public void setConnection(Connection connection);

	/**
	 * Zleca pisanie obiektu na dysku w podanym katalogu.
	 * 
	 * @param path           Identyfikator katalogu, w którym ma zostać zapisany
	 *                       obiekt.
	 * @param obiektDoZapisu Referencja do obiektu, ktory ma zostac zapisany na
	 *                       dysku.
	 * @return Identyfikator obiektu. Podanie tego identyfikatora w metodzie read ma
	 *         pozwolic na odzyskanie obiektu.
	 * @throws IllegalArgumentException błędny identyfikator ścieżki (brak takiej w
	 *                                  bazie) lub problem z przekazaną referencją
	 *                                  obiektDoZapisu.
	 */
	public int save(int path, Object obiektDoZapisu) throws IllegalArgumentException;

	/**
	 * Zleca odczyt obiektu o podanym id.
	 * 
	 * @param obiektDoOdczytu Identyfikator obiektu, ktory chcemy odzyskac.
	 * @return Obiekt typu Optional zawierający (o ile istnieje) obiekt o podanym
	 *         obiektDoOdczytu. W przypadku podania błędnego idektyfikatora
	 *         obiektDoOdczytu metoda zwraca pusty obiekt Optional. Metoda
	 *         <b>nigdy</b> nie zwraca null.
	 */
	public Optional<Object> read(int obiektDoOdczytu);
}
