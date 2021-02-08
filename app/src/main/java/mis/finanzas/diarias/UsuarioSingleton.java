package mis.finanzas.diarias;

public class UsuarioSingleton {
    private int ID;
    private String username;

    private static UsuarioSingleton INSTANCE = null;

    private UsuarioSingleton() {
    }

    public static UsuarioSingleton getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new UsuarioSingleton();
        }
        return (INSTANCE);
    }

    /*public static void reset() {
        INSTANCE = new UsuarioSingleton();
    }*/

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}