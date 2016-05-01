package javaee.configuration.internal;

import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "configuration")
public class ConfigurationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String collection;

    private String key;

    private String value;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, collection, key, value);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ConfigurationEntity)) {
            return false;
        }
        ConfigurationEntity param = (ConfigurationEntity) obj;
        return Objects.equals(getId(), param.getId())
                && Objects.equals(getCollection(), param.getCollection())
                && Objects.equals(getKey(), param.getKey())
                && Objects.equals(getValue(), param.getValue());
    }

    @Override
    public String toString() {
        return "ConfigurationEntity ["
                + " id=" + Objects.toString(getId()) + ","
                + " collection=" + Objects.toString(getCollection()) + ","
                + " key=" + Objects.toString(getKey()) + ","
                + " value=" + Objects.toString(getValue()) + "]";
    }

}
