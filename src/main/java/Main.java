import dao.CooperativaDAO;

// Classe MAIN para testar métodos
public class Main {

    public static void main(String[] args) {

        CooperativaDAO c = new CooperativaDAO();

        System.out.println(c.getUltimoId());

    }

}
