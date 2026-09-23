import dao.CooperativaDAO;
import dao.RotaDAO;
import model.Rota;

import java.util.ArrayList;

// Classe MAIN para testar métodos
public class Main {

    public static void main(String[] args) {

        String PASSWORD = System.getenv("DB_PASSWORD");

        System.out.println();

        CooperativaDAO c = new CooperativaDAO();

        Rota rota = new Rota();
        rota.setIdCooperativa(4L);
        rota.setNomeRota("Jordanésia - Santo André");
        rota.setIdRota(8L);

        RotaDAO rotaDAO = new RotaDAO();

        //int resultado = rotaDAO.atualizarRota(rota);

        ArrayList<Rota> rotas = rotaDAO.listarRotas();

        //int resultado = rotaDAO.cadastrarRota(rota);

        int resultado = rotaDAO.excluirRota(rota);

        System.out.println("Resultado: "+ resultado);


        for (Rota route : rotas) {
            System.out.println("ID: " + route.getIdRota());
            System.out.println("Cooperativa: " + route.getIdCooperativa());
            System.out.println("Nome: " + route.getNomeRota());
        }

        //System.out.println("Resultado: "+ resultado);

        System.out.println(c.getUltimoId());


    }

}
