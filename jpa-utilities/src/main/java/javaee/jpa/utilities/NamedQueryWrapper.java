package javaee.jpa.utilities;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

public class NamedQueryWrapper<T> {

    private EntityManager em;
    private NamedQueryWrapperErrorService errorService;
    private String query;
    private Class<T> resultClass;
    private Map<String, Object> parameters;
    private TypedQuery<T> queryObject;
    private Boolean skip;

    public NamedQueryWrapper(EntityManager em, NamedQueryWrapperErrorService errorService, String query,
            Class<T> resultClass) {
        super();
        skip = false;
        if (errorService == null) {
            throw new NullPointerException("JpaErrorService instance is null.");
        } else {
            try {
                if (em == null) {
                    throw new NullPointerException("EntityManager instance is null.");
                }
                if (query == null) {
                    throw new NullPointerException("Named query name is null");
                } else if (query.isEmpty()) {
                    throw new NullPointerException("Named query name is empty");
                }
                if (resultClass == null) {
                    throw new NullPointerException("Class<?> result class instance is null");
                }
            } catch (NullPointerException exception) {
                errorService.exceptionOnInitialisation(exception, this);
                skip = true;
            }
        }
        this.em = em;
        this.errorService = errorService;
        this.query = query;
        this.resultClass = resultClass;
        parameters = new LinkedHashMap<>();
    }

    public void setParameter(String name, Object value) {
        parameters.put(name, value);
    }

    protected void createQueryObject() {
        if (skip == true) {
            return;
        }
        try {
            queryObject = em.createNamedQuery(query, resultClass);
        } catch (Exception exception) {
            errorService.exceptionOnNamedQueryCreation(exception, this);
            skip = true;
        }
    }

    protected void passParameters() {
        if (skip == true) {
            return;
        }
        for (Entry<String, Object> current : parameters.entrySet()) {
            try {
                queryObject.setParameter(current.getKey(), current.getValue());
            } catch (Exception exception) {
                errorService.exceptionOnSetQueryParameter(exception, current.getKey(), current.getValue(), this);
                break;
            }
        }
    }

    public List<T> getList(List<T> fallback) {
        createQueryObject();
        passParameters();
        List<T> result = fallback;
        try {
            result = queryObject.getResultList();
        } catch (Exception exception) {
            errorService.exceptionOnFetchResultList(exception, this);
        }
        return result;
    }

    public T getOne(T fallback) {
        createQueryObject();
        passParameters();
        T result = fallback;
        try {
            result = queryObject.getSingleResult();
        } catch (NoResultException exception) {
            return result;
        } catch (Exception exception) {
            errorService.exceptionOnFetchSingleResult(exception, this);
        }
        return result;
    }

}
