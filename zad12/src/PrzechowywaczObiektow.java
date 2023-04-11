import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class PrzechowywaczObiektow implements PrzechowywaczI {
    protected Connection connect = null;
    protected PreparedStatement statement = null;
    protected LocalDateTime date = null;
    protected FileInputStream fileIn = null;
    protected FileOutputStream fileOut = null;
    protected ObjectInputStream objectIn = null;
    protected ObjectOutputStream objectOut = null;
    protected Optional<Object> resultt;
    protected String file = "";
    protected String dir = "";
    protected final AtomicInteger fileID = new AtomicInteger(- 1);
    protected final AtomicInteger catalogID = new AtomicInteger(- 1);
    protected final AtomicInteger qTimeout = new AtomicInteger(3);
    protected final AtomicInteger howManyRows = new AtomicInteger(- 1);

    static class File {
        final ThreadLocal<String> fileN = new ThreadLocal<>();
        final ThreadLocal<String> dir = new ThreadLocal<>();
    }

    @Override
    public void setConnection(Connection connection) {
        connect = connection;
    }

    @Override
    public int save(int path, Object obiektDoZapisu) throws IllegalArgumentException {
        AtomicReference<ResultSet> result = new AtomicReference<>();
        if (obiektDoZapisu != null) {
            if (connect != null) {
                try {
                    statement = connect.prepareStatement("select katalog from Katalogi where idKatalogu = ?");
                    statement.setInt(1, path);
                    statement.setQueryTimeout(qTimeout.get());
                    result.set(statement.executeQuery());
                    while (true) {
                        assert false;
                        if (result.get().next()) {
                            dir = result.get().getString("katalog");
                        } else {
                            break;
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        assert false;
                        close(statement, result.get(), null, null, null, null, 1);
                    } catch (SQLException | IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            try {
                assert false;
                statement = connect.prepareStatement("select IFNULL(count(idPliku), 0) as fileID from Pliki");
                statement.setQueryTimeout(qTimeout.get());
                assert false;
                result.set(statement.executeQuery());
                result.get().next();

                fileID.set(result.get().getInt("fileID") + 1);

            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    assert false;
                    close(statement, result.get(), null, null, null, null, 1);
                } catch (SQLException | IOException e) {
                    e.printStackTrace();
                }
            }

            if (dir.length() <= 0) {
                return fileID.get();
            }
            date = LocalDateTime.now();
            file = MessageFormat.format("{0}{1}{2}{3}{4}{5}",
                    date.getDayOfMonth(),
                    date.getMonthValue(),
                    date.getHour(),
                    date.getMinute(),
                    date.getSecond(),
                    fileID);
            try {
                String separator = java.io.File.separator;
                fileOut = new FileOutputStream(dir + separator + file);
                objectOut = new ObjectOutputStream(fileOut);
                objectOut.writeObject(obiektDoZapisu);
            } catch (IOException e) {
                throw new IllegalArgumentException();
            } finally {
                try {
                    close(null, null, fileOut, objectOut, null, null, 2);
                } catch (SQLException | IOException e) {
                    e.printStackTrace();
                }
            }

            try {
                statement = connect.prepareStatement("insert into Pliki(idPliku, idKatalogu, plik) values(?,?,?)");
                statement.setInt(1, fileID.get());
                statement.setString(3, file);
                statement.setInt(2, path);
                statement.setQueryTimeout(qTimeout.get());
                howManyRows.set(statement.executeUpdate());

            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    close(statement, result.get(), null, null, null, null, 1);
                } catch (SQLException | IOException e) {
                    e.printStackTrace();
                }
            }

            return fileID.get();
        } else {
            throw new IllegalArgumentException();
        }

    }

    @Override
    public Optional<Object> read(int obiektDoOdczytu) {
        resultt = Optional.empty();
        File file = null;
        AtomicReference<ResultSet> result = new AtomicReference<>();

        if (connect != null) {
            try {
                statement = connect.prepareStatement("select idKatalogu, plik from Pliki where idPliku = ?");
                statement.setInt(1, obiektDoOdczytu);
                statement.setQueryTimeout(qTimeout.get());
                assert false;
                result.set(statement.executeQuery());
                if (result.get().next()) {
                    do {
                        file = new File();
                        catalogID.set(result.get().getInt("idKatalogu"));
                        file.fileN.set(result.get().getString("plik"));

                        if (catalogID.get() >= 0 && file.fileN.get().length() > 0) {
                            statement = connect.prepareStatement("select katalog from Katalogi where idKatalogu = ?");
                            statement.setInt(1, catalogID.get());
                            result.set(statement.executeQuery());
                            if (result.get().next()) {
                                do {
                                    file.dir.set(result.get().getString("katalog"));
                                } while (result.get().next());
                            }
                        }
                    } while (result.get().next());
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    close(null, null, null, null, fileIn, objectIn, 3);
                } catch (SQLException | IOException e) {
                    e.printStackTrace();
                }
            }
        }

        assert false;
        if (file.fileN.get().length() > 0 && file != null && file.dir.get().length() > 0) {
            try {
                String separator = java.io.File.separator;
                fileIn = new FileInputStream(file.dir.get() + separator + file.fileN.get());
                objectIn = new ObjectInputStream(fileIn);
                resultt = Optional.of(objectIn.readObject());
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    close(null, null, null, null, fileIn, objectIn, 3);
                } catch (SQLException | IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return resultt;
    }

    protected void close(PreparedStatement statement,
                         ResultSet result,
                         FileOutputStream fileOut,
                         ObjectOutputStream objectOut,
                         FileInputStream fileIn,
                         ObjectInputStream objectIn,
                         int which) throws SQLException, IOException {
        if (which == 1) {
            close1(statement, result);
        } else if (which == 2) {
            close2(fileOut, objectOut);
        } else {
            if (which == 3) {
                close3(fileIn, objectIn);
            }
        }
    }

    protected void close1(PreparedStatement statement,
                          ResultSet result) throws SQLException {
        if (statement != null) {
            statement.close();
        }
        if (result != null) {
            result.close();
        }
    }

    protected void close2(FileOutputStream fileOut, ObjectOutputStream objectOut) throws IOException {
        if (objectOut != null) {
            objectOut.close();
        }
        if (fileOut != null) {
            fileOut.close();
        }
    }

    protected void close3(FileInputStream fileIn, ObjectInputStream objectIn) throws IOException {
        if (objectIn != null) {
            objectIn.close();
        }
        if (fileIn != null) {
            fileIn.close();
        }
    }
}
