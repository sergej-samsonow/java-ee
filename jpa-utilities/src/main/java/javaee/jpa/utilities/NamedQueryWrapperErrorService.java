package javaee.jpa.utilities;

import javax.ejb.Stateless;

@Stateless
public class NamedQueryWrapperErrorService {

    public void exceptionOnInitialisation(Exception exception, NamedQueryWrapper<?> wrapper) {

    }

    public void exceptionOnNamedQueryCreation(Exception exception, NamedQueryWrapper<?> wrapper) {

    }

    public void exceptionOnSetQueryParameter(Exception exception, String parameterName, Object parameterValue,
            NamedQueryWrapper<?> wrapper) {

    }

    public void exceptionOnFetchResultList(Exception exception, NamedQueryWrapper<?> wrapper) {

    }

    public void exceptionOnFetchSingleResult(Exception exception, NamedQueryWrapper<?> queryWrapper) {

    }

}
