package model;

import org.bson.codecs.pojo.annotations.BsonId;

public class Funcionario {
    @BsonId
    private int id_funcionario;
    private String nome;
    private String cargo;
    private double salario;
    private String telefone;

    public Funcionario() {}

    public Funcionario(int id_funcionario, String nome, String cargo, double salario, String telefone){
        this.id_funcionario = id_funcionario;
        this.nome = nome;
        this.cargo = cargo;
        this.salario = salario;
        this.telefone = telefone;
    }
    public int getIdFuncionario() { return id_funcionario; }
    public void setIdFuncionario(int id_funcionario) { this.id_funcionario = id_funcionario; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getCargo() { return cargo; }
    public void setCargo(String cargo) { this.cargo = cargo; }
    public double getSalario() { return salario; }
    public void setSalario(double salario) { this.salario = salario; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    @Override
    public String toString() {
        return " : nome='" + nome + '\'' + ", cargo='" + cargo + '\'' + ", salario=" + salario + ", telefone="+ telefone;
    }
}