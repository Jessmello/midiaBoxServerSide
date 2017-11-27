package Model;

import java.io.Serializable;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author Jessica
 */
public class Midia implements Serializable{
    private StringProperty id_codigoProp;
    private int id_codigo;
    private String url;
    private StringProperty nomePro;

    public StringProperty getId_codigoProp() {
        return new SimpleStringProperty(this, String.valueOf(id_codigo));
    }

    public StringProperty getNomePro() {
        return new SimpleStringProperty(this, nome);
    }

    private String nome;
    private String responsavel;
    private String tipo;
    private String descricao;

    public int getId_codigo() {
        return id_codigo;
    }

    public void setId_codigo(int id_codigo) {
        this.id_codigo = id_codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(String responsavel) {
        this.responsavel = responsavel;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
    
    
    
}
