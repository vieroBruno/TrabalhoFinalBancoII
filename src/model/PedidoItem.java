package model;

public class PedidoItem {
    private int id_item;
    private int quantidade;

    // Construtor vazio
    public PedidoItem() {}

    public PedidoItem(int id_item, int quantidade) {
        this.id_item = id_item;
        this.quantidade = quantidade;
    }

    public int getId_item() {
        return id_item;
    }

    public void setId_item(int id_item) {
        this.id_item = id_item;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }
}
