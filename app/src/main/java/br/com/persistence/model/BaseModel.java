package br.com.persistence.model;

import com.j256.ormlite.field.DatabaseField;

import java.util.Date;

/**
 * Created by gilson.maciel on 27/04/2015.
 */
public abstract class BaseModel {
    @DatabaseField(generatedId = true, allowGeneratedIdInsert = true, canBeNull = false)
    protected Long id;
    @DatabaseField(canBeNull = true)
    protected Date dataCriacao;

    public Date getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(Date dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
