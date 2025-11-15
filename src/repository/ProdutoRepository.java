package repository;

import model.Produto;
import java.util.List;

public interface ProdutoRepository {

    void save(Produto produto);
    void update(Produto produto);
    void delete(String id_produto);
    Produto findById(String id_produto);
    Produto findByLegacyId(int id_produto_legado);
    List<Produto> listAll();
}