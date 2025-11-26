package service;

import model.Mesa;
import repository.MesaRepository;
import repository.mongo.MongoMesaRepository;
import java.util.List;

public class MesaService {
    private final MesaRepository repository;

    public MesaService(MesaRepository repository) {
        this.repository = repository;
    }
    public void cadastrarMesa(Mesa mesa) { repository.save(mesa); }
    public void editarMesa(Mesa mesa) { repository.update(mesa); }
    public List<Mesa> listarMesa() { return repository.listAll(); }
    public void excluirMesa(String id) { repository.delete(id); }
    public Mesa findByNumero(int numero) { return repository.findByNumero(numero); }
    public Mesa findById(String id) { return repository.findById(id); }
}